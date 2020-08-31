package com.shentu.paper.di.module

import com.jess.arms.di.scope.ActivityScope
import com.shentu.paper.mvp.contract.SubjectDetailContract
import com.shentu.paper.mvp.model.SubjectDetailModel
import dagger.Module
import dagger.Provides


@Module
//构建SubjectDetailModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
class SubjectDetailModule(private val view: SubjectDetailContract.View) {
    @ActivityScope
    @Provides
    fun provideSubjectDetailView(): SubjectDetailContract.View {
        return this.view
    }

    @ActivityScope
    @Provides
    fun provideSubjectDetailModel(model: SubjectDetailModel): SubjectDetailContract.Model {
        return model
    }
}
