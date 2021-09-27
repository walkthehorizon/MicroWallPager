package com.shentu.paper.viewmodels

import androidx.lifecycle.ViewModel
import com.micro.integration.RepositoryManager
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.entity.Wallpaper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel
@Inject constructor(private val mRepositoryManager: RepositoryManager):ViewModel(){


    val flow: MutableSharedFlow<List<Wallpaper>> = MutableSharedFlow()

    fun loadData(){

    }

}