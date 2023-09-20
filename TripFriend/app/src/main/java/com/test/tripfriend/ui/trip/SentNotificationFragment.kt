package com.test.tripfriend.ui.trip

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentSentNotificationBinding
import com.test.tripfriend.databinding.RowReceivedNotificationBinding
import com.test.tripfriend.databinding.RowSentNotificiationBinding
import com.test.tripfriend.dataclassmodel.TripPost
import com.test.tripfriend.dataclassmodel.TripRequest
import com.test.tripfriend.repository.TripRequestRepository
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.viewmodel.TripPostViewModel
import com.test.tripfriend.viewmodel.TripRequestViewModel
import kotlinx.coroutines.runBlocking


class SentNotificationFragment : Fragment() {

    lateinit var fragmentSentNotificationBinding: FragmentSentNotificationBinding
    lateinit var mainActivity: MainActivity

    lateinit var tripRequestViewModel : TripRequestViewModel
    lateinit var tripPostViewModel: TripPostViewModel

    var currentSentTripRequestPosts = mutableListOf<TripPost>()

    val tripRequestRepository = TripRequestRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentSentNotificationBinding = FragmentSentNotificationBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        initSentTripRequestViewModel(mainActivity.userClass.userEmail)
        sentTripRequestObserver()

        fragmentSentNotificationBinding.run {
            recyclerViewSentNotification.run{
                adapter = SentNotificationAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return fragmentSentNotificationBinding.root
    }

    inner class SentNotificationAdapter : RecyclerView.Adapter<SentNotificationAdapter.SentNofiticationViewHolder>(){

        private var requestItemList : List<TripRequest> = emptyList()

        fun updateItemList(newList : List<TripRequest>){
            this.requestItemList = newList
            notifyDataSetChanged()
            checkListEmpty(requestItemList)
        }

        inner class SentNofiticationViewHolder (rowSentNotificiationBinding: RowSentNotificiationBinding) :
                RecyclerView.ViewHolder(rowSentNotificiationBinding.root)
        {
            val textViewSentNotificationRowTitle : TextView
            val textViewSentNotificationRowAccptState : TextView
            val buttonSentNotificationRowCancel : Button

            init {
                textViewSentNotificationRowTitle = rowSentNotificiationBinding.textViewSentNotificationRowTitle
                textViewSentNotificationRowAccptState = rowSentNotificiationBinding.textViewSentNotificationRowAccptState
                buttonSentNotificationRowCancel = rowSentNotificiationBinding.buttonSentNotificationRowCancel

                buttonSentNotificationRowCancel.setOnClickListener {
                    MaterialAlertDialogBuilder(mainActivity,R.style.DialogTheme).run {
                        setTitle("동행 요청 취소")
                        setMessage("요청을 취소하면 동행글 작성자에게 해당 요청이 더 이상 보이지 않습니다.")
                        setNegativeButton("뒤로가기", null)
                        setPositiveButton("요청취소"){ dialogInterface: DialogInterface, i: Int ->

                            runBlocking {
                                tripRequestRepository.deleteSentTripRequest(tripRequestViewModel.sentTripRequestDocumentId.value!!.get(adapterPosition))
                            }

                            initSentTripRequestViewModel(mainActivity.userClass.userEmail)
                        }
                        show()
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentNofiticationViewHolder {
            val rowSentNotificiationBinding = RowSentNotificiationBinding.inflate(layoutInflater)

            rowSentNotificiationBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return SentNofiticationViewHolder(rowSentNotificiationBinding)
        }

        override fun getItemCount(): Int {
            return requestItemList.size
        }

        override fun onBindViewHolder(holder: SentNofiticationViewHolder, position: Int) {
            holder.textViewSentNotificationRowTitle.text = currentSentTripRequestPosts[position].tripPostTitle
            holder.textViewSentNotificationRowAccptState.text = requestItemList[position].tripRequestState
            holder.buttonSentNotificationRowCancel.visibility =
                if(requestItemList[position].tripRequestState == "대기중") View.VISIBLE else View.GONE
        }
    }

    //동행 요청 뷰모델 초기화 함수
    fun initSentTripRequestViewModel(requestWriterEmail : String){
        tripRequestViewModel = ViewModelProvider(this)[TripRequestViewModel::class.java]
        tripRequestViewModel.getAllSentTripRequest(requestWriterEmail)
    }

    fun sentTripRequestObserver(){
        tripRequestViewModel.sentTripRequest.observe(viewLifecycleOwner){ sentTripRequestList ->

            if(sentTripRequestList != null){
                val sortedSentTripRequestList : MutableList<TripRequest> = mutableListOf()
                currentSentTripRequestPosts.clear()

                sentTripRequestList.forEach { sentTripRequest ->
                    Log.d("tripRequest", sentTripRequest.tripRequestPostId)

                    //요청 목록 중 등록된 동행 날짜가 지나지 않은 목록만 걸러냄
                    tripPostObserver(sentTripRequest.tripRequestPostId)

                    //tripPost 뷰모델 값이 null이면 날짜 조건을 만족하지 않는 값이므로 항목에 포함시키지 않음
                    if(tripPostViewModel.tripPost.value != null){
                        Log.d("tripPostDate", tripPostViewModel.tripPost.value!!.tripPostTitle)
                        Log.d("tripPostDate", tripPostViewModel.tripPost.value?.tripPostDate?.get(0)!!)
                        sortedSentTripRequestList.add(sentTripRequest)
                        currentSentTripRequestPosts.add(tripPostViewModel.tripPost.value!!)
                    }
                }
                (fragmentSentNotificationBinding.recyclerViewSentNotification.adapter as? SentNotificationAdapter)?.updateItemList(sortedSentTripRequestList)
            }
        }
    }

    fun tripPostObserver(tripPostDocumentId : String){
        tripPostViewModel = ViewModelProvider(this)[TripPostViewModel::class.java]
        tripPostViewModel.getTargetUserTripPost(tripPostDocumentId)
    }

    //화면에 표시될 내용 있는지 확인
    fun checkListEmpty(list : List<TripRequest>){
        if(list.isEmpty()){
            fragmentSentNotificationBinding.linearLayoutNoRequest.visibility = View.VISIBLE
        }
        else{
            fragmentSentNotificationBinding.linearLayoutNoRequest.visibility = View.GONE
        }
    }
}