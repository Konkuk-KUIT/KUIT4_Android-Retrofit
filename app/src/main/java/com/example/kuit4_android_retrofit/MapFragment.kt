package com.example.kuit4_android_retrofit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kuit4_android_retrofit.databinding.DialogAddCommentBinding
import com.example.kuit4_android_retrofit.databinding.FragmentMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding : FragmentMapBinding

    private lateinit var navermap : NaverMap
    private lateinit var locationSource : FusedLocationSource

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentMapBinding.inflate(layoutInflater)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment ?: MapFragment.newInstance().also {
            childFragmentManager.beginTransaction().add(R.id.fcv_map, it).commit()
        }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(navermap: NaverMap) {
        this.navermap = navermap
        navermap.locationSource = locationSource
        navermap.locationTrackingMode = LocationTrackingMode.Follow
        navermap.uiSettings.isLocationButtonEnabled = true

/*        navermap.addOnLocationChangeListener { location ->
            val currentLatLng = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdate.scrollTo(currentLatLng)
            navermap.moveCamera(cameraUpdate)
        }*/

        val marker1 = Marker()
        marker1.position = LatLng(37.53517667419473, 127.08265074885249)
        marker1.captionText = "맛집 1"
        marker1.map = navermap

        val marker2 = Marker()
        marker2.position = LatLng(37.53267612285849, 127.07977426099593)
        marker2.captionText = "맛집 2"
        marker2.map = navermap

        marker1.setOnClickListener {
            val message = Toast.makeText(requireContext(), marker1.captionText, Toast.LENGTH_SHORT)
            message.show()
            true
        }
        val dialogbinding = DialogAddCommentBinding.inflate(LayoutInflater.from(requireContext()))

        marker2.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext()).setView(dialogbinding.root).create()

            dialog.show()
            dialogbinding.btnAddMemo.setOnClickListener {
                val memo = dialogbinding.etMemo.text.toString().trim()
                if (memo.isNotEmpty()) {
                    marker2.subCaptionText = memo
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

    }


}
