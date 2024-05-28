package com.kan.dev.familyhealth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kan.dev.familyhealth.data.model.HealthyModel
import com.kan.dev.familyhealth.data.repository.HealthyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthyViewModel @Inject constructor(
    private val repository: HealthyRepository
) : ViewModel() {
    private val _getAll = MutableLiveData<List<HealthyModel>>()
    val getAll: LiveData<List<HealthyModel>> = _getAll

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

    fun getItem(id: Int) {
        viewModelScope.launch {
            repository.getItem(id)
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
}