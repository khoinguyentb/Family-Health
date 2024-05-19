package com.kan.dev.familyhealth.data.repository

import androidx.annotation.WorkerThread
import com.kan.dev.familyhealth.data.dao.FriendDAO
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.data.model.FriendModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FriendRepository @Inject constructor(
    private val dao: FriendDAO
) {
    fun getAll(): Flow<List<FriendModel>> {
        return dao.all
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getItem(id: Int) : FriendModel {
        return withContext(Dispatchers.IO) {
            dao.getItem(id)
        }
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(friendModel: FriendModel) {
        withContext(Dispatchers.IO) {
            dao.insert(friendModel)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(friendModel: FriendModel) {
        withContext(Dispatchers.IO) {
            dao.update(friendModel)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(friendModel: FriendModel) {
        withContext(Dispatchers.IO) {
            dao.delete(friendModel)
        }
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteFriendById(code :String) {
        withContext(Dispatchers.IO) {
            dao.deleteUserByCode(code)
        }
    }
}