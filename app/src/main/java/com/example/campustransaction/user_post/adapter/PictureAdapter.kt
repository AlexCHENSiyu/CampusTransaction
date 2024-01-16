package com.example.campustransaction.user_post.adapter

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
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.campustransaction.R
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PictureAdapter (private val context: Context, private val pictureList: List<String>) : RecyclerView.Adapter<PictureAdapter.PictureViewHolder>() {
    class PictureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.post_list_picture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_picture, parent, false)
        return PictureViewHolder(adapterLayout)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val item = pictureList[position]
        val uri = setBase64ToUri(context, item)
        holder.imageView.setImageURI(uri)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = pictureList.size

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