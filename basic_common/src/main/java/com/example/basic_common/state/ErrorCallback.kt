package com.example.basic_common.state

import com.kingja.loadsir.callback.Callback
import com.example.basic_common.R

/**
 * 作者　: wuquan
 * 时间　: 2020/12/14
 * 描述　:
 */
class ErrorCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_error
    }

}