
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.R
import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.data.MenuData
import com.example.kuit4_android_retrofit.databinding.ItemCategoryBinding
import com.example.kuit4_android_retrofit.databinding.ItemPopularMenuBinding

class RVPopularMenuAdapter(
    private val context : Context,
    private val menuList: List<MenuData>,
    private val onItemClick: (MenuData) -> Unit // 클릭 리스너
) : RecyclerView.Adapter<RVPopularMenuAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemPopularMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuData) {
            //binding.sivCategoryImg.setImageResource(R.drawable.img_barbeque)
            binding.tvPopularMenuName.text = item.menuName
            binding.tvPopularMenuTime.text = item.menuTime.toString()+"분"
            binding.tvPopularMenuRate.text = item.menuRate.toString()


            Glide.with(context)
                .load(item.menuImg)
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
        holder.itemView.setOnClickListener {
            onItemClick(menuList[position])
        }
    }

    override fun getItemCount(): Int = menuList.size
}
