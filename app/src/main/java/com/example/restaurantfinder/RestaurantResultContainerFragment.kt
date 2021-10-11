package com.example.restaurantfinder

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import androidx.fragment.app.setFragmentResult
import com.example.restaurantfinder.databinding.FragmentRestaurantResultContainerBinding
import com.example.restaurantfinder.restaurantlist.RestaurantListFragment
import com.example.restaurantfinder.restaurantlist.RestaurantListFragment.Companion.TAG_RESTAURANT_LIST
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.sharedViewModel


class RestaurantResultContainerFragment: Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentRestaurantResultContainerBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var listFragment: RestaurantListFragment
    private lateinit var menuSearchView: SearchView
    private lateinit var client: PlacesClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private val fm by lazy { childFragmentManager }
    private val viewModel by sharedViewModel<RestaurantResultViewModel>()

    private val markers = mutableListOf<Marker>()

    companion object {
        fun newInstance(): RestaurantResultContainerFragment {
            return RestaurantResultContainerFragment()
        }

        private const val GOOGLE_MAP_ZOOM = 12f

        val TAG_RESTAURANT_RESULT: String = RestaurantResultContainerFragment::class.java.simpleName
        val BUNDLE_RESTAURANT_DETAIL = "bundle_restaurant_detail"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant_result_container, container, false)
        binding = FragmentRestaurantResultContainerBinding.bind(view)

        loadFragments()
        return view
    }

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE])
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkForPermission()
        setupView()
        setupListener()
        setupObserver()
    }

    private fun loadFragments() {
        if (!::mapFragment.isInitialized) {
            mapFragment = SupportMapFragment.newInstance()
        }
        fm.commitNow {
            setReorderingAllowed(true)
            add(R.id.fl_restaurant_result_map, mapFragment)
        }
        if (this::listFragment.isInitialized.not()) {
            listFragment = RestaurantListFragment.newInstance()
        }
        fm.commitNow {
            setReorderingAllowed(true)
            add(R.id.fl_restaurant_result_list, listFragment, TAG_RESTAURANT_LIST)
        }
    }

    private fun checkForPermission() {
        when {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) -> {
                mapFragment.getMapAsync(this)
            }
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                val builder: AlertDialog.Builder? = activity?.let { activity ->
                    AlertDialog.Builder(activity)
                }
                builder?.apply {
                    setTitle(R.string.permission_title)
                    setMessage(R.string.permission_message)
                    setPositiveButton(R.string.ok) { _, _ ->
                        activity?.finish()
                    }
                    create()
                    show()
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(ACCESS_FINE_LOCATION)
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupObserver() {
        viewModel.onViewChangeLiveData.observe(viewLifecycleOwner){ isMapView ->
            if (!::listFragment.isInitialized) listFragment = RestaurantListFragment.newInstance()
            val isListFragmentAdded = fm.findFragmentByTag(TAG_RESTAURANT_LIST) != null
            if (!isListFragmentAdded) {
                fm.commitNow {
                    setReorderingAllowed(true)
                    add(R.id.fl_restaurant_result_list, listFragment, TAG_RESTAURANT_LIST)
                }
            }
            fm.beginTransaction().apply {
                if (isMapView) {
                    show(mapFragment)
                    hide(listFragment)
                } else {
                    hide(mapFragment)
                    show(listFragment)
                }
                commit()
            }
        }

        viewModel.restaurantsLiveData.observe(viewLifecycleOwner) { restaurants ->

            // remove all the markers
            for (marker in markers) {
                marker.remove()
            }
            for (restaurant in restaurants) {
                val marker = MarkerOptions()
                    .title(restaurant.name)
                    .position(restaurant.latLng!!)
                if (this::googleMap.isInitialized) {
                    this.googleMap.addMarker(marker)?.let { mk ->
                        markers.add(mk)
                    }
                }
            }
        }

        viewModel.noResultLiveData.observe(viewLifecycleOwner) {
            Snackbar.make(binding.clFragmentRestaurantResult, R.string.no_restaurant_result, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.viewRestaurantDetail.observe(viewLifecycleOwner) { restaurant ->
            setFragmentResult(TAG_RESTAURANT_RESULT, bundleOf(BUNDLE_RESTAURANT_DETAIL to restaurant))
        }
    }

    private fun setupView() {
        binding.toolbarRestaurantResult.inflateMenu(R.menu.restaurant_result_menu)

        val menu = binding.toolbarRestaurantResult.menu
        menuSearchView = menu.findItem(R.id.menu_search).actionView as SearchView
        menuSearchView.queryHint = getString(R.string.menu_search)
    }

    private fun setupListener() {
        binding.fabRestaurantResult?.setOnClickListener {
            viewModel.onToggleView()
        }

        menuSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @SuppressLint("MissingPermission")
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchQuery ->
                    if (::googleMap.isInitialized) {
                        val bound = googleMap.projection.visibleRegion.latLngBounds
                        viewModel.searchRestaurantByQuery(client, searchQuery, bound)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    @RequiresPermission(allOf = [ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE])
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        googleMap.isMyLocationEnabled = true
        val locationManager =
            activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        val criteria = Criteria()
        val provider = locationManager?.getBestProvider(criteria, true)
        val userCurrentLocation = provider?.let { bestProvider -> locationManager.getLastKnownLocation(bestProvider) }

        userCurrentLocation?.let { location ->
            val latitude: Double = location.latitude
            val longitude: Double = location.longitude
            val coordinate = LatLng(latitude, longitude)
            val currentLocation = CameraUpdateFactory.newLatLngZoom(coordinate, GOOGLE_MAP_ZOOM)
            this.googleMap.animateCamera(currentLocation)
        }

        if (Places.isInitialized().not()) {
            val placeApiKey = BuildConfig.PLACE_API_KEY
            Places.initialize(requireActivity().applicationContext, placeApiKey)
        }

        client = Places.createClient(requireContext())
        // start to find the restaurant near by upon app launch
        viewModel.onPlaceClientReady(client)
    }
}