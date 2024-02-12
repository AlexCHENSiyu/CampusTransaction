package com.example.campustransaction.welcome.create_account

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentCreateAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreateAccountFragment : Fragment() {
    private val viewModel: CreateAccountViewModel by lazy {
        ViewModelProvider(this).get<CreateAccountViewModel>(CreateAccountViewModel::class.java)
    }
    private var timer: TimerUnit? = null

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentCreateAccountBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.responseSendEmail.observe(viewLifecycleOwner){
            if (viewModel.responseSendEmail.value?.Success!= true) {
                binding.titleSendError.visibility = View.VISIBLE
            } else {
                timer?.startTime()
                binding.titleSendError.visibility = View.INVISIBLE
                binding.buttonVerify.isEnabled = true
                binding.buttonVerify.isClickable = true
            }
        }

        viewModel.responseEmailValidation.observe(viewLifecycleOwner) {
            if (viewModel.responseEmailValidation.value?.Success!=true) {
                binding.titleVerifyError.visibility = View.VISIBLE
            } else {
                binding.buttonVerify.text = "Verified"
                binding.buttonVerify.setBackgroundColor(R.color.colorRed)
                binding.titleVerifyError.visibility = View.INVISIBLE
                binding.inputPassword1.visibility = View.VISIBLE
                binding.inputPassword1Again.visibility = View.VISIBLE
                binding.buttonConfirm.visibility = View.VISIBLE
            }
        }

        viewModel.responseSetPassword.observe(viewLifecycleOwner){
            if (viewModel.responseSetPassword.value?.Success!=true) {
                binding.titleSetPasswordError.visibility = View.VISIBLE
            } else {
                binding.buttonConfirm.text = "Password set"
                binding.buttonConfirm.setBackgroundColor(R.color.colorRed)
                binding.titleSetPasswordError.visibility = View.INVISIBLE
                showSuccess()
            }
        }

        binding.buttonSendValidationCode.setOnClickListener {
            // 绑定倒计时
            if (timer == null) {
                timer = TimerUnit(binding.buttonSendValidationCode)
            }
            // 检查此邮件地址账户是否创建,若无，则发送验证码
            viewModel.sdkSendEmail(binding.inputEmailAddress1.text.toString())
        }

        // 检查验证码
        binding.buttonVerify.setOnClickListener {
            viewModel.sdkEmailValidation(binding.inputEmailAddress1.text.toString(),
                binding.inputValidCode.text?.toString()?.toIntOrNull())
        }

        // 设置密码
        binding.buttonConfirm.setOnClickListener {
            // 先检查两次的密码是否相同
            if (binding.inputPassword1.text.toString() != binding.inputPassword1Again.text.toString()) {
                binding.titleSetPasswordError.visibility = View.VISIBLE
                binding.titleSetPasswordError.text = "Password mismatch!"
            } else {
                viewModel.sdkSetPassword(binding.inputEmailAddress1.text.toString(),
                    binding.inputPassword1.text.toString() )
            }
        }

        return binding.root
    }

    private fun showSuccess() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Congratulations!")
            .setMessage("Set Password Success!")
            .setCancelable(false)
            //.setNegativeButton("Exit") { }
            .setPositiveButton("Next") { _, _ -> view?.findNavController()?.navigate(R.id.action_createAccountFragment_to_loginFragment) }
            .show()
    }

}