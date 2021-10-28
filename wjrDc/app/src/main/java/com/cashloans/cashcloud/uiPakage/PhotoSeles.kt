package com.cashloans.cashcloud.uiPakage

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.cashloans.cashcloud.R

/**
 * TODO<请描述这个类是干什么的>
 *
 * @author cjl
 * @data: 2015年7月11日 下午4:49:25
 * @version: V1.0
</请描述这个类是干什么的> */
class PhotoSeles(context: Context, private val mListener: View.OnClickListener?) : Dialog(context, R.style.popup_dialog_anim) {

    private lateinit var tvPhoto: TextView
    private lateinit var tvCamera: TextView
    private lateinit var tvCancel: TextView

    init {
        init(context)
    }

    private fun init(context: Context) {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        val view = View.inflate(context, R.layout.dialog_select_photos, null)
        setContentView(view)
        init(view)
    }

    private fun init(view: View) {
        tvPhoto = view.findViewById(R.id.tv_photo) as TextView
        tvCancel = view.findViewById(R.id.tv_cancel) as TextView
        tvCamera = view.findViewById(R.id.tv_camera) as TextView
        tvPhoto.setOnClickListener { view ->
            dismiss()
            mListener?.onClick(view)
        }
        tvCancel.setOnClickListener { view ->
            dismiss()
            mListener?.onClick(view)
        }
        tvCamera.setOnClickListener { view ->
            dismiss()
            mListener?.onClick(view)
        }
        val dm = context.resources.displayMetrics
        val screenWidth = dm.widthPixels
        val window = window
        val params = window?.attributes
        params?.gravity = Gravity.BOTTOM
        params?.alpha = 1.0f
        params?.width = screenWidth
        window?.attributes = params
    }
}
