package com.kan.dev.familyhealth.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "bmi")
data class BMI(
    var time: String? = null,

    var gender: String? = null,

    var age: Int = 0,

    var weight: Float? = null,

    var checkCm: Boolean? = null,

    var checkSt: Boolean? = null,

    var checkKg: Boolean? = null,

    var checkLb: Boolean? = null,

    var height: Float? = null,

    var bmi: Double = 0.0,

    var isRecent: Boolean? = null
) : Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

}