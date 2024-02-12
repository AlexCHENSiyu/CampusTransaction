package com.example.campustransaction.ui.posts.user_post.adapter

import android.annotation.SuppressLint
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
import com.example.campustransaction.api.ResponseBasicPost
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PostAdapter (private val context: Context, private val postList: List<ResponseBasicPost>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_post)
        val titlePostOwner: TextView = view.findViewById(R.id.title_post_price)
        val titleTitle: TextView = view.findViewById(R.id.title_title)
    }

    interface OnItemClickListener{
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }

    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(adapterLayout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = postList[position]
        holder.titleTitle.text = item.Title
        holder.titlePostOwner.text = "$ ${item.Price.toString()}"
        if(item.Images != null){
            val uri = setBase64ToUri(context, item.Images[0])
            holder.imageView.setImageURI(uri)
        }

        holder.imageView.setOnClickListener {
            onItemClickListener.onItemClick(holder.itemView, position)
        }
        holder.titlePostOwner.setOnClickListener {
            onItemClickListener.onItemClick(holder.itemView, position)
        }
        holder.titleTitle.setOnClickListener {
            onItemClickListener.onItemClick(holder.itemView, position)
        }

        holder.imageView.setOnLongClickListener {
            onItemClickListener.onItemLongClick(holder.itemView, position)
            return@setOnLongClickListener true
        }
        holder.titlePostOwner.setOnLongClickListener {
            onItemClickListener.onItemLongClick(holder.itemView, position)
            return@setOnLongClickListener true
        }
        holder.titleTitle.setOnLongClickListener {
            onItemClickListener.onItemLongClick(holder.itemView, position)
            return@setOnLongClickListener true
        }

    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = postList.size

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