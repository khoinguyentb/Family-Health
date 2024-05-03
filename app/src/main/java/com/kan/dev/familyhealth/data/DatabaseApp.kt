package com.kan.dev.familyhealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kan.dev.familyhealth.data.dao.BMIDao
import com.kan.dev.familyhealth.data.model.BMI
import java.util.concurrent.Executors

@Database(entities = [BMI::class], version = 1, exportSchema = false)
abstract class DatabaseApp : RoomDatabase() {
    abstract fun bmiDAO() : BMIDao

    companion object{
        private var INSTANCE : DatabaseApp? = null
        fun getDatabase(context: Context) : DatabaseApp{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseApp::class.java,
                    "fake_video_call_db"
                ).addCallback(object : Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Executors.newSingleThreadExecutor().execute{

                        }
                    }
                })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
