package com.example.campustransaction.ui.userInfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentUserInfoBinding
import com.example.campustransaction.ui.UIViewModel

class UserInfoFragment : Fragment() {
    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding:FragmentUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         // 设置初始参数emailAddress password
        val emailAddress = arguments?.getString("EmailAddress")
        val password = arguments?.getString("Password")
        Log.d("UserInfoFragment", "Email1:$emailAddress")
        Log.d("UserInfoFragment", "Password1:$password")
        if (emailAddress != null) { viewModel.myUserInfo.EmailAddress = emailAddress }
        if (password != null) { viewModel.myUserInfo.Password = password }
         */

        Log.d("UserInfoFragment", "Email2:"+viewModel.myUserInfo.EmailAddress)
        Log.d("UserInfoFragment", "Password2:"+viewModel.myUserInfo.Password)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // 更新下载下来的HeadPortrait
        viewModel.responseGetMyUserInfo.observe(viewLifecycleOwner){
            if (viewModel.responseGetMyUserInfo.value?.Success == true){
                // GetUserInfo下载完成, 将base64 bitarray转成图片存到viewModel.imageUri
                if (viewModel.myUserInfo.HeadPortrait != null && viewModel.myUserInfo.HeadPortrait != "") {
                    context?.let { viewModel.setBase64ToUri(it, viewModel.myUserInfo.HeadPortrait!!) }
                }
                Log.d("onCreateView","set HeadPortrait into viewModel.imageUri")
            }else{
                Log.d("responseGetMyUserInfo","${viewModel.responseGetMyUserInfo.value?.Error}")
            }

        }

        // 如果viewModel里的图片的Uri被改变，则重新加载图片
        viewModel.photoUri.observe(viewLifecycleOwner){
            if( viewModel.photoUri.value != null){
                viewModel.headPortraitUri = viewModel.photoUri.value
                binding.headPortrait.setImageURI(viewModel.headPortraitUri)
                Log.d("onCreateView","display HeadPortrait")
            }
        }

        binding.myEmailAddress.text = viewModel.myUserInfo.EmailAddress

        binding.headPortrait.setOnClickListener{
            view?.findNavController()?.navigate(R.id.action_userInfoNavigation_to_personalCenterFragment)
        }

        binding.buttonResetPassword.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_userInfoNavigation_to_resetPassword2Fragment)
        }

        binding.buttonPersonalCenter.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_userInfoNavigation_to_personalCenterFragment)
        }

        binding.buttonLogout.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_userInfoNavigation_to_mainActivity)
        }

        binding.buttonAbout.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_userInfoNavigation_to_aboutFragment)
        }

        binding.buttonSetting.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("EmailAddress", viewModel.myUserInfo.EmailAddress)
            bundle.putString("Password", viewModel.myUserInfo.Password)
            bundle.putString("From", "HomeActivity")
            view?.findNavController()?.navigate(R.id.action_userInfoNavigation_to_favoriteActivity,bundle)
        }

        binding.buttonMyPosts.setOnClickListener {
            viewModel.postOwner = viewModel.myUserInfo.EmailAddress
            view?.findNavController()?.navigate(R.id.action_userInfoNavigation_to_userPostFragment)
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 可能是从personal center里面回来的，检查头像被修改了吗
        binding.headPortrait.setImageURI(viewModel.headPortraitUri)
    }


}