package com.example.campustransaction.user_post

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentUserPostBinding
import com.example.campustransaction.ui.UIViewModel
import com.example.campustransaction.user_post.adapter.PostAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class UserPostFragment : Fragment() {
    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentUserPostBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserPostBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.postOwner?.let { viewModel.sdkGetUserInfo(it) }
        viewModel.sdkUserPosts()

        viewModel.responseUserPosts.observe(viewLifecycleOwner){
            if (viewModel.responseUserPosts.value?.Success == true){
                val postList = viewModel.responseUserPosts.value!!.Posts
                binding.recyclerView.adapter = context?.let { it1 -> postList?.let { it2 -> PostAdapter(it1, it2) } }
                binding.recyclerView.setHasFixedSize(false)
                //binding.recyclerView.addItemDecoration(SpaceItemDecoration(5))

                (binding.recyclerView.adapter as PostAdapter?)?.setOnItemClickListener(object: PostAdapter.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {
                        //Toast.makeText(context, "click $position item", Toast.LENGTH_SHORT).show()
                        viewModel.postDetail = viewModel.responseUserPosts.value!!.Posts?.get(position)
                        //Toast.makeText(context, "PostOwner: ${viewModel.postDetail?.PostOwner}", Toast.LENGTH_SHORT).show()
                        view.findNavController().navigate(R.id.action_userPostFragment_to_postDetailFragment)
                    }

                    override fun onItemLongClick(view: View, position: Int) {
                        if(viewModel.myUserInfo.EmailAddress == viewModel.postOwner) {
                            // Toast.makeText(context, "long click $position item", Toast.LENGTH_SHORT).show()
                            val pid = viewModel.responseUserPosts.value!!.Posts?.get(position)?.PID
                            pid?.let { it1 -> askDeletePost(it1) }
                        }
                    }
                })
            }
        }

        viewModel.responseGetUserInfo.observe(viewLifecycleOwner){
            if (viewModel.responseGetUserInfo.value?.Success == true){
                if (viewModel.responseGetUserInfo.value!!.HeadPortrait != null && viewModel.responseGetUserInfo.value!!.HeadPortrait != "") {
                    context?.let { viewModel.responseGetUserInfo.value!!.HeadPortrait?.let { it1 -> viewModel.setBase64ToUri(it, it1) } }
                }
                binding.titleEmailAddress.text = viewModel.responseGetUserInfo.value!!.EmailAddress
                //Log.d("onCreateView","set HeadPortrait into viewModel.imageUri")
            }else{
                Log.d("responseGetUserInfo","${viewModel.responseGetUserInfo.value?.Error}")
            }
        }

        viewModel.photoUri.observe(viewLifecycleOwner){
            viewModel.postOwnerHeadPortraitUri = viewModel.photoUri.value
            binding.headPortrait.setImageURI(viewModel.postOwnerHeadPortraitUri)
        }

        viewModel.responseDeletePost.observe(viewLifecycleOwner){
            if(viewModel.responseDeletePost.value?.Success == true){
                Toast.makeText(context, "Delete a post success", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Delete Error:${viewModel.responseDeletePost.value?.Error}", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    private fun askDeletePost(pid: String){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete this post?")
            .setMessage("Are you sure to delete this post?")
            .setCancelable(false)
            .setNegativeButton("DON'T DELETE") { _, _ -> unDeletePost() }
            .setPositiveButton("DELETE") {_, _ -> deletePost(pid) }
            .show()
    }

    private fun unDeletePost(){}

    private fun deletePost(pid: String){
        viewModel.sdkDeletePost(pid)
        Log.d("UserPostFragment","deletePost")
    }


}

class SpaceItemDecoration(private val space: Int) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = space
        outRect.bottom = space
        //由于每行都只有2个，所以第一个都是2的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) % 2 == 0) {
            outRect.left = 0
        }
    }
}