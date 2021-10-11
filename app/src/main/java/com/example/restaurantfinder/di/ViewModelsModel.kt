package com.example.restaurantfinder.di

import com.example.restaurantfinder.RestaurantResultViewModel
import com.example.restaurantfinder.restaurantdetail.RestaurantDetailViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { RestaurantResultViewModel(repository = get(), executors = get()) }
    viewModel { RestaurantDetailViewModel() }

}