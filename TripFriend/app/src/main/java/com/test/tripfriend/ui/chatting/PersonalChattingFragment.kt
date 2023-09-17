package com.test.tripfriend.ui.chatting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isEmpty
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firestore.bundle.BundleElement
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentPersonalChattingBinding
import com.test.tripfriend.databinding.RowChattingPersonalBinding
import com.test.tripfriend.dataclassmodel.PersonalChatInfo
import com.test.tripfriend.repository.PersonalChatRepository
import com.test.tripfriend.viewmodel.PersonalChatViewModel

class PersonalChattingFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentPersonalChattingBinding: FragmentPersonalChattingBinding
    lateinit var personalChatViewModel: PersonalChatViewModel
    lateinit var personalChatRepository: PersonalChatRepository

    val MY_ID = "sori2189@naver.com"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        mainActivity = activity as MainActivity
        fragmentPersonalChattingBinding = FragmentPersonalChattingBinding.inflate(layoutInflater)
        personalChatViewModel = ViewModelProvider(mainActivity)[PersonalChatViewModel::class.java]
        personalChatRepository = PersonalChatRepository()

        personalChatViewModel.run {
            //데이터 업데이트하기
            chatInfoData.observe(mainActivity) {
                if (it != null) {
                    (fragmentPersonalChattingBinding.recyclerViewPersonalChatting.adapter as? PersonalChattingAdapter)?.updateItemList(
                        it
                    )

                }
            }
            fetchChatRoomInfo(MY_ID)
        }


        fragmentPersonalChattingBinding.run {
            if (true) {      // 채팅방 존재할 경우
                textViewPersonalChatting.visibility = View.GONE
                // 1:1 채팅 목록 리사이클러 뷰
                recyclerViewPersonalChatting.run {
                    visibility = View.VISIBLE
                    adapter = PersonalChattingAdapter()
                    layoutManager = LinearLayoutManager(mainActivity)
                }
            } else {        // 채팅방 없는 경우
                textViewPersonalChatting.visibility = View.VISIBLE
                recyclerViewPersonalChatting.visibility = View.GONE
            }
        }

        return fragmentPersonalChattingBinding.root
    }

    // PersonalChatting 어댑터
    inner class PersonalChattingAdapter :
        RecyclerView.Adapter<PersonalChattingAdapter.PersonalChattingViewHolder>() {
        //보여줄 데이터 정보
        private var itemList: List<PersonalChatInfo> = emptyList()

        //데이터를 가져와서 업데이트하기 위한 메서드
        fun updateItemList(newList: List<PersonalChatInfo>) {
            this.itemList = newList
            notifyDataSetChanged() // 갱신
        }

        inner class PersonalChattingViewHolder(rowChattingPersonalBinding: RowChattingPersonalBinding) :
            RecyclerView.ViewHolder(rowChattingPersonalBinding.root) {
            val imageViewRowPersonalChattingImage: ImageView
            val textViewRowPersonalChattingName: TextView
            val textViewRowPersonalChattingMessage: TextView
            val textViewRowPersonalChattingDate: TextView

            init {
                imageViewRowPersonalChattingImage =
                    rowChattingPersonalBinding.imageViewRowPersonalChattingImage
                textViewRowPersonalChattingName =
                    rowChattingPersonalBinding.textViewRowPersonalChattingName
                textViewRowPersonalChattingMessage =
                    rowChattingPersonalBinding.textViewRowPersonalChattingMessage
                textViewRowPersonalChattingDate =
                    rowChattingPersonalBinding.textViewRowPersonalChattingDate

                //채팅방 클릭 시 채팅방으로 이동
                rowChattingPersonalBinding.root.setOnClickListener {
                    val chatRoomId = itemList[adapterPosition].documentId
                    val userName=itemList[adapterPosition].userNickname
                    val userProfile=itemList[adapterPosition].userProfilePath
                    val bundle=Bundle()
                    bundle.putString("chatRoomId",chatRoomId)
                    //상대방 이름
                    bundle.putString("userName",userName)
                    //상대방 프로필
                    bundle.putString("userProfile",userProfile)
                    mainActivity.replaceFragment(MainActivity.PERSONAL_CHAT_ROOM_FRAGMENT, true, true, bundle)
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): PersonalChattingViewHolder {
            val rowChattingPersonalBinding = RowChattingPersonalBinding.inflate(layoutInflater)

            rowChattingPersonalBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return PersonalChattingViewHolder(rowChattingPersonalBinding)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: PersonalChattingViewHolder, position: Int) {
            holder.textViewRowPersonalChattingName.text = itemList[position].userNickname
            holder.textViewRowPersonalChattingDate.text = itemList[position].lastChatDate
            holder.textViewRowPersonalChattingMessage.text = itemList[position].lastChatContent
            //여기 이미지 설정 해야함.
        }
    }

}