package com.serhankhan.animals

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.serhankhan.animals.di.AppModule
import com.serhankhan.animals.di.DaggerViewModelComponent
import com.serhankhan.animals.models.Animal
import com.serhankhan.animals.models.AnimalApiService
import com.serhankhan.animals.models.ApiKey
import com.serhankhan.animals.util.SharedPreferenceHelper
import com.serhankhan.animals.viewmodel.ListViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.Callable
import java.util.concurrent.Executor

@RunWith(MockitoJUnitRunner::class)
class ListViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var animalService:AnimalApiService

    @Mock
    lateinit var prefs:SharedPreferenceHelper

    val application = Mockito.mock(Application::class.java)

    var listViewModel = ListViewModel(application,true)

    private  var key = "Test Key"

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        DaggerViewModelComponent.builder().
            appModule(AppModule(application))
            .apiModule(ApiModuleTest(animalService))
            .prefsModule(PrefsModuleTest(prefs))
            .build().inject(listViewModel)
    }

    @Before
    fun setupRxSchedulers(){
        val immidiate = object: Scheduler(){
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() },true)
            }
        }

        RxJavaPlugins.setInitNewThreadSchedulerHandler{ t: Callable<Scheduler> -> immidiate  }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { t: Callable<Scheduler> ->  immidiate}
    }


    @Test
    fun getAnimalSuccess(){
        Mockito.`when`(prefs.getApiKey()).thenReturn(key)

        val animal = Animal("cow",null,null,null,null,null,null)

        val animalList = listOf(animal)

        val testSingle = Single.just(animalList)

        Mockito.`when`(animalService.getAnimals(key)).thenReturn(testSingle)

        listViewModel.refresh()

        Assert.assertEquals(1,listViewModel.animals.value?.size)
        Assert.assertEquals(false,listViewModel.loadError.value)
        Assert.assertEquals(false,listViewModel.loading.value)

    }


    @Test
    fun getAnimalsFailure(){

        Mockito.`when`(prefs.getApiKey()).thenReturn(key)
        val testSingle = Single.error<List<Animal>>(Throwable())
        val keySingle = Single.just(ApiKey("OK",key))


        Mockito.`when`(animalService.getAnimals(key)).thenReturn(testSingle)
        Mockito.`when`(animalService.getApiKey()).thenReturn(keySingle)

        listViewModel.refresh()

        Assert.assertEquals(null,listViewModel.animals.value)
        Assert.assertEquals(false,listViewModel.loading.value)
        Assert.assertEquals(true,listViewModel.loadError.value)

    }



    @Test
    fun getKeySuccess(){
        Mockito.`when`(prefs.getApiKey()).thenReturn(null)
        val apikey = ApiKey("OK",key)
        val keySingle = Single.just(apikey)
        Mockito.`when`(animalService.getApiKey()).thenReturn(keySingle)
        val animal = Animal("cow",null,null,null,null,null,null)
        val animalList = listOf(animal)
        val testSingle = Single.just(animalList)
        Mockito.`when`(animalService.getAnimals(key)).thenReturn(testSingle)
        listViewModel.refresh()

        Assert.assertEquals(1,listViewModel.animals.value?.size)
        Assert.assertEquals(false,listViewModel.loadError.value)
        Assert.assertEquals(false,listViewModel.loading.value)
    }


    @Test
    fun getKeyFailure(){
        Mockito.`when`(prefs.getApiKey()).thenReturn(null)
        val keySingle = Single.error<ApiKey>(Throwable())
        Mockito.`when`(animalService.getApiKey()).thenReturn(keySingle)
        listViewModel.refresh()

        Assert.assertEquals(null,listViewModel.animals.value)
        Assert.assertEquals(false,listViewModel.loading.value)
        Assert.assertEquals(true,listViewModel.loadError.value)
    }

}