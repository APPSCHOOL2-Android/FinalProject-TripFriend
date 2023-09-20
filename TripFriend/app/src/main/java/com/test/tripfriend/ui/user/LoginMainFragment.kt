package com.test.tripfriend.ui.user

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentLoginMainBinding
import com.test.tripfriend.dataclassmodel.User
import com.test.tripfriend.repository.UserRepository

class LoginMainFragment : Fragment() {

    lateinit var fragmentLoginMainBinding: FragmentLoginMainBinding
    lateinit var loginMainActivity: LoginMainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginMainActivity = activity as LoginMainActivity
        fragmentLoginMainBinding = FragmentLoginMainBinding.inflate(inflater)

        //자동 로그인
//        val sharedPreferences =
//            loginMainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)
//        if(UserRepository.checkUserInfo(sharedPreferences) == true)
//        {
//            val intent = Intent(loginMainActivity, MainActivity::class.java)
//            startActivity(intent)
//            loginMainActivity.finish()
//        }

        fragmentLoginMainBinding.run {
            buttonLoginMainEmailLogin.setOnClickListener {
                val bundle = Bundle()
                bundle.putBoolean("check",checkBoxLoginMainAutoLogin.isChecked)
                loginMainActivity.replaceFragment(LoginMainActivity.EMAIL_LOGIN_FRAGMENT, true, true, bundle)
            }

            //카카오 로그인
            buttonLoginMainKakaoLogin.run {
                setOnClickListener {
                    KakaoSdk.init(loginMainActivity, "381e6f58b057f226b499d671682fe581")
                    kakaoLogin() //로그인
                }
            }
            //네이버로그인
            buttonLoginMainNaverLogin.run {
                setOnClickListener {
                    naverLogin() //로그인
                }
            }
            checkBoxLoginMainAutoLogin.run {
                setTextColor(getResources().getColor(R.color.black))
                setOnClickListener{
                    if(isChecked == true){
                        Snackbar.make(fragmentLoginMainBinding.root, "자동 로그인 설정", Snackbar.LENGTH_SHORT).show()
                    }
                    else{
                        Snackbar.make(fragmentLoginMainBinding.root, "자동 로그인 해제", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }

            textViewLoginMainJoin.setOnClickListener {
                loginMainActivity.replaceFragment(LoginMainActivity.JOIN_STEP_ONE_FRAGMENT, true, true, null)
            }

            //둘러보기
            textViewLoginMainNonMember.setOnClickListener {

                val sharedPreferences =
                    loginMainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)
                UserRepository.saveNoneUserInfo(sharedPreferences)

                val intent = Intent(loginMainActivity, MainActivity::class.java)
                startActivity(intent)

                loginMainActivity.finish()
            }
        }

        return fragmentLoginMainBinding.root
    }
    fun kakaoLogin() {
        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
            } else if (token != null) {
                //최종적으로 카카오로그인 및 유저정보 가져온 결과
                UserApiClient.instance.me { user, error ->
                    loginMainActivity.userAuth = "카카오"
                    loginMainActivity.userEmail = "${user?.kakaoAccount?.email}"
                    loginMainActivity.userNickname = user?.properties?.get("nickname").toString()
                    var checkAuth = 1
                    var checkLogin = 1
                    //이메일 중복 검사
                    UserRepository.getAllUser{
                        for (document in it.result.documents) {
                            //중복된 이메일은 있지만 인증 방식이 카카오가 아니면
                            if (loginMainActivity.userEmail == document.getString("userEmail")
                                && loginMainActivity.userAuth != document.getString("userAuthentication")) {
                                checkAuth = 0
                                val builder = MaterialAlertDialogBuilder(loginMainActivity, R.style.DialogTheme).apply {
                                    setTitle("로그인 오류")
                                    setMessage("${loginMainActivity.userEmail}은 현재 사용중인 이메일입니다.")
                                    setNegativeButton("확인", null)
                                }
                                builder.show()
                                break
                            }
                            //중복된 이메일이 있고 인증 방식이 카카오면
                            else if(loginMainActivity.userEmail == document.getString("userEmail")
                                && loginMainActivity.userAuth == document.getString("userAuthentication")){
                                checkLogin = 0

                                val userAuthentication= document.getString("userAuthentication").toString()
                                val userEmail = document.getString("userEmail").toString()
                                val userPw = document.getString("userPw").toString()
                                val userNickname= document.getString("userNickname").toString()
                                val userName= document.getString("userName").toString()
                                val userPhoneNum= document.getString("userPhoneNum").toString()
                                val userMBTI= document.getString("userMBTI").toString()
                                val userProfilePath= document.getString("userProfilePath").toString()
                                val userFriendSpeed= document.getDouble("userFriendSpeed")!!
                                val userTripScore= document.getDouble("userTripScore")!!
                                val userTripCount= document.getLong("userTripCount")?.toInt()!!
                                val userChatNotification = document.getBoolean("userChatNotification")!!
                                val userPushNotification= document.getBoolean("userPushNotification")!!

                                val userClass = User(
                                    userAuthentication,
                                    userEmail,
                                    userPw ,
                                    userNickname ,
                                    userName,
                                    userPhoneNum ,
                                    userMBTI,
                                    userProfilePath = userProfilePath,
                                    userFriendSpeed = userFriendSpeed,
                                    userTripScore = userTripScore,
                                    userTripCount = userTripCount,
                                    userChatNotification = userChatNotification,
                                    userPushNotification = userPushNotification
                                )
                                val checkStateAutoLogin = fragmentLoginMainBinding.checkBoxLoginMainAutoLogin.isChecked

                                val sharedPreferences =
                                    loginMainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)
                                UserRepository.saveUserInfo(sharedPreferences,userClass,checkStateAutoLogin)

                                val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                                    setTitle("로그인 성공")
                                    setMessage("로그인에 성공하였습니다. 트립친과 함께 좋은 여행이 되길 바랍니다.^^")
                                    setNegativeButton("메인화면으로",null)
                                    setOnDismissListener{
                                        val intent = Intent(loginMainActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        loginMainActivity.finish()
                                    }
                                }
                                builder.show()
                            }
                        }
                        //중복된 이메일도 없고 첫 로그인일 때
                        if(checkAuth == 1 && checkLogin == 1){
                            //카카오 정보 기반으로 회원가입 진행
                            val bundle = Bundle()
                            bundle.putBoolean("check",fragmentLoginMainBinding.checkBoxLoginMainAutoLogin.isChecked)
                            loginMainActivity.replaceFragment(LoginMainActivity.MORE_KAKAO_INFO_INPUT_FRAGMENT,true,true,bundle)
                        }
                    }
                }
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(loginMainActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(loginMainActivity) { token, error ->
                if (error != null) {
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(loginMainActivity, callback = callback)
                } else if (token != null) {
                    UserApiClient.instance.me { user, error ->
                        loginMainActivity.userAuth = "카카오"
                        loginMainActivity.userEmail = "${user?.kakaoAccount?.email}"
                        loginMainActivity.userNickname = user?.properties?.get("nickname").toString()
                        var checkAuth = 1
                        var checkLogin = 1
                        //이메일 중복 검사
                        UserRepository.getAllUser{
                            for (document in it.result.documents) {
                                //중복된 이메일은 있지만 인증 방식이 카카오가 아니면
                                if (loginMainActivity.userEmail == document.getString("userEmail")
                                    && loginMainActivity.userAuth != document.getString("userAuthentication")) {
                                    checkAuth = 0
                                    val builder = MaterialAlertDialogBuilder(loginMainActivity, R.style.DialogTheme).apply {
                                        setTitle("이메일 입력 오류")
                                        setMessage("${loginMainActivity.userEmail}은 이미 사용중인 이메일입니다.")
                                        setNegativeButton("확인", null)
                                    }
                                    builder.show()
                                    break
                                }
                                //중복된 이메일이 있고 인증 방식이 카카오면
                                else if(loginMainActivity.userEmail == document.getString("userEmail")
                                    && loginMainActivity.userAuth == document.getString("userAuthentication")){

                                    val userAuthentication= document.getString("userAuthentication").toString()
                                    val userEmail = document.getString("userEmail").toString()
                                    val userPw = document.getString("userPw").toString()
                                    val userNickname= document.getString("userNickname").toString()
                                    val userName= document.getString("userName").toString()
                                    val userPhoneNum= document.getString("userPhoneNum").toString()
                                    val userMBTI= document.getString("userMBTI").toString()
                                    val userProfilePath= document.getString("userProfilePath").toString()
                                    val userFriendSpeed= document.getDouble("userFriendSpeed")!!
                                    val userTripScore= document.getDouble("userTripScore")!!
                                    val userTripCount= document.getLong("userTripCount")?.toInt()!!
                                    val userChatNotification = document.getBoolean("userChatNotification")!!
                                    val userPushNotification= document.getBoolean("userPushNotification")!!

                                    val userClass = User(
                                        userAuthentication,
                                        userEmail,
                                        userPw ,
                                        userNickname ,
                                        userName,
                                        userPhoneNum ,
                                        userMBTI,
                                        userProfilePath = userProfilePath,
                                        userFriendSpeed = userFriendSpeed,
                                        userTripScore = userTripScore,
                                        userTripCount = userTripCount,
                                        userChatNotification = userChatNotification,
                                        userPushNotification = userPushNotification
                                    )
                                    val checkStateAutoLogin = fragmentLoginMainBinding.checkBoxLoginMainAutoLogin.isChecked


                                    val sharedPreferences =
                                        loginMainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)

                                    UserRepository.saveUserInfo(sharedPreferences,userClass,checkStateAutoLogin)

                                    val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                                        setTitle("로그인 성공")
                                        setMessage("로그인에 성공하였습니다. 트립친과 함께 좋은 여행이 되길 바랍니다.^^")
                                        setNegativeButton("메인화면으로",null)
                                        setOnDismissListener{
                                            val intent = Intent(loginMainActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            loginMainActivity.finish()
                                        }
                                    }
                                    builder.show()
                                    checkLogin = 0
                                }
                            }
                            //중복된 이메일도 없고 첫 로그인일 때
                            if(checkAuth == 1 && checkLogin == 1){
                                //카카오 정보 기반으로 회원가입 진행
                                val bundle = Bundle()
                                bundle.putBoolean("check",fragmentLoginMainBinding.checkBoxLoginMainAutoLogin.isChecked)
                                loginMainActivity.replaceFragment(LoginMainActivity.MORE_KAKAO_INFO_INPUT_FRAGMENT,true,true,bundle)
                            }
                        }
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(loginMainActivity, callback = callback)
        }
    }
    fun TextMsg(act: Activity, msg : String){
        fragmentLoginMainBinding.buttonLoginMainKakaoLogin.setText(msg)
    }

    fun kakaoLogout(){
        UserApiClient.instance.logout { error ->
            if (error != null) {

            }
            else {

            }
        }
    }
    fun kakaoUnlink(){
        UserApiClient.instance.unlink { error ->
            if (error != null) {

            }
            else {

            }
        }
    }

    fun naverLogin(){
        /** Naver Login Module Initialize */
        val naverClientId = getString(R.string.social_login_info_naver_client_id)
        val naverClientSecret = getString(R.string.social_login_info_naver_client_secret)
        val naverClientName = getString(R.string.social_login_info_naver_client_name)
        NaverIdLoginSDK.initialize(loginMainActivity, naverClientId, naverClientSecret , naverClientName)

        var naverToken :String? = ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                val email = response.profile?.email
                val nickname = response.profile?.nickname
                val phoneNum = response.profile?.mobile
                val name = response.profile?.name
                val userInfo = "Email: $email\nNickname: $nickname\nphoneNum = $phoneNum\nname = $name"
//                fragmentLoginMainBinding.textViewLoginMainSubTitle.text = userInfo

                loginMainActivity.userName = name.toString()
                loginMainActivity.userEmail = email.toString()
                loginMainActivity.userPhoneNumber = phoneNum?.replace("-","").toString()
                loginMainActivity.userNickname = nickname.toString()
                loginMainActivity.userAuth = "네이버"

                var checkAuth = 1
                var checkLogin = 1
                UserRepository.getAllUser {
                    for (document in it.result.documents) {
                        //이메일은 이미 있지만 네이버 인증이 아니면
                        if(document.getString("userEmail") == loginMainActivity.userEmail &&
                            document.getString("userAuthentication") != "네이버"){
                            val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                                setTitle("로그인 오류")
                                setMessage("${loginMainActivity.userEmail}은 이미 사용중인 이메일입니다.")
                                setNegativeButton("취소",null)
                            }
                            builder.show()
                            checkAuth = 0
                            break
                        }
                        //이메일이 있고 인증이 네이버라면 -> 로그인
                        else if(document.getString("userEmail") == loginMainActivity.userEmail &&
                            document.getString("userAuthentication") == "네이버"){

                            val userAuthentication= document.getString("userAuthentication").toString()
                            val userEmail = document.getString("userEmail").toString()
                            val userPw = document.getString("userPw").toString()
                            val userNickname= document.getString("userNickname").toString()
                            val userName= document.getString("userName").toString()
                            val userPhoneNum= document.getString("userPhoneNum").toString()
                            val userMBTI= document.getString("userMBTI").toString()
                            val userProfilePath= document.getString("userProfilePath").toString()
                            val userFriendSpeed= document.getDouble("userFriendSpeed")!!
                            val userTripScore= document.getDouble("userTripScore")!!
                            val userTripCount= document.getLong("userTripCount")?.toInt()!!
                            val userChatNotification = document.getBoolean("userChatNotification")!!
                            val userPushNotification= document.getBoolean("userPushNotification")!!

                            val userClass = User(
                                userAuthentication,
                                userEmail,
                                userPw ,
                                userNickname ,
                                userName,
                                userPhoneNum ,
                                userMBTI,
                                userProfilePath = userProfilePath,
                                userFriendSpeed = userFriendSpeed,
                                userTripScore = userTripScore,
                                userTripCount = userTripCount,
                                userChatNotification = userChatNotification,
                                userPushNotification = userPushNotification
                            )
                            val checkStateAutoLogin = fragmentLoginMainBinding.checkBoxLoginMainAutoLogin.isChecked

                            val sharedPreferences =
                                loginMainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)

                            UserRepository.saveUserInfo(sharedPreferences,userClass,checkStateAutoLogin)

                            val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                                setTitle("로그인 성공")
                                setMessage("로그인에 성공하였습니다. 트립친과 함께 좋은 여행이 되길 바랍니다.^^")
                                setNegativeButton("메인화면으로",null)
                                setOnDismissListener{
                                    val intent = Intent(loginMainActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    loginMainActivity.finish()
                                }
                            }
                            builder.show()
                            checkLogin = 0
                            break
                        }
                    }
                    // 첫 로그인일 경우 추가정보 입력 ㄱ
                    if(checkAuth ==1 && checkLogin == 1){
                        val bundle = Bundle()
                        bundle.putBoolean("check",fragmentLoginMainBinding.checkBoxLoginMainAutoLogin.isChecked)
                        loginMainActivity.replaceFragment(LoginMainActivity.MORE_NAVER_INFO_INPUT_FRAGMENT,true,true,bundle)
                    }
                }

//                Toast.makeText(loginMainActivity, "네이버 아이디 로그인 성공!", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
//                Toast.makeText(loginMainActivity, "errorCode: ${errorCode}\n" +
//                        "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        /** OAuthLoginCallback을 authenticate() 메서드 호출 시 파라미터로 전달하거나 NidOAuthLoginButton 객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다. */
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                naverToken = NaverIdLoginSDK.getAccessToken()
//                var naverRefreshToken = NaverIdLoginSDK.getRefreshToken()
//                var naverExpiresAt = NaverIdLoginSDK.getExpiresAt().toString()
//                var naverTokenType = NaverIdLoginSDK.getTokenType()
//                var naverState = NaverIdLoginSDK.getState().toString()

                //로그인 유저 정보 가져오기
                NidOAuthLogin().callProfileApi(profileCallback)
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
//                Toast.makeText(loginMainActivity, "errorCode: ${errorCode}\n" +
//                        "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
//                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(loginMainActivity, oauthLoginCallback)
    }

    //네이버 로그아웃
    fun startNaverLogout(){
        NaverIdLoginSDK.logout()
        Toast.makeText(loginMainActivity, "네이버 아이디 로그아웃 성공!", Toast.LENGTH_SHORT).show()
    }

    //토큰 삭제
    fun startNaverDeleteToken(){
        NidOAuthLogin().callDeleteTokenApi(loginMainActivity, object : OAuthLoginCallback {
            override fun onSuccess() {
                //서버에서 토큰 삭제에 성공한 상태입니다.
                Toast.makeText(loginMainActivity, "네이버 아이디 토큰삭제 성공!", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Log.d("naver", "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.d("naver", "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }
            override fun onError(errorCode: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                onFailure(errorCode, message)
            }
        })
    }
}