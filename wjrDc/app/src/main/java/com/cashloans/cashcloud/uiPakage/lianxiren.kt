package com.cashloans.cashcloud.uiPakage

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.google.gson.Gson
import com.hjq.toast.ToastUtils
import com.qpg.superhttp.SuperHttp
import com.qpg.superhttp.callback.SimpleCallBack
import com.cashloans.cashcloud.MainActivity
import com.cashloans.cashcloud.R
import com.cashloans.cashcloud.appTool.AppsFlyerTool
import com.cashloans.cashcloud.appTool.BaseActivity
import com.cashloans.cashcloud.appTool.SPHelper
import com.cashloans.cashcloud.appTool.httpManger.DataManager
import com.cashloans.cashcloud.models.*
import com.cashloans.thloans.appTool.BaseBean
import java.util.*
import kotlin.collections.ArrayList


class lianxiren : BaseActivity(), View.OnClickListener , OnOptionsSelectListener{
    private lateinit var backBtns : RelativeLayout

    private lateinit var Firstguanxi: EditText
    private lateinit var FirstName: EditText
    private lateinit var FirstPhone: EditText
    private lateinit var FirstView: ConstraintLayout
    private lateinit var SecondRelation: EditText
    private lateinit var SecondName: EditText
    private lateinit var SecondPhone: EditText

    private lateinit var lianxirenPiker: OptionsPickerView<*>

    private lateinit var SecondView: ConstraintLayout
    private lateinit var lxrArrList: ArrayList<LocalContract>

    private lateinit var mRelationList: ArrayList<String>

    private lateinit var btnContinueAuth: Button
    private lateinit var lxrPiker: lianxirenPiker
    private lateinit var lxrPiker1: lianxirenPiker
    private var mSelectIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun getLayoutId(): Int {
        return  R.layout.activity_lianxiren
    }


    override fun initView() {
        AppsFlyerTool.event("contants_page_open")
        backBtns = findViewById(R.id.id_back)
        Firstguanxi = findViewById(R.id.diyigeguanxiName)
        FirstName = findViewById(R.id.diyigeName)
        FirstPhone = findViewById(R.id.diyigePhone)
        SecondRelation = findViewById(R.id.diergeguanxi)
        SecondName = findViewById(R.id.diergeName)
        SecondPhone = findViewById(R.id.diergePhone)
        btnContinueAuth = findViewById(R.id.sureBtnsss)
        FirstView = findViewById(R.id.cl_operator_first_contact)
        SecondView = findViewById(R.id.cl_operator_second_contact)

        Firstguanxi.setOnClickListener(this)
        FirstName.setOnClickListener(this)
        FirstPhone.setOnClickListener(this)
        SecondRelation.setOnClickListener(this)
        SecondName.setOnClickListener(this)
        SecondPhone.setOnClickListener(this)
        FirstView.setOnClickListener(this)
        SecondView.setOnClickListener(this)
        backBtns.setOnClickListener(this)
        btnContinueAuth.setOnClickListener(this)

        lianxirenPiker = OptionsPickerBuilder(this, this)
            .setContentTextSize(22)
            .setDividerColor(Color.BLACK)
            .setTextColorCenter(Color.BLACK)
            .build<Any>()
        mRelationList = ArrayList<String>()
        mRelationList.add(this.getString(R.string.spouse))
        mRelationList.add(this.getString(R.string.friend))
        mRelationList.add(this.getString(R.string.relatives))
        readLocalContact()
        lxrPiker1 = lianxirenPiker(this, View.OnClickListener { v ->
            when (v.id) {
                R.id.relatives -> {
                    SecondRelation.setText(mRelationList.get(2))
                    SPHelper.getInstance().put("etSecondRelation", mRelationList.get(2))
                }
                R.id.friend -> {
                    SecondRelation.setText(mRelationList.get(1))
                    SPHelper.getInstance().put("etSecondRelation", mRelationList.get(1))
                }
                R.id.spouse -> {
                    SecondRelation.setText(mRelationList.get(0))
                    SPHelper.getInstance().put("etSecondRelation", mRelationList.get(0))
                }
            }
        })
        lxrPiker = lianxirenPiker(this, View.OnClickListener { v ->
            when (v.id) {
                R.id.relatives -> {
                    Firstguanxi.setText(mRelationList.get(2))
                    SPHelper.getInstance().put("etFirstRelation", mRelationList.get(2))
                }
                R.id.friend -> {
                    Firstguanxi.setText(mRelationList.get(1))
                    SPHelper.getInstance().put("etFirstRelation", mRelationList.get(1))
                }
                R.id.spouse -> {
                    Firstguanxi.setText(mRelationList.get(0))
                    SPHelper.getInstance().put("etFirstRelation", mRelationList.get(0))
                }
            }
        })
        FirstName.setText(SPHelper.getInstance().getString("etFirstName"))
        FirstPhone.setText(SPHelper.getInstance().getString("etFirstPhone"))
        Firstguanxi.setText(SPHelper.getInstance().getString("etFirstRelation"))
        SecondName.setText(SPHelper.getInstance().getString("etSecondName"))
        SecondPhone.setText(SPHelper.getInstance().getString("etSecondPhone"))
        SecondRelation.setText(SPHelper.getInstance().getString("etSecondRelation"))
    }

    override fun onClick(v: View?) {
        Log.d("123","clickss")
        when(v?.id) {
             R.id.id_back ->{
                 val intents = Intent(this, MainActivity::class.java)
                 startActivity(intents)
            }

            R.id.diyigeguanxiName ->{
                AppsFlyerTool.event("contants_relationship1button_click")
                mSelectIndex = 0
                Log.d("123","11111")

                lxrPiker.show()
            }
            R.id.diyigeName ->{


                if (lxrArrList.isEmpty()) {
                    ToastUtils.show("No contact phone")
                    return
                }
                AppsFlyerTool.event("contants_person1button_click")
                Log.d("mRelationList", mRelationList.toString())
                getLianxirens()
                mSelectIndex = 10
                return
            }
            R.id.diyigePhone ->{
                Log.d("123","33333")

                if (lxrArrList.isEmpty()) {
                    ToastUtils.show("No contact phone")
                    return
                }
                AppsFlyerTool.event("contants_person1button_click")
                Log.d("mRelationList", mRelationList.toString())
                getLianxirens()
                mSelectIndex = 10
                return
            }
            R.id.sureBtnsss ->{
                Log.d("123","44444")
              sendMessages()

            }
            R.id.diergeName ->{
                if (lxrArrList.isEmpty()) {
                    ToastUtils.show("No contact phone")
                    return
                }
                AppsFlyerTool.event("contants_person1button_click")
                Log.d("mRelationList", mRelationList.toString())
                getLianxirens()
                mSelectIndex = 20
                return
            }
            R.id.diergePhone ->{
                if (lxrArrList.isEmpty()) {
                    ToastUtils.show("No contact phone")
                    return
                }
                AppsFlyerTool.event("contants_person1button_click")
                Log.d("mRelationList", mRelationList.toString())
                getLianxirens()
                mSelectIndex = 20
                return
            }
            R.id.diergeguanxi ->{
                AppsFlyerTool.event("contants_relationship2button_click")

                lxrPiker1.show()
                mSelectIndex = 1
                return
            }

        }
    }

    fun sendMessages(){
        val emergencyContactList: MutableList<EmergencyContact> =
            LinkedList<EmergencyContact>()

        val firstphone: String = FirstPhone.getText().toString()
        val firstRelation: String = Firstguanxi.getText().toString()
        val secondphone: String = SecondPhone.getText().toString()
        val secondRelation: String = SecondRelation.getText().toString()

        if (firstphone.isEmpty() || secondphone.isEmpty()) {
            ToastUtils.show("select a phone number")
            return
        }
        if (firstphone.contentEquals(secondphone)) {
            ToastUtils.show("select the same phone number")
            return
        }
        if (firstRelation.isEmpty() || secondRelation.isEmpty()) {
            ToastUtils.show("select relationship")
            return
        }


        val emergencyContact1 = EmergencyContact(
            FirstName.getText().toString(),
            FirstPhone.getText().toString(), Firstguanxi.getText().toString()
        )
        val emergencyContact2 = EmergencyContact(
            SecondName.getText().toString(),
            SecondPhone.getText().toString(), SecondRelation.getText().toString()
        )
        emergencyContactList.add(emergencyContact1)
        emergencyContactList.add(emergencyContact2)
        val param = ArrayMap<String, @JvmSuppressWildcards Any>()
        param["contacts"] = lxrArrList
        param["emergencyContacts"] = emergencyContactList


        SuperHttp.post(HttpNames.UploadLianxiren)
            .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences("token"))
            .setJson(Gson().toJson(param))
            .request(object : SimpleCallBack<BaseBean>() {
                override fun onSuccess(data: BaseBean) {

                    if (data.code == 0){

                        toYys()
                    }
                }

                override fun onFail(errCode: Int, errMsg: String) {

                    Log.d("mesggg",errMsg)
                }
            })
    }
//    运营商
    fun toYys(){
    AppsFlyerTool.event("operator_port_ask")

    SuperHttp.get(HttpNames.getyys)
        .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences("token"))
        .request(object : SimpleCallBack<String>() {
            override fun onSuccess(data: String) {
                var datas = Gson().fromJson(data,yysResult::class.java)
                if (datas.code.toInt() == 0){
                     for (authObject in datas.data) {
                         if (authObject.note.equals("my_th_operator")) {
                             toYysWebView()
                         }
                     }
                    }else{
                        finish()
                    }
            }
            override fun onFail(errCode: Int, errMsg: String) {

                Log.d("errors",errMsg)
            }
        })
    }
    fun toYysWebView(){
        val param = ArrayMap<String, @JvmSuppressWildcards Any>()
        param["returnUrl"] = "https://www.baidu.com"

        SuperHttp.post(HttpNames.mineYys)
            .setJson(Gson().toJson(param))
            .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences("token"))
            .request(object : SimpleCallBack<BaseBean>() {
                override fun onSuccess(data: BaseBean) {
                    var datas = Gson().fromJson(data.data.toString(),KefuModel::class.java)

                    val intent = Intent(this@lianxiren, baseWebview::class.java)
                    intent.putExtra("url",datas.url)
                    startActivity(intent)
                    Log.d("urlss",datas.url)
                }
                override fun onFail(errCode: Int, errMsg: String) {

                    Log.d("errors",errMsg)
                }
            })

    }

    /**
     * 读取通讯录
     */
    private fun readLocalContact() {
        lxrArrList = ArrayList<LocalContract>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )
            ?: return
        if (cursor.moveToFirst()) {
            do {
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var phone =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                phone = phone.replace(" ".toRegex(), "").replace("-".toRegex(), "")
                val localContract = LocalContract(name, phone)

                lxrArrList.add(localContract as LocalContract)
                Log.d("123",lxrArrList.toString())

            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View?) {
        try {

            if (mSelectIndex == 0) {
                Firstguanxi.setText(mRelationList[options1])
                SPHelper.getInstance().put("etFirstRelation", mRelationList[options1])
                AppsFlyerTool.event("contants_relationship1select_sucess")
            } else {
                SPHelper.getInstance().put("etSecondRelation", mRelationList[options1])
                SecondRelation.setText(mRelationList[options1])
                AppsFlyerTool.event("contants_relationship2select_sucess")
            }
            if (FirstName.getText().length > 0 && SecondName.getText().length > 0) {
                btnContinueAuth.isEnabled = true
            }
        } catch (e: Exception) {
            ToastUtils.show(e.localizedMessage)
        }
    }
    fun getLianxirens() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent,0)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("123",requestCode.toString())
        when (requestCode) {
            0 -> {
                if (data == null) {
                    return
                }
                //处理返回的data,获取选择的联系人信息
                val uri :Uri = data.data as Uri
                val contactsArr: Array<String?>? = getPhoneContacts(uri)
                val contacts :Array<String?>? = contactsArr

                if (mSelectIndex == 10) {
                    FirstName.setText(contacts?.get(0))
                    FirstPhone.setText(contacts?.get(1))
                    SPHelper.getInstance().put("etFirstName", contacts?.get(0))
                    SPHelper.getInstance().put("etFirstPhone", contacts?.get(1))
                    AppsFlyerTool.event("contants_person1select_sucess")
                } else {
                    SecondName.setText(contacts?.get(0))
                    SecondPhone.setText(contacts?.get(1))
                    SPHelper.getInstance().put("etSecondName", contacts?.get(0))
                    SPHelper.getInstance().put("etSecondPhone", contacts?.get(1))
                    AppsFlyerTool.event("contants_person2select_sucess")
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    //根据游标取
    //根据游标取
     fun getPhoneContacts(uri: Uri): Array<String?>? {
        val contact = arrayOfNulls<String>(2)

        //得到ContentResolver对象
        val cr = contentResolver
        //取得电话本中开始一项的光标
        val cursor = cr.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            //取得联系人姓名
            val nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            contact[0] = cursor.getString(nameFieldColumnIndex)
            //取得电话号码
            val ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val phone = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId,
                null,
                null
            )
            if (phone != null) {
                try {
                    phone.moveToFirst()
                    contact[1] =
                        phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                } catch (e: java.lang.Exception) {
                    ToastUtils.show(e.message)
                }
            }
            phone!!.close()
            cursor.close()
        } else {
            return null
        }
        return contact
    }

}