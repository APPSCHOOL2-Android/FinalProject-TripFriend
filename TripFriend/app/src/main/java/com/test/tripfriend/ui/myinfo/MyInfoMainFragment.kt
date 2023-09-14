package com.test.tripfriend.ui.myinfo

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentMyInfoMainBinding

class MyInfoMainFragment : Fragment() {
    lateinit var fragmentMyInfoMainBinding: FragmentMyInfoMainBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentMyInfoMainBinding = FragmentMyInfoMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentMyInfoMainBinding.run {
            myInfoToolbar.run {
                setNavigationIconTint(Color.BLACK)

                setOnMenuItemClickListener {
                    //톱니 바퀴 클릭시 앱 설정 창으로 이동
                    mainActivity.replaceFragment(MainActivity.MY_APP_SETTING_FRAGMENT,true,false,null)
                    mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
                    true
                }
            }

            //자세히 보기 클릭 시
            buttonToDetailFriendSpeed.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_ACCOMPANY_INFO_FRAGMENT,true,false,null)
                mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
            }

            //내 친구 속도 수치를 textView로 보여주기 위한 작업(프로그래스 thumb를 따라다님)
            seekbarFriendSpeed.setOnSeekBarChangeListener(object:OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    val padding = seekbarFriendSpeed.paddingLeft + seekbarFriendSpeed.paddingRight
                    val sPos = seekbarFriendSpeed.left + seekbarFriendSpeed.paddingLeft
                    val xPos = (seekbarFriendSpeed.width - padding) * seekbarFriendSpeed.progress / seekbarFriendSpeed.max + sPos - (textViewFriendSpeed.width / 2)
                    textViewFriendSpeed.x = xPos.toFloat()
                    textViewFriendSpeed.text = seekbarFriendSpeed.progress.toString()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })

        }


        return fragmentMyInfoMainBinding.root
    }


}