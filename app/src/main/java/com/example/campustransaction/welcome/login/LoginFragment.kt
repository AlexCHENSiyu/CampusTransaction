package com.example.campustransaction.welcome.login

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.campustransaction.HomeActivity
import com.example.campustransaction.MainActivity
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentLoginBinding
import com.example.campustransaction.databinding.FragmentMainBinding
import com.example.campustransaction.databinding.FragmentUserPostBinding
import com.example.campustransaction.ui.UIViewModel
import com.example.campustransaction.ui.userInfo.UserInfoFragment
import com.example.campustransaction.ui.userInfo.favorite.FavoriteActivity
import okhttp3.internal.notify


class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    private val viewModel2: UIViewModel by activityViewModels()


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentLoginBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val binding2 = FragmentMainBinding.inflate(inflater)
        binding2.lifecycleOwner = this
        binding2.viewModel = viewModel2

        viewModel.responseLogin.observe(viewLifecycleOwner){
            if (viewModel.responseLogin.value?.Success != true){
                binding.titlePasswordError.visibility = View.VISIBLE
            }else{
                // 存邮箱信息和密码
                binding.titlePasswordError.visibility = View.INVISIBLE
                val emailAddress:String = binding.inputEmailAddress2.text.toString()
                val password:String = binding.inputPassword2.text.toString()
                val bundle = Bundle()
                bundle.putString("EmailAddress", emailAddress)
                bundle.putString("Password", password)
                val intent = Intent(activity, HomeActivity::class.java)
                intent.putExtras(bundle) // 将Bundle对象嵌入Intent中
                startActivity(intent)
//                Log.d("myEmailAddress", "Email:$emailAddress")
//                Log.d("myEmailAddress", "Password:$password")
//

                // 创建 PendingIntent，指定要启动的目标 Activity
                val position = 0
                val intent2 = Intent(activity, FavoriteActivity::class.java)
                val intent3 = Intent(requireActivity(), UserInfoFragment::class.java)

                intent2.putExtras(bundle)
                intent3.putExtras(bundle)
//                intent3.putExtra("fragment_to_open", UserInfoFragment::class.java)
//                intent2.putExtra("position", 1) // 将位置信息添加到 Intent 中
                val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)

//                if(position!=0){
//                    viewModel2.postDetail = viewModel2.responseGetPosts.value!!.Posts?.get(position)
//                    viewModel2.postOwner = viewModel2.postDetail!!.PostOwner
//                    view?.findNavController()?.navigate(R.id.action_mainNavigation_to_postDetailFragment)
//                }


                val channelId = "Channel_ID_1"
                val channelName = "Channel Name"
                val channelDescription = "Channel Description"
                val importance = NotificationManager.IMPORTANCE_LOW
                val channel = NotificationChannel(channelId, channelName, importance).apply {
                    description = channelDescription
                }


                // 获取 NotificationManager 的实例，并将通道添加到通知管理器中
                val notificationManager = NotificationManagerCompat.from(requireContext())
                notificationManager.createNotificationChannel(channel)

                // 创建通知构建器
//                val content =



                val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
                    .setSmallIcon(R.drawable.image2)
                    .setContentTitle("There are items you might interest in!")
                    .setContentText("Welcome! New items is now is stack!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                // 使用通知构建器创建通知
                val notification = notificationBuilder.build()

                // 使用 notify() 方法发送通知
                val notificationId = 1
                notificationManager.notify(notificationId, notification)


            }
        }

        /**
        viewModel.myEmailAddress.observe(viewLifecycleOwner){
            val email:String? = viewModel.myEmailAddress.value
            /**
            val intent =  Intent(activity, HomeActivity::class.java)
            intent.putExtra("EmailAddress", email)
            startActivity(intent)
            Log.d("myEmailAddress", "Email:$email")
            **/
            val bundle = Bundle()
            bundle.putString("EmailAddress", email)
            val intent = Intent(activity, HomeActivity::class.java)
            intent.putExtras(bundle) // 将Bundle对象嵌入Intent中
            startActivity(intent)
        }
        **/

        // 忘记密码
        binding.buttonResetPassword.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }

        // Login
        binding.buttonLogin.setOnClickListener {
            viewModel.sdkLogin( binding.inputEmailAddress2.text.toString(),
                binding.inputPassword2.text.toString() )
        }

        return binding.root
    }

}