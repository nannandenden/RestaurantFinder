package com.example.restaurantfinder

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantfinder.helper.ExecutorsInterface
import com.example.restaurantfinder.helper.SingleLiveEvent
import com.example.restaurantfinder.repository.RestaurantDataSource
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.PlacesClient
import io.reactivex.disposables.CompositeDisposable

class RestaurantResultViewModel(
    private val repository: RestaurantDataSource,
    private val executors: ExecutorsInterface
) : ViewModel() {

    val onViewChangeLiveData = SingleLiveEvent<Boolean>()
    val restaurantsLiveData = MutableLiveData<List<Place>>()
    val viewRestaurantDetail = SingleLiveEvent<Place>()
    val noResultLiveData = SingleLiveEvent<Unit>()
    val isLoading = SingleLiveEvent<Boolean>()

    private var isMapView: Boolean = true
    private val restaurants = mutableListOf<Place>()
    private val compositeDisposable = CompositeDisposable()


    companion object {
        private val TAG: String = RestaurantResultViewModel::class.java.simpleName
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun onToggleView() {
        isMapView = isMapView.not()
        if (isMapView.not()) {
            restaurantsLiveData.value?.let {
                restaurantsLiveData.postValue(restaurantsLiveData.value)
            } ?: noResultLiveData.call()
        }
        onViewChangeLiveData.postValue(isMapView)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE])
    fun onPlaceClientReady(client: PlacesClient) {
        compositeDisposable.add(
            repository.findRestaurantsCurrentLocation(client)
                .subscribeOn(executors.io())
                .observeOn(executors.ui())
                .doOnSubscribe { isLoading.postValue(true) }
                .doAfterTerminate { isLoading.postValue(false) }
                .doOnSuccess { places ->
                    if (places.isEmpty()) {
                        noResultLiveData.call()
                        return@doOnSuccess
                    }
                    restaurants.clear()
                    restaurants.addAll(places)
                    for (restaurant in restaurants) {
                        Log.d(TAG, "${restaurant.name}\t${restaurant.rating}\b${restaurant.userRatingsTotal}\b")
                    }
                    restaurantsLiveData.postValue(restaurants)
                }
                .doOnError {
                    Log.d(TAG, "error getting restaurant near by")
                    noResultLiveData.call()
                }
                .subscribe()
        )
    }

    fun onRestaurantClicked(restaurant: Place) {
        viewRestaurantDetail.postValue(restaurant)
    }

    fun searchRestaurantByQuery(client: PlacesClient, query: String, latLngBounds: LatLngBounds) {
        compositeDisposable.add(
            repository.findRestaurantsByQuery(client, query, latLngBounds)
                .subscribeOn(executors.io())
                .observeOn(executors.ui())
                .doOnSubscribe { isLoading.postValue(true) }
                .doAfterTerminate { isLoading.postValue(false) }
                .doOnSuccess { places ->
                    if (places.isEmpty()) {
                        noResultLiveData.call()
                        return@doOnSuccess
                    }
                    restaurants.clear()
                    restaurants.addAll(places)
                    restaurantsLiveData.postValue(restaurants)
                }
                .doOnError {
                    noResultLiveData.call()
                    Log.d(TAG, "error getting search result: ${it.localizedMessage}")
                }
                .subscribe()
        )
    }
}