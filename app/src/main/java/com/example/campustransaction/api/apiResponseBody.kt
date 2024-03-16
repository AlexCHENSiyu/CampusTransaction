package com.example.campustransaction.api

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class ResponseBasic (
    @SerializedName("Success") val Success: Boolean,
    @SerializedName("Error") val Error: String? = null
)

data class ResponseUserInfo (
    @SerializedName("EmailAddress") val EmailAddress: String,
    @SerializedName("FirstName") val FirstName: String? = null,
    @SerializedName("LastName") val LastName: String? = null,
    @SerializedName("NickName") val NickName: String? = null,
    @SerializedName("StudentID") val StudentID: Int? = null,
    @SerializedName("Birthday") val Birthday: String? = null,
    @SerializedName("Gender") val Gender: String? = null,
    @SerializedName("Profile") val Profile: String? = null,
    @SerializedName("Region") val Region: String? = null,
    @SerializedName("School") val School: String? = null,
    @SerializedName("PhoneNumber") val PhoneNumber: Int? = null,
    @SerializedName("HeadPortrait") val HeadPortrait: String? = null,
    @SerializedName("FavoriteFields") val FavoriteFields: MutableList<String>? = null,
    @SerializedName("Success") val Success: Boolean = false,
    @SerializedName("Error") val Error: String? = null
)

data class ResponseMessages(
    @SerializedName("Data") val Data: MutableList<ResponseBasicMessage>? = null,
    @SerializedName("Success") val Success: Boolean = false,
    @SerializedName("Error") val Error: String? = null
)

data class ResponseBasicMessage(
    @SerializedName("Sender") val Sender: String,
    @SerializedName("NickName") var NickName: String? = null,
    @SerializedName("HeadPortrait") var HeadPortrait: String? = null,
    @SerializedName("myEmailAddress") var myEmailAddress: String? = null,
    @SerializedName("myNickName") var myNickName: String? = null,
    @SerializedName("myHeadPortrait") var myHeadPortrait: Uri? = null,
    @SerializedName("Contents") var Contents: MutableList<ResponseBasicContent>? = null,
)

data class ResponseBasicContent(
    @SerializedName("Text") val Text: String?= null,
    @SerializedName("Image") val Image: String?= null,
    @SerializedName("Direction") val Direction: Boolean = false,
    @SerializedName("CreateTime") val CreateTime: String?= null
)

data class ResponsePosts(
    @SerializedName("Posts") val Posts: MutableList<ResponseBasicPost>? = null,
    @SerializedName("Success") val Success: Boolean = false,
    @SerializedName("Error") val Error: String? = null
)

data class ResponseBasicPost(
    @SerializedName("PID") val PID: String? = null,
    @SerializedName("PostOwner") val PostOwner: String,
    @SerializedName("CreateTime") val CreateTime: String,
    @SerializedName("Title") val Title: String,
    @SerializedName("Text") val Text: String,
    @SerializedName("Price") var Price: Double? = null,
    @SerializedName("Auction") val Auction: Boolean = false,
    @SerializedName("LostFound") val LostFound: Boolean = false,
    @SerializedName("Images") val Images: MutableList<String>? = null,
    @SerializedName("Comments") val Comments: MutableList<ResponseComment>? = null,
    @SerializedName("Latitude") val Latitude: Double? = null,
    @SerializedName("Longitude") val Longitude: Double? = null,
)

data class ResponseComment(
    @SerializedName("Commenter") val Commenter: String,
    @SerializedName("NickName") val NickName: String? = null,
    @SerializedName("HeadPortrait") val HeadPortrait: String? = null,
    @SerializedName("Text") val Text: String,
    @SerializedName("CreateTime") val CreateTime: String? = null
)