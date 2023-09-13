package com.test.tripfriend.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.test.tripfriend.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.DialogHomeMainFilterBinding
import com.test.tripfriend.databinding.FragmentHomeMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeMainFragment : Fragment() {
    lateinit var fragmentHomeMainBinding: FragmentHomeMainBinding
    lateinit var mainActivity: MainActivity

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

        fragmentHomeMainBinding.run {
            // 스피너
            spinnerHomeMainSearch.run {
                spinnerClick()
            }

            // 필터 다이얼로그
            imageButtonHomeMainFilter.run {
                setOnClickListener {
                    val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                        val dialogHomeMainFilterBinding : DialogHomeMainFilterBinding = DialogHomeMainFilterBinding.inflate(layoutInflater)

                        setView(dialogHomeMainFilterBinding.root)

                        setNegativeButton("취소", null)
                        setPositiveButton("적용", null)

//                        // 데이트 피커
//                        dialogHomeMainFilterBinding.button.setOnClickListener {
//                            val dateRangePicker =
//                                MaterialDatePicker.Builder.dateRangePicker()
//                                    .setTitleText("Select dates")
//                                    .setSelection(
//                                        Pair(
//                                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
//                                            MaterialDatePicker.todayInUtcMilliseconds()
//                                        )
//                                    )
//                                    .build()
//
//                            dateRangePicker.show()
//                        }

                        // 데이트 피커
                        dialogHomeMainFilterBinding.calendarTripMain.setCalendarListener(object :
                            CalendarListener {
                            override fun onFirstDateSelected(startDate: Calendar) {
                                val date = startDate.time
                                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                Toast.makeText(mainActivity, "Start Date: " + format.format(date), Toast.LENGTH_SHORT).show()
                            }

                            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                                val startDate = startDate.time
                                val endDate = endDate.time
                                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                Toast.makeText(mainActivity, "Start Date: " + format.format(startDate) + "\nEnd date: " + format.format(endDate), Toast.LENGTH_SHORT).show()
                            }
                        })

                    }

                    builder.show()
                }
            }

            // 뷰페이저
            viewPager2HomeMain.run {
                val adapter = ViewPagerAdapter(mainActivity)
                viewPager2HomeMain.adapter = adapter

                TabLayoutMediator(tabLayoutHomeMain, viewPager2HomeMain) { tab, position ->
                    when(position) {
                        0 -> tab.text = "목록"
                        1 -> tab.text = "지도"
                    }
                }.attach()
            }

            // 플로팅버튼
            floatingActionButtonHomeMain.run {
                setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT1, true, true, null)
                }
            }
        }

        return fragmentHomeMainBinding.root
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
                    android.R.layout.simple_spinner_item,
                    spinnerList
                )
                a1.setDropDownViewResource(android.R.layout.simple_spinner_item)
                adapter = a1

                // Spinner의 항목을 코드로 선택한다.
                // 0부터 시작하는 순서값을 넣어준다.
                setSelection(0)

                // 항목을 선택하면 동작하는 리스너
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    // 항목을 선택했을 호출되는 메서드
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
            }
        }
    }

    // 뷰 페이저 어댑터
    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        val fragments: List<Fragment> = listOf(HomeListFragment(), HomeMapFragment())

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            val resultFragment = when(position){
                0 -> HomeListFragment()
                1 -> HomeMapFragment()
                else -> HomeListFragment()
            }
            return resultFragment
        }
    }
}