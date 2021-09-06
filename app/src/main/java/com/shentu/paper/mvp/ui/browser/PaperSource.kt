package com.shentu.paper.mvp.ui.browser

import java.io.Serializable

sealed class PaperSource : Serializable

data class Recommend(val current: Int) : PaperSource()

data class Subject(val subjectId: Int) : PaperSource()

object Unknown : PaperSource()