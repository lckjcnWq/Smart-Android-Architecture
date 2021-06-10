package com.example.basic_common.base.fragment

import android.view.View
import androidx.fragment.app.Fragment

/**
 * 作者　: wuquan
 * 时间　: 2020/11/18
 * 描述　:
 */
abstract class BaseFragment : Fragment() {

    abstract val layoutId: Int

    var dataBindView : View? = null
}