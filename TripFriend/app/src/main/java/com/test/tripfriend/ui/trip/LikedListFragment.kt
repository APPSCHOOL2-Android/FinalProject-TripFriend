package com.test.tripfriend.ui.trip

import android.content.Context
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
import com.test.tripfriend.databinding.FragmentLikedListBinding
import com.test.tripfriend.databinding.RowTripMainBinding
import com.test.tripfriend.dataclassmodel.TripPost
import com.test.tripfriend.repository.UserRepository
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.viewmodel.TripPostViewModel

class LikedListFragment : Fragment() {
    lateinit var fragmentLikedListBinding: FragmentLikedListBinding
    lateinit var mainActivity: MainActivity

    lateinit var tripPostViewModel: TripPostViewModel

    lateinit var currentUserEmail :String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentLikedListBinding = FragmentLikedListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 로그인 중인 사용자 정보
        val sharedPreferences = mainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userClass = UserRepository.getUserInfo(sharedPreferences)
        currentUserEmail = userClass.userEmail

        tripPostViewModel = ViewModelProvider(mainActivity)[TripPostViewModel::class.java]

        tripPostViewModel.tripPostLikedList.observe(viewLifecycleOwner){
            if(it != null) {
                fragmentLikedListBinding.textViewLikedListNoPost.visibility = View.GONE
                (fragmentLikedListBinding.recyclerViewLikedList.adapter as? LikedListAdapter)?.updateItemList(it)
            } else {
                fragmentLikedListBinding.textViewLikedListNoPost.visibility = View.VISIBLE
                fragmentLikedListBinding.textViewLikedListNoPost.text = "찜한 여행이 없습니다."
            }
        }

        tripPostViewModel.getTripPostLikedData(userClass.userEmail)

        fragmentLikedListBinding.run {
            recyclerViewLikedList.run {
                adapter = LikedListAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return fragmentLikedListBinding.root
    }

    inner class LikedListAdapter : RecyclerView.Adapter<LikedListAdapter.LikedListViewHolder>() {
        var tripPostItemList :List<TripPost> = emptyList()

        fun updateItemList(newList: List<TripPost>) {
            this.tripPostItemList = newList
            notifyDataSetChanged()
        }
        inner class LikedListViewHolder(rowTripMainBinding: RowTripMainBinding) : RecyclerView.ViewHolder(rowTripMainBinding.root) {
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
                    newBundle.putString("tripPostWriterEmail", tripPostItemList[adapterPosition].tripPostWriterEmail) // 작성자 이메일
                    newBundle.putString("tripPostDocumentId", tripPostItemList[adapterPosition].tripPostDocumentId)   // 문서아이디
                    newBundle.putString("viewState", "LikedList") // 어느 화면에서 왔는지 확인
                    newBundle.putString("endDate", tripPostItemList[adapterPosition].tripPostDate?.get(1))
                    val memberList:ArrayList<String> = tripPostItemList[adapterPosition].tripPostMemberList as ArrayList<String>
                    newBundle.putStringArrayList("memberList",memberList)
                    var memberCheck = 1
                    if (memberList != null) {
                        for (member in memberList) {
                            Log.d("aaaa", "member1 = $member")
                            memberCheck = 0
                        }
                    }

                    mainActivity.replaceFragment(MainActivity.READ_POST_FRAGMENT,true,true, newBundle)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedListViewHolder {
            val rowTripMainBinding = RowTripMainBinding.inflate(layoutInflater)

            rowTripMainBinding.root.run{
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            return LikedListViewHolder(rowTripMainBinding)
        }

        override fun getItemCount(): Int {
            return tripPostItemList.size
        }

        override fun onBindViewHolder(holder: LikedListViewHolder, position: Int) {
            holder.textViewTripMainRowTitle.text = tripPostItemList[position].tripPostTitle

            if(tripPostItemList[position].tripPostDate!![1] == null) {
                holder.textViewNotificationDate.text = tripPostItemList[position].tripPostDate!![0]
            } else {
                holder.textViewNotificationDate.text =
                    "${formatDate(tripPostItemList[position].tripPostDate!![0])} ~ ${formatDate(tripPostItemList[position].tripPostDate!![1])}"
            }

            holder.textViewTripMainRowNOP.text = tripPostItemList[position].tripPostMemberCount.toString()
            holder.textViewTripMainRowLocation.text = tripPostItemList[position].tripPostLocationName

            when(tripPostItemList[position].tripPostTripCategory!!.size) {
                1 -> {
                    holder.chipTripMainRowCategory1.text = tripPostItemList[position].tripPostTripCategory!![0]
                    holder.chipTripMainRowCategory1.chipIcon = chipIcon(tripPostItemList[position].tripPostTripCategory!![0])
                    holder.chipTripMainRowCategory2.visibility = View.INVISIBLE
                    holder.chipTripMainRowCategory3.visibility = View.INVISIBLE
                }
                2 -> {
                    holder.chipTripMainRowCategory1.text = tripPostItemList[position].tripPostTripCategory!![0]
                    holder.chipTripMainRowCategory1.chipIcon = chipIcon(tripPostItemList[position].tripPostTripCategory!![0])
                    holder.chipTripMainRowCategory2.text = tripPostItemList[position].tripPostTripCategory!![1]
                    holder.chipTripMainRowCategory2.chipIcon = chipIcon(tripPostItemList[position].tripPostTripCategory!![1])
                    holder.chipTripMainRowCategory3.visibility = View.INVISIBLE
                }
                3 -> {
                    holder.chipTripMainRowCategory1.text = tripPostItemList[position].tripPostTripCategory!![0]
                    holder.chipTripMainRowCategory1.chipIcon = chipIcon(tripPostItemList[position].tripPostTripCategory!![0])
                    holder.chipTripMainRowCategory2.text = tripPostItemList[position].tripPostTripCategory!![1]
                    holder.chipTripMainRowCategory2.chipIcon = chipIcon(tripPostItemList[position].tripPostTripCategory!![1])
                    holder.chipTripMainRowCategory3.text = tripPostItemList[position].tripPostTripCategory!![2]
                    holder.chipTripMainRowCategory3.chipIcon = chipIcon(tripPostItemList[position].tripPostTripCategory!![2])
                }
                else -> {
                    holder.chipTripMainRowCategory1.visibility = View.INVISIBLE
                    holder.chipTripMainRowCategory2.visibility = View.INVISIBLE
                    holder.chipTripMainRowCategory3.visibility = View.INVISIBLE
                }
            }

            holder.textViewTripMainRowHashTag.visibility = View.VISIBLE
            holder.textViewTripMainRowHashTag.text = tripPostItemList[position].tripPostHashTag
            holder.textViewTripMainRowLikedCount.text = tripPostItemList[position].tripPostLiked!!.size.toString()
        }

    }

    // chip 아이콘
    fun chipIcon(chipCategory: String): Drawable? {
        var drawable: Drawable? = null
        when(chipCategory) {
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
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.sports_soccer_20px)
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
        mainActivity.tripMainPosition = 2
        tripPostViewModel = ViewModelProvider(mainActivity)[TripPostViewModel::class.java]
        tripPostViewModel.getTripPostLikedData(currentUserEmail)
        Log.d("aaaa","Liked onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("aaaa","Liked onPause")
    }
}