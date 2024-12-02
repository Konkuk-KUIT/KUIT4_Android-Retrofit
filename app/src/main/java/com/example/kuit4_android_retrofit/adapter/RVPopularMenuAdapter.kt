package com.example.kuit4_android_retrofit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.data.MenuData
import com.example.kuit4_android_retrofit.databinding.ItemPopularMenuBinding
import com.example.kuit4_android_retrofit.HomeFragment

class RVPopularMenuAdapter(
    private val context: Context,
    private var menuList: List<MenuData>,
    private val fragment: HomeFragment
) : RecyclerView.Adapter<RVPopularMenuAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemPopularMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuData) {
            binding.tvPopularMenuName.text = item.menuName
            binding.tvPopularMenuTime.text = item.eta.toString()+"분"
            binding.tvPopularMenuRate.text = item.rating.toString()
            //인자로 받은 함수 호출
            binding.root.setOnClickListener {
                fragment.showPopularMenuOptionsDialog(item)
            }

            Glide.with(context)
                .load(item.menuImgUrl)
                .placeholder(android.R.color.transparent)
                .into(binding.ivPopularMenuImg)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ItemPopularMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size


}
