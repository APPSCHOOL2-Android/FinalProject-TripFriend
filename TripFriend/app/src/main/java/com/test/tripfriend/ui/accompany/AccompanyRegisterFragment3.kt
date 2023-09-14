package com.test.tripfriend.ui.accompany

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.databinding.FragmentAccompanyRegister3Binding

class AccompanyRegisterFragment3 : Fragment() {
    lateinit var fragmentAccompanyRegisterFragment3: FragmentAccompanyRegister3Binding
    lateinit var mainActivity: MainActivity


    // 최대 선택 가능 Chip 갯수
    val maxSelectableChips  = 3
    // 칩 카운트 변수
    var chipCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAccompanyRegisterFragment3 = FragmentAccompanyRegister3Binding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE

        fragmentAccompanyRegisterFragment3.run {
            materialToolbarRegister3.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT3)
                }
            }

            chipMax(chipRegister3Category1)
            chipMax(chipRegister3Category2)
            chipMax(chipRegister3Category3)
            chipMax(chipRegister3Category4)
            chipMax(chipRegister3Category5)
            chipMax(chipRegister3Category6)
            chipMax(chipRegister3Category7)
            chipMax(chipRegister3Category8)
            chipMax(chipRegister3Category9)

            buttonAccompanyRegister3ToSubmit.setOnClickListener {
                Snackbar.make(mainActivity.activityMainBinding.root, "등록이 완료되었습니다..", Snackbar.LENGTH_SHORT).show()

                mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT3)
                mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT2)
                mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT1)

                mainActivity.replaceFragment(MainActivity.READ_POST_FRAGMENT, true, true, null)

            }
        }

        return fragmentAccompanyRegisterFragment3.root
    }

    // 최대 3개 이상의 칩을 선택 못하게 하는 함수
    fun chipMax(chipId: Chip) {
        chipId.setOnClickListener {
            if(chipId.isChecked) {
                if(chipCount >= maxSelectableChips) {
                    chipId.isChecked = false
                    Snackbar.make(fragmentAccompanyRegisterFragment3.root, "여행 카테고리는 최대 3개 선택 가능합니다.", Snackbar.LENGTH_SHORT).show()
                } else{
                    chipCount++
                }
            } else {
                chipCount--
            }
        }
    }
}