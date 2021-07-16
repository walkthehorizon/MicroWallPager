package com.shentu.paper.mvp.ui.activity

import android.animation.ObjectAnimator
import android.graphics.Camera
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import com.jess.arms.base.BaseActivity
import com.jess.arms.mvp.IPresenter
import com.shentu.paper.R
import kotlinx.android.synthetic.main.activity_test.*

/**
 *    date   : 2021/7/6 15:47
 *    author : mingming.li
 *    e-mail : mingming.li@ximalaya.com
 */
class TestActivity : BaseActivity<IPresenter>() {

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_test
    }

    override fun initData(savedInstanceState: Bundle?) {
//        mbPlay.setOnClickListener {
//            val animation = Rotate3dAnimation(0f, 180f, ivTest.width/2.0f, ivTest.height/2.0f, 0f, true)
//            animation.duration = 2000
//            animation.fillAfter = true
//            animation.interpolator = LinearInterpolator()
//            ivTest.startAnimation(animation)
//        }
        mbPlay.setOnClickListener {
            val animator = ObjectAnimator.ofFloat(ivTest,View.ROTATION_Y,0f,180f)
                .setDuration(2000)
            animator.interpolator = LinearInterpolator()
            animator.start()
        }
    }
}

class Rotate3dAnimation
/**
 * 创建一个绕y轴旋转的3D动画效果，旋转过程中具有深度调节，可以指定旋转中心。
 *
 * @param fromDegrees    起始时角度
 * @param toDegrees    结束时角度
 * @param centerX        旋转中心x坐标
 * @param centerY        旋转中心y坐标
 * @param depthZ        最远到达的z轴坐标
 * @param reverse        true 表示由从0到depthZ，false相反
 */(
    private val mFromDegrees: Float,
    private val mToDegrees: Float,
    private val mCenterX: Float,
    private val mCenterY: Float,
    private val mDepthZ: Float,
    private val mReverse: Boolean
) :
    Animation() {
    private lateinit var mCamera: Camera

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        mCamera = Camera()
    }

    protected override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val fromDegrees = mFromDegrees
        val degrees = fromDegrees + (mToDegrees - fromDegrees) * interpolatedTime
        val centerX = mCenterX
        val centerY = mCenterY
        val camera: Camera = mCamera
        val matrix: Matrix = t.matrix
        camera.save()

        // 调节深度
        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime)
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime))
        }

        // 绕y轴旋转
        camera.rotateY(degrees)
        camera.getMatrix(matrix)
        camera.restore()

        // 修正失真，主要修改 MPERSP_0 和 MPERSP_1
        val mValues = FloatArray(9)
        matrix.getValues(mValues) //获取数值
        mValues[6] = mValues[6]   //数值修正
        mValues[7] = mValues[7]  //数值修正
        matrix.setValues(mValues) //重新赋值

        // 调节中心点
        matrix.preTranslate(-centerX, -centerY)
        matrix.postTranslate(centerX, centerY)
    }
}
