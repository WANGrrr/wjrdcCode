package com.cashloans.cashcloud.models

/**
 * Author: Want-Sleep
 * Date: 2019/06/12
 * Desc:
 */
data class LocalContract(val name: String?, val phone: String) {
    override fun toString(): String = name ?: ""
}
