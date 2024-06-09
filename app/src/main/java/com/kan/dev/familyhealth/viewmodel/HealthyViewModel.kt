package com.kan.dev.familyhealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.model.HealthyModel
import com.kan.dev.familyhealth.data.repository.HealthyRepository
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.SharePreferencesUtils
import com.kan.dev.familyhealth.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HealthyViewModel @Inject constructor(
    private val repository: HealthyRepository,
    private val sharePre : SharePreferencesUtils?=null
) : ViewModel() {


    private lateinit var myCode :String
    private val _getAll = MutableStateFlow(UiState.Loading as UiState)
    val getAll: StateFlow<UiState> = _getAll

    private val _uiState = MutableStateFlow(UiState.Loading as UiState)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiStateDate = MutableStateFlow(UiState.Loading as UiState)
    val uiStateDate: StateFlow<UiState> = _uiStateDate.asStateFlow()

    private var healthyModel: HealthyModel ?=null
    private var currentDate: String = ""
    private lateinit var dateFormat : SimpleDateFormat
    init {
        getAll()
        myCode = sharePre!!.getString(MY_CODE,"")!!
    }

    private fun getAll() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getAll()
                    .collect { healthyItem ->
                        _getAll.value = UiState.Success(healthyItem)
                    }
            } catch (e: Exception) {
                _getAll.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun getItem(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getItem(id)
                    .collect { item ->
                        _uiState.value = UiState.Success(item)
                    }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun getItemByDate(date : String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getItemByDate(date)
                    .collect { item ->
                        _uiStateDate.value = UiState.Success(item)
                    }
            } catch (e: Exception) {
                _uiStateDate.value = UiState.Error(e.message.toString())
            }
        }
    }

    fun createOrUpdateRecordForCurrentDate(stepCount : Int,distance : Float, calories : Float) {
        viewModelScope.launch {
            dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            currentDate = dateFormat.format(Date())
            healthyModel = repository.getRecordByDate(currentDate)
            if (healthyModel == null) {
                val newRecord = HealthyModel(0, 0f, 0f, currentDate)
                val healthData = mapOf(
                    "steps" to 0,
                    "distance" to 0f,
                    "calories" to 0f,
                    "date" to currentDate
                )
                RealtimeDAO.pushRealtimeData("$myCode/healthys/$currentDate",healthData){
                    insert(newRecord)
                }
            } else {
                healthyModel!!.steps = stepCount
                healthyModel!!.distance = distance
                healthyModel!!.calories = calories
                val healthData = mapOf(
                    "steps" to stepCount,
                    "distance" to distance,
                    "calories" to calories,
                )
                RealtimeDAO.updateRealtimeData("$myCode/healthys/$currentDate",healthData){
                    update(healthyModel!!)
                }
            }
        }
    }

    fun insert(healthyModel: HealthyModel) {
        viewModelScope.launch {
            repository.insert(healthyModel)
        }
    }

    fun update(healthyModel: HealthyModel) {
        viewModelScope.launch {
            repository.update(healthyModel)
        }
    }

    fun delete(healthyModel: HealthyModel) {
        viewModelScope.launch {
            repository.delete(healthyModel)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}

class HealthyViewModelFactory @Inject constructor(
    private val repository: HealthyRepository,
    private val sharePre: SharePreferencesUtils?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthyViewModel::class.java)) {
            return HealthyViewModel(repository,sharePre) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}