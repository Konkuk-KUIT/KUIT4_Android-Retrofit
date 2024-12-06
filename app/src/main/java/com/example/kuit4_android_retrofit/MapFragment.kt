package com.example.kuit4_android_retrofit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kuit4_android_retrofit.databinding.FragmentMapBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding:FragmentMapBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val markers = ArrayList<Marker>()

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE =1000 // 네이버에서 설정한 이름

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationSource= FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding =FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =childFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment? ?: MapFragment.newInstance().also {
            childFragmentManager.beginTransaction().add(R.id.fcv_map,it).commit()
        }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap =naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        naverMap.uiSettings.isLocationButtonEnabled =true

        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true


        naverMap.setOnMapClickListener{ point, coord ->
            Toast.makeText(requireContext(), "${coord.latitude}, ${coord.longitude}"  ,
                Toast.LENGTH_SHORT).show()
            val marker = Marker()
            marker.position = LatLng(coord.latitude, coord.longitude)
            marker.map = naverMap
            marker.setOnClickListener {
                val infoWindow = InfoWindow()

                val bottomSheetDialog = BottomSheetDialog(requireContext())
                val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
                bottomSheetDialog.setContentView(view)

//            // 이벤트 추가
                view.findViewById<Button>(R.id.btn_bsl_delete).setOnClickListener {
                    marker.map=null
                    bottomSheetDialog.dismiss()
                }


                var word :String
                view.findViewById<Button>(R.id.btn_bsl_add).setOnClickListener {
                    word = view.findViewById<EditText>(R.id.et_bsl_info).text.toString()
                    infoWindow.map = null
                    infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
                        override fun getText(infoWindow: InfoWindow): CharSequence {
                            return word
                        }
                    }
                    infoWindow.open(marker)
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()

                true
            }

            markers.add(marker)
        }


    }


}

