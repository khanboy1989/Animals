package com.serhankhan.animals.di

import com.serhankhan.animals.viewmodel.ListViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiModule::class,PrefsModule::class,AppModule::class])
interface ViewModelComponent {

    fun inject(viewModel:ListViewModel)
}