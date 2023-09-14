package com.test.tripfriend.ui.trip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentModifyPost2Binding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.archit.calendardaterangepicker.customviews.CalendarListener

class ModifyPost2Fragment : Fragment() {
    lateinit var fragmentModifyPost2Binding: FragmentModifyPost2Binding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentModifyPost2Binding = FragmentModifyPost2Binding.inflate(layoutInflater)

        fragmentModifyPost2Binding.run {
            materialToolbarModifyPost2.run {
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MODFY_POST2_FRAGMENT)
                }
            }

            mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
            buttonAccompanyModifyPost2ToNextView.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MODFY_POST3_FRAGMENT,true,true,null)
            }
            textInputLayoutModifyPost2Date.run {
                // 데이트 피커
                fragmentModifyPost2Binding.calendarModifyPost2.setCalendarListener(object :
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
        }

        return fragmentModifyPost2Binding.root
    }

    override fun onPause() {
        super.onPause()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }
}