
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.data.MenuData
import com.example.kuit4_android_retrofit.databinding.ItemPopularMenuBinding

class RVPopularMenuAdapter(
    private val menuList: List<MenuData>,
) : RecyclerView.Adapter<RVPopularMenuAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemPopularMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuData) {
            // 이미지 로딩
            Glide
                .with(binding.root.context)
                .load(item.menuImg)
                .into(binding.ivPopularMenuImg)

            // 텍스트 설정
            binding.tvPopularMenuName.text = item.menuName
            binding.tvPopularMenuTime.text = item.deliveryTime.toString() + "분"
            binding.tvPopularMenuRate.text = item.menuRating.toString()
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
