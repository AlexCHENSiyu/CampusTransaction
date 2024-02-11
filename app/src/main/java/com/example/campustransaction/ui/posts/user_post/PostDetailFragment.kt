package com.example.campustransaction.ui.posts.user_post

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentPostDetailBinding
import com.example.campustransaction.ui.UIViewModel
import com.example.campustransaction.ui.posts.user_post.adapter.CommentAdapter
import com.example.campustransaction.ui.posts.user_post.adapter.PictureAdapter


class PostDetailFragment : Fragment() {
    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentPostDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPostDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // 记录用户点击历史
        viewModel.postDetail?.PID?.let { viewModel.sdkClickPost(it) }

        // 联网现在并设置基本信息
        viewModel.postDetail?.PostOwner?.let { viewModel.sdkGetUserInfo(it) }

        viewModel.responseGetUserInfo.observe(viewLifecycleOwner){
            if (viewModel.responseGetUserInfo.value?.Success == true){
                if (viewModel.responseGetUserInfo.value!!.HeadPortrait != null && viewModel.responseGetUserInfo.value!!.HeadPortrait != "") {
                    context?.let { viewModel.responseGetUserInfo.value!!.HeadPortrait?.let { it1 -> viewModel.setBase64ToUri(it, it1) } }
                }
                binding.titleNickname.text = viewModel.responseGetUserInfo.value!!.NickName
                //Log.d("onCreateView","set HeadPortrait into viewModel.imageUri")
            }else{
                Log.d("responseGetUserInfo","${viewModel.responseGetUserInfo.value?.Error}")
            }
        }

        viewModel.photoUri.observe(viewLifecycleOwner){
            viewModel.postOwnerHeadPortraitUri2 = viewModel.photoUri.value
            binding.headPortrait.setImageURI(viewModel.postOwnerHeadPortraitUri2)
        }

        binding.titleEmailAddress.text = viewModel.postDetail?.PostOwner.toString()
        binding.titlePrice.text = "$ "+ viewModel.postDetail?.Price.toString()
        binding.titleText.text = "Description:\n\t\t"+ viewModel.postDetail?.Text

        // 图片列表
        val pictureList = viewModel.postDetail?.Images
        binding.recyclerPicture.adapter = context?.let { it1 -> pictureList?.let { it2 -> PictureAdapter(it1, it2) } }
        binding.recyclerPicture.setHasFixedSize(false)
        binding.recyclerPicture.addItemDecoration(SpaceItemDecoration(10))

        // 评论列表
        val commentList = viewModel.postDetail?.Comments
        binding.recyclerComment.adapter = context?.let { it1 -> commentList?.let { it2 -> CommentAdapter(it1, it2) } }
        binding.recyclerComment.setHasFixedSize(false)
        binding.recyclerComment.addItemDecoration(SpaceItemDecoration(10))

        binding.buttonAddComment.setOnClickListener {
            if(binding.textNewComment.text != null && binding.textNewComment.text.toString() != ""){
                viewModel.sdkPostComment(viewModel.postDetail?.PID!!, binding.textNewComment.text.toString())
            }
        }

        viewModel.responsePostComment.observe(viewLifecycleOwner){
            if(viewModel.responsePostComment.value?.Success == true){
                Toast.makeText(context, "Comment uploaded", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Error: ${viewModel.responsePostComment.value?.Error}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.headPortrait.setOnClickListener{
            Log.d("OtherPersonalCenter", "Email:${viewModel.otherPersonal}")
            viewModel.otherPersonal = viewModel.postOwner
            view?.findNavController()?.navigate(R.id.action_postDetailFragment_to_otherPersonalCenterFragment)
        }

        return binding.root
    }


}