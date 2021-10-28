package com.cashloans.cashcloud.uiPakage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.google.gson.Gson
import com.hjq.toast.ToastUtils
import com.qpg.superhttp.SuperHttp
import com.qpg.superhttp.callback.SimpleCallBack
import com.cashloans.cashcloud.MainActivity
import com.cashloans.cashcloud.R
import com.cashloans.cashcloud.appTool.AppsFlyerTool
import com.cashloans.cashcloud.appTool.BaseActivity
import com.cashloans.cashcloud.appTool.httpManger.DataManager
import com.cashloans.cashcloud.appTool.loadDialog
import com.cashloans.cashcloud.models.HttpNames
import com.cashloans.cashcloud.models.ocrIdcard
import com.cashloans.thloans.appTool.BaseBean
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class idCardinfo : BaseActivity()  , View.OnClickListener{
    private lateinit var backBtn: RelativeLayout
    private lateinit var quanming: EditText
    private lateinit var xiaoming: EditText
    private lateinit var idCardNum: EditText
    private lateinit var sexStr: TextView
    private lateinit var tijiao: Button

    private lateinit var birthTF: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_id_cardinfo
    }

    override fun initView() {
        val bundle = intent.extras
        var urlStr = bundle?.getString("realeName")
        var gson = Gson().fromJson(urlStr, ocrIdcard::class.java)
        Log.d("messages",gson.data.birthdayEN)

        backBtn = findViewById(R.id.id_back)
        quanming = findViewById(R.id.id_quanming)
        xiaoming = findViewById(R.id.id_xiaoming)
        idCardNum = findViewById(R.id.id_idCardNum)
        sexStr = findViewById(R.id.id_user_sex)
        birthTF = findViewById(R.id.id_user_birthday)
        tijiao = findViewById(R.id.id_tijiao)


        backBtn.setOnClickListener(this)
        quanming.setOnClickListener(this)
        xiaoming.setOnClickListener(this)
        idCardNum.setOnClickListener(this)
        sexStr.setOnClickListener(this)
        birthTF.setOnClickListener(this)
        tijiao.setOnClickListener(this)

        quanming.setText(gson.data.nameTH)
        idCardNum.setText(gson.data.idNumber)
        birthTF.setText(monthEntoNum(gson.data.birthdayEN))
        birthTF.setTextColor(getColor(R.color.black33))

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.id_back -> {
                val intents = Intent(this, MainActivity::class.java)
                startActivity(intents)
            }
            R.id.id_xiaoming ->{
                AppsFlyerTool.event("realname_nickname_input")
            }
            R.id.id_user_birthday -> {
                var imm: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
               birthdayClick()
            }
            R.id.id_user_sex ->{
                var imm: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                sexChoose()
            }
            R.id.id_tijiao ->{
                sendInfo()
            }
        }
    }
    fun sendInfo(){
        val bundle = intent.extras
        var liveId = bundle?.getString("livenessId")
        var imgUrl = bundle?.getString("idCardUrl")
        if (imgUrl != null) {
            Log.d("aaaaaaaaaa",imgUrl)
        }
        if (liveId != null) {
            Log.d("bbbbbbbbbb",liveId)
        }

        loadDialog.show(this,"0")
        if (imgUrl.isNullOrEmpty()) {
            ToastUtils.show(R.string.idcard_shoot_again)
            loadDialog.hideLoadingDialog()
            return
        }
        if (quanming.text.toString().isEmpty()) {
            loadDialog.hideLoadingDialog()
            ToastUtils.show(resources.getString(R.string.input_full_name))
            return
        }

        if (idCardNum.text.length != 13) {
            //请填写您的证件号码
            loadDialog.hideLoadingDialog()
            ToastUtils.show(resources.getString(R.string.check_id_card))
            return
        }
        if (xiaoming.text.length < 1) {
            loadDialog.hideLoadingDialog()
            ToastUtils.show(resources.getString(R.string.small_name_tip))
            return
        }


        if (birthTF.text.length == 1||birthTF.text == getText(R.string.idcard_birthday)) {
            ToastUtils.show(R.string.birthday_error)
            loadDialog.hideLoadingDialog()
            return
        }
        loadDialog.show(this, "loading")
        var map = mutableMapOf<String, @JvmSuppressWildcards Any>()
        map.put("idName", quanming.text.toString())
        map.put("idNumber", idCardNum.text.toString())
        map.put("birth", birthTF.text.toString())
        if (liveId != null) {
            map.put("livenessId",liveId)
        }
        map.put("idcardFrontPhotoUrl", imgUrl)
        map.put("nick", xiaoming.text.toString())
        SuperHttp.post(HttpNames.upLoadUserMessage)
            .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences("token"))
            .setJson(Gson().toJson(map))
            .request(object : SimpleCallBack<BaseBean>() {
                override fun onSuccess(data: BaseBean) {

                    if (data.code == 0){
                        finish()
                    }else{
                        ToastUtils.show(data.message)
                    }
                }

                override fun onFail(errCode: Int, errMsg: String) {

                }
            })
    }
    fun sexChoose(){
        var sexList = mutableListOf<String>(this.getString(R.string.man), this.getString(R.string.woman))
        var pvOptions = OptionsPickerBuilder(this, OnOptionsSelectListener { options1, options2, options3, v ->
            sexStr.setText(sexList[options1])
        }).build<String>()
        pvOptions.setPicker(sexList)
        pvOptions.show()
    }
    fun birthdayClick(){
        val selectedDate = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        selectedDate.set(1990,0,1)
        startDate.set(1953,0,1)
        endDate.set(2010,12,31)
        var timepiker = TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            birthTF.setText(SimpleDateFormat("yyyy-MM-dd").format(date))
        })
            .setLabel(this.getString(R.string.year), this.getString(R.string.month), this.getString(R.string.day), "", "", "")
            .setDate(selectedDate)
            .setRangDate(startDate,endDate)
            .build()
        timepiker.show()


    }

    fun monthEntoNum(date:String):String {
        var dateList = date.split(" ")
        Log.d("jieguo", dateList.size.toString())
        if (dateList.size == 3) {
            var day = dateList.get(0)
            var month = dateList.get(1)
            var year = dateList.get(2)
            when (month) {
                "Jan." -> month = "01"
                "Feb." -> month = "02"
                "Mar." -> month = "03"
                "Apr." -> month = "04"
                "May." -> month = "05"
                "Jun." -> month = "06"
                "Jul." -> month = "07"
                "Aug." -> month = "08"
                "Sep." -> month = "09"
                "Oct." -> month = "10"
                "Nov." -> month = "11"
                "Dec." -> month = "12"
            }
            if (day.length == 1) {
                day = "0" + day
            }
            if (checkIsNumber(year)&&checkIsNumber(month)&&checkIsNumber(day)){
                return year+"-"+month+"-"+day
            }else {
                return "0"
            }
        }else{
            return date
        }
    }

    private fun checkIsNumber(number :String):Boolean {

        val pattern: Pattern = Pattern.compile("[0-9]*")
        val isNum: Matcher = pattern.matcher(number)
        return if (!isNum.matches()) {
            false
        } else true
    }

}