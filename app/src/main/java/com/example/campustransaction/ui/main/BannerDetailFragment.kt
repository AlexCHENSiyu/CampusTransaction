package com.example.campustransaction.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentBannerDetailBinding
import com.example.campustransaction.ui.UIViewModel


class BannerDetailFragment : Fragment() {

    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentBannerDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBannerDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        when(viewModel.bannerIndex){
            2 -> binding.bannerDetail.setText(R.string.Tutorial_for_Beginners)
            1 -> binding.bannerDetail.setText(R.string.Community_Convention)
        }

        return binding.root
    }


}