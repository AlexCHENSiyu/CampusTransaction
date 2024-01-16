package com.example.campustransaction.reset_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentResetPassword2Binding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ResetPassword2Fragment : Fragment() {
    private val viewModel: ResetPassword2ViewModel by lazy {
        ViewModelProvider(this)[ResetPassword2ViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentResetPassword2Binding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.responseResetPassword.observe(viewLifecycleOwner){
            if (viewModel.responseResetPassword.value?.Success != true){
                binding.titleResetPassword2Error.visibility = View.VISIBLE
            }else{
                binding.titleResetPassword2Error.visibility = View.INVISIBLE
                showSuccess()
            }
        }

        binding.buttonResetPassword2Confirm.setOnClickListener {
            viewModel.sdkResetPassword( binding.inputEmailAddressForResetPassword2.text.toString(),
                binding.inputOldPasswordForResetPassword2.text.toString(),binding.inputNewPasswordForResetPassword2.text.toString() )
        }
        return binding.root
    }

    private fun showSuccess() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Congratulations!")
            .setMessage("Reset Password Success!")
            .setCancelable(false)
            //.setNegativeButton("Exit") { }
            .setPositiveButton("Back") { _, _ -> view?.findNavController()?.navigate(R.id.action_resetPassword2Fragment_to_userInfoNavigation) }
            .show()
    }

}