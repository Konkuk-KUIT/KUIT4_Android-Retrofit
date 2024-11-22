package com.example.kuit4_android_retrofit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.adapter.RVPopularMenuAdapter
import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.data.PopularMenuData
import com.example.kuit4_android_retrofit.databinding.FragmentHomeBinding
import com.example.kuit4_android_retrofit.databinding.ItemCategoryBinding
import com.example.kuit4_android_retrofit.retrofit.RetrofitObject
import com.example.kuit4_android_retrofit.retrofit.service.CategoryService
import com.example.kuit4_android_retrofit.retrofit.service.PopularMenuService
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var rvPopularMenuAdapter: RVPopularMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        fetchCategoryInfo()
        fetchPopularMenuInfo()

        return binding.root
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
        rvPopularMenuAdapter = RVPopularMenuAdapter(requireContext(), popularMenuList)
        binding.rvMainPopularMenus.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMainPopularMenus.adapter = rvPopularMenuAdapter
    }
}
