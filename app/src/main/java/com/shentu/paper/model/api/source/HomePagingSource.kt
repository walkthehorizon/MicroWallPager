package com.shentu.paper.model.api.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shentu.paper.model.api.service.MicroService
import com.shentu.paper.model.entity.Wallpaper

class HomePagingSource(private val service: MicroService) : PagingSource<Int, Wallpaper>() {
    override fun getRefreshKey(state: PagingState<Int, Wallpaper>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Wallpaper> {
        return try {
            val page = params.key ?: 1
            val response = service.getRecommendWallpapers(page * MicroService.PAGE_LIMIT)
            val papers = response.data!!.content
            LoadResult.Page(
                data = papers,
                prevKey = null,
                nextKey = if (page == response.data.count) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}