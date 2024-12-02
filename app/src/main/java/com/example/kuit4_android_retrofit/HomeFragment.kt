package com.example.kuit4_android_retrofit

import RVPopularMenuAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.data.MenuData
import com.example.kuit4_android_retrofit.databinding.DialogAddCategoryBinding
import com.example.kuit4_android_retrofit.databinding.DialogAddMenuBinding
import com.example.kuit4_android_retrofit.databinding.FragmentHomeBinding
import com.example.kuit4_android_retrofit.databinding.ItemCategoryBinding
import com.example.kuit4_android_retrofit.retrofit.RetrofitObject
import com.example.kuit4_android_retrofit.retrofit.service.CategoryService
import com.example.kuit4_android_retrofit.retrofit.service.PopularService
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var rvPopularMenuAdapter: RVPopularMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        fetchCategoryInfo()
        fetchMenuInfo()

        binding.ivAddCategory.setOnClickListener {
            addCategoryDialog()
        }
        binding.ivAddMenu.setOnClickListener {
            addMenuDialog()
        }

        return binding.root
    }

    private fun addMenuDialog() {
        val dialogBinding = DialogAddMenuBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

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
            val categoryTime =
                dialogBinding.etCategoryTime.text
                    .toString()
                    .trim()
            val categoryRate =
                dialogBinding.etCategoryRate.text
                    .toString()
                    .trim()

            if (categoryName.isNotEmpty() && categoryImageUrl.isNotEmpty() && categoryTime.isNotEmpty() && categoryRate.isNotEmpty()) {
                try{
                    val newMenu = MenuData(
                        categoryImageUrl,
                        categoryName,
                        categoryTime.toInt(),
                        categoryRate.toDouble(),
                        "0"
                    )
                    val service = RetrofitObject.retrofit.create(PopularService::class.java)
                    val call = service.postMenu(newMenu)
                    fetchData(call)
                    dialog.dismiss()
                }
                catch (e: NumberFormatException){
                    Toast.makeText(requireContext(), "시간이나 평점을 숫자로 입력하세요.", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // "취소" 버튼 클릭 시 동작
        dialogBinding.btnCancelCategory.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

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

    private fun deleteCategory(categoryId: String) {
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.deleteCategory(categoryId)

        call.enqueue(
            object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("성공", "카테고리 삭제 성공: $categoryId")
                        fetchCategoryInfo()
                    } else {
                        Log.d("성공", "카테고리 삭제 실패 : ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("실패", "네트워크 오류")
                }

            }
        )
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
                val updatedCategory = CategoryData(updatedName, updatedImageUrl, category.id)
                updatedCategory(updatedCategory)

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

    private fun updatedCategory(updatedCategory: CategoryData) {
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.putCategory(updatedCategory.id, updatedCategory)

        call.enqueue(
            object : retrofit2.Callback<CategoryData> {
                override fun onResponse(
                    call: Call<CategoryData>,
                    response: Response<CategoryData>
                ) {
                    if (response.isSuccessful) {
                        Log.d("성공", "카테고리 수정 성공: ${response.body()}")
                        fetchCategoryInfo()
                    } else {
                        Log.d("실패", "카테고리 수정 실패 ${response.body()}")
                    }

                }

                override fun onFailure(call: Call<CategoryData>, t: Throwable) {
                    Log.d("실패", "네트워크 오류")
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
                .create()
        Log.d("실행", "실행")
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
            Log.d(categoryName, categoryImageUrl)
            if (categoryName.isNotEmpty() && categoryImageUrl.isNotEmpty()) {
                val newCategory = CategoryData(categoryName, categoryImageUrl, "0")
                addCategory(newCategory)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // "취소" 버튼 클릭 시 동작
        dialogBinding.btnCancelCategory.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun fetchData(call: Call<MenuData>) {
        call.enqueue(
            object : retrofit2.Callback<MenuData> {
                override fun onResponse(call: Call<MenuData>, response: Response<MenuData>) {
                    if (response.isSuccessful) {
                        val addedMenu = response.body()
                        if (addedMenu != null) {
                            Log.d("성공", "메뉴 추가 성공: $addedMenu")
                            // 넣어야지
                            fetchMenuInfo()
                        } else {
                            Log.d("실패", "메뉴 추가 실패: 등답 데이터 없음")
                        }
                    } else {
                        Log.d("실패", "메뉴 추가 실패: 상테코드 ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MenuData>, t: Throwable) {
                    Log.d("실패", "메뉴 추가 실패: ${t.message}")
                }

            }
        )
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
                            Log.d("성공", "카테고리 추가 성공: $addedCategory")
                            fetchCategoryInfo()
                        } else {
                            Log.d("실패", "카테고리 추가 실패: 등답 데이터 없음")
                        }
                    } else {
                        Log.d("실패", "카테고리 추가 실패: 상테코드 ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CategoryData>, t: Throwable) {
                    Log.d("실패", "카테고리 추가 실패: ${t.message}")
                }

            }
        )
    }

     fun fetchMenuInfo() {
        val service = RetrofitObject.retrofit.create(PopularService::class.java)
        val call = service.getMenus()

        call.enqueue(
            object : retrofit2.Callback<List<MenuData>> {
                override fun onResponse(
                    call: Call<List<MenuData>>,
                    response: Response<List<MenuData>>
                ) {
                    if (response.isSuccessful) {
                        val menuDataResponse = response.body()
                        // 데이터가 성공적으로 받아와졌을 때
                        if (!menuDataResponse.isNullOrEmpty()) {

                            Log.d("df",menuDataResponse.toString())
                            initMenuRV(menuDataResponse)

                        } else {
                            Log.d("menuNull", "menuNull")// 빈값을 받아온 경우
                        }
                    } else {
                        Log.d("menuNoResponse", "menuNoResponse")// 서버 응답이 실패했을 때 (상태코드 5**)
                    }
                }

                override fun onFailure(call : Call<List<MenuData>>, t: Throwable) {
                    Log.d("menuCritical", "menuCritical")
                }

            }
        )


    }

    private fun initMenuRV(menuDataResponse: List<MenuData>) {
        rvPopularMenuAdapter = RVPopularMenuAdapter(requireContext())
        rvPopularMenuAdapter.submitList(menuDataResponse)
        binding.rvMainPopularMenus.adapter = rvPopularMenuAdapter
        binding.rvMainPopularMenus.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
                            Log.d("categoryNull", "categoryNull")// 빈값을 받아온 경우
                        }
                    } else {
                        Log.d("categoryNoResponse", "categoryNoResponse")// 서버 응답이 실패했을 때 (상태코드 5**)
                    }
                }

                // 네트워크 오류
                override fun onFailure(call: Call<List<CategoryData>>, t: Throwable) {
                    Log.d("categoryCritical", "categoryCritical")
                }

            }
        )
    }

    private fun showCategoryInfo(categoryList: List<CategoryData>) {
        // 레이아웃 인플레이터를 사용해 카테고리 항목을 동적으로 추가
        val inflater = LayoutInflater.from(requireContext())
        binding.llMainMenuCategory.removeAllViews() // 기존 항목 제거

        categoryList.forEach { category ->// 동적으로 한개씩 넣기
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
}
