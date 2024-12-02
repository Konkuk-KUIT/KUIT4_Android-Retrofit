
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.data.PopularData
import com.example.kuit4_android_retrofit.databinding.ItemPopularMenuBinding

class RVPopularMenuAdapter(
    private val menuList: List<PopularData>,
) : RecyclerView.Adapter<RVPopularMenuAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemPopularMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PopularData) {
            binding.tvPopularMenuName.text = item.menuName
            Glide.with(binding.root.context)
                .load(item.menuImg)
                .into(binding.ivPopularMenuImg)
            binding.tvPopularMenuTime.text = "${item.time} 분"
            binding.tvPopularMenuRate.text = "${item.rate}"
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
