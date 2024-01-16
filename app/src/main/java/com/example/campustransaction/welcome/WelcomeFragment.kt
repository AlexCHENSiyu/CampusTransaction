package com.example.campustransaction.welcome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    // 返回监听设置
    private val mBackPressedCallback: OnBackPressedCallback by lazy  {
        object : OnBackPressedCallback(true) {  //enabled 标记位
            override fun handleOnBackPressed() {
                Log.d("WelcomeFragment","handleOnBackPressed")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, mBackPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<FragmentWelcomeBinding>(inflater,
            R.layout.fragment_welcome,container,false)

        binding.buttonCreateAccount.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_startFragment_to_createAccountFragment)
        }

        binding.buttonLogin.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }

        return binding.root
    }


}