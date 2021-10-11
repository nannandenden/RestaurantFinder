package com.example.restaurantfinder

import android.app.Application
import com.example.restaurantfinder.di.dataSourceModel
import com.example.restaurantfinder.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RestaurantFinderApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
    }

    /**
     * initialize Koin DI
     */
    private fun initKoin() {
        startKoin {
            androidContext(this@RestaurantFinderApplication)

            modules(listOf(dataSourceModel, viewModelsModule))
        }
    }
}