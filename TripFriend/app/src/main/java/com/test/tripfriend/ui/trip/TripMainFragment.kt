package com.test.tripfriend.ui.trip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentTripMainBinding

class TripMainFragment : Fragment() {
    lateinit var fragmentTripMainBinding: FragmentTripMainBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentTripMainBinding = FragmentTripMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentTripMainBinding.run {
            materialToolbarTripMain.run {
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_item_notification -> {
                            mainActivity.replaceFragment(
                                MainActivity.NOTIFICATION_FRAGMENT, true, true, null)
                        }
                    }
                    true
                }
            }

            // 뷰페이저
            viewPager2TripMain.run {
                val adapter = ViewPagerAdapter(mainActivity)
                viewPager2TripMain.adapter = adapter

                TabLayoutMediator(tabLayoutTripMain, viewPager2TripMain) { tab, position ->
                    when (position) {
                        0 -> tab.text = "참여중인 동행"
                        1 -> tab.text = "지난 동행"
                        2 -> tab.text = "찜목록"
                    }
                }.attach()
            }
        }

        return fragmentTripMainBinding.root
    }

    // 뷰 페이저 어댑터
    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        val fragments: List<Fragment> = listOf(InProgressFragment(), PassFragment(), LikedListFragment())

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}