package com.serhankhan.animals

import com.serhankhan.animals.di.ApiModule
import com.serhankhan.animals.models.AnimalApiService


class ApiModuleTest(val mockService:AnimalApiService): ApiModule() {

    override fun provideApiService(): AnimalApiService {
        return mockService
    }

}