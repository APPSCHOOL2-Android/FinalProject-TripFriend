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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentMyAccompanyInfoBinding
import com.test.tripfriend.databinding.MyAccompanyItemRowBinding

class MyAccompanyInfoFragment : Fragment() {
    lateinit var fragmentMyAccompanyInfoBinding: FragmentMyAccompanyInfoBinding
    lateinit var mainActivity: MainActivity
    lateinit var callback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = activity as MainActivity
        fragmentMyAccompanyInfoBinding = FragmentMyAccompanyInfoBinding.inflate(layoutInflater)



        fragmentMyAccompanyInfoBinding.run {

            myAccompanyInfoToolbar.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationIconTint(Color.BLACK)
                //툴바 뒤로가기
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MY_ACCOMPANY_INFO_FRAGMENT)
                    mainActivity.activityMainBinding.bottomNavigationViewMain.visibility=View.VISIBLE
                }
            }

            //내 친구 속도 수치를 textView로 보여주기 위한 작업(프로그래스 thumb를 따라다니도록)
            seekbarFriendSpeed2.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    val padding=seekbarFriendSpeed2.paddingLeft+seekbarFriendSpeed2.paddingRight
                    val sPos = seekbarFriendSpeed2.left+seekbarFriendSpeed2.paddingLeft
                    val xPos = (seekbarFriendSpeed2.width-padding)*seekbarFriendSpeed2.progress/seekbarFriendSpeed2.max+sPos-(textViewFriendSpeed2.width/2)
                    textViewFriendSpeed2.x = xPos.toFloat()
                    textViewFriendSpeed2.text=seekbarFriendSpeed2.progress.toString()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })

            //동행 후기 리사이클러뷰
            recyclerViewMyAccount.run {
                adapter = ReviewItemAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }

        }



        return fragmentMyAccompanyInfoBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val main = activity as MainActivity

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                main.removeFragment(MainActivity.MY_ACCOMPANY_INFO_FRAGMENT)
                main.activityMainBinding.bottomNavigationViewMain.visibility=View.VISIBLE

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }



    inner class ReviewItemAdapter():RecyclerView.Adapter<ReviewItemAdapter.ViewHolder>(){

        inner class ViewHolder(myAccompanyItemRow:MyAccompanyItemRowBinding):RecyclerView.ViewHolder(myAccompanyItemRow.root){
            val textViewReviewerNickName:TextView
            val textViewMyReviewScoreFromPartner:TextView
            val rootViewMyStyleReviewFromPartner:LinearLayout

            init {
                this.textViewReviewerNickName = myAccompanyItemRow.textViewReviewerNickName
                this.textViewMyReviewScoreFromPartner = myAccompanyItemRow.textViewMyReviewScoreFromPartner
                //파트너가 설정한 내 스타일 카드를 addView하기 위한 root뷰
                this.rootViewMyStyleReviewFromPartner = myAccompanyItemRow.rootViewMyStyleReviewFromPartner
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
            return 9
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        }
    }
}