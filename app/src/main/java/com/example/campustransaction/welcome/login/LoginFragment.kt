package com.example.campustransaction.welcome.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.campustransaction.HomeActivity
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentLoginBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

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