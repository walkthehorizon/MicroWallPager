package com.shentu.paper.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.TimeUtils
import com.micro.integration.RepositoryManager
import com.shentu.paper.app.Constant
import com.shentu.paper.app.HkUserManager
import com.shentu.paper.app.errorHandler
import com.shentu.paper.model.api.service.UserService
import com.shentu.paper.model.entity.MicroUser
import com.shentu.paper.model.response.BaseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val mRepositoryManager: RepositoryManager) :
    ViewModel() {

    val liveData = MutableLiveData<BaseResponse<MicroUser>>()
    val signLiveData = MutableLiveData<Int>()

    fun loadAccountInfo() {
        viewModelScope.launch (errorHandler){
            val response = mRepositoryManager.obtainRetrofitService(UserService::class.java)
                .loginAccount()
            withContext(Dispatchers.Main) {
                liveData.postValue(response)
            }
        }
    }

    fun sign() {
        if (!HkUserManager.isLogin) {
            return
        }
        val lastSignMillis = SPUtils.getInstance().getLong(Constant.LAST_SIGN_TIME, 0)
        if (TimeUtils.isToday(lastSignMillis)) {
            Timber.i("今日已签到,签到时间：%s", TimeUtils.millis2String(lastSignMillis))
            return
        }
        SPUtils.getInstance().put(Constant.LAST_SIGN_TIME, System.currentTimeMillis())
        viewModelScope.launch (errorHandler){
            val response = mRepositoryManager.obtainRetrofitService(UserService::class.java)
                .sign()
            if (!response.isSuccess) {
                return@launch
            }
            withContext(Dispatchers.Main) {
                signLiveData.postValue(response.data)
            }
        }
    }

}