package com.zibuyuqing.roundcorner

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.zibuyuqing.roundcorner.service.KeepCornerLiveService
import com.zibuyuqing.roundcorner.utils.SettingsDataKeeper
import android.content.Context.ACTIVITY_SERVICE
import android.app.ActivityManager
import android.content.Context


/**
 * Created by Xijun.Wang on 2017/11/2.
 */

class MyApp : Application() {

    private var mFinalCount: Int = 0

    override fun onCreate() {
        super.onCreate()
        instance = this
        activityLifecycleListener()
    }

    companion object {
        private var instance: MyApp? = null
    }


    //全局判断Activity 状态 用来判断开启安全性
    private fun activityLifecycleListener() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityStopped(p0: Activity?) {
                mFinalCount--
                //如果mFinalCount ==0，说明是前台到后台
                if (mFinalCount == 0) {
                    //说明从前台回到了后台
                    //Toast.makeText(this@MyApp, "后台", Toast.LENGTH_SHORT).show()
                    //stopService(Intent(applicationContext, KeepCornerLiveService::class.java))
                    if (isServiceWork(this@MyApp, "com.zibuyuqing.roundcorner.service.KeepCornerLiveService")) {
                        //Toast.makeText(this@MyApp, "服务已开启", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@MyApp, SettingsDataKeeper.getSettingsBoolean(this@MyApp, SettingsDataKeeper.CORNER_ENABLE).toString(), Toast.LENGTH_SHORT).show()

                        if (SettingsDataKeeper.getSettingsBoolean(this@MyApp, SettingsDataKeeper.CORNER_ENABLE)) {
                            updateCornersWithBool(SettingsDataKeeper.CORNER_ENABLE, false)
                        }
                    }

                }
            }

            override fun onActivityCreated(p0: Activity?, p1: Bundle?) {}

            override fun onActivityPaused(p0: Activity?) {}

            override fun onActivityResumed(p0: Activity?) {}

            override fun onActivityStarted(p0: Activity?) {
                mFinalCount++
                //如果mFinalCount ==1，说明是从后台到前台
                if (mFinalCount == 1) {
                    //说明从后台回到了前台
                    //startService(Intent(applicationContext, KeepCornerLiveService::class.java))
                    if (isServiceWork(this@MyApp, "com.zibuyuqing.roundcorner.service.KeepCornerLiveService")) {
                        //Toast.makeText(this@MyApp, "服务已开启", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@MyApp, SettingsDataKeeper.getSettingsBoolean(this@MyApp, SettingsDataKeeper.CORNER_ENABLE).toString(), Toast.LENGTH_SHORT).show()
                        if (SettingsDataKeeper.getSettingsBoolean(this@MyApp, SettingsDataKeeper.CORNER_ENABLE)) {
                            updateCornersWithBool(SettingsDataKeeper.CORNER_ENABLE, true)
                        }
                    }
                }
            }

            override fun onActivityDestroyed(p0: Activity?) {}

            override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {}
        })
    }

    private fun updateCornersWithBool(key: String, value: Boolean) {
        val intent = Intent(this, KeepCornerLiveService::class.java)
        intent.putExtra(key, value)
        intent.action = key
        startService(intent)
    }


    private fun isServiceWork(mContext: Context, serviceName: String): Boolean {
        var isWork = false
        val myAM = mContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val myList = myAM.getRunningServices(40)
        if (myList.size <= 0) {
            return false
        }
        for (i in myList.indices) {
            val mName = myList[i].service.className.toString()
            if (mName == serviceName) {
                isWork = true
                break
            }
        }
        return isWork
    }
}
