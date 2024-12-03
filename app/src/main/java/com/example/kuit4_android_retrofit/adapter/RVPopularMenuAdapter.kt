
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.R
import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.data.PopularData
import com.example.kuit4_android_retrofit.databinding.ItemCategoryBinding
import com.example.kuit4_android_retrofit.databinding.ItemPopularMenuBinding

class RVPopularMenuAdapter(
    private val menuList: List<PopularData>,
) : RecyclerView.Adapter<RVPopularMenuAdapter.ViewHolder>() {
    private lateinit var itemClickListener: (PopularData) -> Unit

    inner class ViewHolder(
        private val binding: ItemPopularMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PopularData) {
            binding.tvPopularMenuName.text = item.popularName
            binding.tvPopularMenuRate.text = item.popularRating.toString()
            binding.tvPopularMenuTime.text = item.popularTime.toString()

            Glide
                .with(binding.root)
                .load(item.popularImg)
                .into(binding.ivPopularMenuImg)

            binding.root.setOnClickListener {
                itemClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ItemPopularMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size
    fun setItemClickListener(listener: (PopularData) -> Unit) {
        this.itemClickListener = listener

    }
}
