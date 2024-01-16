package com.example.campustransaction.ui.message

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.campustransaction.R
import com.example.campustransaction.api.RequestContent
import com.example.campustransaction.api.RequestMessage
import com.example.campustransaction.api.ResponseBasicContent
import com.example.campustransaction.databinding.FragmentMessageDetailBinding
import com.example.campustransaction.ui.UIViewModel
import com.example.campustransaction.ui.message.adapter.MessageAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MessageDetailFragment : Fragment() {
    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentMessageDetailBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMessageDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.sdkGetMessage(viewModel.messageDetail?.Sender)

        // 设置触发下拉刷新的距离
        binding.swipeChat.setDistanceToTriggerSync(300)
        binding.swipeChat.setColorSchemeResources(R.color.colorBlue)
        binding.swipeChat.setOnRefreshListener { //这里获取数据的逻辑
            viewModel.sdkGetMessage(viewModel.messageDetail?.Sender)
        }

        // 更新内容
        viewModel.responseGetMessage.observe(viewLifecycleOwner){
            binding.swipeChat.isRefreshing = false
            if(viewModel.responseGetMessage.value?.Success == true)
            {
                viewModel.messageDetail?.NickName = viewModel.responseGetMessage.value?.Data?.get(0)?.NickName
                viewModel.messageDetail?.HeadPortrait = viewModel.responseGetMessage.value?.Data?.get(0)?.HeadPortrait
                viewModel.messageDetail?.Contents = viewModel.responseGetMessage.value?.Data?.get(0)?.Contents
                // Toast.makeText(context, "refresh success", Toast.LENGTH_SHORT).show()
                binding.messageList.adapter = context?.let { it1 -> viewModel.messageDetail?.let { it2 -> MessageAdapter(it1, it2) } }
                binding.messageList.setHasFixedSize(false)
            }else{
                Toast.makeText(context, "refresh error:${viewModel.responseGetMessage.value?.Error}", Toast.LENGTH_SHORT).show()
            }
        }

        // 图片列表
        binding.messageList.adapter = context?.let { it1 -> viewModel.messageDetail?.let { it2 -> MessageAdapter(it1, it2) } }
        binding.messageList.setHasFixedSize(false)
        //binding.messageList.addItemDecoration(SpaceItemDecoration(10))
        //binding.messageList.scrollToPosition(0)

        binding.titleNickname.text = viewModel.messageDetail?.NickName?:"Default Name"

        binding.buttonAddComment.setOnClickListener {
            if(binding.newMessage.text!=null && binding.newMessage.text.toString() != ""){
                if(viewModel.newMessage == null){
                    viewModel.newMessage = RequestMessage(Sender = viewModel.myUserInfo.EmailAddress,
                        Receiver = viewModel.messageDetail?.Sender,
                        RequestContent(Text = binding.newMessage.text.toString()) )
                }
                viewModel.sdkSendMessage()
                binding.newMessage.text = null
            }
        }

        viewModel.responseSendMessage.observe(viewLifecycleOwner){
            if(viewModel.responseSendMessage.value?.Success == true){
                if(viewModel.newMessage?.Content?.Text != null){
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val formatted = current.format(formatter)
                    viewModel.messageDetail?.Contents?.add(ResponseBasicContent(Direction = true, Text = viewModel.newMessage?.Content?.Text.toString(), CreateTime = formatted) )
                    binding.messageList.adapter = context?.let { it1 -> viewModel.messageDetail?.let { it2 -> MessageAdapter(it1, it2) } }
                    viewModel.newMessage = null
                }
            }else{
                Log.d("MessageDetailFragment", "responseSendMessage Error: ${viewModel.responseSendMessage.value?.Error}")
            }
        }

        return binding.root
    }


}
