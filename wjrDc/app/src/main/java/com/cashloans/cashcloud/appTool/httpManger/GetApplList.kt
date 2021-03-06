package com.cashloans.cashcloud.appTool.httpManger

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.alibaba.fastjson.JSON
import com.cashloans.cashcloud.models.FSAppInfo
import java.io.*

object GetApplList {

    fun getAppinfoJsonFile(context: Context):File {
        var jsonArray = JSON.toJSONString(getSimpleAppList(context))
        Log.d("jsonArray",jsonArray.toString())
        return writeTextToFile(context, jsonArray, "appinfo.json")
    }

    fun getImageJsonFile(context: Context, compressmageMaps:ArrayList<Map<String,String>>):File {
        var newMaps = ArrayList<Map<String,String>>()
        for (map in compressmageMaps) {
            var newmap = HashMap<String,String>()
            newmap.putAll(map)
            newmap.put("base64", imageToBase64(map["compressPath"]).toString())
            newMaps.add(newmap)
        }
        var jsonArray = JSON.toJSONString(newMaps)
        return writeTextToFile(context, jsonArray,"image.json")
    }




    private fun createNewFile(context:Context, fileName: String): File {
        var file = File(context.getFilesDir().getPath().toString()+fileName)

        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        return file
    }

    private fun readLineFromFile(filePath: String): List<String> {
        val file = File(filePath)
        return file.readLines()
    }

    private fun readTextFromFile(filePath: String): String {
        val file = File(filePath)
        return file.readText()
    }

    private fun writeTextToFile(context: Context, text : String, fileName: String): File{
        var file = createNewFile(context, fileName)
        var strContent =  text
        var randomAccessFile = RandomAccessFile(file, "rwd")
        randomAccessFile.seek(file.length())
        randomAccessFile.write(strContent.toByteArray())
        randomAccessFile.close()
        return file
    }


    /**
     * ??????APP?????????????????????????????????????????????
     *
     * @param context
     */
    fun getSimpleAppList(context: Context):ArrayList<FSAppInfo>{
        //???????????????????????????????????????
        var arrayList = ArrayList<FSAppInfo>()
        try {
            var packageManager = context.getPackageManager()
            var packages = packageManager.getInstalledPackages(0)
            for (pack in packages) {
                try {
                    if ((pack.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                        var applicationInfo = pack.applicationInfo
                        val appname = applicationInfo.loadLabel(packageManager).toString()
                        val appDrawable = applicationInfo.loadIcon(packageManager)
                        val appIcon = Bitmap2StrByBase64(drawableToBitmap(appDrawable))
                        val packageName = applicationInfo.packageName
                        val lastUpdateTime = pack.lastUpdateTime
                        val firstInstallTime = pack.firstInstallTime
                        val isSystemApp = isSystemApp(pack)

                        var fsAppInfo = FSAppInfo(appname, appIcon, packageName,firstInstallTime, lastUpdateTime, isSystemApp)
                        arrayList.add(fsAppInfo)
                    }
                }catch (e: Exception) {

                }
            }
        }catch (e: Exception){

        }
        return arrayList
    }

    // ??????packName??????PackageInfo???????????????????????????
    private fun isSystemApp(pi: PackageInfo): Boolean {
        val isSysApp = pi.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM === 1
        val isSysUpd = pi.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP === 1
        return isSysApp || isSysUpd
    }

    //Drawable ??? Bitmap
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                if (drawable.getOpacity() !== PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)

        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight())
        drawable.draw(canvas)
        return bitmap
    }

    //?????????base64
    fun Bitmap2StrByBase64(bit: Bitmap): String {
        val bos = ByteArrayOutputStream()
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos) //??????100???????????????
        val bytes: ByteArray = bos.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    /**
     * ??????????????????Base64??????????????????
     */
    fun imageToBase64(path: String?): String? {
        if (TextUtils.isEmpty(path)) {
            return null
        }
        var ist: InputStream? = null
        var data: ByteArray? = null
        var result: String? = null
        try {
            ist = FileInputStream(path)
            //???????????????????????????????????????
            data = ByteArray(ist.available())
            //????????????
            ist.read(data)
            //????????????????????????????????????
            result = Base64.encodeToString(data, Base64.NO_CLOSE)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (null != ist) {
                try {
                    ist.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }
}