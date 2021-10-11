package com.example.restaurantfinder.helper

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class Executors: ExecutorsInterface {
    override fun io() = Schedulers.io()
    override fun ui(): Scheduler = AndroidSchedulers.mainThread()
}