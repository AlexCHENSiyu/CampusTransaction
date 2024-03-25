package com.example.campustransaction.ui.posts

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.api.RequestPost
import com.example.campustransaction.databinding.FragmentPostsBinding
import com.example.campustransaction.ui.UIViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PostsFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentPostsBinding

    private var lastPhoto = 0  // 记录上次选择的图片框

    // 删除符号的计时器
    private var timer1: TimerUnit? = null
    private var timer2: TimerUnit? = null
    private var timer3: TimerUnit? = null
    private var timer4: TimerUnit? = null
    // post的labels
    private val labelsArray = arrayOf("Personal Computer", "Phone", "Digital", "Food&Drink", "Books", "Medicine","Bags","Sports","Fruits","Luxury","Sports","Jewelry","Watches")
    private val checkedLabelsArray = booleanArrayOf(false, false, false, false, false, false, false, false, false, false, false, false, false)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private lateinit var googleMap: GoogleMap
    private var customLocation: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPostsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // 重现上次保存的内容, 文字和图片
        if (viewModel.myNewPost != null){
            binding.textTitle.setText(viewModel.myNewPost!!.Title)
            binding.textDescription.setText(viewModel.myNewPost!!.Text)
            if(viewModel.myNewPost!!.Price != null){ binding.textPrice.setText(viewModel.myNewPost!!.Price.toString())}
        }else{
            binding.textTitle.setText("")
            binding.textDescription.setText("")
            binding.textPrice.setText("")
        }
        if (viewModel.postPhotoUri1 != null) {binding.buttonPhoto1.setImageURI(viewModel.postPhotoUri1); binding.buttonPhoto1.tag = "Selected"}
        if (viewModel.postPhotoUri2 != null) {binding.buttonPhoto2.setImageURI(viewModel.postPhotoUri2); binding.buttonPhoto2.tag = "Selected"}
        if (viewModel.postPhotoUri3 != null) {binding.buttonPhoto3.setImageURI(viewModel.postPhotoUri3); binding.buttonPhoto3.tag = "Selected"}
        if (viewModel.postPhotoUri4 != null) {binding.buttonPhoto4.setImageURI(viewModel.postPhotoUri4); binding.buttonPhoto4.tag = "Selected"}

        // 申请相机和图库读写权限
        context?.let { viewModel.onRequestPermissions(it) }

        // 图片点击
        binding.buttonPhoto1.setOnClickListener {
            lastPhoto = 1
            savePost()
            view?.findNavController()?.navigate(R.id.action_postsNavigation_to_photoFragment)
        }
        binding.buttonPhoto2.setOnClickListener {
            lastPhoto = 2
            savePost()
            view?.findNavController()?.navigate(R.id.action_postsNavigation_to_photoFragment)
        }
        binding.buttonPhoto3.setOnClickListener {
            lastPhoto = 3
            savePost()
            view?.findNavController()?.navigate(R.id.action_postsNavigation_to_photoFragment)
        }
        binding.buttonPhoto4.setOnClickListener {
            lastPhoto = 4
            savePost()
            view?.findNavController()?.navigate(R.id.action_postsNavigation_to_photoFragment)
        }

        // 长按显示删除图片的按钮
        binding.buttonPhoto1.setOnLongClickListener {
            showDeleteButton()
            return@setOnLongClickListener true
        }
        binding.buttonPhoto2.setOnLongClickListener {
            showDeleteButton()
            return@setOnLongClickListener true
        }
        binding.buttonPhoto3.setOnLongClickListener {
            showDeleteButton()
            return@setOnLongClickListener true
        }
        binding.buttonPhoto4.setOnLongClickListener {
            showDeleteButton()
            return@setOnLongClickListener true
        }

        // 点击删除当前图片
        binding.buttonDelete1.setOnClickListener{
            binding.buttonPhoto1.setImageResource(R.drawable.choose_photo)
            binding.buttonPhoto1.tag = "unSelected"
            viewModel.postPhotoUri1 = null
            timer1!!.endTime()
            timer1=null
        }
        binding.buttonDelete2.setOnClickListener{
            binding.buttonPhoto2.setImageResource(R.drawable.choose_photo)
            binding.buttonPhoto2.tag = "unSelected"
            viewModel.postPhotoUri2 = null
            timer2!!.endTime()
            timer2=null
        }
        binding.buttonDelete3.setOnClickListener{
            binding.buttonPhoto3.setImageResource(R.drawable.choose_photo)
            binding.buttonPhoto3.tag = "unSelected"
            viewModel.postPhotoUri3 = null
            timer3!!.endTime()
            timer3=null
        }
        binding.buttonDelete4.setOnClickListener{
            binding.buttonPhoto4.setImageResource(R.drawable.choose_photo)
            binding.buttonPhoto4.tag = "unSelected"
            viewModel.postPhotoUri4 = null
            timer4!!.endTime()
            timer4=null
        }

        // 选择帖子的label
        binding.buttonLabel.setOnClickListener {
            val builder = context?.let { it1 -> AlertDialog.Builder(it1) }

            builder?.setTitle("Select labels")
            builder?.setMultiChoiceItems(labelsArray, checkedLabelsArray) { _, which, isChecked ->
                checkedLabelsArray[which] = isChecked
                //Toast.makeText(context, "$labelsList[which] $isChecked", Toast.LENGTH_SHORT).show()
            }

            builder?.setPositiveButton("OK") { _, _ -> }
            builder?.setNeutralButton("Cancel") { _, _ -> }
            val dialog = builder?.create()
            dialog?.show()
        }


        // 如果viewModel里的图片的Uri被改变，则记录新的头像的Uri并重新加载图片
        viewModel.photoUri.observe(viewLifecycleOwner){
            if(viewModel.photoUri.value != null)
            {
                when(lastPhoto){
                    1 -> {
                        viewModel.postPhotoUri1 = viewModel.photoUri.value
                        binding.buttonPhoto1.setImageURI(viewModel.postPhotoUri1)
                        binding.buttonPhoto1.tag = "Selected"
                        lastPhoto = 0
                    }
                    2 -> {
                        viewModel.postPhotoUri2 = viewModel.photoUri.value
                        binding.buttonPhoto2.setImageURI(viewModel.postPhotoUri2)
                        binding.buttonPhoto2.tag = "Selected"
                        lastPhoto = 0
                    }
                    3 -> {
                        viewModel.postPhotoUri3 = viewModel.photoUri.value
                        binding.buttonPhoto3.setImageURI(viewModel.postPhotoUri3)
                        binding.buttonPhoto3.tag = "Selected"
                        lastPhoto = 0
                    }
                    4 -> {
                        viewModel.postPhotoUri4 = viewModel.photoUri.value
                        binding.buttonPhoto4.setImageURI(viewModel.postPhotoUri4)
                        binding.buttonPhoto4.tag = "Selected"
                        lastPhoto = 0
                    }
                }
            }
        }

        // Post按钮监听
        binding.buttonPost.setOnClickListener {
            if (binding.textTitle.text == null || binding.textTitle.text.toString() == "") {
                Toast.makeText(context, "You must have a Title", Toast.LENGTH_SHORT).show()
            } else if (binding.textDescription.text == null || binding.textDescription.text.toString() == "") {
                Toast.makeText(context, "You must have a Description", Toast.LENGTH_SHORT).show()
            } else if (binding.textPrice.text == null || binding.textPrice.text.toString() == "") {
                Toast.makeText(context, "You must have a Price", Toast.LENGTH_SHORT).show()
            } else if (currentLocation == null) {
                Toast.makeText(context, "Please provide a location", Toast.LENGTH_SHORT).show()
            } else {
                // 保存帖子
                savePost()
                // 保存图片
                val base64FromPostPhoto1 = context?.let { viewModel.postPhotoUri1?.let { it1 -> viewModel.getBase64ForUri(it, it1) } }
                val base64FromPostPhoto2 = context?.let { viewModel.postPhotoUri2?.let { it1 -> viewModel.getBase64ForUri(it, it1) } }
                val base64FromPostPhoto3 = context?.let { viewModel.postPhotoUri3?.let { it1 -> viewModel.getBase64ForUri(it, it1) } }
                val base64FromPostPhoto4 = context?.let { viewModel.postPhotoUri4?.let { it1 -> viewModel.getBase64ForUri(it, it1) } }
                val photoList: MutableList<String> = mutableListOf()
                if(base64FromPostPhoto1 != ""){ base64FromPostPhoto1?.let { it1 -> photoList.add(it1) } }
                if(base64FromPostPhoto2 != ""){ base64FromPostPhoto2?.let { it1 -> photoList.add(it1) } }
                if(base64FromPostPhoto3 != ""){ base64FromPostPhoto3?.let { it1 -> photoList.add(it1) } }
                if(base64FromPostPhoto4 != ""){ base64FromPostPhoto4?.let { it1 -> photoList.add(it1) } }
                viewModel.myNewPost?.Images = photoList
                // Log.d("sdkNewPost1", photoList.toString())

                // 上传新帖子
                viewModel.sdkNewPost()
                // binding.buttonPost.isClickable = false
                Toast.makeText(context, "New post uploaded", Toast.LENGTH_SHORT).show()
                clearPost()
            }
        }

//        // 创建帖子的返回监听
//        viewModel.responseNewPost.observe(viewLifecycleOwner){
//            if (viewModel.responseNewPost.value?.Success == true){
//                Toast.makeText(context, "New post uploaded", Toast.LENGTH_SHORT).show()
//                clearPost()
//            }else{
//                Toast.makeText(context, "New post upload failed. Error:"+viewModel.responseNewPost.value?.Error, Toast.LENGTH_SHORT).show()
//            }
//            binding.buttonPost.isClickable = true
//        }

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Request current location instantly
        getCurrentLocation()

        // Set up radio button listener
        binding.radioGroupLocation.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_current_location -> {
                    binding.mapContainer.visibility = View.GONE
                    // Current location is already acquired, no need to request again
                }
                R.id.radio_custom_location -> {
                    binding.mapContainer.visibility = View.VISIBLE
                    // Set up the map and enable the search bar
                    val mapFragment = SupportMapFragment.newInstance()
                    childFragmentManager.beginTransaction()
                        .replace(R.id.map_container, mapFragment)
                        .commit()
                    mapFragment.getMapAsync(this)
                }
            }
        }

        return binding.root
    }



    // 离开时询问是否保存帖子
    override fun onDestroy() {
        super.onDestroy()
        // 判断有无草稿存在
        if(binding.textTitle.text.toString() != ""
            || binding.textDescription.text.toString() != ""
            || binding.textPrice.text.toString() != ""
            || binding.buttonPhoto1.tag.equals("Selected")
            || binding.buttonPhoto2.tag.equals("Selected")
            || binding.buttonPhoto3.tag.equals("Selected")
            || binding.buttonPhoto4.tag.equals("Selected"))
        {
            // 询问保存框
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Save post draft?")
                .setMessage("Do you want to save this post draft?")
                .setCancelable(false)
                .setNegativeButton("DON'T SAVE") { _, _ -> unSavePost() }
                .setPositiveButton("SAVE") {_, _ -> savePost() }
                .show()
        }
    }

    // 保存帖子
    private fun savePost() {
        viewModel.myNewPost = RequestPost(
            PostOwner = viewModel.myUserInfo.EmailAddress,
            Title = binding.textTitle.text.toString(),
            Text = binding.textDescription.text.toString(),
            Price = binding.textPrice.text.toString().toDoubleOrNull(),
            Auction = false,
            LostFound = false,
            Latitude = if (customLocation != null) customLocation?.latitude else currentLocation?.latitude,
            Longitude = if (customLocation != null) customLocation?.longitude else currentLocation?.longitude
        )

        when (binding.spinnerType.selectedItem.toString())
        {
            "Auction"-> viewModel.myNewPost!!.Auction = true
            "Lost&Found"-> viewModel.myNewPost!!.LostFound = true
        }

        for (i in checkedLabelsArray.indices) {
            if (checkedLabelsArray[i]) {
                if(viewModel.myNewPost!!.Fields == null){
                    viewModel.myNewPost!!.Fields = mutableListOf()
                }
                viewModel.myNewPost!!.Fields?.add(labelsArray[i])
            }
        }

        Log.d("PostsFragment","savePost")
    }

    // 不保存帖子
    private fun unSavePost(){
        viewModel.postPhotoUri1 = null
        viewModel.postPhotoUri2 = null
        viewModel.postPhotoUri3 = null
        viewModel.postPhotoUri4 = null
        viewModel.myNewPost = null
        Log.d("PostsFragment","unSavePost")
    }

    // 不保存帖子
    private fun clearPost(){
        unSavePost()
        binding.textTitle.text = null
        binding.textDescription.text = null
        binding.textPrice.text = null
        binding.buttonPhoto1.setImageResource(R.drawable.choose_photo)
        binding.buttonPhoto2.setImageResource(R.drawable.choose_photo)
        binding.buttonPhoto3.setImageResource(R.drawable.choose_photo)
        binding.buttonPhoto4.setImageResource(R.drawable.choose_photo)
        binding.buttonPhoto1.tag = "unSelected"
        binding.buttonPhoto2.tag = "unSelected"
        binding.buttonPhoto3.tag = "unSelected"
        binding.buttonPhoto4.tag = "unSelected"
        viewModel.postPhotoUri1 = null
        viewModel.postPhotoUri2 = null
        viewModel.postPhotoUri3 = null
        viewModel.postPhotoUri4 = null
        lastPhoto = 0
    }

    // 长按显示删除按钮
    private fun showDeleteButton(){
        // 给删除按钮绑定timer
        Log.d("showDeleteButton",binding.buttonPhoto1.tag.toString()+" " +binding.buttonPhoto2.tag.toString()+" " +binding.buttonPhoto3.tag.toString()+" " +binding.buttonPhoto4.tag.toString())
        if(binding.buttonPhoto1.tag.equals("Selected")){
            timer1 = TimerUnit(binding.buttonDelete1)
            timer1?.startTime()
        }
        if(binding.buttonPhoto2.tag.equals("Selected")){
            timer2 = TimerUnit(binding.buttonDelete2)
            timer2?.startTime()
        }
        if(binding.buttonPhoto3.tag.equals("Selected")){
            timer3 = TimerUnit(binding.buttonDelete3)
            timer3?.startTime()
        }
        if(binding.buttonPhoto4.tag.equals("Selected")){
            timer4 = TimerUnit(binding.buttonDelete4)
            timer4?.startTime()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set initial map position to the user's current location
        if (currentLocation != null) {
            val currentLatLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
        }

        // Set custom location when the user clicks on the map
        googleMap.setOnMapClickListener { latLng ->
            customLocation = latLng
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng))
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    customLocation = null // Clear custom location
                    Toast.makeText(requireContext(), "Current location retrieved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Unable to retrieve current location", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting current location", exception)
                Toast.makeText(requireContext(), "Error getting current location", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "PostsFragment"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}