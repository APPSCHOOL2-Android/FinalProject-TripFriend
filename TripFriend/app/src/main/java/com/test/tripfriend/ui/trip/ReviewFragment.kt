package com.test.tripfriend.ui.trip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentReviewBinding
import com.test.tripfriend.databinding.RowReviewBinding

class ReviewFragment : Fragment() {
    lateinit var fragmentReviewBinding: FragmentReviewBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentReviewBinding = FragmentReviewBinding.inflate(layoutInflater)

        fragmentReviewBinding.run {
            textViewReviewPostTitle.text = "화성 여행\n동행자 리뷰"
            materialToolbar2.run {
                setNavigationIcon(R.drawable.arrow_back_24px)
                //백버튼 색깔 지정
//                setNavigationIconTint(getResources().getColor(R.color.highLightColor))
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.REVIEW_FRAGMENT)
                }
                setOnMenuItemClickListener {
                    true
                }
            }
            recyclerViewReview.run {
                adapter = ReviewAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }
            //바텀 네비 가리기
            mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
        }

        return fragmentReviewBinding.root
    }
    inner class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>(){
        inner class ReviewViewHolder(rowReviewBinding: RowReviewBinding) : RecyclerView.ViewHolder(rowReviewBinding.root) {
//            val imageViewReviewUserProfileImage : ImageView
            val textViewReviewUserNickname : TextView // 유저 이름 포함 "~님에 대한 리뷰를 남겨주세요"
            val seekBarReviewScore :SeekBar // 점수
            val textViewReviewScore : TextView //몇점인지?
            val chipReview1 : Chip
            val chipReview2 : Chip
            val chipReview3 : Chip
            val chipReview4 : Chip
            val chipReview5 : Chip
            val chipReview6 : Chip
            val chipReview7 : Chip
            val chipReview8 : Chip
            val chipReview9 : Chip
            val chipReview10 : Chip

            init {
                textViewReviewUserNickname = rowReviewBinding.textViewReviewUserNickname
                seekBarReviewScore = rowReviewBinding.seekBarReviewScore
                textViewReviewScore = rowReviewBinding.textViewReviewScore
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
                                fromUser: Boolean
                            ) {
                                textViewReviewScore.text = "${progress}/10"
                            }
                            override fun onStartTrackingTouch(seekBar: SeekBar?){}
                            override fun onStopTrackingTouch(seekBar: SeekBar?){
                            }
                        })
                    }
                    chipReview1.setOnClickListener {
                        chipReview2.isChecked = false
                    }
                    chipReview2.setOnClickListener {
                        chipReview1.isChecked = false
                    }
                    chipReview3.setOnClickListener {
                        chipReview4.isChecked = false
                    }
                    chipReview4.setOnClickListener {
                        chipReview3.isChecked = false
                    }
                    chipReview5.setOnClickListener {
                        chipReview6.isChecked = false
                    }
                    chipReview6.setOnClickListener {
                        chipReview5.isChecked = false
                    }
                    chipReview7.setOnClickListener {
                        chipReview8.isChecked = false
                    }
                    chipReview8.setOnClickListener {
                        chipReview7.isChecked = false
                    }
                    chipReview9.setOnClickListener {
                        chipReview10.isChecked = false
                    }
                    chipReview10.setOnClickListener {
                        chipReview9.isChecked = false
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
            return 2
        }

        override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
            holder.textViewReviewUserNickname.text = "dragonjean님에 대한 리뷰를 남겨주세요!"
        }
    }

    //뒤로가기 했을 때 바텀네비 다시 보여주기
    override fun onPause() {
        super.onPause()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }
}