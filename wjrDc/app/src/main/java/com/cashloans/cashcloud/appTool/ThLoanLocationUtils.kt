package com.cashloans.cashcloud.appTool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.cashloans.thloans.appTool.App

/**
 * Author: Want-Sleep
 * Date: 2019/07/09
 * Desc:
 */
class ThLoanLocationUtils private constructor() {

    companion object {
        val INSTANCE: ThLoanLocationUtils by lazy {
            ThLoanLocationUtils()
        }
    }

    private var mLocation: Location? = null
    private var mCriteria: Criteria = Criteria()

    private val mLocationListener: LocationListener = object : LocationListener {


        override fun onLocationChanged(location: Location) {

        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}

    }

    init {
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        mCriteria.accuracy = Criteria.ACCURACY_COARSE
        // 设置是否要求速度
        mCriteria.isSpeedRequired = false
        // 设置是否允许运营商收费
        mCriteria.isCostAllowed = false
        // 设置是否需要方位信息
        mCriteria.isBearingRequired = false
        // 设置是否需要海拔信息
        mCriteria.isAltitudeRequired = false
        // 设置对电源的需求
        mCriteria.powerRequirement = Criteria.POWER_LOW
    }

    fun getLocation(): Location? = mLocation

    // 调用此类前请先确认权限，权限不在此处申请
    @SuppressLint("MissingPermission")
    fun addLocationListener() {
        try {
            val applicationContext: Application = App.getInstance()
            val locationManager: LocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val locationProvider: String = locationManager.getBestProvider(mCriteria, true).toString()

            mLocation = locationManager.getLastKnownLocation(locationProvider)

            Log.d("mLocation",mLocation.toString())
            removeLocationListener()
            locationManager.requestLocationUpdates(locationProvider, 1000 * 60, 100f, mLocationListener)
        } catch (e: Exception) {
            e.printStackTrace();
            Log.e("ddd",Log.getStackTraceString(e));
        }

    }

    fun removeLocationListener() {
        val applicationContext: Application = App.getInstance()
        val locationManager: LocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(mLocationListener)
    }
    fun isLocServiceEnable():Boolean{
        val applicationContext: Application = App.getInstance()
        val locationManager:LocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps:Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network:Boolean= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (gps || network) {
            return true
        }
        return false

    }
}
