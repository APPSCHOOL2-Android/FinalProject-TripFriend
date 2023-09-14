package com.test.tripfriend.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.databinding.FragmentHomeListBinding
import com.test.tripfriend.databinding.RowHomeListBinding

class HomeListFragment : Fragment() {
    lateinit var fragmentHomeListBinding: FragmentHomeListBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeListBinding = FragmentHomeListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentHomeListBinding.run {
            recyclerViewHomeList.run {
                adapter = HomeListAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }

        return fragmentHomeListBinding.root
    }

    inner class HomeListAdapter : RecyclerView.Adapter<HomeListAdapter.HomeListViewHolder>() {
        inner class HomeListViewHolder(rowHomeListBinding: RowHomeListBinding) : RecyclerView.ViewHolder(rowHomeListBinding.root) {
            val textViewHomeMainListRowTitle: TextView
            val textViewHomeMainListDate: TextView
            val textViewHomeMainListRowNOP: TextView
            val textViewHomeMainListRowLocation: TextView
            val textViewHomeMainListRowHashTag: TextView

            init {
                textViewHomeMainListRowTitle = rowHomeListBinding.textViewHomeMainListRowTitle
                textViewHomeMainListDate = rowHomeListBinding.textViewHomeMainListDate
                textViewHomeMainListRowNOP = rowHomeListBinding.textViewHomeMainListRowNOP
                textViewHomeMainListRowLocation = rowHomeListBinding.textViewHomeMainListRowLocation
                textViewHomeMainListRowHashTag = rowHomeListBinding.textViewHomeMainListRowHashTag

                rowHomeListBinding.root.setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.READ_POST_FRAGMENT, true, true, null)

                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListViewHolder {
            val rowHomeListBinding = RowHomeListBinding.inflate(layoutInflater)

            rowHomeListBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            return HomeListViewHolder(rowHomeListBinding)
        }

        override fun getItemCount(): Int {
            return 4
        }

        override fun onBindViewHolder(holder: HomeListViewHolder, position: Int) {
            holder.textViewHomeMainListRowTitle.text = "부산 여행갈분"
            holder.textViewHomeMainListDate.text = "2023.09.29 ~ 2023.09.30"
            holder.textViewHomeMainListRowNOP.text = "4명"
            holder.textViewHomeMainListRowLocation.text = "부산"
            holder.textViewHomeMainListRowHashTag.text = "#식도락 #해운대 #해수욕장 #밀면 #암소고기 #해안열차 #부산 #맛집탐방 #바다 #놀러가자 #뿌엥"
        }
    }
}