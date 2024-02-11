package com.example.campustransaction.welcome.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campustransaction.api.MongodbApi
import com.example.campustransaction.api.ResponseBasic
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _responseLogin = MutableLiveData<ResponseBasic>()
    val responseLogin: LiveData<ResponseBasic>
        get() = _responseLogin

    fun sdkLogin(EmailAddress: String, Password: String) {
        viewModelScope.launch {
            try {
                _responseLogin.value = MongodbApi.retrofitService.apiLogin(EmailAddress, Password)
            } catch (e: Exception) {
                _responseLogin.value = ResponseBasic(false,"Failure: ${e.message}")
            }
        }
    }
}

