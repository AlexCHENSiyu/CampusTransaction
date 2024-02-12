package com.example.campustransaction.ui.posts.user_post.adapter

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
import com.example.campustransaction.R
import com.example.campustransaction.api.ResponseComment
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CommentAdapter (private val context: Context, private val CommentList: List<ResponseComment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.comment_head_portrait)
        val textCommenter: TextView = view.findViewById(R.id.comment_commenter)
        val textNickName: TextView = view.findViewById(R.id.comment_nickname)
        val textComment: TextView = view.findViewById(R.id.comment_text)
        val textTime: TextView = view.findViewById(R.id.comment_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(adapterLayout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = CommentList[position]
        val uri = item.HeadPortrait?.let { setBase64ToUri(context, it) }
        holder.imageView.setImageURI(uri)
        holder.textCommenter.text = item.Commenter
        holder.textNickName.text = item.NickName
        holder.textComment.text = item.Text
        holder.textTime.text = item.CreateTime
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = CommentList.size

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