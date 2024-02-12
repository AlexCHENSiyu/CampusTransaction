package com.example.campustransaction.ui.userInfo.personal_center

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.campustransaction.R
import com.example.campustransaction.databinding.FragmentPersonalCenterBinding
import com.example.campustransaction.ui.UIViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class PersonalCenterFragment : Fragment(),  AdapterView.OnItemSelectedListener {
    private val viewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentPersonalCenterBinding

    // 下拉选择框 spin_school
    private lateinit var adapter: ArrayAdapter<CharSequence>
    private var school = arrayOf("Don't SELECT","CSE", "CPEG", "ECE", "FINA", "ECON", "QFIN", "ISDN", "GBUS", "OTHER")

    // 返回监听设置
    private val mBackPressedCallback: OnBackPressedCallback by lazy  {
        object : OnBackPressedCallback(true) {  //enabled 标记位
            override fun handleOnBackPressed() {
                askForSave()
                Log.d("PersonalCenterFragment","saveUserInfo")
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, mBackPressedCallback)
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentPersonalCenterBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // 显示日期选择
        binding.buttonBirthday.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            activity?.let { it1 -> DatePickerDialog(it1, { _, year, month, day -> binding.buttonBirthday.text=" $year-${month+1}-$day" }, year, month, day) }?.show()   //月份小1
        }

        // 下拉选择框 spin_school
        binding.spinnerSchool.onItemSelectedListener = this
        adapter = activity?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, school) }!!
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSchool.adapter = adapter
        binding.spinnerSchool.prompt = "School"
        val spinnerPosition: Int = adapter.getPosition("CSE")
        binding.spinnerSchool.setSelection(spinnerPosition)

        // 申请相机和图库读写权限
        context?.let { viewModel.onRequestPermissions(it) }

        // 头像修改
        binding.headPortrait.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_personalCenterFragment_to_photoFragment)
        }

        // 设置已有的信息
        setUserInfo()

        // 如果viewModel里的图片的Uri被改变，则记录新的头像的Uri并重新加载图片
        viewModel.photoUri.observe(viewLifecycleOwner){
            if(viewModel.photoUri.value != null){
                viewModel.headPortraitUri = viewModel.photoUri.value
                binding.headPortrait.setImageURI(viewModel.headPortraitUri)
            }
        }

        return binding.root
    }

    private fun askForSave() {
        // 返回时的询问框
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Save change?")
            .setMessage("You have changed something, do you want to save?")
            .setCancelable(false)
            .setNegativeButton("DON'T SAVE") { _, _ -> view?.findNavController()?.navigate(R.id.action_personalCenterFragment_to_userInfoNavigation)}
            .setPositiveButton("SAVE") {_, _ ->saveUserInfo() }
            .show()
    }

    private fun saveUserInfo(){
        // 更新viewModel里面的值
        if(binding.textNickname.text.toString() != "") viewModel.myUserInfo.NickName = binding.textNickname.text.toString()
        if(binding.textFirstname.text.toString() != "") viewModel.myUserInfo.FirstName = binding.textFirstname.text.toString()
        if(binding.textLastname.text.toString() != "") viewModel.myUserInfo.LastName = binding.textLastname.text.toString()
        viewModel.myUserInfo.StudentID = binding.textStudentID.text.toString().toIntOrNull()
        if(binding.textProfile.text.toString() != "") viewModel.myUserInfo.Profile = binding.textProfile.text.toString()
        if(binding.textRegion.text.toString() != "") viewModel.myUserInfo.Region = binding.textRegion.text.toString()
        viewModel.myUserInfo.PhoneNumber = binding.textPhoneNumber.text.toString().toIntOrNull()
        viewModel.myUserInfo.School = binding.spinnerSchool.selectedItem.toString()
        viewModel.myUserInfo.Gender = when (binding.radioGroupGender.checkedRadioButtonId) {
            R.id.button_male -> binding.buttonMale.text.toString()
            R.id.button_female -> binding.buttonFemale.text.toString()
            else -> binding.buttonHide.text.toString()
        }
        if(binding.buttonBirthday.text != "Birthday") viewModel.myUserInfo.Birthday = binding.buttonBirthday.text.toString()
        // 将头像转为base64进行上传
        val base64ImageFromUri = context?.let { viewModel.photoUri.value?.let { it1 -> viewModel.getBase64ForUri(it, it1) } }
        if(base64ImageFromUri != ""){ viewModel.myUserInfo.HeadPortrait = base64ImageFromUri }

        // 上传云端数据库
        viewModel.sdkCreateAccount()

        // 返回UserInfo页面
        view?.findNavController()?.navigate(R.id.action_personalCenterFragment_to_userInfoNavigation)
    }

    private fun setUserInfo(){
        // 设置userInfo
        //Log.d("PersonalCenterFragment", "Email:"+viewModel.myUserInfo.EmailAddress)
        //Log.d("PersonalCenterFragment", "Password:"+viewModel.myUserInfo.Password)
        binding.headPortrait.setImageURI(viewModel.headPortraitUri)
        binding.titleEmailAddress.text = viewModel.myUserInfo.EmailAddress
        binding.textNickname.hint = viewModel.myUserInfo.NickName?:"NickName"
        binding.textFirstname.hint = viewModel.myUserInfo.FirstName?:"FirstName"
        binding.textLastname.hint = viewModel.myUserInfo.LastName?:"LastName"
        if(viewModel.myUserInfo.StudentID == null) {binding.textStudentID.hint = "StudentID"}
        else{binding.textStudentID.hint = viewModel.myUserInfo.StudentID.toString()}
        binding.textProfile.hint = viewModel.myUserInfo.Profile?:"Profile"
        binding.textRegion.hint = viewModel.myUserInfo.Region?:"Region"
        if(viewModel.myUserInfo.PhoneNumber == null) {binding.textPhoneNumber.hint = "PhoneNumber"}
        else{binding.textPhoneNumber.hint = viewModel.myUserInfo.PhoneNumber.toString()}
        binding.spinnerSchool.setSelection(adapter.getPosition(viewModel.myUserInfo.School))
        binding.radioGroupGender.check( when(viewModel.myUserInfo.Gender){
            binding.buttonMale.text.toString() -> R.id.button_male
            binding.buttonFemale.text -> R.id.button_female
            else -> R.id.button_hide
        })
        binding.buttonBirthday.text = viewModel.myUserInfo.Birthday?:"Birthday"
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //Toast.makeText(context, school[position] + " Selected", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            2->{
                if(grantResults.isNotEmpty()){
                    for (i in 0 until grantResults.size-1){
                        if(grantResults[i]== PackageManager.PERMISSION_GRANTED){
                            Log.d("tags", "Succeed apply for ${permissions[i]}")
                        }else{
                            Toast.makeText(context, "${permissions[i]}apply failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

}


/**
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var date: String = ""
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        date = "$year - $month - $day"
    }

    fun getDate(): String{
        return date
    }

}

val array:Array<String> = arrayOf("王者荣耀","吃鸡","暗黑破坏神","红色警戒")
val adapter: ArrayAdapter<String>? =
activity?.let { ArrayAdapter(it, R.id.text_spinner, array.toList()) }
//设置下拉模式风格
adapter?.setDropDownViewResource(R.layout.spinner_right_aligned)
binding.spinnerSchool.prompt="请选择要进入的游戏" //标题栏
binding.spinnerSchool.adapter=adapter  //设置adapter
binding.spinnerSchool.setSelection(0)  //设置默认选中项
//设置点击事件
binding.spinnerSchool.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
val item: Int = binding.spinnerSchool.selectedItemPosition
makeText(context, "选中游戏:${array[position]}", Toast.LENGTH_SHORT).show()
}

override fun onNothingSelected(parent: AdapterView<*>?) {
TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
}
**/