package com.test.tripfriend.ui.trip

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.test.tripfriend.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentTripMainBinding
import com.test.tripfriend.databinding.RowTripMainBinding

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
                when(it.itemId){
                    R.id.menu_item_notification->{
                        mainActivity.replaceFragment(MainActivity.NOTIFICATION_FRAGMENT,true,true,null)
                    }
                }
                    true
                }
            }
            //상단 탭
            tapLayoutTripMain.run {
                //탭 선택 했을 때 밑에 줄 색상 강조색으로 변경
                setSelectedTabIndicatorColor(getResources().getColor(R.color.highLightColor))
                //탭 선택 시 선택 안된 것들의 글자 색은 회색, 선택된 것의 글자색은 강조색
                setTabTextColors(Color.GRAY, getResources().getColor(R.color.highLightColor))
                when(selectedTabPosition){
                    0 ->{
                        recyclerViewTripMain.run {
                            adapter = TripMainAdapter()
                            layoutManager = LinearLayoutManager(mainActivity)
                        }
                        textViewTripMainNoPost.visibility = View.GONE
                    }
                    1 ->{
                        textViewTripMainNoPost.text = "지난 동행글 없음"
                    }
                    2->{
                        textViewTripMainNoPost.text = "찜목록 없음"
                    }
                }

                tapLayoutTripMain.addOnTabSelectedListener(object :
                    TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        // 탭이 선택되었을 때 호출되는 메서드
                        val selectedTabPosition = tab?.position
                        when (selectedTabPosition) {
                            0 -> {
                                recyclerViewTripMain.visibility = View.VISIBLE
                                recyclerViewTripMain.run {
                                    adapter = TripMainAdapter()
                                    layoutManager = LinearLayoutManager(mainActivity)
                                }
                                textViewTripMainNoPost.visibility = View.GONE
                            }
                            1 -> {
                                recyclerViewTripMain.visibility = View.GONE
                                textViewTripMainNoPost.visibility = View.VISIBLE
                                textViewTripMainNoPost.text = "지난 동행글 없음"
                            }
                            2 -> {
                                recyclerViewTripMain.visibility = View.GONE
                                textViewTripMainNoPost.visibility = View.VISIBLE
                                textViewTripMainNoPost.text = "찜목록 없음"
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }
                })
            }

        }

        return fragmentTripMainBinding.root
    }
    inner class TripMainAdapter : RecyclerView.Adapter<TripMainAdapter.TripMainViewHolder>(){
        inner class TripMainViewHolder(rowTripMainBinding: RowTripMainBinding) : RecyclerView.ViewHolder(rowTripMainBinding.root) {
            val textViewTripMainRowTitle : TextView // 제목
            val textViewNotificationDate : TextView // 날짜
            val textViewTripMainRowNOP : TextView // 인원
            val textViewTripMainRowLocation : TextView // 지역
            val chipTripMainRowCategory1 : Chip // 카테고리1
            val chipTripMainRowCategory2 : Chip // 카테고리2
            val chipTripMainRowCategory3 : Chip // 카테고리3
            val textViewTripMainRowHashTag : TextView //해시태그

            init {
                textViewTripMainRowTitle = rowTripMainBinding.textViewTripMainRowTitle
                textViewNotificationDate = rowTripMainBinding.textViewNotificationDate
                textViewTripMainRowNOP = rowTripMainBinding.textViewTripMainRowNOP
                textViewTripMainRowLocation = rowTripMainBinding.textViewTripMainRowLocation
                chipTripMainRowCategory1 = rowTripMainBinding.chipTripMainRowCategory1
                chipTripMainRowCategory2 = rowTripMainBinding.chipTripMainRowCategory2
                chipTripMainRowCategory3 = rowTripMainBinding.chipTripMainRowCategory3
                textViewTripMainRowHashTag = rowTripMainBinding.textViewTripMainRowHashTag

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripMainViewHolder {
            val rowTripMainBinding = RowTripMainBinding.inflate(layoutInflater)

            rowTripMainBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return TripMainViewHolder(rowTripMainBinding)
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: TripMainViewHolder, position: Int) {
            if(position % 2 == 1){
                holder.textViewTripMainRowTitle.text = "인천가요"
                holder.textViewNotificationDate.text = "2023-09-26 ~ 2023-10-01"
                holder.textViewTripMainRowNOP.text = "5"
                holder.textViewTripMainRowLocation.text = "인천"
                holder.textViewTripMainRowHashTag.visibility = View.GONE
            }
            else{
                holder.textViewTripMainRowTitle.text = "부산가요"
                holder.textViewNotificationDate.text = "2023-09-26 ~ 2023-10-01"
                holder.textViewTripMainRowNOP.text = "4"
                holder.textViewTripMainRowLocation.text = "부산"
                holder.chipTripMainRowCategory1.text = "맛집탐방"
                holder.chipTripMainRowCategory1.chipIcon = ContextCompat.getDrawable(context!!, R.drawable.restaurant_20px)
                holder.chipTripMainRowCategory2.text = "자연"
                holder.chipTripMainRowCategory2.chipIcon = ContextCompat.getDrawable(context!!, R.drawable.forest_20px)
                holder.chipTripMainRowCategory3.text = "스포츠"
                holder.chipTripMainRowCategory3.chipIcon = ContextCompat.getDrawable(context!!, R.drawable.sports_soccer_20px)
                holder.textViewTripMainRowHashTag.visibility = View.VISIBLE
                holder.textViewTripMainRowHashTag.text = "#국밥충#따로국밥#순대국밥#돼지국밥#소고기국밥#소머리국밥#부산오뎅은?#부산앞바다도가자"
            }

        }
    }
}