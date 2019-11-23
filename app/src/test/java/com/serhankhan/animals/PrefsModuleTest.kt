package com.serhankhan.animals

import android.app.Application
import com.serhankhan.animals.di.PrefsModule
import com.serhankhan.animals.util.SharedPreferenceHelper

class PrefsModuleTest(val mockPrefs:SharedPreferenceHelper):PrefsModule(){

    override fun provideSharePreferences(app: Application): SharedPreferenceHelper {
        return mockPrefs
    }

}