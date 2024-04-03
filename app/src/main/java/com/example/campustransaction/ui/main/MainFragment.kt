package com.example.campustransaction.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentMainBinding
import com.example.campustransaction.ui.UIViewModel
import com.example.campustransaction.ui.posts.user_post.adapter.PostAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

class MainFragment : Fragment() {
    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 由于MainFragment会在activity之前创建，导致此时EmailAddress还没有数值
        Log.d("MainFragment", "Email2:" + viewModel.myUserInfo.EmailAddress)
        Log.d("MainFragment", "Password2:" + viewModel.myUserInfo.Password)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // 设置触发下拉刷新的距离
        binding.swipeRefreshLayout.setDistanceToTriggerSync(300)
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorBlue)
        binding.swipeRefreshLayout.setOnRefreshListener {
            // 这里获取数据的逻辑
            getUserLocation()
        }

        binding.mainNest.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (binding.mainNest.computeVerticalScrollExtent() +
                binding.mainNest.computeVerticalScrollOffset() >=
                binding.mainNest.computeVerticalScrollRange()
            ) {
                // 已经滑到底部
                // Toast.makeText(context, "OnScrollChange", Toast.LENGTH_SHORT).show()
                viewModel.sdkGetMorePosts(null, userLocation?.longitude, userLocation?.latitude, binding.toggleSearchRadius.isChecked)
            }
        }

        // 设置横幅Adapter
        binding.banner.setAdapter(object : BannerImageAdapter<BannerDataBean>(BannerDataBean.testData3) {
            override fun onBindView(holder: BannerImageHolder, data: BannerDataBean, position: Int, size: Int) {
                // 图片加载自己实现
                Glide.with(holder.itemView)
                    .load(data.imageRes)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                    .into(holder.imageView)
            }
        }).addBannerLifecycleObserver(this).indicator = CircleIndicator(context)

        // 设置横幅
        binding.banner.setOnBannerListener { _, position ->
            if (position != 0) {
                viewModel.bannerIndex = position
                view?.findNavController()?.navigate(R.id.action_mainNavigation_to_bannerDetailFragment)
            }
        }

        // 设置搜索条
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // 当搜索内容改变时触发该方法
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!TextUtils.isEmpty(newText)) {
                    viewModel.sdkGetPosts(newText, userLocation?.longitude, userLocation?.latitude, binding.toggleSearchRadius.isChecked)
                } else {
                    viewModel.sdkGetPosts(null, userLocation?.longitude, userLocation?.latitude, binding.toggleSearchRadius.isChecked)
                }
                return false
            }

            override fun onQueryTextSubmit(queryText: String?): Boolean {
                // 得到输入管理对象
                viewModel.sdkGetPosts(queryText, userLocation?.longitude, userLocation?.latitude, binding.toggleSearchRadius.isChecked)
                binding.searchBar.setQuery("", false)
                binding.searchBar.clearFocus() // 不获取焦点
                return true
            }
        })

        // Set up search radius slider
        binding.textSearchRadius.text = "Search Radius: 1 km"
        binding.seekbarSearchRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val radius = progress + 1
                binding.textSearchRadius.text = "Search Radius: $radius km"
                viewModel.searchRadius = radius
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        binding.recyclerPicture.addItemDecoration(SpacesItemDecoration(spacingInPixels))

        // 设置帖子列表
        viewModel.responseGetPosts.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false

            if (viewModel.responseGetPosts.value?.Success == true) {
                val postList = viewModel.responseGetPosts.value!!.Posts
                binding.recyclerPicture.adapter = context?.let { it1 -> postList?.let { it2 -> PostAdapter(it1, it2) } }
                binding.recyclerPicture.setHasFixedSize(false)

                (binding.recyclerPicture.adapter as PostAdapter?)?.setOnItemClickListener(object : PostAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        // Toast.makeText(context, "click $position item", Toast.LENGTH_SHORT).show()
                        viewModel.postDetail = viewModel.responseGetPosts.value!!.Posts?.get(position)
                        viewModel.postOwner = viewModel.postDetail!!.PostOwner
                        // Toast.makeText(context, "PostOwner: ${viewModel.postDetail?.PostOwner}", Toast.LENGTH_SHORT).show()
                        view.findNavController().navigate(R.id.action_mainNavigation_to_postDetailFragment)
                    }

                    override fun onItemLongClick(view: View, position: Int) {
                        Toast.makeText(context, "long click $position item", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // 第一次抓取
        getUserLocation()
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // 如果没有位置权限，请求权限
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLocation = location
                    viewModel.sdkGetPosts(null, userLocation?.longitude, userLocation?.latitude, binding.toggleSearchRadius.isChecked)
                } else {
                    Toast.makeText(context, "Failed to get user location", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，获取用户位置
                getUserLocation()
            } else {
                // 权限被拒绝，显示相应的提示或处理
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class SpacesItemDecoration(private val space: Int) : ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space
        }
    }
}