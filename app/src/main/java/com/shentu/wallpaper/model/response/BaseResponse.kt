
package com.shentu.wallpaper.model.response


import java.io.Serializable

/**
 * ================================================
 * 如果你服务器返回的数据格式固定为这种方式(这里只提供思想,服务器返回的数据格式可能不一致,可根据自家服务器返回的格式作更改)
 * 替换范型即可重用 [BaseResponse]
 *
 *
 * Created by JessYan on 26/09/2016 15:19
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
open class BaseResponse<T> : Serializable {
    val data: T? = null
    val code: Int = -1
    val msg: String = ""

    /**
     * 请求是否成功
     *
     * @return
     */
    val isSuccess: Boolean get() = code == 0
}
