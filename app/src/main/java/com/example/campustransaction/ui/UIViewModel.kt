package com.example.campustransaction.ui

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campustransaction.api.MongodbApi
import com.example.campustransaction.api.RequestMessage
import com.example.campustransaction.api.RequestPost
import com.example.campustransaction.api.RequestUserInfo
import com.example.campustransaction.api.ResponseBasic
import com.example.campustransaction.api.ResponseBasicMessage
import com.example.campustransaction.api.ResponseBasicPost
import com.example.campustransaction.api.ResponseMessages
import com.example.campustransaction.api.ResponsePosts
import com.example.campustransaction.api.ResponseUserInfo
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class UIViewModel : ViewModel() {
    // 调用photoFragment之后图片存储的位置
    var photoUri = MutableLiveData<Uri?>(null)

    // 用户总信息
    var headPortraitUri:Uri? = null
    val myUserInfo = RequestUserInfo("")

    // 新帖子
    var postPhotoUri1:Uri? = null
    var postPhotoUri2:Uri? = null
    var postPhotoUri3:Uri? = null
    var postPhotoUri4:Uri? = null
    var myNewPost: RequestPost? = null

    // User post
    var postOwner: String? = null
    var postOwnerHeadPortraitUri:Uri? = null

    // Other personal center
    var otherPersonal: String? = null

    // post detail
    var postDetail: ResponseBasicPost? = null
    var postOwnerHeadPortraitUri2:Uri? = null

    // message 发送消息
    var newMessage: RequestMessage? = null

    // message detail
    var messageDetail: ResponseBasicMessage? = null

    // banner
    var bannerIndex: Int = 0

    //SDK -> API----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // 设置用户信息
    private val _responseCreateAccount = MutableLiveData<ResponseBasic>()
    val responseCreateAccount: LiveData<ResponseBasic>
        get() = _responseCreateAccount
    fun sdkCreateAccount() {
        viewModelScope.launch {
            try {
                _responseCreateAccount.value = MongodbApi.retrofitService.apiCreateAccount(myUserInfo)
                Log.d("sdkCreateAccount","Success")
            } catch (e: Exception) {
                _responseCreateAccount.value = ResponseBasic(false,"Failure: ${e.message}")
                Log.d("sdkCreateAccount","Failure: ${e.message}")
            }
        }
    }

    // 获取本用户信息
    private val _responseGetMyUserInfo = MutableLiveData<ResponseBasic>()
    val responseGetMyUserInfo: LiveData<ResponseBasic>
        get() = _responseGetMyUserInfo
    fun sdkGetMyUserInfo() {
        viewModelScope.launch {
            try {
                val tempResponseGetUserInfo = MongodbApi.retrofitService.apiGetUserInfo(myUserInfo.EmailAddress)
                if(tempResponseGetUserInfo.Success){
                    myUserInfo.StudentID = tempResponseGetUserInfo.StudentID
                    myUserInfo.FirstName = tempResponseGetUserInfo.FirstName
                    myUserInfo.LastName = tempResponseGetUserInfo.LastName
                    myUserInfo.NickName = tempResponseGetUserInfo.NickName
                    myUserInfo.Birthday = tempResponseGetUserInfo.Birthday
                    myUserInfo.Gender = tempResponseGetUserInfo.Gender
                    myUserInfo.Profile = tempResponseGetUserInfo.Profile
                    myUserInfo.Region = tempResponseGetUserInfo.Region
                    myUserInfo.School = tempResponseGetUserInfo.School
                    myUserInfo.PhoneNumber = tempResponseGetUserInfo.PhoneNumber
                    myUserInfo.HeadPortrait = tempResponseGetUserInfo.HeadPortrait
                }
                _responseGetMyUserInfo.value = ResponseBasic(tempResponseGetUserInfo.Success,tempResponseGetUserInfo.Error)
                Log.d("sdkGetUserInfo","Success")
            } catch (e: Exception) {
                _responseGetMyUserInfo.value = ResponseBasic(false,"Failure: ${e.message}")
                Log.d("sdkGetUserInfo","Failure: ${e.message}")
            }
        }
    }

    // 获取用户信息
    private val _responseGetUserInfo = MutableLiveData<ResponseUserInfo>()
    val responseGetUserInfo: LiveData<ResponseUserInfo>
        get() = _responseGetUserInfo
    fun sdkGetUserInfo(emailAddress: String) {
        viewModelScope.launch {
            try {
                _responseGetUserInfo.value = MongodbApi.retrofitService.apiGetUserInfo(emailAddress)
                Log.d("sdkGetUserInfo","Success")
            } catch (e: Exception) {
                _responseGetUserInfo.value = ResponseUserInfo(Success = false, Error = "Failure: ${e.message}", EmailAddress = emailAddress)
                Log.d("sdkGetUserInfo","Failure: ${e.message}")
            }
        }
    }

    // 上传新的帖子
    private val _responseNewPost = MutableLiveData<ResponseBasic>()
    val responseNewPost: LiveData<ResponseBasic>
        get() = _responseNewPost
    fun sdkNewPost(){
        viewModelScope.launch {
            try {
                // Log.d("sdkNewPost", myNewPost.toString())
                _responseNewPost.value = myNewPost?.let { MongodbApi.retrofitService.apiNewPost(it) }
                if(_responseNewPost.value?.Success == true){
                    Log.d("sdkNewPost","Success")
                }
            } catch (e: Exception) {
                _responseNewPost.value = ResponseBasic(false, "Failure: ${e.message}")
                Log.d("sdkNewPost", "Failure: ${e.message}")
            }
        }
    }

    // 获得某人的帖子
    private val _responseUserPosts = MutableLiveData<ResponsePosts>()
    val responseUserPosts: LiveData<ResponsePosts>
        get() = _responseUserPosts
    fun sdkUserPosts() {
        viewModelScope.launch {
            try {
                _responseUserPosts.value = postOwner?.let { MongodbApi.retrofitService.apiUserPosts(it) }
                if(_responseUserPosts.value?.Success == true){
                    Log.d("sdkUserPosts","Success")
                }
            } catch (e: Exception) {
                _responseUserPosts.value = ResponsePosts(Success = false, Error = "Failure: ${e.message}")
                Log.d("sdkUserPosts", "Failure: ${e.message}" + " postOwner: $postOwner")
            }
        }
    }

    // 添加评论
    private val _responsePostComment = MutableLiveData<ResponseBasic>()
    val responsePostComment: LiveData<ResponseBasic>
        get() = _responsePostComment
    fun sdkPostComment(PID: String, comment: String){
        viewModelScope.launch {
            try {
                _responsePostComment.value = MongodbApi.retrofitService.apiPostComment(myUserInfo.EmailAddress,PID,comment)
                Log.d("sdkPostComment","Success")
            } catch (e: Exception) {
                _responsePostComment.value = ResponseBasic(false,"Failure: ${e.message}")
                Log.d("sdkPostComment","Failure: ${e.message}")
            }
        }
    }

    // 主页获取评论
    private val _responseGetPosts = MutableLiveData<ResponsePosts>()
    val responseGetPosts: LiveData<ResponsePosts>
        get() = _responseGetPosts
    fun sdkGetPosts(Keyword:String? = null) {
        viewModelScope.launch {
            try {
                _responseGetPosts.value = MongodbApi.retrofitService.apiGetPosts(myUserInfo.EmailAddress, Keyword)
                Log.d("sdkGetPosts","Success")
            } catch (e: Exception) {
                _responseGetPosts.value = ResponsePosts(Success = false, Error = "Failure: ${e.message}")
                Log.d("sdkGetPosts", "Failure: ${e.message}")
            }
        }
    }

    // 删除本人的帖子
    private val _responseDeletePost = MutableLiveData<ResponseBasic>()
    val responseDeletePost: LiveData<ResponseBasic>
        get() = _responseDeletePost
    fun sdkDeletePost(PID: String){
        viewModelScope.launch {
            try {
                _responseDeletePost.value = MongodbApi.retrofitService.apiDeletePost(myUserInfo.EmailAddress, myUserInfo.Password, PID)
                Log.d("sdkDeletePost","Success")
            } catch (e: Exception) {
                _responseDeletePost.value = ResponseBasic(Success = false, Error = "Failure: ${e.message}")
                Log.d("sdkDeletePost", "Failure: ${e.message}")
            }
        }
    }

    // 发送消息
    private val _responseSendMessage = MutableLiveData<ResponseBasic>()
    val responseSendMessage: LiveData<ResponseBasic>
        get() = _responseSendMessage
    fun sdkSendMessage(){
        viewModelScope.launch {
            try {
                _responseSendMessage.value = newMessage?.let { MongodbApi.retrofitService.apiSendMessage(it) }
                Log.d("sdkSendMessage","Success")
            } catch (e: Exception) {
                _responseSendMessage.value = ResponseBasic(Success = false, Error = "Failure: ${e.message}")
                Log.d("sdkSendMessage", "Failure: ${e.message}")
            }
        }
    }

    // 获取消息列表
    private val _responseGetMessage = MutableLiveData<ResponseMessages>()
    val responseGetMessage: LiveData<ResponseMessages>
        get() = _responseGetMessage
    fun sdkGetMessage(Sender:String? = null){
        viewModelScope.launch {
            try {
                _responseGetMessage.value = MongodbApi.retrofitService.apiGetMessage(myUserInfo.EmailAddress, myUserInfo.Password, Sender)
                Log.d("sdkGetMessage","Success")
            } catch (e: Exception) {
                _responseGetMessage.value = ResponseMessages(Success = false, Error = "Failure: ${e.message}")
                Log.d("sdkGetMessage", "Failure: ${e.message}")
            }
        }
    }

    // 主页点击帖子
    private val _responseClickPost = MutableLiveData<ResponseBasic>()
    val responseClickPost: LiveData<ResponseBasic>
        get() = _responseClickPost
    fun sdkClickPost(PID:String) {
        viewModelScope.launch {
            try {
                _responseClickPost.value = MongodbApi.retrofitService.apiClickPost(myUserInfo.EmailAddress, PID)
                Log.d("sdkClickPost","Success")
            } catch (e: Exception) {
                _responseClickPost.value = ResponseBasic(Success = false, Error = "Failure: ${e.message}")
                Log.d("sdkClickPost", "Failure: ${e.message}")
            }
        }
    }

    // 获取消息浏览历史
    private val _responsePostHistory = MutableLiveData<ResponsePosts>()
    val responsePostHistory: LiveData<ResponsePosts>
        get() = _responsePostHistory
    fun sdkPostHistory(){
        viewModelScope.launch {
            try {
                _responsePostHistory.value = MongodbApi.retrofitService.apiPostHistory(myUserInfo.EmailAddress)
                Log.d("sdkPostHistory","Success")
            } catch (e: Exception) {
                _responsePostHistory.value = ResponsePosts(Success = false, Error = "Failure: ${e.message}")
                Log.d("sdkPostHistory", "Failure: ${e.message}")
            }
        }
    }

    //SDK -> API----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // Uri图片转base64 string
    fun getBase64ForUri(context: Context, uri: Uri): String {
        var encodedBase64: String? = ""
        try {
//          val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            val bytes = compressImage(context, uri)  // 压缩图片
            encodedBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return encodedBase64!!
    }

    // base64图片转本地
    fun setBase64ToUri(context: Context, string: String){
        try {
            val decodedBitArray = Base64.decode(string, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedBitArray, 0, decodedBitArray.size)
            val file = saveFile(context,decodedBitmap)
            photoUri.value = file.toUri()
            Log.d("setBase64ToUri","Uri="+photoUri.value)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("setBase64ToUri","${e.message}")
        }
    }

    // 保存imagePath里的图片到项目下固定的文件路径
    fun saveFile(context: Context, imagePath:String): File {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        return saveFile(context,bitmap)
    }

    // 保存bitmap到项目下固定的的文件路径
    private fun saveFile(context: Context, bitmap: Bitmap): File {
        val filepath = context.filesDir?.absolutePath

        val fileParent = filepath?.let { File(it) }
        if (fileParent != null) {
            if(!fileParent.exists()){
                fileParent.mkdirs()
            }
        }

        filepath?.let { Log.d("saveFile1", "filepath=$it") }
        val file = File("$filepath/"+System.currentTimeMillis() + ".jpg")
        if (file.exists()){
            file.delete()
        }

        try {
            val bos = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
        }catch (e: IOException){
            Log.d("saveFile1","${e.message}")
        }

        Log.d("saveFile2","filename=${file.absolutePath}")
        return file
    }

    // 压缩图片
    private fun compressImageToBytes(image: Bitmap): ByteArray? {
        val bos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        var options = 90
        val length = bos.toByteArray().size / 1024

        if (length > 5000) {
            bos.reset()
            image.compress(Bitmap.CompressFormat.JPEG, 10, bos)
        } else if (length > 4000) {
            bos.reset()
            image.compress(Bitmap.CompressFormat.JPEG, 20, bos)
        } else if (length > 3000) {
            bos.reset()
            image.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        } else if (length > 2000) {
            bos.reset()
            image.compress(Bitmap.CompressFormat.JPEG, 70, bos)
        }

        while (bos.toByteArray().size / 1024 > 1024) {
            bos.reset()
            image.compress(Bitmap.CompressFormat.JPEG, options, bos)
            options -= 10
        }

        return bos.toByteArray()
    }

    private fun compressImage(context: Context, uri: Uri): ByteArray? {

        // 将图片转换为 bitmap
        val bitmapImg = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))

        val bos = ByteArrayOutputStream()
        bitmapImg.compress(Bitmap.CompressFormat.JPEG, 100, bos)

        if (bos.toByteArray().size / 1024 > 1024) {
            bos.reset()
            bitmapImg.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        }
        val newOpts = BitmapFactory.Options()
        newOpts.inJustDecodeBounds = true
        newOpts.inJustDecodeBounds = false

        val w = newOpts.outWidth
        val h = newOpts.outHeight
        val hh = 800f
        val ww = 480f
        var be = 1

        if (w > h && w > ww) {
            be = (newOpts.outWidth / ww).toInt()
        } else if (w < h && h > hh) {
            be = (newOpts.outHeight / hh).toInt()
        }

        if (be <= 0) be = 1
        newOpts.inSampleSize = be

        val isBm = ByteArrayInputStream(bos.toByteArray())
        val bitmap = BitmapFactory.decodeStream(isBm, null, newOpts)

        return compressImageToBytes(bitmap!!)
    }

    // 权限申请
    private val permissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION)

    fun onRequestPermissions(context: Context){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            var toRequest = false
            for (p in permissions){
                if(context.let { ContextCompat.checkSelfPermission(it, p) } != PackageManager.PERMISSION_GRANTED){
                    toRequest=true
                }
            }
            if(toRequest){
                ActivityCompat.requestPermissions(context as Activity, permissions,2)
            }
        }
    }

}

