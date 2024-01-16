package com.example.campustransaction.reset_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campustransaction.api.MongodbApi
import com.example.campustransaction.api.ResponseBasic
import kotlinx.coroutines.launch

class ResetPassword2ViewModel : ViewModel() {
    private val _responseResetPassword = MutableLiveData<ResponseBasic>()
    val responseResetPassword: LiveData<ResponseBasic>
        get() = _responseResetPassword

    fun sdkResetPassword(EmailAddress: String, Password: String, newPassword: String) {
        viewModelScope.launch {
            try {
                _responseResetPassword.value = MongodbApi.retrofitService.apiResetPassword(EmailAddress,Password,newPassword)
            } catch (e: Exception) {
                _responseResetPassword.value = ResponseBasic(false,"Failure: ${e.message}")
            }
        }
    }
}