package com.example.basic_common.base.activity

import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * 作者　: wuquan
 * 时间　: 2020/11/18
 * 描述　:
 */
abstract class BaseActivity : AppCompatActivity() {

    abstract val layoutId: Int

    var dataBindView :View? = null

}