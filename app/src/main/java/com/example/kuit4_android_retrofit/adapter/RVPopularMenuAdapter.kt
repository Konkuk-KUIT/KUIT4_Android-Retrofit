import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.example.kuit4_android_retrofit.data.MenuData
import com.example.kuit4_android_retrofit.databinding.DialogAddMenuBinding
import com.example.kuit4_android_retrofit.databinding.ItemPopularMenuBinding
import com.example.kuit4_android_retrofit.retrofit.RetrofitObject
import com.example.kuit4_android_retrofit.retrofit.service.PopularService
import retrofit2.Call
import retrofit2.Response

class RVPopularMenuAdapter(
    private val context: Context
) : ListAdapter<MenuData, RVPopularMenuAdapter.ViewHolder>(DiffCallback) {
    inner class ViewHolder(
        private val binding: ItemPopularMenuBinding
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuData) {
            Glide.with(context).load(item.menuImg)
                .into(binding.ivPopularMenuImg)
            binding.tvPopularMenuName.text = item.menuName
            binding.tvPopularMenuRate.text = item.menuRate.toString()
            binding.tvPopularMenuTime.text = item.menuTime.toString()

            binding.ivPopularMenuImg.setOnClickListener {
                val options = arrayOf("수정", "삭제")
                AlertDialog.Builder(context)
                    .setTitle("카테고리 옵션")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> showEditMenuDialog(item) // 수정
                            1 -> deleteMenu(item.id) // 삭제
                        }
                    }.show()
            }
        }

        private fun deleteMenu(id: String) {
            val service = RetrofitObject.retrofit.create(PopularService::class.java)
            val call = service.deleteMenu(id)
            call.enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "메뉴가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        fetchUpdatedData()
                    } else {
                        Log.d("실패", "메뉴 삭제 실패: 상태코드 ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("실패", "메뉴 삭제 실패: ${t.message}")
                }
            })
        }

        private fun showEditMenuDialog(item: MenuData) {
            val dialogBinding = DialogAddMenuBinding.inflate(LayoutInflater.from(context))
            val dialog = AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .create()

            dialogBinding.etCategoryName.setText(item.menuName)
            dialogBinding.etCategoryImageUrl.setText(item.menuImg)
            dialogBinding.etCategoryTime.setText(item.menuTime.toString())
            dialogBinding.etCategoryRate.setText(item.menuRate.toString())

            dialogBinding.btnAddCategory.text = "수정"
            dialogBinding.btnAddCategory.setOnClickListener {
                val categoryName = dialogBinding.etCategoryName.text.toString().trim()
                val categoryImageUrl = dialogBinding.etCategoryImageUrl.text.toString().trim()
                val categoryTime = dialogBinding.etCategoryTime.text.toString().trim()
                val categoryRate = dialogBinding.etCategoryRate.text.toString().trim()

                if (categoryName.isNotEmpty() && categoryImageUrl.isNotEmpty() &&
                    categoryTime.isNotEmpty() && categoryRate.isNotEmpty()
                ) {
                    try {
                        val updatedMenu = MenuData(
                            menuImg = categoryImageUrl,
                            menuName = categoryName,
                            menuTime = categoryTime.toInt(),
                            menuRate = categoryRate.toDouble(),
                            id = item.id
                        )

                        val service = RetrofitObject.retrofit.create(PopularService::class.java)
                        val call = service.putMenu(updatedMenu.id, updatedMenu)
                        fetchData(call)
                        fetchUpdatedData()
                        dialog.dismiss()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "시간이나 평점을 숫자로 입력하세요.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }

            dialogBinding.btnCancelCategory.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPopularMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun fetchData(call: Call<MenuData>) {
        call.enqueue(object : retrofit2.Callback<MenuData> {
            override fun onResponse(call: Call<MenuData>, response: Response<MenuData>) {
                if (response.isSuccessful) {
                    Log.d("성공", "메뉴 수정 성공: ${response.body()}")
                } else {
                    Log.d("실패", "메뉴 수정 실패: 상태코드 ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MenuData>, t: Throwable) {
                Log.d("실패", "메뉴 수정 실패: ${t.message}")
            }
        })
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MenuData>() {
        override fun areItemsTheSame(oldItem: MenuData, newItem: MenuData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MenuData, newItem: MenuData): Boolean {
            return oldItem == newItem
        }
    }
    private fun fetchUpdatedData() {
        val service = RetrofitObject.retrofit.create(PopularService::class.java)
        val call = service.getMenus()
        call.enqueue(object : retrofit2.Callback<List<MenuData>> {
            override fun onResponse(call: Call<List<MenuData>>, response: Response<List<MenuData>>) {
                if (response.isSuccessful) {
                    val updatedList = response.body()
                    if (!updatedList.isNullOrEmpty()) {
                        submitList(updatedList) // 최신 데이터를 어댑터에 설정
                    }
                } else {
                    Log.d("실패", "데이터 갱신 실패: 상태코드 ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MenuData>>, t: Throwable) {
                Log.d("실패", "데이터 갱신 실패: ${t.message}")
            }
        })
    }
}

