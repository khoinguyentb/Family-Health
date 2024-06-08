package com.kan.dev.familyhealth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.data.model.HealthyModel
import com.kan.dev.familyhealth.data.repository.BMIRepository
import com.kan.dev.familyhealth.data.repository.FriendRepository
import com.kan.dev.familyhealth.data.repository.HealthyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repositoryHealthy: HealthyRepository,
    private val repositoryBMI: BMIRepository,
    private val repositoryFriend: FriendRepository,
) :ViewModel() {
    fun deleteAllHealthy() {
        viewModelScope.launch {
            repositoryHealthy.deleteAll()
        }
    }
    fun deleteAllBMI() {
        viewModelScope.launch {
            repositoryBMI.deleteAll()
        }
    }
    fun deleteAllFriend() {
        viewModelScope.launch {
            repositoryFriend.deleteAll()
        }
    }

    fun insertBMI(bmi: BMI) {
        viewModelScope.launch {
            repositoryBMI.insert(bmi)
        }
    }

    fun insertHealthy(healthyModel: HealthyModel) {
        viewModelScope.launch {
            repositoryHealthy.insert(healthyModel)
        }
    }

    fun insertFriend(friendModel: FriendModel) {
        viewModelScope.launch {
            repositoryFriend.insert(friendModel)
        }
    }

}