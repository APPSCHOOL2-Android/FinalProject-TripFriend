package com.test.tripfriend.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.tripfriend.BuildConfig
import com.test.tripfriend.R
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.databinding.FragmentHomeMapBinding
import com.test.tripfriend.dataclassmodel.TripPost
import com.test.tripfriend.viewmodel.HomeViewModel

class HomeMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    lateinit var fragmentHomeMapBinding: FragmentHomeMapBinding
    lateinit var mainActivity: MainActivity
    private lateinit var mMap: GoogleMap

    lateinit var homeViewModel: HomeViewModel

    private var mapReady = false // 맵 준비 여부를 나타내는 플래그

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeMapBinding = FragmentHomeMapBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

//        val MAPS_API_KEY = BuildConfig.MAPS_API_KEY
//
//        if (MAPS_API_KEY.isEmpty()) {
//            Toast.makeText(mainActivity, "Add your own API key in properties as MAPS_API_KEY=YOUR_API_KEY", Toast.LENGTH_LONG).show()
//        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapViewHomeMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the SDK
//        Places.initialize(context, MAPS_API_KEY)

        // Create a new PlacesClient instance
//        val placesClient = Places.createClient(context)

//        val mapFragment = fm.findFragmentById(R.id.mapViewHomeMap) as MapFragment?
//            ?: MapFragment.newInstance().also {
////                fm.beginTransaction().add(R.id.mapViewHomeMap, it).commit()
//            }
//        mapFragment.getMapAsync(this)


        return fragmentHomeMapBinding.root

    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("qwer", "onMapReady")

        googleMap.uiSettings.isMapToolbarEnabled = false

        mMap = googleMap
        mapReady = true

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.getTripPostData()

        homeViewModel.tripPostList.observe(viewLifecycleOwner) {

            for (location in it) {
                var latLng = LatLng(location.tripPostLatitude, location.tripPostLongitude)

                var marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(location.tripPostTitle)
                )
                marker?.tag = location

            }
        }

        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(this)
    }

    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker: Marker): Boolean {

        val markerInfo: TripPost = marker.tag as TripPost

        MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
            setTitle(markerInfo.tripPostTitle)
            setMessage("동행글로 이동하시겠습니까?")
            setNegativeButton("닫기", null)
            setPositiveButton("이동") { dialogInterface: DialogInterface, i: Int ->
                val newBundle = Bundle()
                newBundle.putString(
                    "tripPostWriterEmail",
                    markerInfo.tripPostWriterEmail
                ) // 작성자 이메일
                newBundle.putString("tripPostDocumentId", markerInfo.tripPostDocumentId)   // 문서아이디

                mainActivity.replaceFragment(MainActivity.HOME_MAIN_FRAGMENT, true, false, null)
                mainActivity.replaceFragment(MainActivity.READ_POST_FRAGMENT, true, true, newBundle)
            }
            show()
        }

        return false
    }


    override fun onResume() {
        super.onResume()
        Log.d("qwer", "onResume")

        if (mapReady) {
            onMapReady(mMap)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("qwer", "onStart")
    }

}