package com.example.campustransaction

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.text.Editable
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.campustransaction.databinding.ActivityHomeBinding
import com.example.campustransaction.ui.UIViewModel
import kotlin.concurrent.timer


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    // 此activity和所有子fragment 共用同一个UIViewModel
    private val viewModel: UIViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.supportActionBar?.hide();

        // 获取从login传入的信息：email address and password
        val emailAddress = HomeActivityArgs.fromBundle(intent.extras!!).emailAddress
        val password = HomeActivityArgs.fromBundle(intent.extras!!).password
        Log.d("HomeActivity", "Email:$emailAddress")
        Log.d("HomeActivity", "Password:$password")
        // 保存邮箱和密码到viewModel里面
        viewModel.myUserInfo.EmailAddress = emailAddress
        viewModel.myUserInfo.Password = password
        // 获取用户资料并保存在viewModel里面
        viewModel.sdkGetMyUserInfo()

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mainNavigation, R.id.postsNavigation, R.id.messageNavigation, R.id.userInfoNavigation)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        val mesNum = viewModel.responseGetMessage.value?.Data?.size ?: 0
        val timer = timer(initialDelay = 0, period = 30 * 1000L) {
            // 在这里执行确认消息的操作
            // 您可以调用相应的 ViewModel 方法来获取消息并进行处理
            var currentNum = viewModel.responseGetMessage.value?.Data?.size ?: 0 // 进行空值检查
            if (currentNum != mesNum) {
//                csNotificationChannel()
                // 执行您想要执行的操作，例如通知等
            }
        }


        /** 旧的底部导航
        // 设置传入子fragment的bundle信息
        val bundle = Bundle()
        bundle.putString("EmailAddress", emailAddress)
        bundle.putString("Password", password)
        bundle.putString("From", "HomeActivity")
        // 底部导航
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(this,R.id.nav_host_fragment_activity_home)
        navView.setOnItemSelectedListener{
        when (it.itemId) {
        R.id.mainNavigation -> {
        navController.navigate(R.id.mainNavigation, bundle)
        return@setOnItemSelectedListener true
        }
        R.id.postsNavigation -> {
        navController.navigate(R.id.postsNavigation, bundle)
        return@setOnItemSelectedListener true
        }
        R.id.messageNavigation -> {
        navController.navigate(R.id.messageNavigation, bundle)
        return@setOnItemSelectedListener true
        }
        R.id.userInfoNavigation -> {
        navController.navigate(R.id.userInfoNavigation, bundle)
        return@setOnItemSelectedListener true
        }
        else -> {
        navController.navigate(R.id.mainNavigation, bundle)
        return@setOnItemSelectedListener true
        }
        }
        }
        **/



    }

//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun csNotificationChannel() {
//        val channelId = "Channel_new_Comment"
//        val channelName = "New message received"
//        val channelDescription = "Channel Description"
//        val importance = NotificationManager.IMPORTANCE_LOW
//        val channel = NotificationChannel(channelId, channelName, importance).apply {
//            description = channelDescription
//        }
//
//        // 获取 NotificationManager 的实例，并将通道添加到通知管理器中
//        val notificationManager = NotificationManagerCompat.from(this)
//        notificationManager.createNotificationChannel(channel)
//
//        val currentIntent = createPendingIntent(1.toString())
//
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(androidx.viewpager.R.drawable.notification_bg_normal)
//            .setContentTitle("Important")
//            .setContentText("New message received")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(currentIntent)
//            .setAutoCancel(true)
//
//        // 使用通知构建器创建通知
//        val notification = notificationBuilder.build()
//
//        // 使用 notify() 方法发送通知
//        val notificationId = 1
//        notificationManager.notify(notificationId, notification)
//    }
//
//    // 创建 PendingIntent，用于点击通知时跳转到帖子 Fragment
//    private fun createPendingIntent(pid: String): PendingIntent {
//        val intent = Intent(this, Message::class.java)
//        intent.putExtra("pid", pid) // 将 pid 作为额外数据添加到 Intent 中
//        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//    }




}