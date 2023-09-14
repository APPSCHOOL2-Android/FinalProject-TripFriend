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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentPersonalChattingBinding
import com.test.tripfriend.databinding.RowChattingPersonalBinding

class PersonalChattingFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentPersonalChattingBinding: FragmentPersonalChattingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mainActivity = activity as MainActivity
        fragmentPersonalChattingBinding = FragmentPersonalChattingBinding.inflate(layoutInflater)

        fragmentPersonalChattingBinding.run {

            if(true) {      // 채팅방 존재할 경우
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

        inner class PersonalChattingViewHolder(rowChattingPersonalBinding: RowChattingPersonalBinding) :
            RecyclerView.ViewHolder(rowChattingPersonalBinding.root) {
            val imageViewRowPersonalChattingImage: ImageView
            val textViewRowPersonalChattingName: TextView
            val textViewRowPersonalChattingMessage: TextView
            val textViewRowPersonalChattingDate: TextView

            init {
                imageViewRowPersonalChattingImage = rowChattingPersonalBinding.imageViewRowPersonalChattingImage
                textViewRowPersonalChattingName = rowChattingPersonalBinding.textViewRowPersonalChattingName
                textViewRowPersonalChattingMessage = rowChattingPersonalBinding.textViewRowPersonalChattingMessage
                textViewRowPersonalChattingDate = rowChattingPersonalBinding.textViewRowPersonalChattingDate

                rowChattingPersonalBinding.root.setOnClickListener {
                    val chatRoomIdx = adapterPosition
                    mainActivity.replaceFragment(MainActivity.PERSONAL_CHAT_ROOM_FRAGMENT, true, true, null)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalChattingViewHolder {
            val rowChattingPersonalBinding = RowChattingPersonalBinding.inflate(layoutInflater)

            rowChattingPersonalBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return PersonalChattingViewHolder(rowChattingPersonalBinding)
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: PersonalChattingViewHolder, position: Int) {
            holder.textViewRowPersonalChattingName.text = "이름 $position"
        }
    }

}