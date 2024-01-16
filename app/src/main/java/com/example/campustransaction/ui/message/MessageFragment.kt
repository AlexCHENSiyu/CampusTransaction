package com.example.campustransaction.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentMessageBinding
import com.example.campustransaction.ui.UIViewModel
import com.example.campustransaction.ui.message.adapter.ChatAdapter

class MessageFragment : Fragment() {

    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentMessageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMessageBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.sdkGetMessage()

        //设置触发下拉刷新的距离
        binding.swipeRefreshLayout.setDistanceToTriggerSync(300)
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorBlue)
        binding.swipeRefreshLayout.setOnRefreshListener { //这里获取数据的逻辑
            viewModel.sdkGetMessage()
        }

        // 设置消息列表
        viewModel.responseGetMessage.observe(viewLifecycleOwner){
            binding.swipeRefreshLayout.isRefreshing = false

            if (viewModel.responseGetMessage.value?.Success == true){
                val chatList = viewModel.responseGetMessage.value!!.Data
                binding.recyclerPicture.adapter = context?.let { it1 -> chatList?.let { it2 -> ChatAdapter(it1, it2) } }
                binding.recyclerPicture.setHasFixedSize(false)
                // binding.recyclerPicture.addItemDecoration(SpaceItemDecoration(10))

                (binding.recyclerPicture.adapter as ChatAdapter?)?.setOnItemClickListener(object: ChatAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        // Toast.makeText(context, "click $position item", Toast.LENGTH_SHORT).show()
                        viewModel.messageDetail = viewModel.responseGetMessage.value!!.Data?.get(position)
                        viewModel.messageDetail?.myEmailAddress = viewModel.myUserInfo.EmailAddress
                        viewModel.messageDetail?.myHeadPortrait = viewModel.headPortraitUri
                        viewModel.messageDetail?.myNickName = viewModel.myUserInfo.NickName
                        view.findNavController().navigate(R.id.action_messageNavigation_to_messageDetailFragment)
                    }
                })

            }
        }


        return binding.root
    }

}