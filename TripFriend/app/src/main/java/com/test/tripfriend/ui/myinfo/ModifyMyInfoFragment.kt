package com.test.tripfriend.ui.myinfo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentModifyMyInfoBinding
import com.test.tripfriend.repository.UserRepository
import com.test.tripfriend.viewmodel.UserViewModel
import kotlinx.coroutines.runBlocking

class ModifyMyInfoFragment : Fragment() {

    lateinit var fragmentModifyMyInfoBinding: FragmentModifyMyInfoBinding
    lateinit var mainActivity: MainActivity

    lateinit var userViewModel : UserViewModel

    lateinit var albumLauncher: ActivityResultLauncher<Intent>

    var profileImage : Uri? = null

    val userRepository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        fragmentModifyMyInfoBinding = FragmentModifyMyInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        //앨범 런처 초기화
        albumLauncher = albumSetting(fragmentModifyMyInfoBinding.imageViewMyProfile)

        fragmentModifyMyInfoBinding.run {

            toolbarModifyMyInfo.run {
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationIconTint(Color.BLACK)
                //툴바 뒤로가기
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MODIFY_MY_INFO_FRAGMENT)
                }
            }
            spinnerMbti.run {
                val mbti = resources.getStringArray(R.array.spinner_mbti_list)
                val spinnerAdapter = ArrayAdapter(mainActivity,R.layout.spinner_row, mbti)
                adapter = spinnerAdapter
            }

            imageViewModifyImage.setOnClickListener {
                val albumIntent = Intent(Intent.ACTION_PICK)
                albumIntent.setType("image/*")

                val mimeType = arrayOf("image/*")
                albumIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                albumLauncher.launch(albumIntent)
            }

            buttonModifyMyInfo.setOnClickListener {
                var password = ""

                //이메일 로그인 및 회원가입한 회원일 경우
                if(userViewModel.user.value?.userAuthentication == "이메일"){

                    val passwordText = textInputEditTextPasswordToModify.text.toString()
                    val passwordCheckText = textInputEditTextPasswordCheck.text.toString()

                    //비밀번호 입력 검사
                    if(textIsEmptyCheck(passwordText, textInputLayoutPassword, "비밀번호를 입력해주세요")||
                        textIsEmptyCheck(passwordCheckText, textInputLayoutPasswordCheck, "비밀번호 확인을 입력해주세요")){
                        return@setOnClickListener
                    }

                    //비밀번호 일치 검사
                    if(passwordText != passwordCheckText) {
                        Snackbar.make(fragmentModifyMyInfoBinding.root, "비밀번호가 일치하지 않습니다", Snackbar.LENGTH_SHORT).show()
                        textInputEditTextPasswordCheck.setText("")
                        textInputEditTextPasswordToModify.setText("")
                        textInputLayoutPassword.requestFocus()
                        return@setOnClickListener
                    }
                    password = passwordText
                }

                val imagePath = if(userViewModel.user.value?.userProfilePath?.isEmpty()!!){
                    "UserProfileImage/${mainActivity.userClass.userEmail}/1"
                }else{
                    userViewModel.user.value?.userProfilePath!!
                }

                runBlocking {
                    userRepository.updateTargetUserInfo(userViewModel.userDocumentId.value!!, imagePath, mainActivity.userClass.userNickname, spinnerMbti.selectedItem.toString(), password)
                    //이미지 변경 됐을 때
                    if(profileImage != null){
                        //스토리지에 덮어씌우기
                        userRepository.updateTargetUserProfile(imagePath, profileImage!!)
                    }
                    Snackbar.make(fragmentModifyMyInfoBinding.root, "계정 정보가 수정되었습니다.", Snackbar.LENGTH_SHORT).show()
                    mainActivity.removeFragment(MainActivity.MODIFY_MY_INFO_FRAGMENT)
                }
            }
        }

        initViewModel(mainActivity.userClass.userEmail)

        return fragmentModifyMyInfoBinding.root
    }

    fun initViewModel(userEmail : String) {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.getTargetUserData(userEmail)

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                fragmentModifyMyInfoBinding.run {
                    //이미지
                    if(user.userProfilePath.isEmpty()){
                        //기본 이미지 지정
                        imageViewMyProfile.setImageResource(R.drawable.person_24px)
                    }
                    else{
                        //유저 정보에 저장된 이미지 지정
                        userViewModel.getTargetUserProfileImage(user.userProfilePath)
                    }

                    //mbti
                    val mbti = resources.getStringArray(R.array.spinner_mbti_list)

                    for(idx in 0 until mbti.size){
                        if(user.userMBTI == "모름"){
                            spinnerMbti.setSelection(0)
                        }
                        else if(mbti[idx] == user.userMBTI){
                            spinnerMbti.setSelection(idx)
                        }
                    }

                    //pw
                    if(user.userAuthentication == "카카오" || user.userAuthentication == "네이버"){
                        textInputLayoutPassword.visibility = View.GONE
                        textInputLayoutPasswordCheck.visibility = View.GONE
                        textViewModifyMyInfoPw.visibility = View.GONE
                    }
                }
            }
        }

        userViewModel.userProfileImage.observe(viewLifecycleOwner){ uri ->
            if(uri != null){
                Glide.with(mainActivity).load(uri)
                    .into(fragmentModifyMyInfoBinding.imageViewMyProfile)
            }
            else{
                fragmentModifyMyInfoBinding.imageViewMyProfile.setImageResource(R.drawable.person_24px)
            }
        }
    }

    fun textIsEmptyCheck(text : String, textLayout : TextInputLayout, errorMessage : String) : Boolean{
        if(text.isEmpty()){
            textLayout.error = errorMessage
            textLayout.requestFocus()
            return true
        }
        else{
            textLayout.error = null
            return false
        }
    }

    fun albumSetting(imageView : ImageView) : ActivityResultLauncher<Intent> {
        //앨범에서 이미지 가져오기
        val albumLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            //이미지 가져오기 성공
            if(it.resultCode == Activity.RESULT_OK){
                it.data?.data?.let { uri ->
                    if(uri != null){
                        profileImage = uri

                        mainActivity.setImage(uri, imageView)
                    }
                }
            }
        }
        return albumLauncher
    }
}