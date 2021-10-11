package com.example.restaurantfinder.restaurantlist

import com.google.android.libraries.places.api.model.Place

interface OnRestaurantClickListener {
    fun onRestaurantClicked(restaurant: Place)
}