package com.example.restaurantfinder.helper

import io.reactivex.Scheduler


interface ExecutorsInterface {
    fun io(): Scheduler
    fun ui(): Scheduler
}