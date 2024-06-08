package com.kan.dev.familyhealth.data.repository

import androidx.annotation.WorkerThread
import com.kan.dev.familyhealth.data.dao.BMIDao
import com.kan.dev.familyhealth.data.model.BMI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BMIRepository @Inject constructor (private val dao: BMIDao) {
    fun getAll(): Flow<List<BMI>> {
        return dao.all
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getItem(id: Int) : Flow<BMI> {
        return withContext(Dispatchers.IO) {
            dao.getItem(id)
        }
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(bmi: BMI) {
        withContext(Dispatchers.IO) {
            dao.insert(bmi)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(bmi: BMI) {
        withContext(Dispatchers.IO) {
            dao.update(bmi)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(bmi: BMI) {
        withContext(Dispatchers.IO) {
            dao.delete(bmi)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dao.deleteAll()
        }
    }
}

