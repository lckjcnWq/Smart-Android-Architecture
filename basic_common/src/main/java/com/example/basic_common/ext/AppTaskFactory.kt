package com.example.basic_common.ext

import com.effective.android.anchors.Project
import com.effective.android.anchors.Task
import com.effective.android.anchors.TaskCreator
import com.example.basic_common.BuildConfig
import com.example.basic_common.listener.KtxActivityLifecycleCallbacks
import com.example.basic_common.net.NetHttpClient
import com.example.basic_common.state.EmptyCallback
import com.example.basic_common.state.ErrorCallback
import com.example.basic_common.state.LoadingCallback
import com.example.basic_common.util.XLog
import com.example.basic_net.interception.LogInterceptor
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.tencent.mmkv.MMKV
import rxhttp.wrapper.param.RxHttp

/**
 * 作者　: wuquan
 * 时间　: 2020/11/17
 * 描述　:
 */
object TaskCreator : TaskCreator {
    override fun createTask(taskName: String): Task {
        return when (taskName) {
            InitNetWork.TASK_ID -> InitNetWork()
            InitComm.TASK_ID -> InitComm()
            InitUtils.TASK_ID -> InitUtils()
            InitDataBase.TASK_ID -> InitDataBase()
            InitAppLifecycle.TASK_ID -> InitAppLifecycle()
            InitAppCrash.TASK_ID -> InitAppCrash()
            else -> InitDefault()
        }
    }
}

class InitDefault : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "0"
    }

    override fun run(name: String) {

    }
}

//初始化网络
class InitNetWork : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "1"
    }
    override fun run(name: String) {
        //传入自己的OKHttpClient 并添加了自己的拦截器
        RxHttp.init(NetHttpClient.getDefaultOkHttpClient().run {
            addInterceptor(LogInterceptor())//添加Log拦截器
        }.build())
    }
}

//初始化常用控件类
class InitComm : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "2"
    }

    override fun run(name: String) {
        //注册界面状态管理
        LoadSir.beginBuilder()
            .addCallback(ErrorCallback())
            .addCallback(EmptyCallback())
            .addCallback(LoadingCallback())
            .setDefaultCallback(SuccessCallback::class.java)
            .commit()
    }
}

//初始化Utils
class InitUtils : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "3"
    }

    override fun run(name: String) {
        //初始化Log打印
        XLog.init(BuildConfig.DEBUG)
        //初始化MMKV
        MMKV.initialize(appContext.filesDir.absolutePath + "/mmkv")
    }
}

//初始化數據庫
class InitDataBase : Task(TASK_ID, false) {
    companion object {
        const val TASK_ID = "4"
    }

    override fun run(name: String) {
//        DataBaseCacheUtils.init()
    }
}

//感知生命周期
class InitAppLifecycle : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "5"
    }
    override fun run(name: String) {
        //注册全局的Activity生命周期管理
        appContext.registerActivityLifecycleCallbacks(KtxActivityLifecycleCallbacks())
    }
}
//防止项目奔溃  奔溃后重新进入页面
class InitAppCrash : Task(TASK_ID, true) {
    companion object {
        const val TASK_ID = "6"
    }
    override fun run(name: String) {
//            CaocConfig.Builder.create()
//                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
//                .enabled(true)//是否启用CustomActivityOnCrash崩溃拦截机制 必须启用！
//                .showErrorDetails(false) //是否必须显示包含错误详细信息的按钮 default: true
//                .showRestartButton(false) //是否必须显示“重新启动应用程序”按钮或“关闭应用程序”按钮default: true
//                .logErrorOnRestart(false) //是否必须重新堆栈堆栈跟踪 default: true
//                .trackActivities(true) //是否必须跟踪用户访问的活动及其生命周期调用 default: false
//                .minTimeBetweenCrashesMs(2000) //应用程序崩溃之间必须经过的时间 default: 3000
//                .restartActivity(ReflectUtils.reflect("com.kandao.videocall.ui.activity.SplashActivity").newInstance().get()) // 重启的activity
//                .apply()
        }
    }





class AppTaskFactory : Project.TaskFactory(TaskCreator)
