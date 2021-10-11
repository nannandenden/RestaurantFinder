package com.example.restaurantfinder.restaurantlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurantfinder.R
import com.example.restaurantfinder.RestaurantResultViewModel
import com.example.restaurantfinder.databinding.FragmentRestaurantListBinding
import com.example.restaurantfinder.helper.BasicSpaceItemDecoration
import com.google.android.libraries.places.api.model.Place
import org.koin.android.viewmodel.ext.android.sharedViewModel

class RestaurantListFragment: Fragment(), OnRestaurantClickListener {

    private lateinit var binding: FragmentRestaurantListBinding
    private lateinit var adapter: RestaurantListAdapter
    private val viewModel by sharedViewModel<RestaurantResultViewModel>()

    companion object {

        fun newInstance(): RestaurantListFragment {
            val fragment = RestaurantListFragment()
            return fragment
        }

        val TAG_RESTAURANT_LIST: String = RestaurantListFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant_list, container, false)
        binding = FragmentRestaurantListBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
    }

    override fun onRestaurantClicked(restaurant: Place) {
        viewModel.onRestaurantClicked(restaurant)
        Toast.makeText(context, "restaurant + ${restaurant.name} selected", Toast.LENGTH_LONG).show()
    }

    private fun setupObserver() {
        viewModel.restaurantsLiveData.observe(viewLifecycleOwner) { places ->
            adapter.setData(places)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loaderRestaurantList.visibility = if (isLoading) VISIBLE else GONE
        }
    }

    private fun setupView() {
        adapter = RestaurantListAdapter(this)
        binding.apply {
            rvRestaurantList.layoutManager = LinearLayoutManager(requireContext())
            rvRestaurantList.adapter = adapter
            rvRestaurantList.addItemDecoration(BasicSpaceItemDecoration())
            rvRestaurantList.setHasFixedSize(true)
        }
    }
}