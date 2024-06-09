package com.kan.dev.familyhealth.data

import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.data.model.AboutModel
import com.kan.dev.familyhealth.data.model.FilterModel
import com.kan.dev.familyhealth.data.model.IntroModel
import com.kan.dev.familyhealth.data.model.LanguageModel
import com.kan.dev.familyhealth.data.model.PlaceModel
import com.kan.dev.familyhealth.data.model.SettingModel
import com.kan.dev.familyhealth.data.model.YearModel
import com.kan.dev.familyhealth.utils.BANK
import com.kan.dev.familyhealth.utils.BUS
import com.kan.dev.familyhealth.utils.CAFE
import com.kan.dev.familyhealth.utils.CINEMA
import com.kan.dev.familyhealth.utils.PETROL
import java.time.LocalDate

class Data {
    companion object {
        val languageList = arrayListOf<LanguageModel>(
            LanguageModel(R.drawable.english, "English", "en", false),
            LanguageModel(R.drawable.spanish, "Spanish", "es", false),
            LanguageModel(R.drawable.french, "French", "fr", false),
            LanguageModel(R.drawable.hindi, "Hindi", "hi", false),
            LanguageModel(R.drawable.portugeese, "Portuguese", "pt", false),
            LanguageModel(R.drawable.german, "German", "de", false),
            LanguageModel(R.drawable.indo, "Indonesian", "io", false)
        )
        val introModelList = arrayListOf<IntroModel>(
            IntroModel(R.drawable.intro1, R.string.titleIntro1, R.string.desIntro1),
            IntroModel(R.drawable.intro2, R.string.titleIntro2, R.string.desIntro2),
            IntroModel(R.drawable.intro3, R.string.titleIntro3, R.string.desIntro3)
        )

        var aboutModelListAdults: ArrayList<AboutModel> = object : ArrayList<AboutModel>() {
            init {
                add(
                    AboutModel(
                        R.color.very_severely_underweight,
                        R.string.very_Severely_underweight,
                        R.string.tv_16
                    )
                )
                add(
                    AboutModel(
                        R.color.severely_underweight,
                        R.string.severely_underweight,
                        R.string.tv_16_169
                    )
                )
                add(AboutModel(R.color.underweight, R.string.underweight, R.string.tv_17_184))
                add(AboutModel(R.color.normal, R.string.normal, R.string.tv_185_249))
                add(AboutModel(R.color.overweight, R.string.overweight, R.string.tv_25_299))
                add(AboutModel(R.color.obese_class_I, R.string.obese_Class_I, R.string.tv_30_349))
                add(AboutModel(R.color.obese_class_II, R.string.obese_Class_II, R.string.tv_35_399))
                add(AboutModel(R.color.obese_class_III, R.string.obese_Class_II, R.string.tv_40))
            }
        }

        var aboutModelListTeenagers: ArrayList<AboutModel> = object : ArrayList<AboutModel>() {
            init {
                add(AboutModel(R.color.underweight20, R.string.underweight, R.string.tv_154))
                add(AboutModel(R.color.normal20, R.string.normal, R.string.tv_154_255))
                add(AboutModel(R.color.overweight20, R.string.overweight, R.string.tv_226_263))
                add(AboutModel(R.color.obese_class_I20, R.string.obese_Class_I, R.string.tv_264))
            }
        }

        var settingModelList: List<SettingModel> = arrayListOf(
            SettingModel(R.drawable.language, R.string.Language),
            SettingModel(R.drawable.share, R.string.Share),
            SettingModel(R.drawable.rate, R.string.Rate),
            SettingModel(R.drawable.friend,R.string.Friend),
            SettingModel(R.drawable.version, R.string.Version),
            SettingModel(R.drawable.logout, R.string.LogOut),
        )

        val filterList: List<FilterModel> = arrayListOf(
            FilterModel(R.string.January, 1, false),
            FilterModel(R.string.February, 2, false),
            FilterModel(R.string.March, 3, false),
            FilterModel(R.string.April, 4, false),
            FilterModel(R.string.May, 5, false),
            FilterModel(R.string.June, 6, false),
            FilterModel(R.string.July, 7, false),
            FilterModel(R.string.August, 8, false),
            FilterModel(R.string.September, 9, false),
            FilterModel(R.string.October, 10, false),
            FilterModel(R.string.November, 11, false),
            FilterModel(R.string.December, 12, false),
        )

        var yearList: List<YearModel> = arrayListOf(
            YearModel(LocalDate.now().year - 10, false),
            YearModel(LocalDate.now().year - 9, false),
            YearModel(LocalDate.now().year - 8, false),
            YearModel(LocalDate.now().year - 7, false),
            YearModel(LocalDate.now().year - 6, false),
            YearModel(LocalDate.now().year - 5, false),
            YearModel(LocalDate.now().year - 4, false),
            YearModel(LocalDate.now().year - 3, false),
            YearModel(LocalDate.now().year - 2, false),
            YearModel(LocalDate.now().year - 1, false),
            YearModel(LocalDate.now().year, false),
            YearModel(LocalDate.now().year + 1, false),
            YearModel(LocalDate.now().year + 2, false),
            YearModel(LocalDate.now().year + 3, false),
            YearModel(LocalDate.now().year + 4, false),
            YearModel(LocalDate.now().year + 5, false),
            YearModel(LocalDate.now().year + 6, false),
            YearModel(LocalDate.now().year + 7, false),
            YearModel(LocalDate.now().year + 8, false),
            YearModel(LocalDate.now().year + 9, false),
            YearModel(LocalDate.now().year + 10, false),
        )

        val placeList = mutableListOf(
            PlaceModel(BUS, R.string.bus_stop, R.drawable.ic_place_1),
            PlaceModel(PETROL, R.string.petrol_station, R.drawable.ic_place_2),
            PlaceModel(CAFE, R.string.cafe, R.drawable.ic_place_3),
            PlaceModel(CINEMA, R.string.cinema, R.drawable.ic_place_4),
            PlaceModel(BANK, R.string.bank, R.drawable.ic_place_5),
        )
    }
}