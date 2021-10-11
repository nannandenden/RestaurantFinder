package com.example.restaurantfinder.restaurantlist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantfinder.R
import com.example.restaurantfinder.databinding.ItemRestaurantBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest

class RestaurantListAdapter(private val onClick: OnRestaurantClickListener): RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder>() {

    private lateinit var binding: ItemRestaurantBinding
    private val restaurantList = mutableListOf<Place>()

    companion object {
        private val TAG: String = RestaurantListAdapter::class.java.simpleName
    }

    fun setData(restaurants: List<Place>) {
        restaurantList.clear()
        restaurantList.addAll(restaurants)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_restaurant, parent, false)
        binding = ItemRestaurantBinding.bind(view)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.bind(restaurant)
    }

    override fun getItemCount(): Int = restaurantList.size

    inner class RestaurantViewHolder(private val binding: ItemRestaurantBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(restaurant: Place) {
            binding.root.setOnClickListener {
                onClick.onRestaurantClicked(restaurant)
            }

            binding.tvItemRestaurantName.text = restaurant.name

            val metadata = restaurant.photoMetadatas?.firstOrNull()
            binding.ivItemRestaurant.setImageBitmap(null)
            metadata?.let { photo ->
                val photoRequest = FetchPhotoRequest.builder(photo).build()
                val client = Places.createClient(binding.root.context)
                client.fetchPhoto(photoRequest)
                    .addOnSuccessListener {
                        binding.ivItemRestaurant.setImageBitmap(it.bitmap)
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "fail to load the image")
                    }

            }
            val rating = restaurant.rating?.toFloat() ?: 0f
            val ratingCount = restaurant.userRatingsTotal ?: 0
            binding.ratingItemRestaurant.setRating(rating, ratingCount)
        }
    }
}