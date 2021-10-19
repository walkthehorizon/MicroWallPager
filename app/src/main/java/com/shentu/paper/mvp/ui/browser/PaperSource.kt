package com.shentu.paper.mvp.ui.browser

import androidx.core.app.ActivityOptionsCompat
import com.shentu.paper.model.entity.Wallpaper
import java.io.Serializable

sealed class PaperSource : Serializable

data class SourceRecommend(val papers: List<Wallpaper>, val curPosition: Int = 0) : PaperSource()

data class SourceSubject(val curPosition: Int = 0, val subjectId: Int) : PaperSource()

data class SourceCategory(val curPosition: Int = 0, val categoryId: Int) : PaperSource()

data class SourcePaper(val paperId: Long) : PaperSource()

data class SourceCollect(val papers: List<Wallpaper>, val curPosition: Int = 0) : PaperSource()

object Unknown : PaperSource()