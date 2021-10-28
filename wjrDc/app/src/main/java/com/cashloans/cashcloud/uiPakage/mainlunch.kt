package com.cashloans.cashcloud.uiPakage

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.cashloans.cashcloud.MainActivity
import com.cashloans.cashcloud.R
import com.cashloans.cashcloud.appTool.BaseActivity
import com.cashloans.cashcloud.appTool.httpManger.DataManager

class mainlunch : BaseActivity(), View.OnClickListener {
    private lateinit var sureBtns: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun getLayoutId(): Int {
        return R.layout.activity_mainlunch
    }

    override fun initView() {
        fullScreen(this)
        sureBtns = findViewById(R.id.launcher_button)

        sureBtns.setOnClickListener(this)
    }
    override fun onClick(v: View) {

        when (v.id) {
            R.id.launcher_button -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                DataManager.INSTANCE.saveDataToSP("isFirst","false")

            }
        }
    }
    /**
    * 通过设置全屏，设置状态栏透明
    *
    * @param activity
    */
    private fun fullScreen(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                val window = activity.window
                val decorView = window.decorView
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = option
                window.statusBarColor = Color.TRANSPARENT
            } else {
                val window = activity.window
                val attributes = window.attributes
                val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                val flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                attributes.flags = attributes.flags or flagTranslucentStatus
                //                attributes.flags |= flagTranslucentNavigation;
                window.attributes = attributes
            }
        }
    }

}