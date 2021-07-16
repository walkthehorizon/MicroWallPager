package com.shentu.paper.di.module

import android.content.Context
import com.shentu.paper.mvp.contract.SubjectDetailContract
import com.shentu.paper.mvp.model.SubjectDetailModel
import com.shentu.paper.mvp.ui.activity.SubjectDetailActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@InstallIn(ActivityComponent::class)
@Module
//构建SubjectDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class SubjectDetailModule() {
    @ActivityScoped
    @Provides
    fun provideSubjectDetailView(@ActivityContext context: Context): SubjectDetailContract.View {
        return context as SubjectDetailActivity
    }

    @ActivityScoped
    @Provides
    fun provideSubjectDetailModel(model: SubjectDetailModel): SubjectDetailContract.Model {
        return model
    }
}
