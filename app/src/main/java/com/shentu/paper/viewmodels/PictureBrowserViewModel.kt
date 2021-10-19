package com.shentu.paper.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.micro.integration.RepositoryManager
import com.shentu.paper.app.errorHandler
import com.shentu.paper.model.api.service.CollectService
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.api.service.PaperService
import com.shentu.paper.model.api.service.SubjectService
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.model.response.WallpaperPageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class PictureBrowserViewModel
@Inject constructor(private val mRepositoryManager: RepositoryManager) : ViewModel() {

    private val LOAD_COUNT = 5

    private val _liveData: MutableLiveData<WallpaperPageResponse> = MutableLiveData()
    val liveDataRecommend: LiveData<WallpaperPageResponse> = _liveData
    private val _liveDataPaper: MutableLiveData<Wallpaper> = MutableLiveData()
    val liveDataPaper: LiveData<Wallpaper> = _liveDataPaper
    private val _liveDataSubject: MutableLiveData<List<Wallpaper>> = MutableLiveData()
    val liveDataSubject: LiveData<List<Wallpaper>> = _liveDataSubject
    private var offset = MicroService.PAGE_START
    val liveDataShare: MutableLiveData<Wallpaper> = MutableLiveData()
    val liveDataAdd: MutableLiveData<Boolean> = MutableLiveData()
    val liveDataCollects : MutableLiveData<WallpaperPageResponse> = MutableLiveData()

    fun loadRecommendPapers(position: Int, append: Boolean = true) {
        offset = if (append) position + LOAD_COUNT else max(position - LOAD_COUNT, 0)
        viewModelScope.launch(errorHandler) {
            val response = mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getRecommendWallpapers(offset)
            _liveData.postValue(response)
        }
    }

    fun loadCollectPapers(position: Int, append: Boolean = true) {
        offset = if (append) position + LOAD_COUNT else max(position - LOAD_COUNT, 0)
        viewModelScope.launch(errorHandler) {
            val response = mRepositoryManager.obtainRetrofitService(CollectService::class.java)
                .getMyCollects(offset)
            liveDataCollects.postValue(response)
        }
    }

    fun loadPaperDetail(id: Long) {
        viewModelScope.launch {
            val response = mRepositoryManager.obtainRetrofitService(PaperService::class.java)
                .getPaperDetail(id)
            if (!response.isSuccess) {
                throw Throwable(response.msg)
            }
            _liveDataPaper.postValue(response.data)
        }
    }

    fun loadSubjectAllPapers(subjectId: Int) {
        viewModelScope.launch (errorHandler){
            val response = mRepositoryManager.obtainRetrofitService(SubjectService::class.java)
                .getSubjectAllPapers(subjectId)
            if (!response.isSuccess) {
                throw Throwable(response.msg)
            }
            _liveDataSubject.postValue(response.data?.content)
        }
    }

    fun loadPaperShare(id: Long) {
        viewModelScope.launch {
            val response = mRepositoryManager.obtainRetrofitService(PaperService::class.java)
                .getPaperDetail(id)
            if (!response.isSuccess) {
                throw Throwable(response.msg)
            }
            liveDataShare.postValue(response.data)
        }
    }

    fun modifyPaperCollect(id: Long) {
        viewModelScope.launch {
            val response = mRepositoryManager.obtainRetrofitService(CollectService::class.java)
                .modifyPaperCollect(id)
            if (!response.isSuccess) {
                throw Throwable(response.msg)
            }
            liveDataAdd.postValue(response.data)
        }
    }
}