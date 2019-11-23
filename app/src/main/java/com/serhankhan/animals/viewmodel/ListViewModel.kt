package com.serhankhan.animals.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.serhankhan.animals.di.AppModule
import com.serhankhan.animals.di.DaggerViewModelComponent
import com.serhankhan.animals.models.Animal
import com.serhankhan.animals.models.AnimalApiService
import com.serhankhan.animals.models.ApiKey
import com.serhankhan.animals.util.SharedPreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ListViewModel(application:Application):AndroidViewModel(application) {


    constructor(application: Application,test:Boolean = true):this(application){
        injected = true
    }


    val animals by lazy {MutableLiveData<List<Animal>>()}
    val loadError by lazy {MutableLiveData<Boolean>()}
    val loading by lazy {MutableLiveData<Boolean>()}

    @Inject
    lateinit var apiService:AnimalApiService

    @Inject
    lateinit var prefs:SharedPreferenceHelper

    fun inject() {
        if(!injected) {
            DaggerViewModelComponent.builder()
                .appModule(AppModule(getApplication()))
                .build()
                .inject(this)
        }
    }
    private var invalidApiKey = false
    private var injected = false

    /*
    * When we attach the singles to observables
    * if the lifecycle of the ListViewModel is finished (destroyed)
    * we will still maintain the Singles
    * so in order to not to have memory leak we dispose all of the
    * single links and clear the memory.
    * */
    private val disposable = CompositeDisposable()

    /**
     * The refresh function is called when first
     * ListFragment is launched and created
     * If checks if we have previously stored api key
     * it calls the getAnimals service
     * if not so calls the getKey function
     */
    fun refresh(){
        inject()
        loading.value = true
        invalidApiKey = false
        val key = prefs.getApiKey()
        if(key.isNullOrEmpty()){
            getKey()
        }else{
            getAnimals(key)
        }
    }

    fun hardRefresh(){
        inject()
        loading.value = true
        getKey()
    }

    //subscribeOn goes to background thread by creating new background thread
    //observeOn comes to mainThread and gets the data from mainThread

    private fun getKey(){
        disposable.add(
            apiService.getApiKey().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object:DisposableSingleObserver<ApiKey>(){
                    override fun onSuccess(key: ApiKey) {
                        if(key.key.isNullOrEmpty()){
                            loadError.value = true
                            loading.value = false
                        }else{
                            prefs.saveApiKey(key.key)
                            getAnimals(key.key)
                        }
                    }

                    override fun onError(p0: Throwable) {
                        if(!invalidApiKey){
                            invalidApiKey = true
                            getKey()
                        }else{
                            p0.printStackTrace()
                            loading.value = false
                            animals.value = null
                            loadError.value = true
                        }
                    }
                })
        )
    }

    private fun getAnimals(key:String){

        disposable.add(
            apiService.getAnimals(key).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object:DisposableSingleObserver<List<Animal>>(){
                    override fun onSuccess(list: List<Animal>) {
                        loadError.value = false
                        animals.value = list
                        loading.value =  false
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loading.value = false
                        animals.value = null
                        loadError.value = true
                    }

                })
        )

    }

    //when Viewmodel instance is destroyed dispose all of the HTTP calls
    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}