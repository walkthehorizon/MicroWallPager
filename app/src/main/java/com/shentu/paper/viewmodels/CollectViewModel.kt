package com.shentu.paper.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.micro.integration.RepositoryManager
import com.shentu.paper.app.errorHandler
import com.shentu.paper.model.api.service.CollectService
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.body.DelCollectBody
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.model.response.WallpaperPageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectViewModel @Inject constructor(private val rm: RepositoryManager) : ViewModel() {
    val liveData: MutableLiveData<WallpaperPageResponse> = MutableLiveData()
    val delLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var offset = MicroService.PAGE_START

    fun loadMyCollects(clear: Boolean) {
        viewModelScope.launch(errorHandler) {
            offset = if (clear) MicroService.PAGE_START else offset + MicroService.PAGE_LIMIT
            val response = rm.obtainRetrofitService(CollectService::class.java)
                .getMyCollects(offset)
            liveData.postValue(response)
        }
    }


    fun delCollectPaper(body: DelCollectBody) {
        viewModelScope.launch(errorHandler) {
            val response = rm.obtainRetrofitService(CollectService::class.java)
                .delCollects(body)
            if (!response.isSuccess) {
                return@launch
            }
            delLiveData.postValue(true)
        }
    }
}