package com.example.restaurantfinder.restaurantdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.restaurantfinder.R
import com.example.restaurantfinder.databinding.FragmentRestaurantDetailBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient

class RestaurantDetailFragment: Fragment() {

    private lateinit var binding: FragmentRestaurantDetailBinding
    private lateinit var client: PlacesClient

    companion object {
        fun newInstance(restaurant: Place): RestaurantDetailFragment {
            val fragment = RestaurantDetailFragment()
            fragment.arguments = bundleOf(BUNDLE_RESTAURANT_DETAIL to restaurant)
            return fragment
        }
        private const val BUNDLE_RESTAURANT_DETAIL = "bundle_restaurant_detail"
        var TAG_RESTAURANT_DETAIL: String = RestaurantDetailFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false)
        binding = FragmentRestaurantDetailBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        client = Places.createClient(requireContext())

        setupView(
            arguments?.getParcelable(BUNDLE_RESTAURANT_DETAIL)
        )
    }

    private fun setupView(restaurant: Place?) {
        binding.apply {
            // load restaurant detail image
            val photoMetaData = restaurant?.photoMetadatas?.firstOrNull()
            photoMetaData?.let { photo ->
                val photoRequest = FetchPhotoRequest.builder(photo).build()
                client.fetchPhoto(photoRequest)
                    .addOnSuccessListener {
                        ivFragmentRestaurantDetail.setImageBitmap(it.bitmap)
                    }
                    .addOnFailureListener {
                        Log.d(TAG_RESTAURANT_DETAIL, "Error loading the image")
                    }
            }

            tvFragmentRestaurantName.text = restaurant?.name ?: ""
            // set the rating
            val rating = restaurant?.rating?.toFloat() ?: 0.0f
            val ratingCount = restaurant?.userRatingsTotal ?: 0
            ratingRestaurantDetail.setRating(rating, ratingCount)

        }
    }
}