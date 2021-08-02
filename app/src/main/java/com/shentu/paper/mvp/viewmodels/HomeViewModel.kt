package com.shentu.paper.mvp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.micro.integration.RepositoryManager
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.api.source.HomePagingSource
import com.shentu.paper.model.entity.Banner
import com.shentu.paper.model.entity.Wallpaper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(private val mRepositoryManager: RepositoryManager) : ViewModel() {

    val bannerLiveData:MutableLiveData<MutableList<Banner>> = MutableLiveData()

    fun getRecommends(clear: Boolean):Flow<PagingData<Wallpaper>> {
        return Pager(
            config = PagingConfig(25,enablePlaceholders = false),
            pagingSourceFactory = {HomePagingSource(
                mRepositoryManager.obtainRetrofitService(MicroService::class.java)
            )}
        ).flow.cachedIn(viewModelScope)
    }

    fun getBanners() {
        viewModelScope.launch {
            val response = mRepositoryManager.obtainRetrofitService(MicroService::class.java)
                .getBanners()
            if(!response.isSuccess){
                return@launch
            }
            bannerLiveData.postValue(response.data?.content)
        }
    }

    class Factory(private val mRepositoryManager: RepositoryManager) : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(RepositoryManager::class.java).newInstance(mRepositoryManager)
        }
    }

}