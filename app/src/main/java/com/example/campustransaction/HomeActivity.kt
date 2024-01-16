package com.example.campustransaction

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.campustransaction.databinding.ActivityHomeBinding
import com.example.campustransaction.ui.UIViewModel


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    // 此activity和所有子fragment 共用同一个UIViewModel
    private val viewModel: UIViewModel by viewModels()

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


}