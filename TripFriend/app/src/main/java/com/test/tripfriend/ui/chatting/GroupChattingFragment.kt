package com.test.tripfriend.ui.chatting

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.databinding.FragmentGroupChattingBinding
import com.test.tripfriend.databinding.RowChattingGroupBinding
import com.test.tripfriend.dataclassmodel.GroupChatInfo
import com.test.tripfriend.dataclassmodel.PersonalChatInfo
import com.test.tripfriend.viewmodel.GroupChatViewModel

class GroupChattingFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentGroupChattingBinding: FragmentGroupChattingBinding
    lateinit var groupChatViewModel:GroupChatViewModel
    lateinit var MY_NICKNAME:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = activity as MainActivity
        fragmentGroupChattingBinding = FragmentGroupChattingBinding.inflate(layoutInflater)
        //로그인된 사람의 닉네임 설정
        MY_NICKNAME=mainActivity.userClass.userNickname
        groupChatViewModel = ViewModelProvider(this)[GroupChatViewModel::class.java]
        groupChatViewModel.run {
            groupChatRoomInfo.observe(viewLifecycleOwner){
                (fragmentGroupChattingBinding.recyclerViewGroupChatting.adapter as? GroupChattingAdapter)?.updateItemList(it)
            }
            changeString.observe(viewLifecycleOwner){
                fetchGroupChatRoomInfo(MY_NICKNAME)
            }

            Log.d("testtt",MY_NICKNAME)
            fetchGroupChatRoomInfo(MY_NICKNAME)
        }

        fragmentGroupChattingBinding.run {

            if(true) {      // 채팅방 존재할 경우
                textViewGroupChatting.visibility = View.GONE
                // 그룹 채팅 목록 리사이클러 뷰
                recyclerViewGroupChatting.run {
                    visibility = View.VISIBLE
                    adapter = GroupChattingAdapter()
                    layoutManager = LinearLayoutManager(mainActivity)
                }
            } else {        // 채팅방 없는 경우
                textViewGroupChatting.visibility = View.VISIBLE
                recyclerViewGroupChatting.visibility = View.GONE
            }

        }

        return fragmentGroupChattingBinding.root
    }

    // GroupChatting 어댑터
    inner class GroupChattingAdapter :
        RecyclerView.Adapter<GroupChattingAdapter.GroupChattingViewHolder>() {
        //보여줄 데이터 정보
        private var itemList: List<GroupChatInfo> = emptyList()

        //데이터를 가져와서 업데이트하기 위한 메서드
        fun updateItemList(newList: MutableList<GroupChatInfo>) {
            this.itemList = newList
            notifyDataSetChanged() // 갱신
        }

        inner class GroupChattingViewHolder(rowChattingGroupBinding: RowChattingGroupBinding) :
            RecyclerView.ViewHolder(rowChattingGroupBinding.root) {
            val textViewRowGroupChattingTitle: TextView
            val textViewRowGroupChattingNumber: TextView
            val textViewRowGroupChattingMessage: TextView
            val textViewRowGroupChattingDate: TextView

            init {
                textViewRowGroupChattingTitle = rowChattingGroupBinding.textViewRowGroupChattingTitle
                textViewRowGroupChattingNumber = rowChattingGroupBinding.textViewRowGroupChattingNumber
                textViewRowGroupChattingMessage = rowChattingGroupBinding.textViewRowGroupChattingMessage
                textViewRowGroupChattingDate = rowChattingGroupBinding.textViewRowGroupChattingDate

                rowChattingGroupBinding.root.setOnClickListener {
                    val bundle=Bundle()
                    bundle.putString("groupRoomId",itemList[adapterPosition].roomId)
                    bundle.putString("postId",itemList[adapterPosition].tripPostId)
                    bundle.putString("postTitle",itemList[adapterPosition].tripPostTitle)
                    bundle.putString("roomOwnerEmail",itemList[adapterPosition].groupChatPostWriterEmail)
                    bundle.putString("postId",itemList[adapterPosition].tripPostId)
                    mainActivity.replaceFragment(MainActivity.GROUP_CHAT_ROOM_FRAGMENT, true, true, bundle)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChattingViewHolder {
            val rowChattingGroupBinding = RowChattingGroupBinding.inflate(layoutInflater)

            rowChattingGroupBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return GroupChattingViewHolder(rowChattingGroupBinding)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: GroupChattingViewHolder, position: Int) {
            itemList[position].roomId?.let { groupChatViewModel.fetchChangeInfo(it) }
            holder.textViewRowGroupChattingTitle.text = "${itemList[position].tripPostTitle}"
            holder.textViewRowGroupChattingNumber.text = "${itemList[position].memberCount}"
            holder.textViewRowGroupChattingMessage.text = "${itemList[position].lastChatContent}"
            holder.textViewRowGroupChattingDate.text = "${itemList[position].lastChatDate}"
        }
    }

}