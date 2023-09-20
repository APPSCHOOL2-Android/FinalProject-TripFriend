package com.test.tripfriend.ui.trip

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentReceivedNotificationBinding
import com.test.tripfriend.databinding.RowReceivedNotificationBinding
import com.test.tripfriend.dataclassmodel.TripPost
import com.test.tripfriend.dataclassmodel.TripRequest
import com.test.tripfriend.repository.GroupChatRepository
import com.test.tripfriend.repository.TripPostRepository
import com.test.tripfriend.repository.TripRequestRepository
import com.test.tripfriend.repository.UserRepository
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.viewmodel.GroupChatViewModel
import com.test.tripfriend.viewmodel.TripPostViewModel
import com.test.tripfriend.viewmodel.TripRequestViewModel
import com.test.tripfriend.viewmodel.UserViewModel
import kotlinx.coroutines.runBlocking


class ReceivedNotificationFragment : Fragment() {

    lateinit var fragmentReceivedNotificationBinding: FragmentReceivedNotificationBinding
    lateinit var mainActivity: MainActivity

    lateinit var tripRequestViewModel : TripRequestViewModel
    lateinit var userViewModel : UserViewModel
    lateinit var tripPostViewModel: TripPostViewModel
    lateinit var groupChatViewModel: GroupChatViewModel

    var currentTripRequestPosts = mutableListOf<TripPost>()

    val tripPostRepository = TripPostRepository()
    val tripRequestRepository = TripRequestRepository()
    val groupChatRepository = GroupChatRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentReceivedNotificationBinding = FragmentReceivedNotificationBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        initTripRequestViewModel(mainActivity.userClass.userEmail)
        tripRequestObserver()

        fragmentReceivedNotificationBinding.run {
            recyclerViewReceivedNotification.run {
                adapter = ReceivedNotificationiAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return fragmentReceivedNotificationBinding.root
    }

    inner class ReceivedNotificationiAdapter : RecyclerView.Adapter<ReceivedNotificationiAdapter.ReceivedNotificationViewHolder>(){

        private var requestItemList : List<TripRequest> = emptyList()

        fun updateItemList(newList : List<TripRequest>){
            this.requestItemList = newList
            notifyDataSetChanged()
            checkListEmpty(requestItemList)
        }

        inner class ReceivedNotificationViewHolder (rowReceivedNotificationBinding: RowReceivedNotificationBinding) :
                RecyclerView.ViewHolder (rowReceivedNotificationBinding.root)
        {
            val textViewNotificationRowTitle : TextView
            val imageViewNotificationRowProfileImage : ImageView
            val textViewNotificationRowNickname : TextView
            val textViewNotificationRowContent : TextView
            val buttonNotificationRowAccept : Button
            val buttonNotificationRowRefuse : Button

            init {
                textViewNotificationRowTitle = rowReceivedNotificationBinding.textViewNotificationRowTitle
                imageViewNotificationRowProfileImage = rowReceivedNotificationBinding.imageViewNotificationRowProfileImage
                textViewNotificationRowNickname = rowReceivedNotificationBinding.textViewNotificationRowNickname
                textViewNotificationRowContent = rowReceivedNotificationBinding.textViewNotificationRowContent
                buttonNotificationRowAccept = rowReceivedNotificationBinding.buttonNotificationRowAccept
                buttonNotificationRowRefuse = rowReceivedNotificationBinding.buttonNotificationRowRefuse

                //프로필 이미지와 닉네임 클릭 시 동행 리뷰 상세 화면으로 넘어감
                imageViewNotificationRowProfileImage.setOnClickListener{
                    goToAccompanyInfoFragment(requestItemList[adapterPosition].tripRequestWriterEmail,
                        textViewNotificationRowNickname.text.toString())
                }
                textViewNotificationRowNickname.setOnClickListener {
                    goToAccompanyInfoFragment(requestItemList[adapterPosition].tripRequestWriterEmail,
                        textViewNotificationRowNickname.text.toString())
                }

                //수락 버튼 클릭 이벤트
                buttonNotificationRowAccept.setOnClickListener{
                    //동행글의 모집 인원 수와 현재 멤버 수를 비교해서 추가여부 가림
                    if(currentTripRequestPosts[adapterPosition].tripPostMemberList?.size!! < currentTripRequestPosts[adapterPosition].tripPostMemberCount - 1){

                        createDialog("동행 요청 수락", "요청을 수락하면 등록하신 동행글 단체 채팅방에 해당 유저가 초대됩니다.", "수락")
                        {
                            //추가 가능한 상태라면 동행 멤버로 추가
                            //1. 동행글 데이터에 멤버로 추가
                            tripPostRepository.addTripMemberNickname(textViewNotificationRowNickname.text.toString(), requestItemList[adapterPosition].tripRequestPostId)

                            //2. textViewNotificationRowNickname 값으로 채팅 멤버에 추가
                            initGroupChatViewModel(requestItemList[adapterPosition].tripRequestPostId, textViewNotificationRowNickname.text.toString())

                            //3. 요청 상태값 바꾸기
                            runBlocking {
                                tripRequestRepository.updateTripRequestAcceptState("수락됨",
                                    tripRequestViewModel.tripRequestDocumentId.value!!.get(adapterPosition))
                            }

                            //리스트 갱신
                            initTripRequestViewModel(mainActivity.userClass.userEmail)
                        }

                    }
                    else{
                        Snackbar.make(fragmentReceivedNotificationBinding.root, "모집이 마감된 동행입니다.", Snackbar.LENGTH_SHORT).show()
                    }
                }

                //거절 버튼 클릭 이벤트
                buttonNotificationRowRefuse.setOnClickListener {
                    createDialog("동행 요청 거절", "요청을 거절하면 등록하신 동행글에 해당 유저가 다시 동행 신청을 할 수 없습니다.", "거절")
                    {
                        //요청 상태값 바꾸기
                        runBlocking{
                            tripRequestRepository.updateTripRequestAcceptState("거절됨",
                                tripRequestViewModel.tripRequestDocumentId.value!!.get(adapterPosition))
                        }

                        //리스트 갱신
                        initTripRequestViewModel(mainActivity.userClass.userEmail)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivedNotificationViewHolder {
            val rowReceivedNotificationBinding = RowReceivedNotificationBinding.inflate(layoutInflater)

            rowReceivedNotificationBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return ReceivedNotificationViewHolder(rowReceivedNotificationBinding)
        }

        override fun getItemCount(): Int {
            return requestItemList.size
        }

        override fun onBindViewHolder(holder: ReceivedNotificationViewHolder, position: Int) {

            initUserViewModel(requestItemList[position].tripRequestWriterEmail)

            if(userViewModel.userProfileImage != null){
                //아이템 프로필 이미지 설정
                Glide.with(mainActivity).load(userViewModel.userProfileImage.value)
                    .error(R.drawable.person_24px)
                    .into(holder.imageViewNotificationRowProfileImage)
            }

            //아이템 닉네임 설정
            holder.textViewNotificationRowNickname.text = userViewModel.user.value?.userNickname
            //아이템 요청 내용 설정
            holder.textViewNotificationRowContent.text = requestItemList[position].tripRequestContent
            //아이템 제목 설정
            holder.textViewNotificationRowTitle.text = currentTripRequestPosts[position].tripPostTitle
        }
    }

    //동행 요청 뷰모델 초기화 함수
    fun initTripRequestViewModel(requestReceiverEmail : String){
        tripRequestViewModel = ViewModelProvider(this)[TripRequestViewModel::class.java]
        tripRequestViewModel.getAllTripRequest(requestReceiverEmail)
    }

    fun tripRequestObserver(){
        tripRequestViewModel.tripRequest.observe(viewLifecycleOwner){ tripRequestList ->

            if(tripRequestList != null){

                val sortedTripRequestList : MutableList<TripRequest> = mutableListOf()
                currentTripRequestPosts.clear()
                //해당 유저의 동행글에 등록된 요청 중 수락 상태 값이 "대기중"인 모든 요청 목록을 가져옴
                tripRequestList.forEach { tripRequest ->

                    //요청 목록 중 등록된 동행 날짜가 지나지 않은 목록만 걸러냄
                    tripPostObserver(tripRequest.tripRequestPostId)

                    //tripPost 뷰모델 값이 null이면 날짜 조건을 만족하지 않는 값이므로 항목에 포함시키지 않음
                    if(tripPostViewModel.tripPost.value != null){
                        sortedTripRequestList.add(tripRequest)
                        currentTripRequestPosts.add(tripPostViewModel.tripPost.value!!)
                    }
                }

                //두 조건을 만족하는 요청들만 리싸이클러뷰의 항목으로 넘김
                (fragmentReceivedNotificationBinding.recyclerViewReceivedNotification.adapter as? ReceivedNotificationiAdapter)?.updateItemList(sortedTripRequestList)
            }
        }
    }

    fun tripPostObserver(tripPostDocumentId : String){
        tripPostViewModel = ViewModelProvider(this)[TripPostViewModel::class.java]
        tripPostViewModel.getTargetUserTripPost(tripPostDocumentId)
    }

    //유저 뷰모델 초기화 함수
    fun initUserViewModel(userEmail : String){
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.getTargetUserData(userEmail)
        userViewModel.getTargetUserProfileImage(userViewModel.user.value?.userProfilePath!!)
    }

    //유저의 동행 정보 화면으로 전환시키는 함수
    fun goToAccompanyInfoFragment(userEmail: String, userNickname: String){
        val newBundle = Bundle()
        newBundle.putString("userEmail", userEmail)
        newBundle.putString("userNickname", userNickname)

        mainActivity.replaceFragment(MainActivity.MY_ACCOMPANY_INFO_FRAGMENT, true, true, newBundle)
    }

    //다이얼로그 생성 함수
    fun createDialog(title : String, message : String, buttonLabel : String, callback : () -> Unit){
        MaterialAlertDialogBuilder(mainActivity,R.style.DialogTheme).run {
            setTitle(title)
            setMessage(message)
            setNegativeButton("취소", null)
            setPositiveButton(buttonLabel){ dialogInterface: DialogInterface, i: Int ->
                callback()
            }
            show()
        }
    }

    //화면에 표시될 내용 있는지 확인
    fun checkListEmpty(list : List<TripRequest>){
        if(list.isEmpty()){
            fragmentReceivedNotificationBinding.linearLayoutNoRequest.visibility = View.VISIBLE
        }
        else{
            fragmentReceivedNotificationBinding.linearLayoutNoRequest.visibility = View.GONE
        }
    }

    fun initGroupChatViewModel(tripPostDocumentId : String, userNickname : String){
        groupChatViewModel = ViewModelProvider(this)[GroupChatViewModel::class.java]
        groupChatViewModel.getGroupChatDocumentId(tripPostDocumentId)

        groupChatRepository.addGroupChatMemberNickname(userNickname,
            groupChatViewModel.groupChatDocumentId.value!!)
    }
}