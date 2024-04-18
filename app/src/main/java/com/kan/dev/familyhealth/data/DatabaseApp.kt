package com.kan.dev.familyhealth.data

/*
@Database(entities = [], version = 1, exportSchema = false)
abstract class DatabaseApp : RoomDatabase() {

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

}*/
