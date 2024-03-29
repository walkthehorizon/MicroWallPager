package com.shentu.paper.mvp.ui.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.micro.integration.lifecycle.FragmentLifecycleable
import com.micro.mvp.IView
import com.shentu.paper.R
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

open class BaseBottomSheetDialog : BottomSheetDialogFragment(), IView, FragmentLifecycleable {
    private val mLifecycleSubject = BehaviorSubject.create<FragmentEvent>()

    override fun provideLifecycleSubject(): Subject<FragmentEvent> {
        return mLifecycleSubject
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.findViewById<View>(R.id.design_bottom_sheet)?.background = ColorDrawable(Color.TRANSPARENT)
    }
}