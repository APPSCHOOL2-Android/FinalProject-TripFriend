package com.test.tripfriend.ui.accompany

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.bumptech.glide.Glide
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.databinding.FragmentAccompanyRegister2Binding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AccompanyRegisterFragment2 : Fragment() {
    lateinit var fragmentAccompanyRegisterFragment2: FragmentAccompanyRegister2Binding
    lateinit var mainActivity: MainActivity

    lateinit var albumLauncher: ActivityResultLauncher<Intent>

    var profileImage : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAccompanyRegisterFragment2 = FragmentAccompanyRegister2Binding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE

        //앨범 런처 초기화
        albumLauncher = albumSetting(fragmentAccompanyRegisterFragment2.imageViewRegister2)

        fragmentAccompanyRegisterFragment2.run {
            materialToolbarRegister2.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT2)
                }
            }

            // 앨범이미지
            imageViewRegister2.run {
                setOnClickListener {
                    //앨범 이동
                    val albumIntent = Intent(Intent.ACTION_PICK)
                    albumIntent.setType("image/*")

                    val mimeType = arrayOf("image/*")
                    albumIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                    albumLauncher.launch(albumIntent)
                }
            }
            
            // 달력
            textInputLayoutRegister2Date.run {
                // 데이트 피커
                fragmentAccompanyRegisterFragment2.calendarRegister2.setCalendarListener(object :
                    CalendarListener {
                    override fun onFirstDateSelected(startDate: Calendar) {
                        val date = startDate.time
                        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        Toast.makeText(mainActivity, "Start Date: " + format.format(date), Toast.LENGTH_SHORT).show()
                    }

                    override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                        val startDate = startDate.time
                        val endDate = endDate.time
                        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        Toast.makeText(mainActivity, "Start Date: " + format.format(startDate) + "\nEnd date: " + format.format(endDate), Toast.LENGTH_SHORT).show()
                    }
                })
            }

            // 다음 버튼
            buttonAccompanyRegister2ToNextView.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT3, true, true, null)
            }
        }

        return fragmentAccompanyRegisterFragment2.root
    }

    // 앨범 설정 함수
    fun albumSetting(imageView : ImageView) : ActivityResultLauncher<Intent> {
        //앨범에서 이미지 가져오기
        val albumLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            //이미지 가져오기 성공
            if(it.resultCode == Activity.RESULT_OK){
                it.data?.data?.let { uri ->
                    if(uri != null){
                        profileImage = uri

                        setImage(uri, imageView)
                    }
                }
            }
        }
        return albumLauncher
    }

    //uri를 이미지뷰에 셋팅하는 함수
    fun setImage(image: Uri, imageView: ImageView){
        val inputStream = mainActivity.contentResolver.openInputStream(image)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        //회전 각도값을 가져옴
        val degree = getDegree(image)

        //회전 이미지를 생성한다
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotateBitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, false)

        //글라이드 라이브러리로 view에 이미지 출력
        Glide.with(mainActivity).load(rotateBitmap)
            .into(imageView)
    }

    // 이미지 파일에 기록되어 있는 회전 정보를 가져온다.
    fun getDegree(uri:Uri) : Int{
        var exifInterface: ExifInterface? = null

        // 사진 파일로 부터 tag 정보를 관리하는 객체를 추출한다.
        try {
            val inputStream = mainActivity.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                exifInterface = ExifInterface(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var degree = 0
        if(exifInterface != null){
            // 각도 값을 가지고온다.
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)

            when(orientation){
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        }
        return degree
    }

}