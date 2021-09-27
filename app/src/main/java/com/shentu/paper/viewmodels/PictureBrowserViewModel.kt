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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class PictureBrowserViewModel
@Inject constructor(private val mRepositoryManager: RepositoryManager) : ViewModel() {

    private val _liveData: MutableLiveData<List<Wallpaper>> = MutableLiveData()
    val liveData: LiveData<List<Wallpaper>> = _liveData
    private val _liveDataPaper: MutableLiveData<Wallpaper> = MutableLiveData()
    val liveDataPaper: LiveData<Wallpaper> = _liveDataPaper
    private val _liveDataSubject: MutableLiveData<List<Wallpaper>> = MutableLiveData()
    val liveDataSubject: LiveData<List<Wallpaper>> = _liveDataSubject
    private var offset = MicroService.PAGE_START
    val liveDataShare: MutableLiveData<Wallpaper> = MutableLiveData()
    val liveDataCollect: MutableLiveData<Boolean> = MutableLiveData()

    fun loadRecommendPapers(position: Int, append: Boolean = true) {
        offset = if (append) position + 5 else max(position - 5, 0)
        viewModelScope.launch(errorHandler) {
            val response = mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getRecommendWallpapers(offset)
            if (!response.isSuccess) {
                throw Throwable(response.msg)
            }
            _liveData.postValue(response.data?.content)
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
        viewModelScope.launch {
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
            liveDataCollect.postValue(response.data)
        }
    }
}