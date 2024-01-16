package com.example.campustransaction.personal_center

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.api.ResponseBasicMessage
import com.example.campustransaction.databinding.FragmentOtherPersonalCenterBinding
import com.example.campustransaction.ui.UIViewModel


class OtherPersonalCenterFragment : Fragment() {
    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentOtherPersonalCenterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOtherPersonalCenterBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.otherPersonal?.let { viewModel.sdkGetUserInfo(it) }
        viewModel.responseGetUserInfo.observe(viewLifecycleOwner){
            if (viewModel.responseGetUserInfo.value?.Success == true){
                if (viewModel.responseGetUserInfo.value!!.HeadPortrait != null && viewModel.responseGetUserInfo.value!!.HeadPortrait != "") {
                    context?.let { viewModel.responseGetUserInfo.value!!.HeadPortrait?.let { it1 -> viewModel.setBase64ToUri(it, it1) } }
                }
                binding.titleEmailAddress.text = viewModel.responseGetUserInfo.value!!.EmailAddress
                binding.titleNickname.text = viewModel.responseGetUserInfo.value!!.NickName
                binding.titleBirthday.text = viewModel.responseGetUserInfo.value!!.Birthday
                binding.titleFirstname.text = viewModel.responseGetUserInfo.value!!.FirstName
                binding.titleLastname.text = viewModel.responseGetUserInfo.value!!.LastName
                binding.titleGender.text = viewModel.responseGetUserInfo.value!!.Gender
                binding.titlePhoneNumber.text = viewModel.responseGetUserInfo.value!!.PhoneNumber.toString()
                binding.titleProfile.text = viewModel.responseGetUserInfo.value!!.Profile
                binding.titleRegion.text = viewModel.responseGetUserInfo.value!!.Region
                binding.titleSchool.text = viewModel.responseGetUserInfo.value!!.School
                binding.titleStudentId.text = viewModel.responseGetUserInfo.value!!.StudentID.toString()
                //Log.d("onCreateView","set HeadPortrait into viewModel.imageUri")
            }else{
                Log.d("responseGetUserInfo","${viewModel.responseGetUserInfo.value?.Error}")
            }
        }

        viewModel.photoUri.observe(viewLifecycleOwner){
            viewModel.postOwnerHeadPortraitUri = viewModel.photoUri.value
            binding.headPortrait.setImageURI(viewModel.postOwnerHeadPortraitUri)
        }

        binding.buttonCheckPost.setOnClickListener {
            viewModel.postOwner = viewModel.otherPersonal
            view?.findNavController()?.navigate(R.id.action_otherPersonalCenterFragment_to_userPostFragment)
        }

        binding.buttonSendMessage.setOnClickListener {
            viewModel.messageDetail = ResponseBasicMessage(Sender = viewModel.otherPersonal!!)
            viewModel.messageDetail?.myEmailAddress = viewModel.myUserInfo.EmailAddress
            viewModel.messageDetail?.myHeadPortrait = viewModel.headPortraitUri
            viewModel.messageDetail?.myNickName = viewModel.myUserInfo.NickName
            viewModel.messageDetail?.NickName = viewModel.responseGetUserInfo.value!!.NickName
            view?.findNavController()?.navigate(R.id.action_otherPersonalCenterFragment_to_messageDetailFragment)
        }

        return binding.root
    }

}