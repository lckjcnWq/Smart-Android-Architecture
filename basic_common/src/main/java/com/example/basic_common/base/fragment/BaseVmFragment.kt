package com.example.basic_common.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.example.basic_common.base.BaseIView
import com.example.basic_common.base.BaseViewModel
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.example.basic_common.ext.*
import com.example.basic_common.state.EmptyCallback
import com.example.basic_common.state.ErrorCallback
import com.example.basic_common.state.LoadingCallback
import com.example.basic_net.entity.base.LoadStatusEntity
import com.example.basic_net.entity.base.LoadingDialogEntity
import com.example.basic_net.entity.loadingtype.LoadingType

/**
 * 作者　: wuquan
 * 时间　: 2020/11/18
 * 描述　:
 */
abstract class BaseVmFragment<VM : BaseViewModel> : BaseFragment(){

    //是否第一次加载
    private var isFirst: Boolean = true

    //当前Fragment绑定的泛型类ViewModel
    lateinit var mViewModel: VM

    //父类activity
    lateinit var mActivity: AppCompatActivity


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        BarUtils.setNavBarVisibility(this.mActivity,false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isFirst = true
        javaClass.simpleName.logD()
        return if (dataBindView == null) {
            inflater.inflate(layoutId, container, false)
        } else {
            dataBindView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = createViewModel()
        BarUtils.setNavBarVisibility(mActivity,false)
        BarUtils.setStatusBarVisibility(mActivity,false)
        view.post {
            initView(savedInstanceState)
        }
    }

    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(mActivity).get(getVmClazz(this))
    }

    /**
     * 初始化view操作
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 懒加载
     */
    open fun lazyLoadData() {}


    override fun onResume() {
        super.onResume()
        onVisible()
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            view?.post {
                lazyLoadData()
                isFirst = false
            }
        }
    }
}