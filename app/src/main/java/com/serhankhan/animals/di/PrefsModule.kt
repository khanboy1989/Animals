package com.serhankhan.animals.di

import android.app.Application
import com.serhankhan.animals.util.SharedPreferenceHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
open class PrefsModule {


    @Provides
    @Singleton
    open fun provideSharePreferences(app:Application):SharedPreferenceHelper{
        return SharedPreferenceHelper(app)
    }

}