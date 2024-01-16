package com.example.campustransaction.ui.message.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.campustransaction.R
import com.example.campustransaction.api.ResponseBasicMessage
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChatAdapter (private val context: Context, private val ChatList: List<ResponseBasicMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.chat_head_portrait)
        val chatNickName: TextView = view.findViewById(R.id.chat_nickname)
        val chatText: TextView = view.findViewById(R.id.chat_text)
        val chatTime: TextView = view.findViewById(R.id.chat_time)
    }

    interface OnItemClickListener{
        fun onItemClick(view: View, position: Int)
    }

    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(adapterLayout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = ChatList[position]
        val uri = item.HeadPortrait?.let { setBase64ToUri(context, it) }
        holder.imageView.setImageURI(uri)
        holder.chatNickName.text = item.NickName
        holder.chatText.text = item.Contents?.last()?.Text

        val createTime = item.Contents?.last()?.CreateTime
        val formatForm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatTo = DateTimeFormatter.ofPattern("MM-dd HH:mm")
        val localDateTime = LocalDateTime.parse(createTime, formatForm)
        val chatTime = localDateTime.format(formatTo)

        holder.chatTime.text = chatTime

        holder.imageView.setOnClickListener {
            onItemClickListener.onItemClick(holder.itemView, position)
        }
        holder.chatNickName.setOnClickListener {
            onItemClickListener.onItemClick(holder.itemView, position)
        }
        holder.chatText.setOnClickListener {
            onItemClickListener.onItemClick(holder.itemView, position)
        }
        holder.chatTime.setOnClickListener {
            onItemClickListener.onItemClick(holder.itemView, position)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = ChatList.size

    // 转换base64为本地图片
    private fun setBase64ToUri(context: Context, string: String): Uri?{
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


}