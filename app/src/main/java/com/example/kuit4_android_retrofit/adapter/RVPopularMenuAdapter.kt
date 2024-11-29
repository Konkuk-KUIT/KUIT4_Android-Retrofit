package com.example.kuit4_android_retrofit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.data.PopularMenuData
import com.example.kuit4_android_retrofit.databinding.ItemPopularMenuBinding

interface PopularMenuClickListener {
    fun onMenuClick(menu: PopularMenuData)
}

class RVPopularMenuAdapter(
    private val context: Context,
    private val popularMenuList: List<PopularMenuData>,
    private val popularMenuClickListener: PopularMenuClickListener
) : RecyclerView.Adapter<RVPopularMenuAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemPopularMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PopularMenuData) {
            binding.tvPopularMenuName.text = item.popularMenuName

            Glide.with(context)
                .load(item.popularMenuImg)
                .into(binding.ivPopularMenuImg)

            binding.tvPopularMenuTime.text = item.popularMenuTime.toString() + "분"
            binding.tvPopularMenuRate.text = item.popularMenuRating.toString()

            binding.root.setOnClickListener {
                popularMenuClickListener.onMenuClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPopularMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(popularMenuList[position])
    }

    override fun getItemCount(): Int = popularMenuList.size
}
