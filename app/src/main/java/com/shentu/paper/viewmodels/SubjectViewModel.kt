package com.shentu.paper.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.micro.integration.RepositoryManager
import com.shentu.paper.app.errorHandler
import com.shentu.paper.model.api.service.SubjectService
import com.shentu.paper.model.entity.Wallpaper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubjectViewModel
@Inject constructor(private val mRepositoryManager: RepositoryManager) : ViewModel() {
    private val flow: MutableSharedFlow<List<Wallpaper>> = MutableSharedFlow()

    private fun loadData(subjectId: Int) {
        viewModelScope.launch(errorHandler) {
            val response = mRepositoryManager.obtainRetrofitService(SubjectService::class.java)
                .getSubjectAllPapers(subjectId)
            if (!response.isSuccess) {
                return@launch
            }
            flow.tryEmit(response.data!!.content)
        }
    }

}