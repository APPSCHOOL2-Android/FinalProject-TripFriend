package com.test.tripfriend.ui.myinfo

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentModifyMyInfoBinding

class ModifyMyInfoFragment : Fragment() {
    lateinit var fragmentModifyMyInfoBinding: FragmentModifyMyInfoBinding
    lateinit var mainActivity: MainActivity
    var nicknameIsCheck:Boolean=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentModifyMyInfoBinding = FragmentModifyMyInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

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


            buttonNickNameCheck.setOnClickListener {
                //닉네임 중복 검사해서 중복이면 textViewWarningNickName 의 visible=true, textViewCompleteCheck visible=false
                //닉네임 중복 검사해서 중복이 아니면 textViewWarningNickName 의 visible=false,textViewCompleteCheck visible=true, nicknameIsCheck=true
                //Boolean 전역변수 하나 파서 중복 검사 통과시 true값으로 변경 후 변경 완료 버튼리스너에 해당 분기 삽입
            }

            buttonModifyMyInfo.setOnClickListener {
                if (!nicknameIsCheck){
                    //중복 검사에 통과하지 못했습니다 다이얼로그 띄우기
                    return@setOnClickListener
                }
                else if (textInputEditTextPasswordCheck.text!=textInputEditTextPasswordToModify.text){
                    //비밀번호가 일치하지 않는 다이얼로그
                    return@setOnClickListener
                }

            }
        }

        return fragmentModifyMyInfoBinding.root
    }
}