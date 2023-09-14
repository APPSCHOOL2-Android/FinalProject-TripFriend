package com.test.tripfriend.ui.trip

import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.DialogSubmitBinding
import com.test.tripfriend.databinding.FragmentReadPostBinding
import kotlin.concurrent.thread

class ReadPostFragment : Fragment() {
    lateinit var fragmentReadPostBinding : FragmentReadPostBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentReadPostBinding = FragmentReadPostBinding.inflate(layoutInflater)

        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
        fragmentReadPostBinding.run {
            materialToolbarReadPost.run {
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.READ_POST_FRAGMENT)
                }

//                // 메뉴를 보이게 하려면
//                var toolbar = findViewById<MaterialToolbar>(R.id.materialToolbarReadPost)
//                toolbar.menu.findItem(R.id.menu_item_modify).isVisible = true
//
//                // 메뉴를 숨기려면
//                toolbar = findViewById<MaterialToolbar>(R.id.materialToolbarReadPost)
//                toolbar.menu.findItem(R.id.menu_item_delete).isVisible = false

                setOnMenuItemClickListener {
                    when(it.itemId){
                        //삭제
                        R.id.menu_item_delete->{
                            val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme)
                            builder.run {
                                setTitle("게시글 삭제")
                                setMessage("게시글을 삭제하시면 관련된 정보 및 그룹채팅이 삭제 됩니다.")
                                setPositiveButton("삭제") { dialogInterface: DialogInterface, i: Int ->

                                }
                                setNegativeButton("취소", null)
                                show()
                            }
                        }
                        //수정
                        R.id.menu_item_modify ->{
                            mainActivity.replaceFragment(MainActivity.MODFY_POST_FRAGMENT,true,true,null)
                        }
                    }
                    true
                }
            }

            //imageViewUserProfileImage.run { }
            //imageViewReadPostMainImage.run{}

            textViewShowProfile.run{
                isClickable = true
                setOnClickListener {
                    textViewReadPostToolbarTitle.text = "프로필 보기"
                }
            }

            textViewUserNickname.text = "일론 머스크"

            textViewUserMBTI.text = "CUTE"

            textViewReadPostTitle.text = "화성 갈끄니까~"

            textViewReadPostDate.text = "2250-01-01 ~ 2251-12-31"

            textViewReadPostNOP.text = "3000"

            textViewReadPostLocatoin.text = "측정불가"

            textViewReadPostHashTag.text = "#화성#비트코인#테슬라#로켓#도지코인"

            textViewReadPostContent.text = "화성 갈사람 구해요 우주 왕복선 값은 제가 내드림 나 돈 많음 ㅋ 거하게 소주 적시고 해장국 맛깔나게 드실분만 신청주세요."

            //DM버튼
            buttonReadPostDM.setOnClickListener {
                //다이얼로그 띄움
                val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme)
                builder.run {
                    setTitle("1 : 1 문의하기")
                    setMessage("1:1 대화방에 입장합니다. 상대방을 비하하거나 불쾌한 언행을 엄격하게 모니터링 중이며 그에 따른 책임은 작성자에게 물을 수 있으므로 매너있는 채팅 부탁드립니다.")
                    setPositiveButton("입장") { dialogInterface: DialogInterface, i: Int ->

                    }
                    setNegativeButton("취소", null)
                    show()
                }
            }

            //동행 신청 버튼
            buttonReadPostSubmit.setOnClickListener {
                //다이얼로그 띄움
                val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme)
                builder.run {
                    val dialogBinding = DialogSubmitBinding.inflate(layoutInflater)
                    setTitle("동행 신청하기")
                    setMessage("동행 신청을 위한 자기소개를 해주세요")

                    // 새로운 뷰를 설정한다.
                    builder.setView(dialogBinding.root)

                    dialogBinding.editTextInputSelfIntroduce.requestFocus()

                    thread {
                        SystemClock.sleep(500)
                        val imm = mainActivity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(dialogBinding.editTextInputSelfIntroduce, 0)
                    }

                    builder.setPositiveButton("신청") { dialogInterface: DialogInterface, i: Int ->
                        // 입력한 내용을 가져온다.
                        val str1 = dialogBinding.editTextInputSelfIntroduce.text.toString()
                        textViewReadPostToolbarTitle.text = str1
                    }
                    builder.setNegativeButton("취소", null)

                    builder.show()
                }
            }

            //그룹 채팅으로 이동 버튼
            buttonReadPostMoveChat.setOnClickListener {
                textViewReadPostToolbarTitle.text = "그룹 채팅으로 이동"
            }

            //리뷰 버튼
            buttonReadPostReview.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.REVIEW_FRAGMENT,true,true,null)
            }
        }

        return fragmentReadPostBinding.root
    }

    override fun onPause() {
        super.onPause()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }
}