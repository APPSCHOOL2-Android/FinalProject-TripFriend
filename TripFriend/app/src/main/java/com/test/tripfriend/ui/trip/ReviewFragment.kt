package com.test.tripfriend.ui.trip

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentReviewBinding
import com.test.tripfriend.databinding.RowReviewBinding
import com.test.tripfriend.dataclassmodel.ReviewContentState
import com.test.tripfriend.dataclassmodel.TripMemberInfo
import com.test.tripfriend.viewmodel.ReviewViewModel

class ReviewFragment : Fragment() {
    lateinit var fragmentReviewBinding: FragmentReviewBinding
    lateinit var mainActivity: MainActivity
    lateinit var reviewViewModel: ReviewViewModel

    //번들로 받은 리스트 이곳에 초기화
    lateinit var memberList: MutableList<String>
    lateinit var myEmail:String
    lateinit var tripPostDocumentId:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mainActivity = activity as MainActivity
        fragmentReviewBinding = FragmentReviewBinding.inflate(layoutInflater)

        //여기서 넘어온 멤버 리스트 번들값을 받고 전역 변수들 초기화한다.
        memberList = arguments?.getStringArrayList("tripPostMemberList")!!
        myEmail = mainActivity.userClass.userEmail
        tripPostDocumentId = arguments?.getString("tripPostDocumentId")!!

        reviewViewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
        reviewViewModel.run {
            userInfoList.observe(viewLifecycleOwner) {
                (fragmentReviewBinding.recyclerViewReview.adapter as ReviewAdapter).updateItemList(
                    it
                )
            }
            saveState.observe(viewLifecycleOwner){
                Snackbar.make(fragmentReviewBinding.root, "리뷰 작성이 완료되었습니다", Snackbar.LENGTH_SHORT).show()
                mainActivity.removeFragment(MainActivity.REVIEW_FRAGMENT)
                //화면 종료(리무브)
            }

            getUserInfo(memberList, mainActivity.userClass.userNickname)
        }

        fragmentReviewBinding.run {
            textViewReviewPostTitle.text = "${arguments?.getString("tripPostTitle") ?:""}\n동행자 리뷰"
            materialToolbar2.run {
                setNavigationIcon(R.drawable.arrow_back_24px)
                //백버튼 색깔 지정
//                setNavigationIconTint(getResources().getColor(R.color.highLightColor))
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.REVIEW_FRAGMENT)
                }
                setOnMenuItemClickListener {
                    var state:Boolean = true
                    //칩이 모두 잘 선택이 됐는지 확인하는 작업
                    for (c1 in (fragmentReviewBinding.recyclerViewReview.adapter as ReviewAdapter).reviewResultList) {
                        for (chip in c1.tripReviewStyle) {
                            if (chip==null) {
                                state = false
                            }
                        }

                    }
                    //유효성 검사
                    if (state) {
                        //데이터 저장
                        val builder= MaterialAlertDialogBuilder(mainActivity,R.style.DialogTheme).apply {
                            setMessage("🎅해당 리뷰는 동행자의 동행 점수에 큰 영향이 미칠 수 있습니다. 리뷰 작성을 완료합니다")
                            setNegativeButton("취소", null)
                            setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
                                reviewViewModel.saveToReview((fragmentReviewBinding.recyclerViewReview.adapter as ReviewAdapter).reviewResultList)
                            }
                        }
                        builder.show()

                    } else {
                        //모두 설정이 안됐다면 다이얼로그 띄움
                        val builder= MaterialAlertDialogBuilder(mainActivity,R.style.DialogTheme).apply {
                            setTitle("저장 실패!")
                            setMessage("각 동행자의 여행스타일 5가지를 모두 선택해주세요!")
                            setNegativeButton("확인", null)
                        }
                        builder.show()
                        return@setOnMenuItemClickListener true
                    }
                    true
                }
            }

            recyclerViewReview.run {
                var reviewStateList =
                    Array<ReviewContentState>(memberList.size - 1) { i -> ReviewContentState() }
                adapter = ReviewAdapter(reviewStateList)
                layoutManager = LinearLayoutManager(mainActivity)
            }

            //바텀 네비 가리기
            mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
        }

        return fragmentReviewBinding.root
    }

    inner class ReviewAdapter(reviewList: Array<ReviewContentState>) :
        RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
        var reviewResultList = reviewList

        //보여줄 데이터 정보
        private var itemList = mutableListOf<TripMemberInfo>()

        //데이터를 가져와서 업데이트하기 위한 메서드
        fun updateItemList(newList: MutableList<TripMemberInfo>) {
            //변경된 사항이 날아오므로 add를 수행
            this.itemList = newList

            notifyDataSetChanged() // 갱신
        }

        inner class ReviewViewHolder(rowReviewBinding: RowReviewBinding) :
            RecyclerView.ViewHolder(rowReviewBinding.root) {
            //            val imageViewReviewUserProfileImage : ImageView
            val textViewReviewUserNickname: TextView // 유저 이름 포함 "~님에 대한 리뷰를 남겨주세요"
            val seekBarReviewScore: SeekBar // 점수
            val textViewReviewScore: TextView //몇점인지?
            val chipGroup1: ChipGroup
            val chipGroup2: ChipGroup
            val chipGroup3: ChipGroup
            val chipGroup4: ChipGroup
            val chipGroup5: ChipGroup
            val chipReview1: Chip
            val chipReview2: Chip
            val chipReview3: Chip
            val chipReview4: Chip
            val chipReview5: Chip
            val chipReview6: Chip
            val chipReview7: Chip
            val chipReview8: Chip
            val chipReview9: Chip
            val chipReview10: Chip

            init {
                textViewReviewUserNickname = rowReviewBinding.textViewReviewUserNickname
                seekBarReviewScore = rowReviewBinding.seekBarReviewScore
                textViewReviewScore = rowReviewBinding.textViewReviewScore
                chipGroup1 = rowReviewBinding.chipGroup1
                chipGroup2 = rowReviewBinding.chipGroup2
                chipGroup3 = rowReviewBinding.chipGroup3
                chipGroup4 = rowReviewBinding.chipGroup4
                chipGroup5 = rowReviewBinding.chipGroup5
                chipReview1 = rowReviewBinding.chipReview1
                chipReview2 = rowReviewBinding.chipReview2
                chipReview3 = rowReviewBinding.chipReview3
                chipReview4 = rowReviewBinding.chipReview4
                chipReview5 = rowReviewBinding.chipReview5
                chipReview6 = rowReviewBinding.chipReview6
                chipReview7 = rowReviewBinding.chipReview7
                chipReview8 = rowReviewBinding.chipReview8
                chipReview9 = rowReviewBinding.chipReview9
                chipReview10 = rowReviewBinding.chipReview10

                rowReviewBinding.run {
                    seekBarReviewScore.run {
                        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            //progress : 새롭게 설정된 값
                            //fromUser : 사용자가 변경한 것인지 여부
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean,
                            ) {
                                textViewReviewScore.text = "${progress}/10"
                                //속도 설정
                                reviewResultList[adapterPosition].tripReviewScore = progress
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                        })
                    }


                    chipGroup1.setOnCheckedStateChangeListener { group, checkedIds ->
                        if (chipReview1.isChecked) {
                            reviewResultList[adapterPosition].tripReviewStyle[0] =
                                chipReview1.text.toString()

                        } else if (chipReview2.isChecked) {
                            reviewResultList[adapterPosition].tripReviewStyle[0] =
                                chipReview2.text.toString()
                        } else {
                            reviewResultList[adapterPosition].tripReviewStyle[0] = null
                        }
                    }
                    chipGroup2.setOnCheckedStateChangeListener { group, checkedIds ->
                        if (chipReview3.isChecked) {
                            reviewResultList[adapterPosition].tripReviewStyle[1] =
                                chipReview3.text.toString()
                        } else if (chipReview4.isChecked) {
                            reviewResultList[adapterPosition].tripReviewStyle[1] =
                                chipReview4.text.toString()
                        } else {
                            reviewResultList[adapterPosition].tripReviewStyle[1] = null
                        }
                    }
                    chipGroup3.setOnCheckedStateChangeListener { group, checkedIds ->
                        if (chipReview5.isChecked) {
                            reviewResultList[adapterPosition].tripReviewStyle[2] =
                                chipReview5.text.toString()
                        } else if (chipReview6.isChecked) {
                            reviewResultList[adapterPosition].tripReviewStyle[2] =
                                chipReview6.text.toString()
                        } else {
                            reviewResultList[adapterPosition].tripReviewStyle[2] = null
                        }
                    }
                    chipGroup4.setOnCheckedStateChangeListener { group, checkedIds ->
                        if (chipReview7.isChecked) {
                            reviewResultList[adapterPosition].tripReviewStyle[3] =
                                chipReview7.text.toString()
                        } else if (chipReview8.isChecked) {
                            reviewResultList[adapterPosition].tripReviewStyle[3] =
                                chipReview8.text.toString()
                        } else {
                            reviewResultList[adapterPosition].tripReviewStyle[3] = null
                        }
                    }
                    chipGroup5.setOnCheckedStateChangeListener { group, checkedIds ->
                        if (chipReview9.isChecked) {
                            reviewResultList[adapterPosition].tripReviewStyle[4] =
                                chipReview9.text.toString()
                        } else if (chipReview10.isChecked) {
                            reviewResultList[adapterPosition].tripReviewStyle[4] =
                                chipReview10.text.toString()
                        } else {
                            reviewResultList[adapterPosition].tripReviewStyle[4] = null
                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
            val rowReviewBinding = RowReviewBinding.inflate(layoutInflater)

            rowReviewBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return ReviewViewHolder(rowReviewBinding)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
            //db에 저장할 나머지 정보 저장
            reviewResultList[position].tripReviewTargetUserEmail=itemList[position].userEmail.toString()
            reviewResultList[position].tripReviewWriterEmail=myEmail
            reviewResultList[position].tripReviewPostId=tripPostDocumentId

            holder.textViewReviewUserNickname.text =
                "${itemList[position].userNickname}님에 대한 리뷰를 남겨주세요!"
        }
    }

    //뒤로가기 했을 때 바텀네비 다시 보여주기
    override fun onPause() {
        super.onPause()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }
}