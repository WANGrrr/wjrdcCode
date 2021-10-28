package com.cashloans.cashcloud.appTool

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.cashloans.cashcloud.appTool.httpManger.IBaseView
import com.cashloans.thloans.appTool.App

/**
 * Author: Unknown
 * Date: 2019/05/21
 * Desc:
 */
abstract class BaseActivity : AppCompatActivity(), IBaseView {

    private lateinit var alertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        alertDialog= AlertDialog.Builder(this)
                .setMessage("请打开定位服务!")
                .setTitle("提醒")
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialogInterface, i ->
                    val activity= App.getInstance().topActivity()

                    activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                })
                .setCancelable(false)
                .create()
              initView()

    }

    override fun onResume() {
        super.onResume()

        if(!ThLoanLocationUtils.INSTANCE.isLocServiceEnable()){
            alertDialog.show()
        }
        else{
            alertDialog.hide()
        }
    }




    protected abstract fun getLayoutId(): Int

    protected abstract fun initView()

    override fun getContext(): Context = this
}
