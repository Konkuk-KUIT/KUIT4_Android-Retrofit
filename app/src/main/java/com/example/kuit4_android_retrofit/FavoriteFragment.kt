package com.example.kuit4_android_retrofit

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.kuit4_android_retrofit.databinding.FragmentFavoriteBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior


class FavoriteFragment : Fragment() {
    private lateinit var binding:FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFavoriteBinding.inflate(layoutInflater)

        return binding.root
    }


}
