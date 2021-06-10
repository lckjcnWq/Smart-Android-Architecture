package com.example.basic_net.entity.base

import com.example.basic_net.entity.loadingtype.LoadingType

/**
 * 作者　: wuquan
 * 时间　: 2020/11/3
 * 描述　:
 */
data class LoadingDialogEntity(
    @LoadingType var loadingType: Int = LoadingType.LOADING_NULL,
    var loadingMessage: String = "",
    var isShow: Boolean = false,
    var requestCode: String = "mmp"
)