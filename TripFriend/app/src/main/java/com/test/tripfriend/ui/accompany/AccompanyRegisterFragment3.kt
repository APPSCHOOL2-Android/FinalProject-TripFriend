package com.test.tripfriend.ui.accompany

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.test.tripfriend.R
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.databinding.FragmentAccompanyRegister3Binding
import com.test.tripfriend.dataclassmodel.TripPost
import com.test.tripfriend.repository.AccompanyRegisterRepository
import com.test.tripfriend.repository.UserRepository
import kotlinx.coroutines.runBlocking

class AccompanyRegisterFragment3 : Fragment() {
    lateinit var fragmentAccompanyRegisterFragment3: FragmentAccompanyRegister3Binding
    lateinit var mainActivity: MainActivity


    // 최대 선택 가능 Chip 갯수
    val maxSelectableChips = 3

    // 칩 카운트 변수
    var chipCount = 0

    val chipCategory = mutableListOf<String>()
    val chipGender = arrayOf<Boolean>(false, false)

    var categories = mutableListOf<Chip>()
    var categoryChecked = false
    var genderChecked = false

    val accompanyRegisterRepository = AccompanyRegisterRepository()

    var documentId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAccompanyRegisterFragment3 =
            FragmentAccompanyRegister3Binding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val sharedPreferences =
            mainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userClass = UserRepository.getUserInfo(sharedPreferences)

        // bundle 가져오기

        val country = arguments?.getString("country")
        val title = arguments?.getString("title")
        val postImagePath = arguments?.getString("postImagePath") as String
        val dates = arguments?.getStringArray("dates") as List<*>?
        val people = arguments?.getString("people")
        val content = arguments?.getString("content")
        val tripPostIdx = arguments?.getLong("tripPostIdx")
        val startDate = arguments?.getString("startDate")
        val endDate = arguments?.getString("endDate")
        val latitude = arguments?.getDouble("latitude")
        val longitude = arguments?.getDouble("longitude")
        val image = arguments?.getString("imageUri")
        val imageUri = Uri.parse(image)

        val date = mutableListOf<String>()

        date.add(startDate.toString())
        date.add(endDate.toString())

        Log.d("qwer", "$country")
        Log.d("qwer", "${date.get(0)} ${date.get(1)} $dates")
        Log.d("qwer", "$postImagePath")
        Log.d("qwer", "${title} ${people} $content")

        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE

        fragmentAccompanyRegisterFragment3.run {
            materialToolbarRegister3.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT3)
                }
            }

            chipMax(chipRegister3Category1)
            chipMax(chipRegister3Category2)
            chipMax(chipRegister3Category3)
            chipMax(chipRegister3Category4)
            chipMax(chipRegister3Category5)
            chipMax(chipRegister3Category6)
            chipMax(chipRegister3Category7)
            chipMax(chipRegister3Category8)
            chipMax(chipRegister3Category9)

            categories.add(chipRegister3Category1)
            categories.add(chipRegister3Category2)
            categories.add(chipRegister3Category3)
            categories.add(chipRegister3Category4)
            categories.add(chipRegister3Category5)
            categories.add(chipRegister3Category6)
            categories.add(chipRegister3Category7)
            categories.add(chipRegister3Category8)
            categories.add(chipRegister3Category9)

            buttonAccompanyRegister3ToSubmit.setOnClickListener {

                for (category in categories) {
                    if (category.isChecked) {
                        chipCategory.add(category.text.toString())
                        categoryChecked = true
                    }

                }

                chipGender[0] = chipGender1.isChecked
                chipGender[1] = chipGender2.isChecked

                Log.d("qwer", "chipGender : ${chipGender[0]}, ${chipGender[1]}")
                Log.d("qwer", "chipGenderif : ${!(chipGender[0] == true || chipGender[1] == true)}")

                val hashTag = textInputEditTextRegister3Hashtag.text.toString()

                if (!categoryChecked) {
                    MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                        setTitle("카테고리 입력")
                        setMessage("카테고리를 입력해주세요.")
                        setNegativeButton("닫기", null)
                        show()
                        return@setOnClickListener
                    }
                }

                if (!(chipGender[0] == true || chipGender[1] == true)) {
                    MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                        setTitle("성별 입력")
                        setMessage("성별을 입력해주세요.")
                        setNegativeButton("닫기", null)
                        show()
                        return@setOnClickListener
                    }
                }

                if(textInputEditTextRegister3Hashtag.text.toString() == "") {
                    MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                        setTitle("해시태그 입력")
                        setMessage("해시태그를 입력해주세요.")
                        setNegativeButton("닫기", null)
                        show()
                        return@setOnClickListener
                    }
                }

                if (title != null && country != null && content != null && tripPostIdx != null && latitude != null && longitude != null) {
                    val tripPost = TripPost(
                        userClass.userEmail,
                        title,
                        null,
                        people!!.toInt(),
                        postImagePath,
                        date,
                        country,
                        latitude,
                        longitude,
                        emptyList(),
                        chipCategory,
                        hashTag,
                        content,
                        "",
                        tripPostIdx.toInt(),
                        chipGender.toList()
                    )

//                    Log.d("qwer", "isEmpty() : ${postImagePath?.isEmpty()}")
//                    Log.d("qwer", "length : ${postImagePath?.length}")
//                    Log.d("qwer", "postImagePath : ${postImagePath}")
//                    Log.d("qwer", "toString() : ${postImagePath.toString()}")
//                    Log.d("qwer", "isNullOrEmpty() : ${postImagePath.isNullOrEmpty()}")
//                    Log.d("qwer", "orEmpty : ${postImagePath.orEmpty()}")

                    runBlocking { accompanyRegisterRepository.saveAccompanyToDB(tripPost) {
                        Log.d("qwer", "result id : ${it.result.id}")
                        documentId = it.result.id

                        accompanyRegisterRepository.uploadImages(imageUri, postImagePath) {
                            Log.d("qwer", "이미지 저장 되고나서 이동")
                            completePost(userClass.userEmail, documentId)
                        }
                    } }

//                    if(postImagePath.isEmpty()) {
//                        accompanyRegisterRepository.uploadImages(imageUri, postImagePath) {
//                            Log.d("qwer", "???")
//                            completePost(userClass.userEmail, documentId)
//                        }
//                    } else {
//
//                    }



//                    if(imageUri == null) {
//                        Log.d("qwer", "ImageUri == null")
//                    } else {
//                        Log.d("qwer", "Image 있는 경우")
//                        if (postImagePath != null) {
//                            Log.d("qwer", "postImagePath != null 인 경우")
//                            accompanyRegisterRepository.uploadImages(imageUri, postImagePath) {
//                                Log.d("qwer", "이미지 저장 되고나서 이동")
//                                completePost(userClass.userEmail, documentId)
//                            }
//                        } else {
//                            Log.d("qwer", "Image 없는 경우")
//                            // Image 없는 경우
//                            completePost(userClass.userEmail, documentId)
//                        }
//                    }

                }

            }
        }

        return fragmentAccompanyRegisterFragment3.root
    }

    // 최대 3개 이상의 칩을 선택 못하게 하는 함수
    fun chipMax(chipId: Chip) {
        chipId.setOnClickListener {
            if (chipId.isChecked) {
                if (chipCount >= maxSelectableChips) {
                    chipId.isChecked = false
                    Snackbar.make(
                        fragmentAccompanyRegisterFragment3.root,
                        "여행 카테고리는 최대 3개 선택 가능합니다.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    chipCount++
                }
            } else {
                chipCount--
            }
        }
    }

    fun completePost(userEmail: String, documentId: String) {
        Snackbar.make(
            mainActivity.activityMainBinding.root,
            "등록이 완료되었습니다..",
            Snackbar.LENGTH_SHORT
        ).show()

        val bundle = Bundle()
        bundle.putString("tripPostWriterEmail", userEmail)
        bundle.putString("tripPostDocumentId", documentId)
        bundle.putString("viewState", "InProgress")

        mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT3)
        mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT2)
        mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT1)

        mainActivity.replaceFragment(MainActivity.READ_POST_FRAGMENT, true, true, bundle)
    }
}