package com.example.restaurantfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.commit
import com.example.restaurantfinder.RestaurantResultContainerFragment.Companion.TAG_RESTAURANT_RESULT
import com.example.restaurantfinder.databinding.ActivityMainBinding
import com.example.restaurantfinder.restaurantdetail.RestaurantDetailFragment
import com.example.restaurantfinder.restaurantdetail.RestaurantDetailFragment.Companion.TAG_RESTAURANT_DETAIL
import com.google.android.libraries.places.api.model.Place

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var resultContainerFragment: RestaurantResultContainerFragment

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupListener()

    }

    private fun setupListener() {
        // use fragment result API to open the restaurant detail view
        supportFragmentManager.setFragmentResultListener(TAG_RESTAURANT_RESULT, this) {  _, bundle ->
            val restaurant: Place? = bundle.getParcelable(RestaurantResultContainerFragment.BUNDLE_RESTAURANT_DETAIL)
            restaurant?.let {
                val fragment = RestaurantDetailFragment.newInstance(it)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    addToBackStack(TAG_RESTAURANT_DETAIL)
                    add(R.id.fl_restaurant_finder, fragment)
                }
            } ?: Log.d(TAG, "restaurant detail null")
        }
    }

    private fun setupView() {
        resultContainerFragment = RestaurantResultContainerFragment.newInstance()

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fl_restaurant_finder, resultContainerFragment, TAG_RESTAURANT_DETAIL)
        }
    }

}