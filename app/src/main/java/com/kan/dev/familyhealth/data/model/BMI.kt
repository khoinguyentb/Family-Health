package com.kan.dev.familyhealth.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
@IgnoreExtraProperties
@Entity(tableName = "bmi")
data class BMI(
    var time: String,

    var gender: String,

    var age: Int,

    var weight: Float,

    var checkCm: Boolean,

    var checkSt: Boolean,

    var checkKg: Boolean,

    var checkLb: Boolean,

    var height: Float,

    var bmi: Float,

    var isRecent: Boolean = false
) : Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
    constructor() : this("", "", 0,0f,true,true,true,true,0f,0f)
}