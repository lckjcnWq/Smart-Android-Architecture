package com.example.basic_common.core.viewmodel

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.example.basic_common.base.BaseViewModel

/**
 * 作者　: wuquan
 * 时间　: 2020/11/18
 * 描述　:专门发送全局的消息
 */
class EventViewModel : BaseViewModel() {

    //登录成功后的通知
    val loginEvent = UnPeekLiveData<Boolean>()
}