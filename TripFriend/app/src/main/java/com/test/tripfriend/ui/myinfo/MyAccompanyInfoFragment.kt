package com.test.tripfriend.ui.myinfo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.google.android.material.chip.Chip
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentMyAccompanyInfoBinding
import com.test.tripfriend.databinding.MyAccompanyItemRowBinding
import com.test.tripfriend.dataclassmodel.TripReview
import com.test.tripfriend.viewmodel.TripReviewViewModel
import com.test.tripfriend.viewmodel.UserViewModel
import java.lang.NullPointerException

class MyAccompanyInfoFragment : Fragment() {

    lateinit var fragmentMyAccompanyInfoBinding: FragmentMyAccompanyInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var callback: OnBackPressedCallback

    lateinit var userViewModel : UserViewModel
    lateinit var tripReviewViewModel: TripReviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        mainActivity = activity as MainActivity
        fragmentMyAccompanyInfoBinding = FragmentMyAccompanyInfoBinding.inflate(layoutInflater)

        //로그인 된 유저의 정보로 변경 필
        val userEmail = getUserEmail()

        fragmentMyAccompanyInfoBinding.run {

            myAccompanyInfoToolbar.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationIconTint(Color.BLACK)
                //툴바 뒤로가기
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MY_ACCOMPANY_INFO_FRAGMENT)
                    mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
                }
            }

            //동행 후기 리사이클러뷰
            recyclerViewMyAccountInfo.run {
                adapter = ReviewItemAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }

            seekbarMyAccompanyInfoFriendSpeed.isEnabled = false

        }

        initUserViewModel(userEmail, "이메일")
        initReviewViewModel(userEmail)

        return fragmentMyAccompanyInfoBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val main = activity as MainActivity

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                main.removeFragment(MainActivity.MY_ACCOMPANY_INFO_FRAGMENT)
                main.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }


    inner class ReviewItemAdapter : RecyclerView.Adapter<ReviewItemAdapter.ViewHolder>(){

        private var reviewItemList : List<TripReview> = emptyList()

        fun updateItemList(newList : List<TripReview>){
            this.reviewItemList = newList
            notifyDataSetChanged()
        }

        inner class ViewHolder(myAccompanyItemRow : MyAccompanyItemRowBinding) : RecyclerView.ViewHolder(myAccompanyItemRow.root){
            val textViewReviewerNickName : TextView
            val textViewMyReviewScoreFromPartner : TextView
            val chipRowLaziness : Chip
            val chipRowPlan : Chip
            val chipRowActive : Chip
            val chipRowNeatness : Chip
            val chipRowLoudness : Chip

            init {
                this.textViewReviewerNickName = myAccompanyItemRow.textViewReviewerNickName
                this.textViewMyReviewScoreFromPartner = myAccompanyItemRow.textViewMyReviewScoreFromPartner
                this.chipRowLaziness = myAccompanyItemRow.chipRowLaziness
                this.chipRowPlan = myAccompanyItemRow.chipRowPlan
                this.chipRowActive = myAccompanyItemRow.chipRowActive
                this.chipRowNeatness = myAccompanyItemRow.chipRowNeatness
                this.chipRowLoudness = myAccompanyItemRow.chipRowLoudness
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val myAccompanyItemRow = MyAccompanyItemRowBinding.inflate(layoutInflater)
            myAccompanyItemRow.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return ViewHolder(myAccompanyItemRow)
        }

        override fun getItemCount(): Int {
            return reviewItemList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewMyReviewScoreFromPartner.text = reviewItemList[position].tripReviewScore.toString()
            holder.chipRowLaziness.text = reviewItemList[position].tripReviewStyle[0]
            holder.chipRowPlan.text = reviewItemList[position].tripReviewStyle[1]
            holder.chipRowActive.text = reviewItemList[position].tripReviewStyle[2]
            holder.chipRowNeatness.text = reviewItemList[position].tripReviewStyle[3]
            holder.chipRowLoudness.text = reviewItemList[position].tripReviewStyle[4]
            holder.textViewReviewerNickName.text = setReviewWriterNickname(reviewItemList[position].tripReviewWriterEmail)
        }
    }

    fun initUserViewModel(userEmail : String, userAuthentication : String) {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.getTargetUserData(userEmail, userAuthentication)

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            if(user != null){
                fragmentMyAccompanyInfoBinding.run {
                    textViewMyAccompanyInfoFriendSpeed.text = "${user.userFriendSpeed} km"
                    seekbarMyAccompanyInfoFriendSpeed.progress = user.userFriendSpeed.toInt()
                    formatFriendSpeed(user.userTripScore)
                    textViewMyAccompanyInfoCount.text = user.userTripCount.toString()
                }
            }
        }
    }

    fun initReviewViewModel(userEmail : String){
        tripReviewViewModel = ViewModelProvider(this)[TripReviewViewModel::class.java]
        tripReviewViewModel.getTargetUserReviews(userEmail)

        tripReviewViewModel.userReviewList.observe(viewLifecycleOwner){ reviewList ->
            if(reviewList != null){
                (fragmentMyAccompanyInfoBinding.recyclerViewMyAccountInfo.adapter as? ReviewItemAdapter)?.updateItemList(reviewList)
                fragmentMyAccompanyInfoBinding.textViewMyAccompanyInfoReviewTotalCount.setText(reviewList.size.toString())
            }
        }
    }

    fun setReviewWriterNickname(reviewWriterEmail : String) : String{
        tripReviewViewModel.getReviewWriterNickname(reviewWriterEmail)

        var reviewerNickname = ""

        tripReviewViewModel.reviewWriter.observe(viewLifecycleOwner){ nickName ->
            if(nickName != null){
                reviewerNickname = nickName
            }
        }
        return reviewerNickname
    }

    fun formatFriendSpeed(userTripScore : Double){
        fragmentMyAccompanyInfoBinding.textViewMyAccompanyInfoScore.text = when(userTripScore){
            0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0 ->
                "${userTripScore.toInt()}"
            else -> "$userTripScore"
        }
    }

    fun getUserEmail() : String{
        try {
            val bundleUserEmail = arguments?.getString("userEmail")!!
            val bundleUserNickname = arguments?.getString("userNickname")!!

            fragmentMyAccompanyInfoBinding.textViewToolbarTitle.setText(bundleUserNickname)

            return bundleUserEmail
        }catch (e : NullPointerException){
            return mainActivity.userClass.userEmail
        }
    }
}