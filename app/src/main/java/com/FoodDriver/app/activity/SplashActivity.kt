package com.FoodDriver.app.activity

import android.os.Handler
import android.os.Looper
import com.FoodDriver.app.R
import com.FoodDriver.app.base.BaseActivity
import com.FoodDriver.app.utils.SharePreference
import com.FoodDriver.app.utils.SharePreference.Companion.getBooleanPref


class SplashActivity : BaseActivity() {

    override fun setLayout(): Int {
       return R.layout.activity_splash
    }
    override fun InitView() {
      Handler(Looper.getMainLooper()).postDelayed({
          if(getBooleanPref(this@SplashActivity,SharePreference.isLogin)){
              openActivity(DashboardActivity::class.java)
              finish()
          }else{
              openActivity(LoginActivity::class.java)
              finish()
          }
        },3000)
    }
}