package com.kan.dev.familyhealth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.data.repository.BMIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BMIViewModel @Inject constructor(
    private val repository: BMIRepository
)  : ViewModel() {
    private val _getAll = MutableLiveData<List<BMI>>()
    val getAll: LiveData<List<BMI>> = _getAll

    init {
        getAll()
    }

    private fun getAll() {
        viewModelScope.launch {
            repository.getAll().collect {
                _getAll.postValue(it)
            }
        }
    }

    fun getBmi(id: Int) {
        viewModelScope.launch {
            repository.getItem(id)
        }
    }

    fun insert(bmi: BMI) {
        viewModelScope.launch {
            repository.insert(bmi)
        }
    }

    fun update(bmi: BMI) {
        viewModelScope.launch {
            repository.update(bmi)
        }
    }

    fun delete(bmi: BMI) {
        viewModelScope.launch {
            repository.delete(bmi)
        }
    }
}