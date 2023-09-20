package com.test.tripfriend.ui.accompany

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.tripfriend.R
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.databinding.FragmentAccompanyRegister1Binding

class AccompanyRegisterFragment1 : Fragment(), OnMapReadyCallback {
    lateinit var fragmentAccompanyRegister1Binding: FragmentAccompanyRegister1Binding
    lateinit var mainActivity: MainActivity
    var country: String? = ""
    var locality: String? = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    private var mapFragment: SupportMapFragment? = null
    private lateinit var coordinates: LatLng
    private var map: GoogleMap? = null
    private var marker: Marker? = null
    private var startAutocompleteIntentListener = View.OnClickListener { view: View ->
        view.setOnClickListener(null)
        startAutocompleteIntent()
    }

    // [START maps_solutions_android_autocomplete_define]
    private val startAutocomplete = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        fragmentAccompanyRegister1Binding.iconButton.setOnClickListener(
            startAutocompleteIntentListener
        )

        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val place = Autocomplete.getPlaceFromIntent(intent)

                // Place의 주소 구성요소
                Log.d("map", "Place: " + place.addressComponents)
                fillInAddress(place)
            }
        } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
            // 작업 취소
            Log.i("map", "User canceled autocomplete")
        }
    }
    // [END maps_solutions_android_autocomplete_define]

    // [START maps_solutions_android_autocomplete_intent]
    private fun startAutocompleteIntent() {
        // 사용자가 선택한 후 반환할 장소 데이터 유형 지정
        val fields = listOf(
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG
        )

        // 필드, 국가 및 유형 필터가 적용된 자동 완성 인텐트 구축
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY,
            fields
        )
//            .setCountries(listOf("US"))
//            .setPlaceTypes(listOf(Place.Type., Place.Type.POINT_OF_INTEREST))
//            .setTypesFilter(listOf("landmark", "restaurant", "store"))
//            .setTypesFilter(listOf(PlaceTypes.LANDMARK))
            .setTypesFilter(listOf(PlaceTypes.ADDRESS))
            .setTypesFilter(listOf(PlaceTypes.CITIES))        // 도시
//            .setTypesFilter(listOf(PlaceTypes.COUNTRY))     // 국가
//            .setTypesFilter(listOf(PlaceTypes.CAFE))
            //TODO: https://developers.google.com/maps/documentation/places/android-sdk/autocomplete
//            .setTypesFilter(listOf(TypeFilter.ADDRESS.toString().lowercase()))
            .build(mainActivity)
        startAutocomplete.launch(intent)
    }
    // [END maps_solutions_android_autocomplete_intent]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAccompanyRegister1Binding =
            FragmentAccompanyRegister1Binding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // search icon
        fragmentAccompanyRegister1Binding.iconButton.setOnClickListener(
            startAutocompleteIntentListener
        )

        // 바텀 네비 GONE
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE

        fragmentAccompanyRegister1Binding.run {

            materialToolbarRegister1.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT1)
                }
            }

            buttonAccompanyRegister1ToNextView.setOnClickListener {

//                if(country == "") {
//                    MaterialAlertDialogBuilder(mainActivity,R.style.DialogTheme).run {
//                        setTitle("여행지 선택")
//                        setMessage("여행지를 선택해주세요.")
//                        setNegativeButton("닫기") { dialogInterface: DialogInterface, i: Int ->
//                        }
//                        show()
//                    }
//                } else {
//                    val bundle = Bundle()
//                    bundle.putString("country", country)
//
//                    mainActivity.replaceFragment(
//                        MainActivity.ACCOMPANY_REGISTER_FRAGMENT2,
//                        true,
//                        true,
//                        bundle
//                    )
//                }

                val bundle = Bundle()
                bundle.putString("country", country + " " + locality)
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude", longitude)

                if (country == "") {
                    MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                        setTitle("위치 입력")
                        setMessage("위치를 입력해주세요.")
                        setNegativeButton("닫기", null)
                        show()
                        return@setOnClickListener
                    }
                }

                mainActivity.replaceFragment(
                    MainActivity.ACCOMPANY_REGISTER_FRAGMENT2,
                    true,
                    true,
                    bundle
                )

            }
        }

        return fragmentAccompanyRegister1Binding.root
    }

    private fun fillInAddress(place: Place) {

        val components = place.addressComponents


        if (components != null) {
            for (component in components.asList()) {
                when (component.types[0]) {
                    "country" -> {
                        country = component.name
                    }

                    "locality" -> {
                        locality = component.name
                    }
                }
            }
        }
        fragmentAccompanyRegister1Binding.toolbarText.setText(country + " " + locality)

        // 지도 추가
        showMap(place)
    }

    // [START maps_solutions_android_autocomplete_map_add]
    private fun showMap(place: Place) {
        coordinates = place.latLng as LatLng
        latitude = coordinates.latitude
        longitude = coordinates.longitude

        // 태그 설정하여 검색
        mapFragment =
            mainActivity.supportFragmentManager.findFragmentByTag("MAP") as SupportMapFragment?

        // 아직 존재하지 않는 경우에만 프래그먼트 생성
        if (mapFragment == null) {
            val mapOptions = GoogleMapOptions()
            mapOptions.mapToolbarEnabled(false)

            mapFragment = SupportMapFragment.newInstance(mapOptions)

            // FragmentTransaction 사용하여 추가
            mainActivity.supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.mapViewAccompanyRegister1,
                    mapFragment!!,
                    "MAP"
                )
                .commit()
            mapFragment?.getMapAsync(this)
        } else {
            updateMap(coordinates)
        }
    }
    // [END maps_solutions_android_autocomplete_map_add]

    private fun updateMap(latLng: LatLng) {
//        marker?.position = latLng
//        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        marker = map?.addMarker(MarkerOptions().position(coordinates))
    }

    // [START maps_solutions_android_autocomplete_map_ready]
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        try {
            // 정의된 JSON 객체를 사용하여 기본 지도의 스타일을 맞춤설정
            val success = map!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(mainActivity, R.raw.style_json)
            )
            if (!success) {
                Log.e("map", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("map", "Can't find style. Error: ", e)
        }
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
        marker = map?.addMarker(MarkerOptions().position(coordinates))
    }
    // [END maps_solutions_android_autocomplete_map_ready]

    override fun onStop() {
        super.onStop()
        Log.d("qwer", "onStop mapFragment : ${mapFragment}")
        Log.d("qwer", "onStop map : ${map}")
        Log.d("qwer", "onStop marker : ${marker}")
    }
}