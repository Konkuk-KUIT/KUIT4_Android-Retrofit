package com.example.kuit4_android_retrofit

import RVPopularMenuAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.data.PopularData
import com.example.kuit4_android_retrofit.databinding.DialogAddCategoryBinding
import com.example.kuit4_android_retrofit.databinding.DialogAddPopularBinding
import com.example.kuit4_android_retrofit.databinding.FragmentHomeBinding
import com.example.kuit4_android_retrofit.databinding.ItemCategoryBinding
import com.example.kuit4_android_retrofit.retrofit.RetrofitObject
import com.example.kuit4_android_retrofit.retrofit.service.CategoryService
import com.example.kuit4_android_retrofit.retrofit.service.HomePopularService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popularAdapter: RVPopularMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        fetchCategoryInfo()
        fetchPopularInfo()

        binding.ivAddCategory.setOnClickListener {
            addCategoryDialog()
        }

        binding.ivAddMenu.setOnClickListener {
            addPopularDialog()
        }

        return binding.root
    }

    private fun addPopularDialog() {
        val popularDialogBinding =
            DialogAddPopularBinding.inflate(LayoutInflater.from(requireContext()))

        val popularDialog =
            AlertDialog
                .Builder(requireContext())
                .setView(popularDialogBinding.root)
                .create() // popualr dialog 객체 생성

        // "추가" 버튼 클릭 시 동작
        popularDialogBinding.btnAddPopular.setOnClickListener {
            val popularName =
                popularDialogBinding.etPopularName.text
                    .toString()
                    .trim()
            val popularTime =
                popularDialogBinding.etPopularTime.text
                    .toString()
                    .trim()
            val popularRating =
                popularDialogBinding.etPopularRating.text
                    .toString()
                    .trim()
            val popularImageUrl =
                popularDialogBinding.etPopularImageUrl.text
                    .toString()
                    .trim()

            if (popularName.isNotEmpty() && popularTime.isNotEmpty() && popularRating.isNotEmpty() && popularImageUrl.isNotEmpty()) {
                val newPopular = PopularData(
                    popularName,
                    popularTime.toInt(),
                    popularRating.toDouble(),
                    popularImageUrl,
                    "0"
                )
                addPopular(newPopular)
                popularDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
        popularDialogBinding.btnCancelPopular.setOnClickListener {
            popularDialog.dismiss()
        }
        popularDialog.show()

    }

    private fun addPopular(popularData: PopularData) {
        var popularService = RetrofitObject.retrofit.create(HomePopularService::class.java)
        var call = popularService.postPopular(popularData)

        call.enqueue(
            object : retrofit2.Callback<PopularData> {
                override fun onResponse(call: Call<PopularData>, response: Response<PopularData>) {
                    if (response.isSuccessful) {
                        val addedPopular = response.body()
                        if (addedPopular != null) {
                            Log.d("성공", "인기메뉴 추가 성공 : $addedPopular")
                            fetchPopularInfo()
                        } else {
                            Log.d("실패", "인기메뉴 추가 실패: 응답 데이터 없음")
                        }
                    } else {
                        Log.d("실패", "인기메뉴 추가 실패: 상태코드 ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PopularData>, t: Throwable) {
                    Log.d("실패", "네트워크 요청 실패: ${t.message}")
                }

            }
        )

    }

    private fun showPopularOptionsDialog(popular: PopularData) {
        val options = arrayOf("수정", "삭제")

        AlertDialog
            .Builder(requireContext())
            .setTitle("인기메뉴 옵션")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditPopularDialog(popular) // 수정
                    1 -> deletePopular(popular.id) // 삭제
                }
            }.show()
    }

    private fun deletePopular(popularId: String) {
        val service = RetrofitObject.retrofit.create(HomePopularService::class.java)
        val call = service.deletePopular(popularId)

        call.enqueue(
            object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("성공", "인기메뉴 삭제 성공 : $popularId")
                        fetchPopularInfo()
                    } else {
                        Log.d("실패", "인기메뉴 삭제 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("실패", "네트워크 요청 실패: ${t.message}")
                }

            }
        )

    }

    private fun showEditPopularDialog(popular: PopularData) {
        val popularDialogBinding =
            DialogAddPopularBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(popularDialogBinding.root)
                .create()

        popularDialogBinding.etPopularName.setText(popular.popularName)
        popularDialogBinding.etPopularTime.setText(popular.popularTime.toString())
        popularDialogBinding.etPopularRating.setText(popular.popularRating.toString())
        popularDialogBinding.etPopularImageUrl.setText(popular.popularImg)

        popularDialogBinding.btnAddPopular.text = "수정"
        popularDialogBinding.btnAddPopular.setOnClickListener {
            val updatedName =
                popularDialogBinding.etPopularName.text
                    .toString()
                    .trim()
            val updatedTime =
                popularDialogBinding.etPopularTime.text
                    .toString()
                    .trim()
            val updatedRating =
                popularDialogBinding.etPopularRating.text
                    .toString()
                    .trim()
            val updatedImageUrl =
                popularDialogBinding.etPopularImageUrl.text
                    .toString()
                    .trim()

            if (updatedName.isNotEmpty() && updatedTime.isNotEmpty() && updatedRating.isNotEmpty() && updatedImageUrl.isNotEmpty()) {
                val updatedPopular = PopularData(
                    updatedName,
                    updatedTime.toInt(),
                    updatedRating.toDouble(),
                    updatedImageUrl,
                    popular.id
                )
                updatePopular(updatedPopular)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
        popularDialogBinding.btnCancelPopular.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updatePopular(updatedPopular: PopularData) {
        val service = RetrofitObject.retrofit.create(HomePopularService::class.java)
        val call = service.putPopular(updatedPopular.id, updatedPopular)

        call.enqueue(
            object : retrofit2.Callback<PopularData> {
                override fun onResponse(
                    call: Call<PopularData>,
                    response: Response<PopularData>
                ) {
                    if (response.isSuccessful) {
                        Log.d("성공", "인기메뉴 수정 성공 : ${response.body()}")
                        fetchPopularInfo()
                    } else {
                        Log.d("실패", "인기메뉴 수정 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PopularData>, t: Throwable) {
                    Log.d("실패", "네트워크 요청 실패: ${t.message}")
                }
            }
        )

    }

    private fun showCategoryOptionsDialog(category: CategoryData) {
        val options = arrayOf("수정", "삭제")

        AlertDialog
            .Builder(requireContext())
            .setTitle("카테고리 옵션")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditCategoryDialog(category) // 수정
                    1 -> deleteCategory(category.id) // 삭제
                }
            }.show()
    }

    private fun showEditCategoryDialog(category: CategoryData) {
        val dialogBinding = DialogAddCategoryBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        // 기존 데이터로 다이얼로그 초기화
        dialogBinding.etCategoryName.setText(category.categoryName)
        dialogBinding.etCategoryImageUrl.setText(category.categoryImg)

        // "수정" 버튼 클릭 시
        dialogBinding.btnAddCategory.text = "수정"
        dialogBinding.btnAddCategory.setOnClickListener {
            val updatedName =
                dialogBinding.etCategoryName.text
                    .toString()
                    .trim()
            val updatedImageUrl =
                dialogBinding.etCategoryImageUrl.text
                    .toString()
                    .trim()

            if (updatedName.isNotEmpty() && updatedImageUrl.isNotEmpty()) {
                // TODO: 수정할 데이터 설정하기
                val updatedCategory = CategoryData(updatedName, updatedImageUrl, category.id)

                updateCategory(updatedCategory)

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // "취소" 버튼 클릭 시
        dialogBinding.btnCancelCategory.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun updateCategory(updatedCategory: CategoryData) {
        val service = RetrofitObject.retrofit.create(CategoryService::class.java) // 서비스 객체 생성
        val call = service.putCategory(updatedCategory.id, updatedCategory) // 수정 요청

        call.enqueue(
            object : retrofit2.Callback<CategoryData> {
                override fun onResponse(
                    call: Call<CategoryData>,
                    response: Response<CategoryData>
                ) {
                    if (response.isSuccessful) {
                        Log.d("성공", "카테고리 수정 성공 : ${response.body()}")
                        fetchCategoryInfo()
                    } else {
                        Log.d("실패", "카테고리 수정 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CategoryData>, t: Throwable) {
                    Log.d("실패", "네트워크 요청 실패: ${t.message}")
                }

            }
        )

    }

    private fun deleteCategory(CategoryId: String) {
        var service = RetrofitObject.retrofit.create(CategoryService::class.java)
        var call = service.deleteCategory(CategoryId)

        call.enqueue(
            object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("성공", "카테고리 삭제 성공 : $CategoryId")
                        fetchCategoryInfo() // 카테고리 정보 다시 불러오기(최신정보로)
                    } else {
                        Log.d("실패", "카테고리 삭제 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("실패", "네트워크 요청 실패: ${t.message}")
                }

            }
        )
    }

    private fun addCategoryDialog() {
        // ViewBinding을 활용해 dialog_add_category 레이아웃 바인딩
        val dialogBinding = DialogAddCategoryBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create() // Dialog 객체 생성

        // "추가" 버튼 클릭 시 동작
        dialogBinding.btnAddCategory.setOnClickListener {
            val categoryName =
                dialogBinding.etCategoryName.text
                    .toString()
                    .trim()
            val categoryImageUrl =
                dialogBinding.etCategoryImageUrl.text
                    .toString()
                    .trim()

            if (categoryName.isNotEmpty() && categoryImageUrl.isNotEmpty()) {
                // TODO: 데이터 설정하기
                val newCategory = CategoryData(categoryName, categoryImageUrl, "0")
                // id는 서버에서 자동 생성되므로 0이라 설정해도 됨

                addCategory(newCategory)

                dialog.dismiss() // 다이얼로그 닫기
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // "취소" 버튼 클릭 시 동작
        dialogBinding.btnCancelCategory.setOnClickListener {
            dialog.dismiss() // 다이얼로그 닫기
        }

        dialog.show()
    }

    private fun addCategory(categoryData: CategoryData) {
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.postCategory(categoryData)

        call.enqueue(
            object : retrofit2.Callback<CategoryData> {
                override fun onResponse(
                    call: Call<CategoryData>,
                    response: Response<CategoryData>
                ) {
                    if (response.isSuccessful) {
                        val addedCategory = response.body()

                        if (addedCategory != null) {
                            Log.d("성공", "카테고리 추가 성공 : $addedCategory")
                            fetchCategoryInfo()
                        } else {
                            Log.d("실패", "카테고리 추가 실패: 응답 데이터 없음")
                        }
                    } else {
                        Log.d("실패", "카테고리 추가 실패: 상태코드 ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CategoryData>, t: Throwable) {
                    Log.d("실패", "네트워크 요청 실패: ${t.message}")
                }

            }
        )

    }

    private fun fetchPopularInfo() {
        val service = RetrofitObject.retrofit.create(HomePopularService::class.java)
        val call = service.getPopular()

        call.enqueue(
            object : retrofit2.Callback<List<PopularData>> {
                override fun onResponse(
                    call: Call<List<PopularData>>,
                    response: Response<List<PopularData>>
                ) {
                    if (response.isSuccessful) {
                        val popularResponse = response.body()

                        if (!popularResponse.isNullOrEmpty()) {
                            showPopularInfo(popularResponse)
                        } else {
                            Log.d("빈값받아옴", "빈값받아옴")
                        }
                    } else {
                        Log.d("서버 응답 실패", "서버 응답 실패")
                    }
                }

                override fun onFailure(call: Call<List<PopularData>>, t: Throwable) {
                    Log.d("실패3", "실패3")
                }

            }
        )
    }

    private fun fetchCategoryInfo() {
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.getCategories()

        call.enqueue(
            object : retrofit2.Callback<List<CategoryData>> {
                override fun onResponse(
                    call: Call<List<CategoryData>>,
                    response: Response<List<CategoryData>>
                ) {
                    if (response.isSuccessful) {
                        val categoryResponse = response.body()

                        // 데이터가 성공적으로 받아와졌을 때
                        if (!categoryResponse.isNullOrEmpty()) {
                            showCategoryInfo(categoryResponse)
                        } else {
                            Log.d("빈값받아옴", "빈값받아옴")
                            // 빈 값을 받아온 경우
                        }
                    } else {
                        Log.d("서버 응답 실패", "서버 응답 실패")
                        // 서버 응답이 실패했을 때 (상태 코드 5**)
                    }
                }

                override fun onFailure(call: Call<List<CategoryData>>, t: Throwable) {
                    Log.d("실패3", "실패3")
                }

            }
        )
    }

    private fun showCategoryInfo(categoryList: List<CategoryData>) {
        // 레이아웃 인플레이터를 사용해 카테고리 항목을 동적으로 추가
        val inflater = LayoutInflater.from(requireContext())
        binding.llMainMenuCategory.removeAllViews() // 기존 항목 제거

        categoryList.forEach { category ->
            val categoryBinding =
                ItemCategoryBinding.inflate(inflater, binding.llMainMenuCategory, false)

            // 이미지 로딩: Glide 사용 (이미지 URL을 ImageView에 로드)
            Glide
                .with(this)
                .load(category.categoryImg)
                .into(categoryBinding.sivCategoryImg)

            // 카테고리 이름 설정
            categoryBinding.tvCategoryName.text = category.categoryName

            categoryBinding.root.setOnClickListener {
                showCategoryOptionsDialog(category)
            }

            // 레이아웃에 카테고리 항목 추가
            binding.llMainMenuCategory.addView(categoryBinding.root)
        }
    }

    private fun showPopularInfo(popularList: List<PopularData>) {
        popularAdapter = RVPopularMenuAdapter(popularList)
        binding.rvMainPopularMenus.adapter = popularAdapter
        binding.rvMainPopularMenus.layoutManager = LinearLayoutManager(requireContext())
        popularAdapter.notifyDataSetChanged()
        popularAdapter.setItemClickListener { popular ->
            showPopularOptionsDialog(popular)
        }
    }
}
