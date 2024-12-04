package com.example.kuit4_android_retrofit

import android.app.AlertDialog
import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kuit4_android_retrofit.databinding.DialogAddPinBinding
import com.example.kuit4_android_retrofit.databinding.FragmentMapBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    //bottom sheet 사용하려고 선언
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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
        val mapFragment = childFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.fcv_map, it).commit()
            }
        mapFragment.getMapAsync(this)

        initBottomSheet()

    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        //현재 위치 데이터 제공
        naverMap.locationSource = locationSource
        //사용자 현재 위치를 추적
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        //ui 설정 관련
        naverMap.uiSettings.isLocationButtonEnabled = true
        //카메라 초기 위치 설정
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5423265, 127.0759204))
        naverMap.moveCamera(cameraUpdate)
    }

    private fun initBottomSheet(){
        //bottom sheet 가져오기
        val bottomSheet = binding.llBottomSheetMap
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        //초기상태
        bottomSheetBehavior.isFitToContents = true //content 크기에 맞게 확장
        bottomSheetBehavior.isHideable = false //숨김상태 비활성화
        bottomSheetBehavior.peekHeight = 40 //collapsed 상태의 높이
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.btnBottomSheetMap.text = "Expand Bottom Sheet"
        //상태 변경 처리
        bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState){
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.btnBottomSheetMap.text = "Collapse Bottom Sheet"
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.btnBottomSheetMap.text = "Expand Bottom Sheet"
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        //버튼 클릭시 bottom sheet 열고 닫기
        binding.btnBottomSheetMap.setOnClickListener {
            when(bottomSheetBehavior.state){
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }

        //add pin 버튼 누르면 위도 경도 입력하는 dialog 출력
        binding.btnAddPin.setOnClickListener {
            showAddPinDialog()
        }
    }

    private fun showAddPinDialog(){
        val dialogBinding = DialogAddPinBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        //"추가" 클릭시의 동작
        dialogBinding.btnAddPin.setOnClickListener {
            val latitude =
                dialogBinding.etLatitude.text
                    .toString()
                    .trim()
                    .toDoubleOrNull()
            val longitude =
                dialogBinding.etLongitude.text
                    .toString()
                    .trim()
                    .toDoubleOrNull()

            if(latitude != null && longitude != null){
                //새 마커 만들기
                addPinToMap(latitude,longitude)
                dialog.dismiss()
            }else{
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        //"취소" 버튼 클릭시 동작
        dialogBinding.btnCancelPin.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //지도에 위도 경도 값 받아서 marker 추가하는 함수
    private fun addPinToMap(latitude: Double, longitude: Double){
        val newPin = Marker()
        newPin.position = LatLng(latitude, longitude)
        newPin.map = naverMap
        newPin.width = 30
        newPin.height = 40
        newPin.icon = MarkerIcons.RED //OverlayImage.fromResource(R.drawable. )처럼 지정도 가능

    }
}
