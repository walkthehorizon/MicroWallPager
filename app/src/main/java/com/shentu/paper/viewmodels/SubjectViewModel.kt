package com.shentu.paper.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.micro.integration.RepositoryManager
import com.shentu.paper.app.errorHandler
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.api.service.SubjectService
import com.shentu.paper.model.entity.Wallpaper
import com.shentu.paper.model.response.SubjectDetailResponse
import com.shentu.paper.model.response.WallpaperPageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubjectViewModel
@Inject constructor(private val rm: RepositoryManager) : ViewModel() {
    val papersLiveData = MutableLiveData<WallpaperPageResponse>()
    val subjectDetailLiveData = MutableLiveData<SubjectDetailResponse>()

    private fun loadSubjectPapers(subjectId: Int) {
        viewModelScope.launch(errorHandler) {
            val response = rm.obtainRetrofitService(SubjectService::class.java)
                .getSubjectAllPapers(subjectId)
            papersLiveData.postValue(response)
        }
    }

    fun loaBannerData(bannerId: Int) {
        viewModelScope.launch(errorHandler) {
            val response = rm.obtainRetrofitService(MicroService::class.java)
                .getBannerWallpapers(bannerId)
            papersLiveData.postValue(response)
        }
    }

    private fun loadSubjectDetail(subjectId: Int){
        viewModelScope.launch(errorHandler) {
            val response = rm.obtainRetrofitService(SubjectService::class.java)
                .getSubjectDetail(subjectId)
            subjectDetailLiveData.postValue(response)
        }
    }

    fun loadSubjectData(subjectId: Int){
        loadSubjectDetail(subjectId)
        loadSubjectPapers(subjectId)
    }
}