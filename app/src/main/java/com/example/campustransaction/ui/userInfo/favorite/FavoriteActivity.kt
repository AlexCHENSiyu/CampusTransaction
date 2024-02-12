package com.example.campustransaction.ui.userInfo.favorite

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.campustransaction.HomeActivityArgs
import com.example.campustransaction.R
import com.example.campustransaction.databinding.ActivityFavoriteBinding
import com.example.campustransaction.ui.UIViewModel
import com.igalata.bubblepicker.BubblePickerListener
import com.igalata.bubblepicker.adapter.BubblePickerAdapter
import com.igalata.bubblepicker.model.BubbleGradient
import com.igalata.bubblepicker.model.PickerItem

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private val viewModel: UIViewModel by viewModels()

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    private val mBackPressedCallback: OnBackPressedCallback by lazy  {
        object : OnBackPressedCallback(true) {  //enabled 标记位
            override fun handleOnBackPressed() {
                findNavController(R.id.favoriteActivity).navigate(R.id.userInfoNavigation)
                Log.d("FavoriteActivity","mBackPressedCallback")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        //binding.viewModel = viewModel

        // 获取从Home传入的信息：email address and password
        val emailAddress = HomeActivityArgs.fromBundle(intent.extras!!).emailAddress
        val password = HomeActivityArgs.fromBundle(intent.extras!!).password
        Log.d("FavoriteActivity", "Email:$emailAddress")
        Log.d("FavoriteActivity", "Password:$password")
        // 保存邮箱和密码到viewModel里面
        viewModel.myUserInfo.EmailAddress = emailAddress
        viewModel.myUserInfo.Password = password

        // disable侧边返回
        onBackPressedDispatcher.addCallback(this, mBackPressedCallback)

        val titles = resources.getStringArray(R.array.fields)
        val colors = resources.obtainTypedArray(R.array.colors)
        val images = resources.obtainTypedArray(R.array.images)

        binding.picker.adapter = object : BubblePickerAdapter {
            override val totalCount = titles.size
            override fun getItem(position: Int): PickerItem {
                return PickerItem().apply {
                    title = titles[position]
                    gradient = BubbleGradient(colors.getColor((position * 2) % 8, 0),
                        colors.getColor((position * 2) % 8 + 1, 0), BubbleGradient.VERTICAL)
                    textColor = ContextCompat.getColor(this@FavoriteActivity, android.R.color.white)
                    backgroundImage = ContextCompat.getDrawable(this@FavoriteActivity, images.getResourceId(position, 0))
                }
            }
        }

        binding.buttonConfirm2.setOnClickListener {
            // 上传云端数据库
            Log.d("FavoriteFragment", "sdkCreateAccount:" + viewModel.myUserInfo.FavoriteFields)
            viewModel.sdkCreateAccount()
            findNavController(R.id.favoriteActivity).navigate(R.id.userInfoNavigation)
        }

//        viewModel.responseCreateAccount.observe(this){
//            if (viewModel.responseCreateAccount.value?.Success == true){
//                Log.d("FavoriteFragment", "sdkCreateAccount:" + viewModel.myUserInfo.FavoriteFields)
//                findNavController(R.id.favoriteActivity).navigate(R.id.userInfoNavigation)
//            }else{
//                Log.d("FavoriteFragment", "Error:"+viewModel.responseCreateAccount.value?.Error)
//            }
//        }

        binding.picker.bubbleSize = 1
        binding.picker.listener = object : BubblePickerListener {
            override fun onBubbleSelected(item: PickerItem) {
                //toast("${item.title} selected")
                //Log.d("FavoriteFragment", "add:" + item.title)
                if(viewModel.myUserInfo.FavoriteFields == null){
                    item.title?.let { viewModel.myUserInfo.FavoriteFields = mutableListOf<String>(it)}
                }else{
                    item.title?.let { viewModel.myUserInfo.FavoriteFields?.add(it) }
                }
            }

            override fun onBubbleDeselected(item: PickerItem) {
                //toast("${item.title} deselected")
                //Log.d("FavoriteFragment", "remove:" + item.title)
                item.title?.let { viewModel.myUserInfo.FavoriteFields?.remove(it) }
            }
        }

        colors.recycle()
        images.recycle()
    }

    override fun onResume() {
        super.onResume()
        //Log.d("FavoriteFragment", "onResume")
        binding.picker.onResume()
    }

    override fun onPause() {
        super.onPause()
        //Log.d("FavoriteFragment", "onResume")
        binding.picker.onPause()
    }


}