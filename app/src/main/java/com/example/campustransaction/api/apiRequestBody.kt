package com.example.campustransaction.api

import com.google.gson.annotations.SerializedName

data class RequestUserInfo (
    @SerializedName("EmailAddress") var EmailAddress: String,
    @SerializedName("Password") var Password: String? = null,
    @SerializedName("StudentID") var StudentID: Int? = null,
    @SerializedName("FirstName") var FirstName: String? = null,
    @SerializedName("LastName") var LastName: String? = null,
    @SerializedName("NickName") var NickName: String? = null,
    @SerializedName("Birthday") var Birthday: String? = null,
    @SerializedName("Gender") var Gender: String? = null,
    @SerializedName("Profile") var Profile: String? = null,
    @SerializedName("Region") var Region: String? = "Hong Kong",
    @SerializedName("School") var School: String? = null,
    @SerializedName("PhoneNumber") var PhoneNumber: Int? = null,
    @SerializedName("HeadPortrait") var HeadPortrait: String? = null,
    @SerializedName("FavoriteFields") var FavoriteFields: MutableList<String>? = null
)

data class RequestMessage(
    @SerializedName("Sender") var Sender: String,
    @SerializedName("Receiver") var Receiver: String? = null,
    @SerializedName("Content") var Content: RequestContent
)

data class RequestContent(
    @SerializedName("Text") var Text: String?= null,
    @SerializedName("Image") var Image: String?= null
)

data class RequestPost(
    @SerializedName("PostOwner") val PostOwner: String,
    @SerializedName("Title") var Title: String,
    @SerializedName("Text") var Text: String,
    @SerializedName("Price") var Price: Double? = null,
    @SerializedName("Auction") var Auction: Boolean = false,
    @SerializedName("LostFound") var LostFound: Boolean = false,
    @SerializedName("Fields") var Fields: MutableList<String>? = null,
    @SerializedName("Images") var Images: MutableList<String>? = null
)
