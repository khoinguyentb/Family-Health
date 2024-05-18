package com.kan.dev.familyhealth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val repository: FriendRepository
) : ViewModel() {

    private val _getAll = MutableLiveData<List<FriendModel>>()
    val getAll: LiveData<List<FriendModel>> = _getAll

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

    fun insert(friendModel: FriendModel) {
        viewModelScope.launch {
            repository.insert(friendModel)
        }
    }

    fun update(friendModel: FriendModel) {
        viewModelScope.launch {
            repository.update(friendModel)
        }
    }

    fun delete(friendModel: FriendModel) {
        viewModelScope.launch {
            repository.delete(friendModel)
        }
    }

    fun deleteById(code:String) {
        viewModelScope.launch {
            repository.deleteFriendById(code)
        }
    }

}