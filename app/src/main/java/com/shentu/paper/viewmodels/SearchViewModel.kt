package com.shentu.paper.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.micro.integration.RepositoryManager
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.response.SubjectPageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: RepositoryManager) : ViewModel() {
    private val flow: MutableStateFlow<String> = MutableStateFlow("")
    val liveData = MutableLiveData<SubjectPageResponse>()
    val historyLiveData = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            flow.sample(500)
                .collectLatest {
                    withContext(Dispatchers.Main){
                        historyLiveData.postValue(it)
                    }
                    if(it.isNotEmpty()){
                        val result = repository.obtainRetrofitService(MicroService::class.java)
                            .searchSubject(it, 0, 100)
                        withContext(Dispatchers.Main) {
                            liveData.postValue(result)
                        }
                    }
                }
        }
    }

    fun search(key: String) {
        flow.tryEmit(key)
    }

}