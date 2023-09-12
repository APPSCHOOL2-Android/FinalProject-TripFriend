package com.test.tripfriend.ui.chatting

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.tripfriend.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentPersonalChatRoomBinding
import com.test.tripfriend.databinding.RowChatRoomUserBinding

class PersonalChatRoomFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentPersonalChatRoomBinding: FragmentPersonalChatRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = activity as MainActivity
        fragmentPersonalChatRoomBinding = FragmentPersonalChatRoomBinding.inflate(layoutInflater)

        //하단 nav bar 안보이게
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE

        fragmentPersonalChatRoomBinding.run {
            materialPersonalChatRoomToolbar.run {
                // 백버튼
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PERSONAL_CHAT_ROOM_FRAGMENT)
                }

                // 메뉴 버튼
                setOnMenuItemClickListener {
                    if(it.itemId == R.id.item_chat_menu)
                        drawerLayoutPersonalChatRoom.openDrawer(Gravity.RIGHT)
                    true
                }

                // 리사이클러 뷰
                recyclerViewPersonalChatRoom.run {
                    adapter = PersonalChatRoomAdapter()
                    layoutManager = LinearLayoutManager(mainActivity)
                }

                // 나가기 버튼
                buttonPersonalChatRoomExit.run {
                    setOnClickListener {
                        //다이얼로그 띄움
                        val builder = AlertDialog.Builder(
                            mainActivity
                        )
                        builder.run {
                            setTitle("채팅방 나가기")
                            setMessage("나가기를 하면 대화내용이 모두 삭제되고 채팅 목록에서도 삭제됩니다.")
                            setPositiveButton("나가기") { dialogInterface: DialogInterface, i: Int ->

                            }
                            setNegativeButton("취소", null)
                            show()
                        }
                    }
                }
            }
        }

        return fragmentPersonalChatRoomBinding.root
    }

    // PersonalChatRoom 어댑터
    inner class PersonalChatRoomAdapter :
        RecyclerView.Adapter<PersonalChatRoomAdapter.PersonalChatRoomViewHolder>() {

        inner class PersonalChatRoomViewHolder(rowChatRoomUserBinding: RowChatRoomUserBinding) :
            RecyclerView.ViewHolder(rowChatRoomUserBinding.root) {
            val textViewRowChatRoomUser: TextView

            init {
                textViewRowChatRoomUser = rowChatRoomUserBinding.textViewRowChatRoomUser
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalChatRoomViewHolder {
            val rowChatRoomUserBinding = RowChatRoomUserBinding.inflate(layoutInflater)

            rowChatRoomUserBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return PersonalChatRoomViewHolder(rowChatRoomUserBinding)
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: PersonalChatRoomViewHolder, position: Int) {
            holder.textViewRowChatRoomUser.text = "채팅 $position"
        }
    }

    override fun onStop() {
        super.onStop()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }



}