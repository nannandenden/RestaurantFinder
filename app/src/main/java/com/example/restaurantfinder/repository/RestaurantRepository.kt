package com.example.restaurantfinder.repository

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import io.ashdavies.rx.rxtasks.toSingle
import io.reactivex.Single

class RestaurantRepository: RestaurantDataSource {

    companion object {

        private val placeFieldList = listOf(
            Place.Field.TYPES,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.PHOTO_METADATAS,
            Place.Field.RATING,
            Place.Field.USER_RATINGS_TOTAL
        )

        private val TAG: String =  RestaurantRepository::class.java.simpleName
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE])
    override fun findRestaurantsCurrentLocation(client: PlacesClient): Single<List<Place>> {
        val currentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFieldList)
        val currentPlaceTask = client.findCurrentPlace(currentPlaceRequest)
        return currentPlaceTask.toSingle()
            .map { response ->
                val places = response.placeLikelihoods.filter { likelihood ->
                    val place = likelihood.place
                    place.types?.contains(Place.Type.RESTAURANT) == true &&
                            place.name != null &&
                            place.latLng != null
                }.map { likelihood ->
                    val restaurant = likelihood.place
                    Log.d(TAG, "${restaurant.name}\t${restaurant.types}\t${restaurant.priceLevel}\t${restaurant.rating}\t${restaurant.userRatingsTotal}\t")
                    restaurant
                }
                places
            }
    }

    override fun findRestaurantsByQuery(
        client: PlacesClient,
        query: String,
        latLngBounds: LatLngBounds
    ): Single<List<Place>> {

        val token = AutocompleteSessionToken.newInstance()
        val bounds = RectangularBounds.newInstance(
            latLngBounds.southwest,
            latLngBounds.northeast
        )
        val request =
            FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setOrigin(latLngBounds.center)
                .setCountry("US")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(query)
                .build()
        return client.findAutocompletePredictions(request).toSingle()
            .map { predictionResponse ->
                val restaurantIds =
                    predictionResponse.autocompletePredictions.filter { prediction ->
                        prediction.placeTypes.contains(Place.Type.RESTAURANT)
                    }.map { prediction ->
                        prediction.placeId
                    }
                restaurantIds
            }
            .toObservable()
            .flatMapIterable { placeIds -> placeIds }
            .flatMapSingle { placeId ->
                val fetchPlaceRequest = FetchPlaceRequest.newInstance(placeId, placeFieldList)
                client.fetchPlace(fetchPlaceRequest).toSingle()
                    .map { fetchPlaceResponse ->
                        fetchPlaceResponse.place
                    }
            }
            .toList()

    }
}