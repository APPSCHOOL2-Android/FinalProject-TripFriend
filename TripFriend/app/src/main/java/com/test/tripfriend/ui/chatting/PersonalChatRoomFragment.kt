package com.test.tripfriend.ui.chatting

import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentPersonalChatRoomBinding
import com.test.tripfriend.databinding.RowChatRoomUserBinding
import com.test.tripfriend.dataclassmodel.PersonalChatting
import com.test.tripfriend.dataclassmodel.PersonalChatting2
import com.test.tripfriend.repository.PersonalChatRepository
import com.test.tripfriend.viewmodel.ChattingViewModel
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar


class PersonalChatRoomFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var fragmentPersonalChatRoomBinding: FragmentPersonalChatRoomBinding
    lateinit var chattingViewModel: ChattingViewModel
    lateinit var displayMetrics: DisplayMetrics
    lateinit var opponentName: String
    lateinit var opponentProfile: String
    var personalChatRepository = PersonalChatRepository()
    lateinit var MY_EMAIL: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = activity as MainActivity
        fragmentPersonalChatRoomBinding = FragmentPersonalChatRoomBinding.inflate(layoutInflater)
        MY_EMAIL = mainActivity.userClass.userEmail
        chattingViewModel = ViewModelProvider(this)[ChattingViewModel::class.java]

        chattingViewModel.run {
            chattingList.observe(viewLifecycleOwner) {

                //데이터 변경 시 리사이클러뷰 업데이트
                if (it != null) {
                    (fragmentPersonalChatRoomBinding.recyclerViewPersonalChatRoom.adapter as? PersonalChatRoomAdapter)?.updateItemList(
                        it
                    )
                }
            }
            deleteData.observe(viewLifecycleOwner) {
                Log.d("zzzzzz","메세지들이 삭제됨")
                //토스트메시지를 띄우고 채팅방 나가도록
                Snackbar.make(
                    fragmentPersonalChatRoomBinding.root,
                    "상대방이 채팅을 나가서 화면을 전환합니다",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("X") {
                        // Responds to click on the action
                    }
                    .show()
                mainActivity.removeFragment(MainActivity.PERSONAL_CHAT_ROOM_FRAGMENT)

            }
            myProfile.observe(viewLifecycleOwner) {
                if (it != null) {
                    Glide.with(mainActivity).load(it)
                        .into(fragmentPersonalChatRoomBinding.imageViewPersonalChatRoomUser)
                } else {
                    fragmentPersonalChatRoomBinding.imageViewPersonalChatRoomUser.setImageResource(R.drawable.person_24px)
                }
            }
            getMyImageUri(mainActivity.userClass.userProfilePath)
        }

        //하단 nav bar 안보이게
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE

        // 기기의 화면 너비 구하기
        displayMetrics = resources.displayMetrics

        //번들로 받은 방 아이디 가져오기
        val roomId = arguments?.getString("chatRoomId")
        opponentName = arguments?.getString("userName").toString()
        opponentProfile = arguments?.getString("userProfile").toString()
        if (opponentProfile == null || opponentProfile == "" || ::opponentProfile.isInitialized == false) {
            opponentProfile = "null"
        }
        if (roomId != null) {
        }

        if (roomId != null && opponentName != null && opponentProfile != null) {
            //db의 데이터 변경을 감시하기 위한 리스너
            chattingViewModel.chattingChangeListener(roomId)
            chattingViewModel.deleteChatRoomListener(roomId)
        }

        fragmentPersonalChatRoomBinding.run {
            materialPersonalChatRoomToolbar.run {
                // 백버튼
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.PERSONAL_CHAT_ROOM_FRAGMENT)
                }

                // 메뉴 버튼
                setOnMenuItemClickListener {
                    if (it.itemId == R.id.item_chat_menu)
                        drawerLayoutPersonalChatRoom.openDrawer(Gravity.RIGHT)
                    true
                }
                //햄버거 클릭시 나오는 내용들 설정
                textViewOpponentName.text = opponentName + "님과의 대화방"
                textViewPersonalChattingUserName.text = "${mainActivity.userClass.userNickname}"
                textViewPersonalChattingOpponentName.text = opponentName
                imageViewPersonalChatRoomOpponent
                if (opponentProfile != "null") {
                    Glide.with(mainActivity).load(opponentProfile.toUri())
                        .into(imageViewPersonalChatRoomOpponent)
                } else {
                    imageViewPersonalChatRoomOpponent.setImageResource(R.drawable.person_24px)
                }


                // 리사이클러 뷰
                recyclerViewPersonalChatRoom.run {
                    adapter = PersonalChatRoomAdapter()
                    val manager = LinearLayoutManager(mainActivity)
                    manager.stackFromEnd = true
                    layoutManager = manager
                }

                // 나가기 버튼
                buttonPersonalChatRoomExit.run {
                    setOnClickListener {
                        //다이얼로그 띄움
                        MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).run {
                            setTitle("채팅방 나가기")
                            setMessage("나가기를 하면 대화내용이 모두 삭제되고 채팅 목록에서도 삭제됩니다.")
                            setNegativeButton("취소", null)
                            setPositiveButton("나가기") { dialogInterface: DialogInterface, i: Int ->
                                mainActivity.removeFragment(MainActivity.PERSONAL_CHAT_ROOM_FRAGMENT)


                                if (roomId != null) {
                                    chattingViewModel.removePersonalChatRoom(roomId)
                                }
                            }
                            show()
                        }
                    }
                }
            }

            // 입력창의 최대 높이 설정 (기기 세로 사이즈의 1/3)
            val oneThirdScreenHeight = displayMetrics.heightPixels / 3
            textInputEditTextPersonalChatRoomSearch.maxHeight = oneThirdScreenHeight

            //전송버튼
            buttonPersonalChatRoomSend.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1 // 월은 0부터 시작하므로 +1
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val currentDate = "${year.toString().padStart(2, '0')}-${
                    month.toString().padStart(2, '0')
                }-${day.toString().padStart(2, '0')}"

                val calendar2 = Calendar.getInstance()
                val hour = calendar2.get(Calendar.HOUR_OF_DAY) // 24시간 형식
                val minute = calendar2.get(Calendar.MINUTE)
                val second = calendar2.get(Calendar.SECOND)

                val currentTime =
                    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

                //채팅 내용
                val personalChatContent = textInputEditTextPersonalChatRoomSearch.text.toString()

                //전송 시기
                val personalChatSendDateAndTime = "${currentDate} / ${currentTime}"

                //타임 스탬프
                val personalChatSendTimeStamp = ("$year${month.toString().padStart(2, '0')}${
                    day.toString().padStart(2, '0')
                }${hour.toString().padStart(2, '0')}${
                    minute.toString().padStart(2, '0')
                }${second.toString().padStart(2, '0')}").toLong()

                //저장할 데이터 생성
                val personalChatting = PersonalChatting(
                    MY_EMAIL,
                    personalChatContent,
                    personalChatSendDateAndTime,
                    personalChatSendTimeStamp
                )

                //문서 아이디가 null이 아니라면 채팅 저장
                if (roomId != null) {
                    personalChatRepository.saveMyContentToDB(roomId, personalChatting)
                } else {
                }
                textInputEditTextPersonalChatRoomSearch.setText("")
            }

        }

        return fragmentPersonalChatRoomBinding.root
    }

    // PersonalChatRoom 어댑터
    inner class PersonalChatRoomAdapter :
        RecyclerView.Adapter<PersonalChatRoomAdapter.PersonalChatRoomViewHolder>() {
        //보여줄 데이터 정보
        private var itemList = mutableListOf<PersonalChatting2>()

        //데이터를 가져와서 업데이트하기 위한 메서드
        fun updateItemList(newList: MutableList<PersonalChatting2>) {
            //변경된 사항이 날아오므로 add를 수행
            this.itemList.addAll(newList)

            notifyDataSetChanged() // 갱신

            //업데이트시 스크롤 맨 아래로
            fragmentPersonalChatRoomBinding.recyclerViewPersonalChatRoom.scrollToPosition(itemList.size - 1)
        }

        inner class PersonalChatRoomViewHolder(rowChatRoomUserBinding: RowChatRoomUserBinding) :
            RecyclerView.ViewHolder(rowChatRoomUserBinding.root) {
            val textViewRowChatRoomUser: TextView
            val textViewChatMoment: TextView

            val imageViewOpponent: CircleImageView
            val textViewOpponentName: TextView
            val textViewOpponentContent: TextView
            val textViewOpponentChatMoment: TextView
            val textViewOutNotification:TextView

            init {
                //내 채팅
                textViewRowChatRoomUser = rowChatRoomUserBinding.textViewRowChatRoomUser
                //내 채팅 시간대
                textViewChatMoment = rowChatRoomUserBinding.textViewMyChatMoment
                //상대방 이미지
                imageViewOpponent = rowChatRoomUserBinding.imageViewRowChatRoomOpponent
                //상대방 이름
                textViewOpponentName = rowChatRoomUserBinding.textViewRowChatRoomOpponentName
                //상대방 채팅
                textViewOpponentContent = rowChatRoomUserBinding.textViewRowChatRoomOpponent
                //상대방 채팅 시간대
                textViewOpponentChatMoment = rowChatRoomUserBinding.textViewOpponentChatMoment
                //그룹채팅방에서 사용되는 것 visible처리예정
                textViewOutNotification = rowChatRoomUserBinding.textViewOutNotification

            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): PersonalChatRoomViewHolder {
            val rowChatRoomUserBinding = RowChatRoomUserBinding.inflate(layoutInflater)

            rowChatRoomUserBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return PersonalChatRoomViewHolder(rowChatRoomUserBinding)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: PersonalChatRoomViewHolder, position: Int) {
            // 리사이클러뷰 아이템의 최대 너비 설정 (기기 가로 사이즈의 절반)
            val halfScreenWidth = displayMetrics.widthPixels / 2
            // 최대 너비 설정
            holder.textViewRowChatRoomUser.maxWidth = halfScreenWidth
            holder.textViewOpponentContent.maxWidth = halfScreenWidth

            //받아온 데이터가 내가 보낸 게 아니라면
            if (itemList[position].personalChatWriterEmail != MY_EMAIL) {
                holder.textViewRowChatRoomUser.visibility = View.GONE
                holder.textViewChatMoment.visibility = View.GONE
                holder.textViewOutNotification.visibility = View.GONE

                holder.imageViewOpponent.visibility = View.VISIBLE
                holder.textViewOpponentName.visibility = View.VISIBLE
                holder.textViewOpponentContent.visibility = View.VISIBLE
                holder.textViewOpponentChatMoment.visibility = View.VISIBLE
                if (opponentProfile != "null") {
                    Glide.with(mainActivity).load(opponentProfile.toUri())
                        .into(holder.imageViewOpponent)
                } else {
                    holder.imageViewOpponent.setImageResource(R.drawable.person_24px)
                }
                holder.textViewOpponentName.text = opponentName
                holder.textViewOpponentContent.text = itemList[position].personalChatContent
                holder.textViewOpponentChatMoment.text =
                    itemList[position].personalChatSendDateAndTime
                //받아온 데이터가 내가 보낸 거라면
            } else {
                holder.textViewRowChatRoomUser.visibility = View.VISIBLE
                holder.textViewChatMoment.visibility = View.VISIBLE
                holder.textViewRowChatRoomUser.text = itemList[position].personalChatContent
                holder.textViewChatMoment.text = itemList[position].personalChatSendDateAndTime

                holder.imageViewOpponent.visibility = View.GONE
                holder.textViewOpponentName.visibility = View.GONE
                holder.textViewOpponentContent.visibility = View.GONE
                holder.textViewOpponentChatMoment.visibility = View.GONE
                holder.textViewOutNotification.visibility = View.GONE
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }


}