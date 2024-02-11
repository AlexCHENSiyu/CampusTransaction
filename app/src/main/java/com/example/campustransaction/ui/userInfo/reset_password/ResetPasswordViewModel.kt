package com.example.campustransaction.ui.userInfo.reset_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campustransaction.api.MongodbApi
import com.example.campustransaction.api.ResponseBasic
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {
    private val _responseSendEmail = MutableLiveData<ResponseBasic>()
    val responseSendEmail: LiveData<ResponseBasic>
        get() = _responseSendEmail

    fun sdkSendEmail(EmailAddress: String) {
        viewModelScope.launch {
            try {
                val responseEmailNoExist = MongodbApi.retrofitService.apiEmailNoExist(EmailAddress)
                if (responseEmailNoExist.Success){
                    // 此邮件地址未创建账户
                    _responseSendEmail.value = ResponseBasic(false,"This email did not have an account!")
                }else{
                    // 此邮件地址已经创建账户
                    _responseSendEmail.value = MongodbApi.retrofitService.apiEmailValidation(EmailAddress)
                }
            } catch (e: Exception) {
                _responseSendEmail.value = ResponseBasic(false,"Failure: ${e.message}")
            }
        }
    }

    private val _responseEmailValidation = MutableLiveData<ResponseBasic>()
    val responseEmailValidation: LiveData<ResponseBasic>
        get() = _responseEmailValidation

    fun sdkEmailValidation(EmailAddress: String, InputCode: Int?) {
        viewModelScope.launch {
            try {
                if(InputCode == null) {
                    _responseEmailValidation.value = MongodbApi.retrofitService.apiEmailValidation(EmailAddress, 1)
                }else{
                    _responseEmailValidation.value = MongodbApi.retrofitService.apiEmailValidation(EmailAddress, InputCode)
                }
            } catch (e: Exception) {
                _responseEmailValidation.value = ResponseBasic(false,"Failure: ${e.message}")
            }
        }
    }

    private val _responseResetPassword = MutableLiveData<ResponseBasic>()
    val responseResetPassword: LiveData<ResponseBasic>
        get() = _responseResetPassword

    fun sdkResetPassword(EmailAddress: String, newPassword: String) {
        viewModelScope.launch {
            try {
                _responseResetPassword.value = MongodbApi.retrofitService.apiResetPassword(EmailAddress, NewPassword = newPassword)
            } catch (e: Exception) {
                _responseResetPassword.value = ResponseBasic(false,"Failure: ${e.message}")
            }
        }
    }

}