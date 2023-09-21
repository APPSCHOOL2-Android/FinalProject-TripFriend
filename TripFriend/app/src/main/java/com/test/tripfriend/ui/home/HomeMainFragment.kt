package com.test.tripfriend.ui.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Selection.setSelection
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.DialogHomeMainFilterBinding
import com.test.tripfriend.databinding.FragmentHomeMainBinding
import com.test.tripfriend.repository.UserRepository
import com.test.tripfriend.ui.trip.TripMainFragment
import com.test.tripfriend.ui.user.LoginMainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeMainFragment : Fragment() {
    lateinit var fragmentHomeMainBinding: FragmentHomeMainBinding
    lateinit var mainActivity: MainActivity
    lateinit var loginMainActivity: LoginMainActivity
    lateinit var viewPager: ViewPager2
    lateinit var viewPagerAdapter: HomeMainFragment.ViewPagerAdapter
    // 최대 선택 가능 Chip 갯수
    val maxSelectableChips = 3

    // 칩 카운트 변수
    var chipCount = 0

    val spinnerList = arrayOf(
        "제목+내용", "해시태그"
    )




    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeMainBinding = FragmentHomeMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE

        val sharedPreferences =
            mainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userClass = UserRepository.getUserInfo(sharedPreferences)

        Log.d("aaaa","Main===============================================")
        Log.d("aaaa","인증방식 = ${userClass.userAuthentication}")
        Log.d("aaaa","이메일 = ${userClass.userEmail}")
        Log.d("aaaa","비밀번호 = ${userClass.userPw}")
        Log.d("aaaa","닉네임 = ${userClass.userNickname}")
        Log.d("aaaa","이름 = ${userClass.userName}")
        Log.d("aaaa","휴대폰 번호 = ${userClass.userPhoneNum}")
        Log.d("aaaa","MBTI = ${userClass.userMBTI}")
        Log.d("aaaa","userProfilePath = ${userClass.userProfilePath}")
        Log.d("aaaa","userFriendSpeed = ${userClass.userFriendSpeed}")
        Log.d("aaaa","userTripScore = ${userClass.userTripScore}")
        Log.d("aaaa","userTripCount = ${userClass.userTripCount}")
        Log.d("aaaa","userChatNotification = ${userClass.userChatNotification}")
        Log.d("aaaa","userPushNotification = ${userClass.userPushNotification}")
        Log.d("aaaa","자동 로그인 = ${userClass.checkAutoLogin}")

        viewPager = fragmentHomeMainBinding.viewPager2HomeMain
        viewPagerAdapter = ViewPagerAdapter(mainActivity)
        viewPager.adapter = viewPagerAdapter

        fragmentHomeMainBinding.run {

            // 스피너
            spinnerHomeMainSearch.run {
                spinnerClick()
            }

            // 필터 다이얼로그
            imageButtonHomeMainFilter.run {
                setOnClickListener {
                    val builder =
                        MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                            val dialogHomeMainFilterBinding: DialogHomeMainFilterBinding =
                                DialogHomeMainFilterBinding.inflate(layoutInflater)

                            setView(dialogHomeMainFilterBinding.root)

                            dialogHomeMainFilterBinding.run {
                                chipMax(chipDialogFilterCategory1)
                                chipMax(chipDialogFilterCategory2)
                                chipMax(chipDialogFilterCategory3)
                                chipMax(chipDialogFilterCategory4)
                                chipMax(chipDialogFilterCategory5)
                                chipMax(chipDialogFilterCategory6)
                                chipMax(chipDialogFilterCategory7)
                                chipMax(chipDialogFilterCategory8)
                                chipMax(chipDialogFilterCategory9)

                                // 데이트 피커
                                dialogHomeMainFilterBinding.calendarTripMain.setCalendarListener(
                                    object :
                                        CalendarListener {
                                        override fun onFirstDateSelected(startDate: Calendar) {
                                            val date = startDate.time
                                            val format =
                                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            Toast.makeText(
                                                mainActivity,
                                                "Start Date: " + format.format(date),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onDateRangeSelected(
                                            startDate: Calendar,
                                            endDate: Calendar
                                        ) {
                                            val startDate = startDate.time
                                            val endDate = endDate.time
                                            val format =
                                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            Toast.makeText(
                                                mainActivity,
                                                "Start Date: " + format.format(startDate) + "\nEnd date: " + format.format(
                                                    endDate
                                                ),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }



                            setNegativeButton("취소", null)
                            setPositiveButton("적용", null)
                        }

                    builder.show()
                }
            }

            // 뷰페이저
            viewPager2HomeMain.run {
                viewPager2HomeMain.adapter = adapter
                isUserInputEnabled = false

                TabLayoutMediator(tabLayoutHomeMain, viewPager2HomeMain) { tab, position ->
                    when (position) {
                        0 -> tab.text = "목록"
                        1 -> tab.text = "지도"
                    }
                }.attach()
                // 탭의 텍스트 색상 변경
                val tabLayout = fragmentHomeMainBinding.tabLayoutHomeMain
                val selectedColor = ContextCompat.getColor(requireContext(), R.color.highLightColor)
                val unselectedColor = ContextCompat.getColor(requireContext(), R.color.gray)

                // 선택되지 않은 탭의 텍스트 색상 설정
                tabLayout.setTabTextColors(unselectedColor, selectedColor)
                // 선택된 탭의 텍스트 색상 설정
                tabLayout.setSelectedTabIndicatorColor(selectedColor)
            }


            // 플로팅버튼

            floatingActionButtonHomeMain.run {
                setOnClickListener {
                    if(userClass.userEmail == "NoneUserEmail") {
                        MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
//                    setTitle("내용 입력")
                            setMessage("로그인한 회원만 이용하실 수 있는 서비스입니다. \n로그인 하시겠습니까?")
                            setNegativeButton("취소", null)
                            setPositiveButton("로그인") { dialogInterface: DialogInterface, i: Int ->
                                val intent = Intent(mainActivity, LoginMainActivity::class.java)
                                startActivity(intent)
                                mainActivity.removeFragment(MainActivity.HOME_MAIN_FRAGMENT)
                            }
                            show()
                            return@setOnClickListener
                        }
                    }

                    mainActivity.replaceFragment(
                        MainActivity.ACCOMPANY_REGISTER_FRAGMENT1,
                        true,
                        true,
                        null
                    )
                }
            }
        }

        return fragmentHomeMainBinding.root
    }

    // 최대 3개 이상의 칩을 선택 못하게 하는 함수
    fun chipMax(chipId: Chip) {
        chipId.setOnClickListener {
            if (chipId.isChecked) {
                if (chipCount >= maxSelectableChips) {
                    chipId.isChecked = false
                    Snackbar.make(
                        fragmentHomeMainBinding.root,
                        "여행 카테고리는 최대 3개 선택 가능합니다.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    chipCount++
                }
            } else {
                chipCount--
            }
        }
    }

    // 스피너 기능
    fun spinnerClick() {
        fragmentHomeMainBinding.run {
            spinnerHomeMainSearch.run {
                // 값을 가져올때는 spinnerHomeMainSearch.selectedItemPosition 으로 인덱스 가져와
                // spinnerList 에 접근해서 가져오기

                // 스피너 어댑터 설정
                val a1 = ArrayAdapter(
                    mainActivity,
                    // Spinner가 접혀있을 때 모양
                    R.layout.spinner_row,
                    spinnerList
                )
                a1.setDropDownViewResource(R.layout.spinner_row)
                adapter = a1

                // Spinner의 항목을 코드로 선택한다.
                // 0부터 시작하는 순서값을 넣어준다.
                setSelection(0)

                // 항목을 선택하면 동작하는 리스너
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    // 항목을 선택했을 호출되는 메서드
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
            }
        }
    }

    // 뷰 페이저 어댑터
    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        val fragments: List<Fragment> = listOf(HomeListFragment(), HomeMapFragment())

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            val resultFragment = when (position) {
                0 -> HomeListFragment()
                1 -> HomeMapFragment()
                else -> HomeListFragment()
            }
            return resultFragment
        }
    }

    override fun onResume() {
        super.onResume()
        viewPagerAdapter = ViewPagerAdapter(mainActivity)
        viewPager.adapter = viewPagerAdapter
        viewPager.setCurrentItem(mainActivity.homeMainPosition, false)
    }
}