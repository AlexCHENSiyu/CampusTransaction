package com.example.campustransaction.ui.message.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.campustransaction.api.ResponseBasicMessage
import com.example.campustransaction.ui.message.MyDrawIndicator
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MessageAdapter (private val context: Context, private val MessageList: ResponseBasicMessage) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageHeadPortrait: ImageView = view.findViewById(com.example.campustransaction.R.id.message_head_portrait)
        val bubbleText: pw.xiaohaozi.bubbleview.BubbleView = view.findViewById(com.example.campustransaction.R.id.bubble_text)
        val bubbleImage: pw.xiaohaozi.bubbleview.BubbleView = view.findViewById(com.example.campustransaction.R.id.bubble_image)
        val messageText: TextView = view.findViewById(com.example.campustransaction.R.id.message_text)
        val messageImage: ImageView = view.findViewById(com.example.campustransaction.R.id.message_image)

    }

    private val TYPE_LEFT = 0       // false
    private val TYPE_RIGHT = 1      // true
    override fun getItemViewType(position: Int): Int {
        val item = MessageList.Contents?.get(position)
        return if (item!!.Direction) {
            TYPE_RIGHT
        } else TYPE_LEFT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // create a new view
        return when (viewType) {
            TYPE_LEFT -> {
                val adapterLayout = LayoutInflater.from(parent.context)
                    .inflate(com.example.campustransaction.R.layout.item_message_left, parent, false)
                MessageViewHolder(adapterLayout)
            }
            else -> {
                val adapterLayout = LayoutInflater.from(parent.context)
                    .inflate(com.example.campustransaction.R.layout.item_message_right, parent, false)
                MessageViewHolder(adapterLayout)
            }
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = MessageList.Contents?.get(position)
        if (item != null) {
            if(!item.Direction){
                // false for left
                val uri = MessageList.HeadPortrait?.let { setBase64ToUri(context, it) }
                holder.messageHeadPortrait.setImageURI(uri)
            }else{
                // true for right
                holder.messageHeadPortrait.setImageURI(MessageList.myHeadPortrait)
            }

            if(item.Text != null){
                holder.messageText.text = item.Text
                holder.bubbleText.setDrawIndicator(MyDrawIndicator())
                holder.bubbleText.visibility = View.VISIBLE
                holder.bubbleImage.visibility = View.INVISIBLE
            }else{
                val uri = item.Image?.let { setBase64ToUri(context, it) }
                holder.messageImage.setImageURI(uri)
                holder.bubbleImage.setDrawIndicator(MyDrawIndicator())
                holder.bubbleText.visibility = View.INVISIBLE
                holder.bubbleImage.visibility = View.VISIBLE
            }

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = MessageList.Contents?.size?:0

}

// 转换base64为本地图片
fun setBase64ToUri(context: Context, string: String): Uri?{
    return try {
        val decodedBitArray = Base64.decode(string, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBitArray, 0, decodedBitArray.size)
        val file = saveFile(context, decodedBitmap)
        Log.d("setBase64ToUri","Uri="+file.toUri())
        file.toUri()
    } catch (e: IOException) {
        e.printStackTrace()
        Log.d("setBase64ToUri","${e.message}")
        null
    }
}

// 保存bitmap到项目下固定的的文件路径
fun saveFile(context: Context, bitmap: Bitmap): File {
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
