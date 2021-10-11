package com.example.restaurantfinder.repository

import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import io.reactivex.Single

interface RestaurantDataSource {
    /**
     * find restaurants based on the near by location
     */
    fun findRestaurantsCurrentLocation(client: PlacesClient): Single<List<Place>>

    /**
     * find the restaurants based on query
     */
    fun findRestaurantsByQuery(client: PlacesClient, query: String, latLngBounds: LatLngBounds): Single<List<Place>>
}