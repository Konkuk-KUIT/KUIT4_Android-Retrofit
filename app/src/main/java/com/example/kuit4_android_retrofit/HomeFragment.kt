package com.example.kuit4_android_retrofit

import com.example.kuit4_android_retrofit.adapter.RVPopularMenuAdapter
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
import com.example.kuit4_android_retrofit.databinding.DialogAddPopularMenuBinding
import com.example.kuit4_android_retrofit.databinding.FragmentHomeBinding
import com.example.kuit4_android_retrofit.databinding.ItemCategoryBinding
import com.example.kuit4_android_retrofit.databinding.ItemPopularMenuBinding
import com.example.kuit4_android_retrofit.retrofit.RetrofitObject
import com.example.kuit4_android_retrofit.retrofit.service.CategoryService
import com.example.kuit4_android_retrofit.retrofit.service.PopularMenuService
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var rvAdapterPopular: RVPopularMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        fetchCategoryInfo()
        fetchPopularMenuData()

        binding.ivAddCategory.setOnClickListener {
            addCategoryDialog()
        }

        binding.ivAddPopularMenu.setOnClickListener {
            addPopularMenuDialog()
        }

        return binding.root
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
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.putCategory(updatedCategory.id, updatedCategory)

        call.enqueue(
            object : retrofit2.Callback<CategoryData>{
                override fun onResponse(
                    call: Call<CategoryData>,
                    response: Response<CategoryData>
                ) {
                    if (response.isSuccessful){
                        Log.d("successful", "카테고리 수정 성공: ")
                        fetchCategoryInfo()
                    } else{
                        Log.d("fail", "카테고리 수정 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CategoryData>, t: Throwable) {
                    Log.d("fail", "네트워크 요청 실패: ${t.message}")
                }
            }
        )
    }

    private fun deleteCategory(categoryId: String){
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.deleteCategory(categoryId)

        call.enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        Log.d("successful", "카테고리 삭제 성공: $categoryId")
                        fetchCategoryInfo()
                    }else{
                        Log.d("fail", "카테고리 삭제 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("fail", "네트워크 요청 실패: ${t.message}")
                }

            }
        )
    }

    private fun addCategoryDialog(){
        // ViewBinding을 활용해 dialog_add_category 레이아웃 바인딩
        val dialogBinding = DialogAddCategoryBinding.inflate(LayoutInflater.from(requireContext()))

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

            if (categoryName.isNotEmpty() && categoryImageUrl.isNotEmpty()) {
                val newCategory = CategoryData(categoryName, categoryImageUrl, "")
                //서버에 넣기
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

    private fun addCategory(categoryData: CategoryData) {
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.postCategory(categoryData)

        call.enqueue(
            object : retrofit2.Callback<CategoryData>{
                override fun onResponse(
                    call: Call<CategoryData>,
                    response: Response<CategoryData>
                ) {
                    if(response.isSuccessful){
                        val addedCategory = response.body()

                        if(addedCategory!=null){
                            Log.d("successful", "카테고리 추가 성공 : $addedCategory")
                            fetchCategoryInfo()
                        }else{
                            Log.d("fail", "응답 데이터 없음")
                        }
                    }else{
                        Log.d("fail", "상태코드 :  ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CategoryData>, t: Throwable) {
                    Log.d("fail", "네트워크 요청 실패: ${t.message}")
                }

            }
        )

    }

    private fun fetchCategoryInfo() {
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.getCategories()

        //비동기 통신
        call.enqueue(
            object : retrofit2.Callback<List<CategoryData>>{
                override fun onResponse(
                    call: Call<List<CategoryData>>,
                    response: Response<List<CategoryData>>
                ) {
                    if(response.isSuccessful){
                        //여기서 body는 서버에서 보낸 data
                        val categoryResponse = response.body()

                        //data 성공적으로 받아온 경우
                        if(!categoryResponse.isNullOrEmpty()){
                            showCategoryInfo(categoryResponse)
                        }else{
                            Log.d("fail","response is null or empty in category")
                        }
                    }else{
                        Log.d("fail","response failure in category") //보통 상태코드 5xx
                    }
                }

                override fun onFailure(call: Call<List<CategoryData>>, t: Throwable) {
                    Log.d("fail", "network failure in category")
                }
            }
        )
    }

    private fun showCategoryInfo(categoryList: List<CategoryData>) {
        // 레이아웃 인플레이터를 사용해 카테고리 항목을 동적으로 추가
        val inflater = LayoutInflater.from(requireContext())
        binding.llMainMenuCategory.removeAllViews() // 기존 항목 제거

        categoryList.forEach { category ->
            val categoryBinding = ItemCategoryBinding.inflate(inflater, binding.llMainMenuCategory, false)

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

    fun showPopularMenuOptionsDialog(popularMenu: MenuData){
        val options = arrayOf("수정", "삭제")

        AlertDialog
            .Builder(requireContext())
            .setTitle("인기메뉴 옵션")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditPopularMenuDialog(popularMenu) // 수정
                    1 -> deletePopularMenu(popularMenu.id) // 삭제
                }
            }.show()
    }

    private fun showEditPopularMenuDialog(popularMenu: MenuData) {
        val dialogBinding = DialogAddPopularMenuBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        // 기존 데이터로 다이얼로그 초기화
        dialogBinding.etPopularMenuName.setText(popularMenu.menuName)
        dialogBinding.etPopularMenuImageUrl.setText(popularMenu.menuImgUrl)
        dialogBinding.etPopularMenuRating.setText(popularMenu.rating.toString())
        dialogBinding.etPopularMenuEta.setText(popularMenu.eta.toString())


        // "수정" 버튼 클릭 시
        dialogBinding.btnAddPopularMenu.text = "수정"
        dialogBinding.btnAddPopularMenu.setOnClickListener {
            val updatedName =
                dialogBinding.etPopularMenuName.text
                    .toString()
                    .trim()
            val updatedImageUrl =
                dialogBinding.etPopularMenuImageUrl.text
                    .toString()
                    .trim()
            val updatedRating =
                dialogBinding.etPopularMenuRating.text
                    .toString()
                    .trim()
            val updatedETA =
                dialogBinding.etPopularMenuEta.text
                    .toString()
                    .trim()

            if (updatedName.isNotEmpty() && updatedImageUrl.isNotEmpty() && updatedRating.isNotEmpty() && updatedETA.isNotEmpty()) {
                val updatedPopularMenu = MenuData(
                    updatedName,
                    updatedImageUrl,
                    updatedRating.toDouble(),
                    updatedETA.toInt(),
                    popularMenu.id
                )
                updatePopularMenu(updatedPopularMenu)

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        //"취소" 버튼 클릭 시
        dialogBinding.btnCancelPopularMenu.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updatePopularMenu(updatedMenu: MenuData){
        val service = RetrofitObject.retrofit.create(PopularMenuService::class.java)
        val call = service.putPopular(updatedMenu.id, updatedMenu)

        call.enqueue(
            object : retrofit2.Callback<MenuData>{
                override fun onResponse(call: Call<MenuData>, response: Response<MenuData>) {
                    if (response.isSuccessful){
                        Log.d("successful", "인기메뉴 수정 성공 : ")
                        fetchPopularMenuData()
                    }else{
                        Log.d("fail", "카테고리 수정 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MenuData>, t: Throwable) {
                    Log.d("fail", "네트워크 요청 실패: ${t.message}")
                }

            }
        )
    }

    private fun deletePopularMenu(popularMenuId: String){
        val service = RetrofitObject.retrofit.create(PopularMenuService::class.java)
        val call = service.deletePopular(popularMenuId)

        call.enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        Log.d("successful", "인기메뉴 삭제 성공: $popularMenuId")
                        fetchPopularMenuData()
                    }else{
                        Log.d("fail", "인기메뉴 삭제 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("fail", "네트워크 요청 실패: ${t.message}")
                }
            }
        )
    }

    private fun addPopularMenuDialog(){
        val dialogBinding = DialogAddPopularMenuBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

        //"추가" 버튼 클릭 시 동작
        dialogBinding.btnAddPopularMenu.setOnClickListener {
            val popularMenuName =
                dialogBinding.etPopularMenuName.text
                    .toString()
                    .trim()
            val popularMenuImageUrl =
                dialogBinding.etPopularMenuImageUrl.text
                    .toString()
                    .trim()
            val popularMenuRating =
                dialogBinding.etPopularMenuRating.text
                    .toString()
                    .trim()
            val popularMenuETA =
                dialogBinding.etPopularMenuEta.text
                    .toString()
                    .trim()

            if (popularMenuName.isNotEmpty() && popularMenuImageUrl.isNotEmpty() && popularMenuRating.isNotEmpty() && popularMenuETA.isNotEmpty()){
                val newPopularMenu = MenuData(
                    popularMenuName,
                    popularMenuImageUrl,
                    popularMenuRating.toDouble(),
                    popularMenuETA.toInt(),
                    ""
                )
                //서버에 넣기
                addPopularMenu(newPopularMenu)

                dialog.dismiss()
            }else{
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        //"취소" 버튼 클릭 시 동작
        dialogBinding.btnCancelPopularMenu.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addPopularMenu(menuData: MenuData){
        val service = RetrofitObject.retrofit.create(PopularMenuService::class.java)
        val call = service.postPopular(menuData)

        call.enqueue(
            object : retrofit2.Callback<MenuData>{
                override fun onResponse(call: Call<MenuData>, response: Response<MenuData>) {
                    if (response.isSuccessful){
                        val addedPopularMenu = response.body()

                        if (addedPopularMenu!=null){
                            Log.d("successful", "인기메뉴 추가 성공: $addedPopularMenu")
                            fetchPopularMenuData()
                        }else{
                            Log.d("fail", "응답 데이터 없음")
                        }
                    }else{
                        Log.d("fail", "상태코드 : ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MenuData>, t: Throwable) {
                    Log.d("fail", "네트워크 요청 실패: ${t.message}")
                }

            }
        )
    }

    private fun fetchPopularMenuData(){
        val service = RetrofitObject.retrofit.create(PopularMenuService::class.java)
        val call = service.getCategories()

        //비동기 통신
        call.enqueue(
            object : retrofit2.Callback<List<MenuData>>{
                override fun onResponse(
                    call: Call<List<MenuData>>,
                    response: Response<List<MenuData>>
                ) {
                    if(response.isSuccessful){
                        //여기서 body는 서버에서 보낸 data
                        val menuResponse = response.body()

                        //data 성공적으로 받아온 경우
                        if(!menuResponse.isNullOrEmpty()){
                            initRVPopularMenu(menuResponse)
                        }else{
                            Log.d("fail","response is null or empty in popular")
                        }
                    }else{
                        Log.d("fail","response failure in popular") //보통 상태코드 5xx
                    }
                }

                override fun onFailure(call: Call<List<MenuData>>, t: Throwable) {
                    Log.d("fail", "network failure in popular")
                }

            }
        )
    }

    private fun initRVPopularMenu(popularList: List<MenuData>){
        rvAdapterPopular = RVPopularMenuAdapter(requireContext(),popularList,this)
        with(binding.rvMainPopularMenus){
            adapter = rvAdapterPopular
            layoutManager =
                LinearLayoutManager(requireContext())
        }
    }
}
