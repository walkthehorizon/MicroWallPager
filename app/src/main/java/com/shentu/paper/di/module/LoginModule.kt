//package com.shentu.paper.di.module
//
//import android.content.Context
//import com.shentu.paper.mvp.contract.LoginContract
//import com.shentu.paper.mvp.model.LoginModel
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ActivityComponent
//import dagger.hilt.android.qualifiers.ActivityContext
//import dagger.hilt.android.scopes.ActivityScoped
//
//@InstallIn(ActivityComponent::class)
//@Module
////构建LoginModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
//class LoginModule {
//    @ActivityScoped
//    @Provides
//    fun provideLoginView(@ActivityContext context: Context): LoginContract.View {
//        return context as LoginContract.View
//    }
//
//    @ActivityScoped
//    @Provides
//    fun provideLoginModel(model: LoginModel): LoginContract.Model {
//        return model
//    }
//}
