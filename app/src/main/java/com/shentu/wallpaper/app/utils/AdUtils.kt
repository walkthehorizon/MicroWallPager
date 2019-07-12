package com.shentu.wallpaper.app.utils

import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.shentu.wallpaper.app.HkApplication
import timber.log.Timber

class AdUtils {

    private var rewardedAd: RewardedAd = RewardedAd(HkApplication.getInstance(), "ca-app-pub-9991643602960691/8499785513")

    init {
        createAndLoadRewardedAd()
    }

    companion object {
        val instance = AdUtils()
    }

    fun createAndLoadRewardedAd() {
        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                Timber.d("激励ad已加载完成")
            }

            override fun onRewardedAdFailedToLoad(p0: Int) {
                when (p0) {
                    AdRequest.ERROR_CODE_INTERNAL_ERROR -> Timber.e("ad内部异常")
                    AdRequest.ERROR_CODE_INVALID_REQUEST -> Timber.e("ad请求无效")
                    AdRequest.ERROR_CODE_NETWORK_ERROR -> Timber.e("ad网络异常")
                    AdRequest.ERROR_CODE_NO_FILL -> Timber.e("ad请求成功，但由于缺少广告资源，未返回广告")
                }
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }

    fun showUrgeAd(activity: AppCompatActivity, adCallback: RewardedAdCallback) {
        if (rewardedAd.isLoaded) {
            rewardedAd.show(activity, adCallback)
        } else {
            ToastUtils.showShort("广告加载未完成")
        }
    }
}
