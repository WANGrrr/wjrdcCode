package com.cashloans.cashcloud.uiPakage

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageView
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.hjq.toast.ToastUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import com.cashloans.cashcloud.R
import com.cashloans.cashcloud.appTool.BaseActivity
import com.cashloans.cashcloud.appTool.httpManger.PhotoUtils
import com.cashloans.cashcloud.appTool.loadDialog
import io.reactivex.disposables.CompositeDisposable


class mainKefuvc : BaseActivity() {
    private lateinit var webView: BridgeWebView
    private var mSelectPhotoDialog: PhotoSeles? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private lateinit var img: ImageView
    private val mRxPermissions by lazy {
        RxPermissions(this)
    }
    private val mDisposables by lazy {
        CompositeDisposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_kefu_main
    }

    override fun initView() {
        img = findViewById(R.id.iv_bankcard_auth_back)
        webView = findViewById(R.id.webview)
        val webSettings = webView.settings
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(false)
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.allowContentAccess = true;
        webSettings.allowFileAccess = true;
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false)
        webSettings.domStorageEnabled = true // local storage
        webSettings.blockNetworkImage = false    // 解决图片不显示
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        loadDialog.show(this,"0")
        img.setOnClickListener {
            finish()
        }
        webView.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(p0: WebView?, p1: ValueCallback<Array<Uri>>?, p2: FileChooserParams?): Boolean {
                mFilePathCallback = p1
                val acceptTypes = p2?.acceptTypes
                Log.d("webViewChromeClient", acceptTypes.toString())
                showSelectDialog()
                return true
            }
            override fun onProgressChanged(p0: WebView?, p1: Int) {
                if (p1>=100) {

                    loadDialog.hideLoadingDialog()
                }
                super.onProgressChanged(p0, p1)
            }

        }


        val bundle = intent.extras
        var urlStr = bundle?.getString("url")

        webView.loadUrl(urlStr.toString())
    }
    /**
     * 显示相册/拍照选择对话框
     */
    private fun showSelectDialog() {
        if (mSelectPhotoDialog == null) {
            mSelectPhotoDialog = PhotoSeles(this, View.OnClickListener { view ->
                when (view.id) {

                    R.id.tv_camera -> startCamera()
                    R.id.tv_photo -> startAlbum()
                    //不管选择还是不选择，必须有返回结果，否则就会调用一次
                    R.id.tv_cancel -> {
                        mFilePathCallback?.onReceiveValue(null)
                        mFilePathCallback = null
                    }
                }
            })
        }
        mSelectPhotoDialog?.show()
    }

    /**
     * 打开相册
     */
    private fun startAlbum() {
        val disposable = mRxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {
                        PhotoUtils.startAlbum(this)
                    } else {
                        mFilePathCallback?.onReceiveValue(null)
                        ToastUtils.show(R.string.permission_camera_storage_denied)
                    }
                }
        mDisposables.add(disposable)

    }

    /**
     * 拍照
     */
    fun startCamera() {
        val disposable = mRxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {
                        PhotoUtils.startCamera(this, PhotoUtils.RESULT_CODE_PERSHON)
                    } else {
                        mFilePathCallback?.onReceiveValue(null)
                        ToastUtils.show(R.string.permission_camera_storage_denied)
                    }
                }
    }

}