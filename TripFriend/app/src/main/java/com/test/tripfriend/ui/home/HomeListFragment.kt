package com.test.tripfriend.ui.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.test.tripfriend.R
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.databinding.FragmentHomeListBinding
import com.test.tripfriend.databinding.RowHomeListBinding
import com.test.tripfriend.databinding.RowTripMainBinding
import com.test.tripfriend.dataclassmodel.TripPost
import com.test.tripfriend.ui.trip.InProgressFragment
import com.test.tripfriend.ui.trip.TripMainFragment
import com.test.tripfriend.viewmodel.HomeViewModel
import com.test.tripfriend.viewmodel.TripPostViewModel

class HomeListFragment : Fragment() {
    lateinit var fragmentHomeListBinding: FragmentHomeListBinding
    lateinit var mainActivity: MainActivity

    lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeListBinding = FragmentHomeListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initHomeViewModel()

        fragmentHomeListBinding.run {
            recyclerViewHomeList.run {
                adapter = HomeListAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return fragmentHomeListBinding.root
    }

    inner class HomeListAdapter : RecyclerView.Adapter<HomeListAdapter.HomeListViewHolder>() {
        var homePostItemList: List<TripPost> = emptyList()

        fun updateItemList(newList: List<TripPost>) {
            this.homePostItemList = newList
            notifyDataSetChanged()
        }

//        inner class HomeListViewHolder(rowHomeListBinding: RowHomeListBinding) : RecyclerView.ViewHolder(rowHomeListBinding.root) {
//            val textViewHomeMainListRowTitle: TextView
//            val textViewHomeMainListDate: TextView
//            val textViewHomeMainListRowNOP: TextView
//            val textViewHomeMainListRowLocation: TextView
//            val chipHomeMainListRowCategory1: Chip // 카테고리1
//            val chipHomeMainListRowCategory2: Chip // 카테고리2
//            val chipHomeMainListRowCategory3: Chip // 카테고리3
//            val textViewHomeMainListRowHashTag: TextView
//
//            init {
//                textViewHomeMainListRowTitle = rowHomeListBinding.textViewHomeMainListRowTitle
//                textViewHomeMainListDate = rowHomeListBinding.textViewHomeMainListDate
//                textViewHomeMainListRowNOP = rowHomeListBinding.textViewHomeMainListRowNOP
//                textViewHomeMainListRowLocation = rowHomeListBinding.textViewHomeMainListRowLocation
//                chipHomeMainListRowCategory1: rowHomeListBinding.chipHomeMainListRowCategory1
//                chipHomeMainListRowCategory2: rowHomeListBinding.chipHomeMainListRowCategory2
//                chipHomeMainListRowCategory3: rowHomeListBinding.chipHomeMainListRowCategory3
//                textViewHomeMainListRowHashTag = rowHomeListBinding.textViewHomeMainListRowHashTag
//
//                rowHomeListBinding.root.setOnClickListener {
//                    mainActivity.replaceFragment(MainActivity.READ_POST_FRAGMENT, true, true, null)
//
//                }
//            }
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListViewHolder {
//            val rowHomeListBinding = RowHomeListBinding.inflate(layoutInflater)
//
//            rowHomeListBinding.root.layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//            )
//            return HomeListViewHolder(rowHomeListBinding)
//        }
//
//        override fun getItemCount(): Int {
//            return homePostItemList.size
//        }
//
//        override fun onBindViewHolder(holder: HomeListViewHolder, position: Int) {
//            holder.textViewHomeMainListRowTitle.text = homePostItemList.get(position).tripPostTitle
//            holder.textViewHomeMainListDate.text = "${homePostItemList.get(position).tripPostDate?.get(0)} ~ ${homePostItemList.get(position).tripPostDate?.get(1)}"
//            holder.textViewHomeMainListRowNOP.text = homePostItemList.get(position).tripPostMemberCount.toString()
//            holder.textViewHomeMainListRowLocation.text = homePostItemList.get(position).tripPostLocationName
//            holder.textViewHomeMainListRowHashTag.text = homePostItemList.get(position).tripPostHashTag
//        }

        inner class HomeListViewHolder(rowTripMainBinding: RowTripMainBinding) :
            RecyclerView.ViewHolder(rowTripMainBinding.root) {
            val textViewTripMainRowTitle: TextView // 제목
            val textViewNotificationDate: TextView // 날짜
            val textViewTripMainRowNOP: TextView // 인원
            val textViewTripMainRowLocation: TextView // 지역
            val chipTripMainRowCategory1: Chip // 카테고리1
            val chipTripMainRowCategory2: Chip // 카테고리2
            val chipTripMainRowCategory3: Chip // 카테고리3
            val textViewTripMainRowHashTag: TextView // 해시태그
            val textViewTripMainRowLikedCount: TextView // 좋아요 수

            init {
                textViewTripMainRowTitle = rowTripMainBinding.textViewTripMainRowTitle
                textViewNotificationDate = rowTripMainBinding.textViewNotificationDate
                textViewTripMainRowNOP = rowTripMainBinding.textViewTripMainRowNOP
                textViewTripMainRowLocation = rowTripMainBinding.textViewTripMainRowLocation
                chipTripMainRowCategory1 = rowTripMainBinding.chipTripMainRowCategory1
                chipTripMainRowCategory2 = rowTripMainBinding.chipTripMainRowCategory2
                chipTripMainRowCategory3 = rowTripMainBinding.chipTripMainRowCategory3
                textViewTripMainRowHashTag = rowTripMainBinding.textViewTripMainRowHashTag
                textViewTripMainRowLikedCount = rowTripMainBinding.textViewTripMainRowLikedCount

                rowTripMainBinding.root.setOnClickListener {

                    val newBundle = Bundle()
                    newBundle.putString("tripPostWriterEmail", homePostItemList[adapterPosition].tripPostWriterEmail) // 작성자 이메일
                    newBundle.putString("tripPostDocumentId", homePostItemList[adapterPosition].tripPostDocumentId)   // 문서아이디

                    mainActivity.replaceFragment(MainActivity.READ_POST_FRAGMENT, true, true, newBundle)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListViewHolder {
            val rowTripMainBinding = RowTripMainBinding.inflate(layoutInflater)

            rowTripMainBinding.root.run {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            return HomeListViewHolder(rowTripMainBinding)
        }

        override fun getItemCount(): Int {
            return homePostItemList.size
        }

        override fun onBindViewHolder(holder: HomeListViewHolder, position: Int) {
            holder.textViewTripMainRowTitle.text = homePostItemList[position].tripPostTitle

            if (homePostItemList[position].tripPostDate!![1] == null) {
                holder.textViewNotificationDate.text = formatDate(homePostItemList[position].tripPostDate!![0])
            } else {
                holder.textViewNotificationDate.text =
                    "${formatDate(homePostItemList[position].tripPostDate!![0])} ~ ${
                        formatDate(homePostItemList[position].tripPostDate!![1])
                    }"
            }

            holder.textViewTripMainRowNOP.text =
                homePostItemList[position].tripPostMemberCount.toString()
            holder.textViewTripMainRowLocation.text =
                homePostItemList[position].tripPostLocationName

            when (homePostItemList[position].tripPostTripCategory!!.size) {
                1 -> {
                    holder.chipTripMainRowCategory1.text =
                        homePostItemList[position].tripPostTripCategory!![0]
                    holder.chipTripMainRowCategory1.chipIcon =
                        chipIcon(homePostItemList[position].tripPostTripCategory!![0])
                    holder.chipTripMainRowCategory2.visibility = View.INVISIBLE
                    holder.chipTripMainRowCategory3.visibility = View.INVISIBLE
                }

                2 -> {
                    holder.chipTripMainRowCategory1.text =
                        homePostItemList[position].tripPostTripCategory!![0]
                    holder.chipTripMainRowCategory1.chipIcon =
                        chipIcon(homePostItemList[position].tripPostTripCategory!![0])
                    holder.chipTripMainRowCategory2.text =
                        homePostItemList[position].tripPostTripCategory!![1]
                    holder.chipTripMainRowCategory2.chipIcon =
                        chipIcon(homePostItemList[position].tripPostTripCategory!![1])
                    holder.chipTripMainRowCategory3.visibility = View.INVISIBLE
                }

                3 -> {
                    holder.chipTripMainRowCategory1.text =
                        homePostItemList[position].tripPostTripCategory!![0]
                    holder.chipTripMainRowCategory1.chipIcon =
                        chipIcon(homePostItemList[position].tripPostTripCategory!![0])
                    holder.chipTripMainRowCategory2.text =
                        homePostItemList[position].tripPostTripCategory!![1]
                    holder.chipTripMainRowCategory2.chipIcon =
                        chipIcon(homePostItemList[position].tripPostTripCategory!![1])
                    holder.chipTripMainRowCategory3.text =
                        homePostItemList[position].tripPostTripCategory!![2]
                    holder.chipTripMainRowCategory3.chipIcon =
                        chipIcon(homePostItemList[position].tripPostTripCategory!![2])
                }

                else -> {
                    holder.chipTripMainRowCategory1.visibility = View.INVISIBLE
                    holder.chipTripMainRowCategory2.visibility = View.INVISIBLE
                    holder.chipTripMainRowCategory3.visibility = View.INVISIBLE
                }
            }

            holder.textViewTripMainRowHashTag.visibility = View.VISIBLE
            holder.textViewTripMainRowHashTag.text = homePostItemList[position].tripPostHashTag
            holder.textViewTripMainRowLikedCount.text =
                homePostItemList[position].tripPostLiked!!.size.toString()
        }
    }

    fun initHomeViewModel() {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.getTripPostData()

        homeViewModel.tripPostList.observe(viewLifecycleOwner) {
//            fragmentHomeListBinding.textViewHomeListNoPost.visibility = View.GONE
//            (fragmentHomeListBinding.recyclerViewHomeList.adapter as? HomeListAdapter)?.updateItemList(it)
            if(it != null) {
                fragmentHomeListBinding.textViewHomeListNoPost.visibility = View.GONE
                (fragmentHomeListBinding.recyclerViewHomeList.adapter as? HomeListAdapter)?.updateItemList(it)
            } else {
                fragmentHomeListBinding.textViewHomeListNoPost.visibility = View.VISIBLE
                fragmentHomeListBinding.textViewHomeListNoPost.text = "동행 중인 여행이 없습니다."
            }
        }
    }

    // chip 아이콘
    fun chipIcon(chipCategory: String): Drawable? {
        var drawable: Drawable? = null
        when (chipCategory) {
            "맛집 탐방" -> {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.restaurant_20px)
            }

            "휴양" -> {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.breaktime_20px)
            }

            "관광" -> {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.tour_20px)
            }

            "축제" -> {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.festival_20px)
            }

            "자연" -> {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.forest_20px)
            }

            "쇼핑" -> {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.shopping_bag_20px)
            }

            "액티비티" -> {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.activity_20px)
            }

            "사진촬영" -> {
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.photo_camera_20px)
            }

            "스포츠" -> {
                drawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.sports_soccer_20px)
            }
        }
        return drawable
    }

    // 날짜 형식 변환
    fun formatDate(date: String): String {
        if (date != "") {
            val year = date.substring(0, 4)
            val month = date.substring(4, 6)
            val day = date.substring(6, 8)

            val formattedDate = "$year-$month-$day"

            return formattedDate
        }
        return ""
    }

    override fun onResume() {
        super.onResume()
        initHomeViewModel()
        Log.d("qwer", "listFragment onResume")
    }

    override fun onStop() {
        super.onStop()
        Log.d("qwer", "listFragment onStop")
    }

}