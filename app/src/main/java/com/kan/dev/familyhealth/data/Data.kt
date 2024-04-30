package com.kan.dev.familyhealth.data

import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.data.model.IntroModel
import com.kan.dev.familyhealth.data.model.LanguageModel

class Data {
    companion object{
        val languageList = arrayListOf<LanguageModel>(
            LanguageModel(R.drawable.english,"English","en",false),
            LanguageModel(R.drawable.spanish, "Spanish","es",false),
            LanguageModel(R.drawable.french,"French","fr",false),
            LanguageModel(R.drawable.hindi,"Hindi","hi",false),
            LanguageModel(R.drawable.portugeese,"Portuguese","pt",false),
            LanguageModel(R.drawable.german,"German","de",false),
            LanguageModel(R.drawable.indo,"Indonesian","io",false)
        )
        val introModelList = arrayListOf<IntroModel> (
              IntroModel(R.drawable.intro1, R.string.titleIntro1, R.string.desIntro1),
              IntroModel(R.drawable.intro2, R.string.titleIntro2, R.string.desIntro2),
              IntroModel(R.drawable.intro3, R.string.titleIntro3, R.string.desIntro3)
            )
    }
}