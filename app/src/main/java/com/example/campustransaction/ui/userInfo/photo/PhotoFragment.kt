package com.example.campustransaction.ui.userInfo.photo

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.campustransaction.databinding.FragmentPhotoBinding
import com.example.campustransaction.ui.UIViewModel
import java.io.File

class PhotoFragment : Fragment() {
    private val viewModel: UIViewModel by activityViewModels()

    private lateinit var takePictureLaunch: ActivityResultLauncher<Uri>
    private lateinit var getContentLaunch: ActivityResultLauncher<String>
    private lateinit var popBackPhotoLaunch: ActivityResultLauncher<Intent>
    private var cameraUri: Uri? = null

    // 返回监听设置
    private val mBackPressedCallback: OnBackPressedCallback by lazy  {
        object : OnBackPressedCallback(true) {  //enabled 标记位
            override fun handleOnBackPressed() {
                viewModel.photoUri.value = null
                view?.findNavController()?.popBackStack()
                Log.d("PhotoFragment","mBackPressedCallback")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 新的调用相机拍照的写法
        takePictureLaunch = registerForActivityResult(ActivityResultContracts.TakePicture()){
                success -> if (success) { if(cameraUri != null) {viewModel.photoUri.value = cameraUri}; cropPhoto() }
        }

        // 新的调用图库的写法
        getContentLaunch = registerForActivityResult(ActivityResultContracts.GetContent()){
                uri: Uri? -> uri?.let { it1 -> handleImageONKitKat(it1); cropPhoto()}
        }

        // 裁剪图片的回调
        popBackPhotoLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            Toast.makeText(context, "New Photo Selected", Toast.LENGTH_SHORT).show()
            //view?.findNavController()?.navigate(R.id.action_photoFragment_to_personalCenterFragment)
            view?.findNavController()?.popBackStack()
        }

        // 监听返回键
        requireActivity().onBackPressedDispatcher.addCallback(this, mBackPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentPhotoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // 拍照
        binding.camera.setOnClickListener {
            /** 旧的调用相机拍照的写法
            val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.imageUri)
            openCameraIntent.putExtra("photo", "" + viewModel.imageUri)
            startActivity(openCameraIntent)
             **/

            //Log.d("PhotoFragment", "camera")
            val outImage = File(context?.externalCacheDir, "outPut_image.jpg")
            try {
                if(outImage.exists()){
                    outImage.delete()
                }
                outImage.createNewFile()
            }catch (e:Exception){
                e.printStackTrace()
            }
            cameraUri = if(Build.VERSION.SDK_INT>=24){
                context?.let { it1 -> FileProvider.getUriForFile(it1,"com.example.campustransaction.fileProvider", outImage) }
            }else{
                Uri.fromFile(outImage)
            }

            takePictureLaunch.launch(cameraUri)
        }

        // 图库选择
        binding.photo.setOnClickListener {
            //Log.d("PhotoFragment", "photo")

            //旧的图库选择方法
            //val openAlbumIntent = Intent(Intent.ACTION_GET_CONTENT)
            //openAlbumIntent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.imageUri)
            //openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")
            //startActivity(openAlbumIntent)

            // 新的图库选择方法
            getContentLaunch.launch("image/*")
        }

        // 取消
        binding.cancel.setOnClickListener {
            //Log.d("PhotoFragment", "cancel")
            viewModel.photoUri.value = null
            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()
            view?.findNavController()?.popBackStack()
        }

        return binding.root
    }

    private fun handleImageONKitKat(uri:Uri){
        //val uri = data.data
        var imagePath:String? = null
        Log.d("handleImageONKitKat","uri=$uri")

        if(DocumentsContract.isDocumentUri(context,uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            Log.d("handleImageONKitKat","docId=$docId")
            Log.d("handleImageONKitKat","auth=${uri.authority}")
            Log.d("handleImageONKitKat","scheme=${uri.scheme}")

            if("com.android.providers.media.documents" == uri.authority){
                val id = docId.split(":")[1]
                Log.d("handleImageONKitKat","id=$id")
                val selection = "${MediaStore.Images.Media._ID}=$id"
                Log.d("handleImageONKitKat","selection=$selection")
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection)
                Log.d("handleImageONKitKat","imagePath=$imagePath")
            }else if("com.android.providers.downloads.documents"==uri.authority){
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    docId.toLong(),
                )
                Log.d("handleImageONKitKat","contentUri=$contentUri")
                imagePath=getImagePath(contentUri,null)
            }
        }else if ("content".equals(uri.scheme,true)){
            imagePath = getImagePath(uri,null)
        }else if ("file".equals(uri.scheme,true)){
            imagePath = uri.path
        }

        Log.d("handleImageONKitKat","imagePath=$imagePath")
        val file = imagePath?.let { context?.let { it1 -> viewModel.saveFile(it1, it)} }
        if (file != null){
            viewModel.photoUri.value = if(Build.VERSION.SDK_INT>=24){
                context?.let { it1 -> file.let { FileProvider.getUriForFile(it1, requireContext().packageName +".fileProvider", it) } }
            }else{
                Uri.fromFile(file)
            }
            Log.d("handleImageONKitKat","filePath=${file.toUri()}")
            Log.d("handleImageONKitKat","imageUri=${viewModel.photoUri.value}")
        }
    }

    private fun cropPhoto() {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(viewModel.photoUri.value, "image/*")
        context?.grantUriPermission(requireContext().packageName, viewModel.photoUri.value, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context?.grantUriPermission(requireContext().packageName, viewModel.photoUri.value, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        intent.putExtra("crop", "true")// 发送裁剪信号，去掉也能进行裁剪
        intent.putExtra("scale", true)// 设置缩放
        intent.putExtra("scaleUpIfNeeded", true)// 去黑边
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // 上述两个属性控制裁剪框的缩放比例。
        // 当用户用手拉伸裁剪框时候，裁剪框会按照上述比例缩放。
        intent.putExtra("outputX", 200)// 属性控制裁剪完毕，保存的图片的大小格式。
        intent.putExtra("outputY", 200)// 你按照1:1的比例来裁剪的，如果最后成像是800*400，那么按照2:1的样式保存，
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())// 输出裁剪文件的格式
        intent.putExtra("return-data", true)// 是否返回裁剪后图片的Bitmap
        intent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.photoUri.value)// 设置输出路径
        popBackPhotoLaunch.launch(intent)
    }

    @SuppressLint("Range")
    private fun getImagePath(uri: Uri, Selection:String?):String?{
        var path:String? = null
        val cursor = context?.contentResolver?.query(uri, null, Selection, null,null)
        if(cursor!=null){
            //Log.d(tag,cursor.toString())
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }



/**
 *
 * @SuppressLint("QueryPermissionsNeeded")
private fun grantPermission(intent: Intent, uri: Uri?) {
var flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
flag = flag or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
intent.addFlags(flag)
val resInfoList = context?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
if (resInfoList != null) {
for (resolveInfo in resInfoList) {
val packageName = resolveInfo.activityInfo.packageName
context?.grantUriPermission(packageName, uri, flag)
Log.d("grantPermission","packageName:$packageName")
}
}
}

@SuppressLint("QueryPermissionsNeeded")
private fun grantPermissionFix(intent: Intent, uri: Uri?) {
var flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
flag = flag or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
intent.addFlags(flag)
val resInfoList = context?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
if (resInfoList != null) {
for (resolveInfo in resInfoList) {
val packageName = resolveInfo.activityInfo.packageName
try {
context?.grantUriPermission(packageName, uri, flag)
} catch (e: Exception) {
continue
}
intent.action = null
intent.component = ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name)
break
}
}
}
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("PhotoFragment", "onActivityResult")
        if(resultCode== AppCompatActivity.RESULT_OK){
            when(requestCode){
                5 ->{
                    if (data!=null){
                        view?.findNavController()?.navigate(R.id.action_photoFragment_to_personalCenterFragment)
                        Toast.makeText(context, "New headPortrait Selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
**/

}


/**
 // 裁剪源图片
CoroutineScope(Dispatchers.IO).launch {
val bitmap = context?.let { Glide.with(it).asBitmap().load(viewModel.imageUri).circleCrop().placeholder(android.R.drawable.progress_indeterminate_horizontal)
.error(android.R.drawable.stat_notify_error).submit(5,5).get() }
bitmap?.let { context?.let { it1 -> viewModel.saveFile(it1,it) } }

}
 */