package com.test.tripfriend.ui.chatting

import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentGroupChatRoomBinding
import com.test.tripfriend.databinding.RowChatRoomOpponentBinding
import com.test.tripfriend.databinding.RowGroupChatRoomBinding

class GroupChatRoomFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentGroupChatRoomBinding: FragmentGroupChatRoomBinding

    lateinit var displayMetrics: DisplayMetrics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = activity as MainActivity
        fragmentGroupChatRoomBinding = FragmentGroupChatRoomBinding.inflate(layoutInflater)

        //하단 nav bar 안보이게
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE

        // 기기의 화면 너비 구하기
        displayMetrics = resources.displayMetrics

        fragmentGroupChatRoomBinding.run {

            materialGroupChatRoomToolbar.run {
                // 백버튼
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.GROUP_CHAT_ROOM_FRAGMENT)
                }

                // 메뉴 버튼
                setOnMenuItemClickListener {
                    if(it.itemId == R.id.item_chat_menu)
                        drawerLayoutGroupChatRoom.openDrawer(Gravity.RIGHT)
                    true
                }

                // 참가자 리사이클러 뷰
                recyclerViewGroupChatRoomParticipants.run {
                    adapter = ParticipantsAdapter()
                    layoutManager = LinearLayoutManager(mainActivity)
                }

                // 채팅 리사이클러 뷰
                recyclerViewGroupChatRoom.run {
                    adapter = GroupChatRoomAdapter()
                    layoutManager = LinearLayoutManager(mainActivity)
                }

                // 나가기 버튼
                buttonGroupChatRoomExit.run {
                    setOnClickListener {
                        //다이얼로그 띄움
                        MaterialAlertDialogBuilder(mainActivity,R.style.DialogTheme).run {
                            setTitle("채팅방 나가기")
                            setMessage("나가기를 하면 대화내용이 모두 삭제되며 동행 신청이 취소되고 채팅 목록에서도 삭제됩니다.")
                            setNegativeButton("취소", null)
                            setPositiveButton("나가기") { dialogInterface: DialogInterface, i: Int ->

                            }
                            show()
                        }
                    }
                }
            }

            // 입력창의 최대 높이 설정 (기기 세로 사이즈의 1/3)
            val oneThirdScreenHeight = displayMetrics.heightPixels / 3
            textInputEditTextGroupChatRoomSearch.maxHeight = oneThirdScreenHeight
        }

        return fragmentGroupChatRoomBinding.root
    }


    // Participants 어댑터
    inner class ParticipantsAdapter :
        RecyclerView.Adapter<ParticipantsAdapter.ParticipantsViewHolder>() {

        inner class ParticipantsViewHolder(rowGroupChatRoomBinding: RowGroupChatRoomBinding) :
            RecyclerView.ViewHolder(rowGroupChatRoomBinding.root) {
            val imageViewGroupChatRoomImage: ImageView
            val textViewGroupChatRoomName: TextView

            init {
                imageViewGroupChatRoomImage = rowGroupChatRoomBinding.imageViewGroupChatRoomImage
                textViewGroupChatRoomName = rowGroupChatRoomBinding.textViewGroupChatRoomName
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsViewHolder {
            val rowGroupChatRoomBinding = RowGroupChatRoomBinding.inflate(layoutInflater)

            rowGroupChatRoomBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return ParticipantsViewHolder(rowGroupChatRoomBinding)
        }

        override fun getItemCount(): Int {
            return 3
        }

        override fun onBindViewHolder(holder: ParticipantsViewHolder, position: Int) {
            holder.textViewGroupChatRoomName.text = "참여자 $position"
        }
    }


    // GroupChatRoom 어댑터
    inner class GroupChatRoomAdapter :
        RecyclerView.Adapter<GroupChatRoomAdapter.GroupChatRoomViewHolder>() {

        inner class GroupChatRoomViewHolder(rowChatRoomOpponentBinding: RowChatRoomOpponentBinding) :
            RecyclerView.ViewHolder(rowChatRoomOpponentBinding.root) {
            val textViewRowChatRoomOpponent: TextView

            init {
                textViewRowChatRoomOpponent = rowChatRoomOpponentBinding.textViewRowChatRoomOpponent
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatRoomViewHolder {
            val rowChatRoomOpponentBinding = RowChatRoomOpponentBinding.inflate(layoutInflater)

            rowChatRoomOpponentBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return GroupChatRoomViewHolder(rowChatRoomOpponentBinding)
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: GroupChatRoomViewHolder, position: Int) {

            // 리사이클러뷰 아이템의 최대 너비 설정 (기기 가로 사이즈의 절반)
            val halfScreenWidth = displayMetrics.widthPixels / 2
            // 최대 너비 설정
            holder.textViewRowChatRoomOpponent.maxWidth = halfScreenWidth
            holder.textViewRowChatRoomOpponent.text = "opajopsejfopijfiopajoifja oiejfopas jfopopa sjepofija seopfij aseopifj asoiefpj aspoie jfpaos eifapoes jifpoas ejfpoaiesj fpo $position"
        }
    }


    override fun onStop() {
        super.onStop()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }


//    class RecyclerMessagesAdapter(
//        val context: Context,
//        var chatRoomKey: String?,
//        val opponentUid: String?
//    ) :
//        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//        var messages: ArrayList<Message> = arrayListOf()     //메시지 목록
//        var messageKeys: ArrayList<String> = arrayListOf()   //메시지 키 목록
//        val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
//        val recyclerView = (context as ChatRoomActivity).recycler_talks   //목록이 표시될 리사이클러 뷰
//
//        init {
//            setupMessages()
//        }
//
//        fun setupMessages() {
//            getMessages()
//        }
//
//        fun getMessages() {
//            FirebaseDatabase.getInstance().getReference("ChatRoom")
//                .child("chatRooms").child(chatRoomKey!!).child("messages")   //전체 메시지 목록 가져오기
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onCancelled(error: DatabaseError) {}
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        messages.clear()
//                        for (data in snapshot.children) {
//                            messages.add(data.getValue<Message>()!!)         //메시지 목록에 추가
//                            messageKeys.add(data.key!!)                        //메시지 키 목록에 추가
//                        }
//                        notifyDataSetChanged()          //화면 업데이트
//                        recyclerView.scrollToPosition(messages.size - 1)    //스크롤 최 하단으로 내리기
//                    }
//                })
//        }
//
//        override fun getItemViewType(position: Int): Int {               //메시지의 id에 따라 내 메시지/상대 메시지 구분
//            return if (messages[position].senderUid.equals(myUid)) 1 else 0
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            return when (viewType) {
//                1 -> {            //메시지가 내 메시지인 경우
//                    val view =
//                        LayoutInflater.from(context)
//                            .inflate(R.layout.list_talk_item_mine, parent, false)   //내 메시지 레이아웃으로 초기화
//
//                    MyMessageViewHolder(ListTalkItemMineBinding.bind(view))
//                }
//                else -> {      //메시지가 상대 메시지인 경우
//                    val view =
//                        LayoutInflater.from(context)
//                            .inflate(R.layout.list_talk_item_others, parent, false)  //상대 메시지 레이아웃으로 초기화
//                    OtherMessageViewHolder(ListTalkItemOthersBinding.bind(view))
//                }
//            }
//        }
//
//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            if (messages[position].senderUid.equals(myUid)) {       //레이아웃 항목 초기화
//                (holder as MyMessageViewHolder).bind(position)
//            } else {
//                (holder as OtherMessageViewHolder).bind(position)
//            }
//        }
//
//        override fun getItemCount(): Int {
//            return messages.size
//        }
//
//        inner class OtherMessageViewHolder(itemView: ListTalkItemOthersBinding) :         //상대 메시지 뷰홀더
//            RecyclerView.ViewHolder(itemView.root) {
//            var background = itemView.background
//            var txtMessage = itemView.txtMessage
//            var txtDate = itemView.txtDate
//            var txtIsShown = itemView.txtIsShown
//
//            fun bind(position: Int) {           //메시지 UI 항목 초기화
//                var message = messages[position]
//                var sendDate = message.sended_date
//
//                txtMessage.text = message.content
//
//                txtDate.text = getDateText(sendDate)
//
//                if (message.confirmed.equals(true))           //확인 여부 표시
//                    txtIsShown.visibility = View.GONE
//                else
//                    txtIsShown.visibility = View.VISIBLE
//
//                setShown(position)             //해당 메시지 확인하여 서버로 전송
//            }
//
//            fun getDateText(sendDate: String): String {    //메시지 전송 시각 생성
//
//                var dateText = ""
//                var timeString = ""
//                if (sendDate.isNotBlank()) {
//                    timeString = sendDate.substring(8, 12)
//                    var hour = timeString.substring(0, 2)
//                    var minute = timeString.substring(2, 4)
//
//                    var timeformat = "%02d:%02d"
//
//                    if (hour.toInt() > 11) {
//                        dateText += "오후 "
//                        dateText += timeformat.format(hour.toInt() - 12, minute.toInt())
//                    } else {
//                        dateText += "오전 "
//                        dateText += timeformat.format(hour.toInt(), minute.toInt())
//                    }
//                }
//                return dateText
//            }
//
//            fun setShown(position: Int) {          //메시지 확인하여 서버로 전송
//                FirebaseDatabase.getInstance().getReference("ChatRoom")
//                    .child("chatRooms").child(chatRoomKey!!).child("messages")
//                    .child(messageKeys[position]).child("confirmed").setValue(true)
//                    .addOnSuccessListener {
//                        Log.i("checkShown", "성공")
//                    }
//            }
//        }
//
//        inner class MyMessageViewHolder(itemView: ListTalkItemMineBinding) :       // 내 메시지용 ViewHolder
//            RecyclerView.ViewHolder(itemView.root) {
//            var background = itemView.background
//            var txtMessage = itemView.txtMessage
//            var txtDate = itemView.txtDate
//            var txtIsShown = itemView.txtIsShown
//
//            fun bind(position: Int) {            //메시지 UI 레이아웃 초기화
//                var message = messages[position]
//                var sendDate = message.sended_date
//                txtMessage.text = message.content
//
//                txtDate.text = getDateText(sendDate)
//
//                if (message.confirmed.equals(true))
//                    txtIsShown.visibility = View.GONE
//                else
//                    txtIsShown.visibility = View.VISIBLE
//            }
//
//            fun getDateText(sendDate: String): String {        //메시지 전송 시각 생성
//                var dateText = ""
//                var timeString = ""
//                if (sendDate.isNotBlank()) {
//                    timeString = sendDate.substring(8, 12)
//                    var hour = timeString.substring(0, 2)
//                    var minute = timeString.substring(2, 4)
//
//                    var timeformat = "%02d:%02d"
//
//                    if (hour.toInt() > 11) {
//                        dateText += "오후 "
//                        dateText += timeformat.format(hour.toInt() - 12, minute.toInt())
//                    } else {
//                        dateText += "오전 "
//                        dateText += timeformat.format(hour.toInt(), minute.toInt())
//                    }
//                }
//                return dateText
//            }
//        }
//
//    }

}