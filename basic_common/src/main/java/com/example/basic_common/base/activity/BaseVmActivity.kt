package com.example.basic_common.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.ModalDialog.setBackgroundColor
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.example.basic_common.R
import com.example.basic_common.base.BaseIView
import com.example.basic_common.base.BaseViewModel
import com.example.basic_common.ext.getColorExt
import com.example.basic_common.ext.getVmClazz
import com.example.basic_common.ext.toast
import com.example.basic_common.state.EmptyCallback
import com.example.basic_common.state.ErrorCallback
import com.example.basic_common.state.LoadingCallback
import com.example.basic_net.entity.base.LoadStatusEntity
import com.example.basic_net.entity.base.LoadingDialogEntity
import com.example.basic_net.entity.loadingtype.LoadingType
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir

/**
 * 作者　: wuquan
 * 时间　: 2020/11/18
 * 描述　:
 */
abstract class BaseVmActivity<VM : BaseViewModel> : BaseActivity() {

    //当前Activity绑定的 ViewModel
    lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setNavBarVisibility(this,false)
        BarUtils.setStatusBarVisibility(this,false)
        setContentView(R.layout.activity_base)
        mViewModel = createViewModel()
        LogUtils.i("activity viewModel:$mViewModel")
        initStatusView(savedInstanceState)
    }

    private fun initStatusView(savedInstanceState: Bundle?) {
        findViewById<FrameLayout>(R.id.baseContentView).apply {
            addView(if (dataBindView == null) LayoutInflater.from(this@BaseVmActivity).inflate(layoutId, null) else dataBindView)
            post {
                initView(savedInstanceState)
            }
        }
    }

    /**
     * 初始化view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }
}