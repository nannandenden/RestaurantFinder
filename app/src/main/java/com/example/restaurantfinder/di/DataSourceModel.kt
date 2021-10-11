package com.example.restaurantfinder.di

import com.example.restaurantfinder.helper.Executors
import com.example.restaurantfinder.helper.ExecutorsInterface
import com.example.restaurantfinder.repository.RestaurantDataSource
import com.example.restaurantfinder.repository.RestaurantRepository
import org.koin.dsl.module

val dataSourceModel = module {
    factory<ExecutorsInterface> { Executors() }
    factory<RestaurantDataSource> { RestaurantRepository() }
}