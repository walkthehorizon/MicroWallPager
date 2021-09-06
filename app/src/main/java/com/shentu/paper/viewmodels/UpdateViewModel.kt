package com.shentu.paper.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.micro.integration.RepositoryManager
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.entity.AppUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(private val repositoryManager: RepositoryManager) : ViewModel() {

    val liveData = MutableLiveData<AppUpdate>()

    fun checkUpdate(isUser:Boolean){
        viewModelScope.launch {
            val response = repositoryManager.obtainRetrofitService(MicroService::class.java)
            .updateInfo()
            if (!response.isSuccess) {
                return@launch
            }
            response.data?.isUser = isUser
            liveData.postValue(response.data)
        }
    }

}