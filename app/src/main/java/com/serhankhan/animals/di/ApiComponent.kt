package com.serhankhan.animals.di

import com.serhankhan.animals.models.AnimalApiService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service:AnimalApiService)
}