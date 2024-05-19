package com.kan.dev.familyhealth.interfacces

import com.kan.dev.familyhealth.data.model.BMI

interface IRecentListener {
    fun deleteRecent(bmi: BMI)
    fun clickRecent(item : BMI)
}