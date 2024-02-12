package com.example.campustransaction.ui.userInfo.reset_password

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.welcome.create_account.TimerUnit
import com.example.campustransaction.databinding.FragmentResetPasswordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ResetPasswordFragment : Fragment() {
    private val viewModel: ResetPasswordViewModel by lazy {
        ViewModelProvider(this)[ResetPasswordViewModel::class.java]
    }
    private var timer: TimerUnit? = null

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentResetPasswordBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.responseSendEmail.observe(viewLifecycleOwner){
            if (viewModel.responseSendEmail.value?.Success!= true) {
                binding.titleSendError2.visibility = View.VISIBLE
            } else {
                timer?.startTime()
                binding.titleSendError2.visibility = View.INVISIBLE
                binding.buttonVerify2.isEnabled = true
                binding.buttonVerify2.isClickable = true
            }
        }

        viewModel.responseEmailValidation.observe(viewLifecycleOwner) {
            if (viewModel.responseEmailValidation.value?.Success!=true) {
                binding.titleVerifyError2.visibility = View.VISIBLE
            } else {
                binding.buttonVerify2.text = "Verified"
                binding.buttonVerify2.setBackgroundColor(R.color.colorRed)
                binding.titleVerifyError2.visibility = View.INVISIBLE
                binding.inputNewPassword.visibility = View.VISIBLE
                binding.inputNewPasswordAgain.visibility = View.VISIBLE
                binding.buttonResetPasswordConfirm.visibility = View.VISIBLE
            }
        }

        viewModel.responseResetPassword.observe(viewLifecycleOwner){
            if (viewModel.responseResetPassword.value?.Success != true){
                binding.titleResetPasswordError.visibility = View.VISIBLE
            }else{
                //binding.buttonResetPasswordConfirm.text = "Reset success"
                //binding.buttonResetPasswordConfirm.setBackgroundColor(R.color.colorRed)
                binding.titleResetPasswordError.visibility = View.INVISIBLE
                showSuccess()
            }
        }

        binding.buttonSendValidationCode2.setOnClickListener {
            // 绑定倒计时
            if (timer == null) {
                timer = TimerUnit(binding.buttonSendValidationCode2)
            }
            // 检查此邮件地址账户是否创建,若无，则发送验证码
            viewModel.sdkSendEmail(binding.inputEmailAddress3.text.toString())
        }

        // 检查验证码
        binding.buttonVerify2.setOnClickListener {
            viewModel.sdkEmailValidation(binding.inputEmailAddress3.text.toString(),
                binding.inputValidCode2.text?.toString()?.toIntOrNull())
        }

        binding.buttonResetPasswordConfirm.setOnClickListener {
            if (binding.inputNewPassword.text.toString() != binding.inputNewPasswordAgain.text.toString()) {
                binding.titleResetPasswordError.visibility = View.VISIBLE
                binding.titleResetPasswordError.text = "Password mismatch!"
            } else {
                viewModel.sdkResetPassword( binding.inputEmailAddress3.text.toString(),
                    binding.inputNewPassword.text.toString() )
            }
        }

        return binding.root
    }

    private fun showSuccess() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Congratulations!")
            .setMessage("Reset Password Success!")
            .setCancelable(false)
            //.setNegativeButton("Exit") { }
            .setPositiveButton("Back") { _, _ ->  view?.findNavController()?.navigate(R.id.action_resetPasswordFragment_to_loginFragment) }
            .show()
    }
}