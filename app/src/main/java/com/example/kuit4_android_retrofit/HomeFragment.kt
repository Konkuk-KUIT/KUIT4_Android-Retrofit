package com.example.kuit4_android_retrofit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.adapter.PopularMenuClickListener
import com.example.kuit4_android_retrofit.adapter.RVPopularMenuAdapter
import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.data.PopularMenuData
import com.example.kuit4_android_retrofit.databinding.DialogAddCategoryBinding
import com.example.kuit4_android_retrofit.databinding.DialogAddMenuBinding
import com.example.kuit4_android_retrofit.databinding.FragmentHomeBinding
import com.example.kuit4_android_retrofit.databinding.ItemCategoryBinding
import com.example.kuit4_android_retrofit.retrofit.RetrofitObject
import com.example.kuit4_android_retrofit.retrofit.service.CategoryService
import com.example.kuit4_android_retrofit.retrofit.service.PopularMenuService
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment(), PopularMenuClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var rvPopularMenuAdapter: RVPopularMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        fetchCategoryInfo()
        fetchPopularMenuInfo()

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

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnAddMenu.setOnClickListener {
            val menuName = dialogBinding.etMenuName.text.toString().trim()
            val menuImageUrl = dialogBinding.etMenuImageUrl.text.toString().trim()
            val menuTime = dialogBinding.etMenuTime.text.toString().trim().toIntOrNull()
            val menuRating = dialogBinding.etMenuRating.text.toString().trim().toDoubleOrNull()

            if (menuName.isNotEmpty() && menuImageUrl.isNotEmpty() && menuTime != null && menuRating != null) {
                val newMenu = PopularMenuData(menuName, menuImageUrl, menuTime, menuRating, "")
                addMenu(newMenu)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBinding.btnCancelMenu.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addMenu(menuData: PopularMenuData) {
        val service = RetrofitObject.retrofit.create(PopularMenuService::class.java)
        val call = service.postMenu(menuData)

        call.enqueue(object : retrofit2.Callback<PopularMenuData> {
            override fun onResponse(
                call: Call<PopularMenuData>,
                response: Response<PopularMenuData>
            ) {
                if (response.isSuccessful) {
                    fetchPopularMenuInfo()
                }
            }
            override fun onFailure(call: Call<PopularMenuData>, t: Throwable) {
            }
        })
    }

    override fun onMenuClick(menu: PopularMenuData) {
        showMenuOptionsDialog(menu)
    }

    private fun showMenuOptionsDialog(menu: PopularMenuData) {
        val options = arrayOf("수정", "삭제")

        AlertDialog.Builder(requireContext())
            .setTitle("인기 메뉴 옵션")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditMenuDialog(menu)
                    1 -> deleteMenu(menu.id)
                }
            }
            .show()
    }

    private fun showEditMenuDialog(menu: PopularMenuData) {
        val dialogBinding = DialogAddMenuBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.etMenuName.setText(menu.popularMenuName)
        dialogBinding.etMenuImageUrl.setText(menu.popularMenuImg)
        dialogBinding.etMenuTime.setText(menu.popularMenuTime.toString())
        dialogBinding.etMenuRating.setText(menu.popularMenuRating.toString())
        dialogBinding.btnAddMenu.text = "수정"

        dialogBinding.btnAddMenu.setOnClickListener {
            val updatedName = dialogBinding.etMenuName.text.toString().trim()
            val updatedImageUrl = dialogBinding.etMenuImageUrl.text.toString().trim()
            val updatedTime = dialogBinding.etMenuTime.text.toString().trim().toIntOrNull()
            val updatedRating = dialogBinding.etMenuRating.text.toString().trim().toDoubleOrNull()

            if (updatedName.isNotEmpty() && updatedImageUrl.isNotEmpty() && updatedTime != null && updatedRating != null) {
                val updatedMenu = menu.copy(
                    popularMenuName = updatedName,
                    popularMenuImg = updatedImageUrl,
                    popularMenuTime = updatedTime,
                    popularMenuRating = updatedRating
                )
                editMenu(updatedMenu)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 올바르게 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.btnCancelMenu.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun editMenu(menu: PopularMenuData) {
        val service = RetrofitObject.retrofit.create(PopularMenuService::class.java)
        val call = service.putMenu(menu.id, menu)

        call.enqueue(object : retrofit2.Callback<PopularMenuData> {
            override fun onResponse(
                call: Call<PopularMenuData>,
                response: Response<PopularMenuData>
            ) {
                if (response.isSuccessful) {
                    fetchPopularMenuInfo()
                }
            }

            override fun onFailure(call: Call<PopularMenuData>, t: Throwable) {
            }
        })
    }

    private fun deleteMenu(menuId: String) {
        val service = RetrofitObject.retrofit.create(PopularMenuService::class.java)
        val call = service.deleteMenu(menuId)

        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    fetchPopularMenuInfo()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
            }
        })
    }

    private fun addCategoryDialog(){
        val dialogBinding = DialogAddCategoryBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog =
            AlertDialog
                .Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

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
                addCategory(newCategory)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

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
                    0 -> showEditCategoryDialog(category)
                    1 -> deleteCategory(category.id)
                }
            }.show()
    }

    private fun deleteCategory(categoryId: String){
        val service = RetrofitObject.retrofit.create(CategoryService::class.java)
        val call = service.deleteCategory(categoryId)

        call.enqueue(
            object : retrofit2.Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful){
                        fetchCategoryInfo()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
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

        dialogBinding.etCategoryName.setText(category.categoryName)
        dialogBinding.etCategoryImageUrl.setText(category.categoryImg)

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

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

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
                            fetchCategoryInfo()
                        }
                    }
                }

                override fun onFailure(call: Call<CategoryData>, t: Throwable) {
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

                        if(!categoryResponse.isNullOrEmpty()) {
                            showCategoryInfo(categoryResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<List<CategoryData>>, t: Throwable) {
                }

            }
        )
    }

    private fun showCategoryInfo(categoryList: List<CategoryData>) {
        val inflater = LayoutInflater.from(requireContext())
        binding.llMainMenuCategory.removeAllViews()

        categoryList.forEach { category ->
            val categoryBinding = ItemCategoryBinding.inflate(inflater, binding.llMainMenuCategory, false)

            Glide
                .with(this)
                .load(category.categoryImg)
                .into(categoryBinding.sivCategoryImg)

            categoryBinding.tvCategoryName.text = category.categoryName

            categoryBinding.root.setOnClickListener() {
                showCategoryOptionsDialog(category)
            }


            binding.llMainMenuCategory.addView(categoryBinding.root)
        }
    }

    private fun fetchPopularMenuInfo() {
        val service = RetrofitObject.retrofit.create(PopularMenuService::class.java)
        val call = service.getPopularMenu()

        call.enqueue(
            object : retrofit2.Callback<List<PopularMenuData>> {
                override fun onResponse(
                    call: Call<List<PopularMenuData>>,
                    response: Response<List<PopularMenuData>>
                ) {
                    if (response.isSuccessful) {
                        val popularMenuResponse = response.body()
                        if (!popularMenuResponse.isNullOrEmpty()) {
                            showPopularMenuInfo(popularMenuResponse)
                        }
                    }
                }

                override fun onFailure(call: Call<List<PopularMenuData>>, t: Throwable) {
                }
            })
    }

    private fun showPopularMenuInfo(popularMenuList: List<PopularMenuData>) {
        rvPopularMenuAdapter = RVPopularMenuAdapter(requireContext(), popularMenuList, this)
        binding.rvMainPopularMenus.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMainPopularMenus.adapter = rvPopularMenuAdapter
    }

}

