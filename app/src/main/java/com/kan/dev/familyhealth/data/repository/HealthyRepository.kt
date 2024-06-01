package com.kan.dev.familyhealth.data.repository

import androidx.annotation.WorkerThread
import com.kan.dev.familyhealth.data.dao.HealthyDao
import com.kan.dev.familyhealth.data.model.HealthyModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HealthyRepository @Inject constructor(
    private val dao: HealthyDao
) {

    fun getAll(): Flow<List<HealthyModel>> {
        return dao.all
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getItem(id: Int) : Flow<HealthyModel> {
        return withContext(Dispatchers.IO) {
            dao.getItem(id)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getItemByDate(date: String) : Flow<HealthyModel> {
        return withContext(Dispatchers.IO) {
            dao.getItemByDate(date)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getRecordByDate(date : String) : HealthyModel? {
        return withContext(Dispatchers.IO) {
            dao.getRecordByDate(date)
        }
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(healthyModel: HealthyModel) {
        withContext(Dispatchers.IO) {
            dao.insert(healthyModel)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(healthyModel: HealthyModel) {
        withContext(Dispatchers.IO) {
            dao.update(healthyModel)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(healthyModel: HealthyModel) {
        withContext(Dispatchers.IO) {
            dao.delete(healthyModel)
        }
    }
    
}