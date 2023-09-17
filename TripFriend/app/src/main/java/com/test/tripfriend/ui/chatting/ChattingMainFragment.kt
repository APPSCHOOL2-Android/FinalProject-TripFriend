package com.test.tripfriend.ui.chatting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.databinding.FragmentChattingMainBinding

class ChattingMainFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentChattingMainBinding: FragmentChattingMainBinding
    var lastSelectedTab: TabLayout.Tab? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = activity as MainActivity
        fragmentChattingMainBinding = FragmentChattingMainBinding.inflate(layoutInflater)

        fragmentChattingMainBinding.run {

            // 탭 레이아웃
            tabLayoutChatting.run {
                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        // TODO("Not yet implemented")
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        // TODO("Not yet implemented")
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                        // TODO("Not yet implemented")
                    }

                })
            }

            // 뷰 페이저
            viewPager2Chatting.run {
                val adapter = FragmentPagerAdapter(mainActivity)
                viewPager2Chatting.adapter = adapter

                TabLayoutMediator(tabLayoutChatting, this) { tab, position ->
                    when(position) {
                        0 -> tab.text = "1:1 대화"
                        1 -> tab.text = "그룹 대화"
                    }
                }.attach()
            }

        }

        return fragmentChattingMainBinding.root
    }

    // 뷰 페이저 어댑터
    class FragmentPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        val fragments: List<Fragment>
        init {
            fragments = listOf(PersonalChattingFragment(), GroupChattingFragment())
        }

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }

}