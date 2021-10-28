package com.cashloans.cashcloud.uiPakage

import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.cashloans.cashcloud.R
import com.cashloans.cashcloud.appTool.AppsFlyerTool
import com.cashloans.cashcloud.appTool.BaseActivity
import com.cashloans.cashcloud.appTool.httpManger.Constants
import com.cashloans.cashcloud.appTool.loadDialog

class baseWebview : BaseActivity() {

    private lateinit var flContainer: FrameLayout
    private lateinit var mWebView: BridgeWebView

    override fun onDestroy() {
        super.onDestroy()
        flContainer.removeAllViews()
        mWebView.removeAllViews()
        mWebView.destroy()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_base_webview
    }

    override fun initView() {
        flContainer = findViewById(R.id.webview_container)
        mWebView = BridgeWebView(this)
        flContainer.addView(mWebView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val webSettings = mWebView.settings
        webSettings.domStorageEnabled = true // local storage
        webSettings.javaScriptEnabled = true // 启用js
        webSettings.blockNetworkImage = false    // 解决图片不显示
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        mWebView.webChromeClient = WebChromeClient()

        loadDialog.show(this,"loading...")

        mWebView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress>=100) {

                    loadDialog.hideLoadingDialog()
                }
                Log.d("123123",view?.url.toString())
                var url = view?.url.toString()
                if (url.contains("certificationResult=CANCEL")||url.contains("certificationResult=FAIL")) {
                    if (url.contains("certificationResult=CANCEL")) {
                        AppsFlyerTool.event("my_operator_crawlpage_cancel")
                    }else{
                        AppsFlyerTool.event("operator_mycrawl_fail")
                    }
                    finish()

                }else if (url.contains("certificationResult=SUCCESS")) {
                    loadDialog.show(this@baseWebview,"loading...")
                    AppsFlyerTool.event("operator_mycrawl_sucess")
                    finish()


                }else if (url.startsWith("https://www.baidu.com", true)) {
                    finish()

                }else if (url.startsWith("https://www.google.com",true)) {
                    AppsFlyerTool.event("v1c_operator_crawlpage_success")
                    try {
//                        mWebViewPresenter.v1cOperatorfinish(intent.getStringExtra(Constants.NONCE))
                        finish()
                    }catch (e:Exception){
                        finish()
                    }


                }
                super.onProgressChanged(view, newProgress)
            }
        }
        val url = intent.getStringExtra(Constants.EXTRA_URL)
        mWebView.loadUrl(url.toString())
    }

}