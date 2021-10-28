package com.cashloans.cashcloud.appTool

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import cn.bingoogolapple.progressbar.BGAProgressBar
import com.cashloans.cashcloud.R
import com.cashloans.thloans.appTool.App
import pl.droidsonroids.gif.GifDrawable
import java.lang.ref.WeakReference


/**
 * Author: Unknown
 * Date: 2019/05/22
 * Desc:
 */
class loadDialog {
    companion object {
        private var mDialogReference: WeakReference<Dialog>? = null
        private lateinit var img :ImageView
        private lateinit var progressbar:BGAProgressBar
        fun show(context: Context) {
            hideLoadingDialog()
            val dialog = Dialog(context, R.style.loading_dialog)
            mDialogReference = WeakReference(dialog)

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_loading)
            dialog.findViewById<View>(R.id.tv_loading_dialog).visibility = View.GONE

            dialog.show()
        }

        fun show(context: Context, content: String) {
            hideLoadingDialog()

            val dialog = Dialog(context, R.style.loading_dialog)
            mDialogReference = WeakReference(dialog)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_loading)
            img = dialog.findViewById<View>(R.id.img) as ImageView
            var gifDrawable = GifDrawable(App.getInstance().resources,R.mipmap.interface_loading)
            img.setImageDrawable(gifDrawable)

            dialog.show()
        }

        fun showLoadurl(context: Context, content: String) {
            hideLoadingDialog()
            val dialog = Dialog(context, R.style.loading_dialog)
            mDialogReference = WeakReference(dialog)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.loading_dialog)
            progressbar = dialog.findViewById<View>(R.id.progressbar) as BGAProgressBar
            progressbar.progress = 0
            img = dialog.findViewById<View>(R.id.img) as ImageView
            var gifDrawable = GifDrawable(App.getInstance().resources,R.mipmap.loading)
            img.setImageDrawable(gifDrawable)

            var bg = dialog.findViewById<View>(R.id.bg) as RelativeLayout
            dialog.show()
        }

        fun changeText(content: Int) {

            if (mDialogReference !=null) {
                val dialog = mDialogReference?.get()
                if (dialog != null) {
                    try {
                        (dialog?.findViewById<View>(R.id.progressbar) as BGAProgressBar).progress = content
                    }catch (e:Exception){

                    }
                }
            }
        }

        fun hideLoadingDialog() {
            val dialog = mDialogReference?.get()
            if (dialog != null) {
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }
            mDialogReference?.clear()
        }
    }
}
//"TypeError: Cannot read property 'data' of undefined", source: file:///android_asset/web/static/js/vendor.63f6b99e4d07ccdfb178.js