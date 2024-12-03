package com.example.kuit4_android_retrofit

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kuit4_android_retrofit.databinding.FragmentMapBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons


class MapFragment : Fragment(), OnMapReadyCallback { // OnMapReadyCallback : 콜백 인터페이스
    private lateinit var binding: FragmentMapBinding

    private lateinit var naverMap : NaverMap
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment? ?: MapFragment.newInstance().also {
            childFragmentManager.beginTransaction().add(R.id.fcv_map, it).commit()
        }
        mapFragment.getMapAsync(this)

        binding.fabFavorite.setOnClickListener {
            showDialog()
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow // 사용자 위치 추적 모드
        naverMap.uiSettings.isLocationButtonEnabled = true  // 사용자 위치 버튼 활성화

        // 카메라 이동
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(35.114495, 129.03933))
            .animate(CameraAnimation.Fly, 4000)
            .finishCallback {
                Toast.makeText(context, "카메라 이동 완료", Toast.LENGTH_SHORT).show()
            }
            .cancelCallback {
                Toast.makeText(context, "카메라 이동 취소", Toast.LENGTH_SHORT).show() // 이동하다가 중간에 내위치 버튼 누르면 취소돼서 이 메시지 뜸
            }
        naverMap.moveCamera(cameraUpdate)

        naverMap.setOnMapClickListener { point, coord ->
            Toast.makeText(
                context, "위도 : ${coord.latitude} \n경도 : ${coord.longitude}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // 마커 추가
        val marker = Marker()
        marker.position = LatLng(37.5409582, 127.0684686)
        marker.captionText = "로니로티"
        marker.map = naverMap

        val marker2 = Marker()
        marker2.position = LatLng(37.540519810693404, 127.06822426612183)
        marker2.captionText = "맘스터치"
        marker2.icon = MarkerIcons.BLACK
        marker2.iconTintColor = Color.RED
        marker2.map = naverMap

        val marker3 = Marker()
        marker3.position = LatLng(35.11518808347285, 129.03932913063082)
        marker3.captionText = "공차"
        marker3.icon = MarkerIcons.BLACK
        marker3.iconTintColor = Color.BLUE
        marker3.map = naverMap

        // 마커 클릭 이벤트

        marker.setOnClickListener {
            Toast.makeText(context, marker.captionText, Toast.LENGTH_SHORT).show()
            true
        }

        marker3.setOnClickListener {
            Toast.makeText(context, marker3.captionText, Toast.LENGTH_SHORT).show()
            true
        }

        //marker.map = null // 마커 삭제

        // 마커 클릭 시 정보창 표시(bottom sheet)
        val bottomSheet = BottomSheetDialog(requireContext())
        bottomSheet.setContentView(R.layout.bottom_sheet)
        marker2.setOnClickListener {
            bottomSheet.show()
            true
        }


    }

    // 특정 위치 누르면 다이얼로그 띄우기
    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("맘스터치")
            .setMessage("건대 맘스터치 위치를 확인하겠습니까?")
            .setPositiveButton("확인") { dialog, which ->
                Toast.makeText(context, "이동완료", Toast.LENGTH_SHORT).show()
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.540519810693404, 127.06822426612183))
                    .animate(CameraAnimation.Fly, 4000)
                naverMap.moveCamera(cameraUpdate)
            }
            .setNegativeButton("취소") { dialog, which ->
                Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

}
