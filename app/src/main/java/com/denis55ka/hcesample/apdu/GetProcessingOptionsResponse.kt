package com.denis55ka.hcesample.apdu

import com.denis55ka.hcesample.buildTemplate
import com.denis55ka.hcesample.toHexString
import java.math.BigInteger

const val AIP_FLAG_RFU = 1 shl 15
const val AIP_FLAG_SDA_SUPPORTED = 1 shl 14
const val AIP_FLAG_DDA_SUPPORTED = 1 shl 13
const val AIP_FLAG_CARDHOLDER_VERIFICATION_SUPPORTED = 1 shl 12
const val AIP_FLAG_TERMINAL_RISK_MANAGEMENT_PERFORMED = 1 shl 11
const val AIP_FLAG_ISSUER_AUTHENTICATION_SUPPORTED = 1 shl 10
const val AIP_FLAG_ON_DEVICE_CARDHOLDER_VERIFICATION_SUPPORTED = 1 shl 9
const val AIP_FLAG_CDA_SUPPORTED = 1 shl 8

const val AIP_FLAG_EMV_MODE_SUPPORTED = 1 shl 7
const val AIP_FLAG_RELAY_RESISTANCE_PROTOCOL_SUPPORTED = 1 shl 0

const val TAG = "77"
const val AIP_TAG = "82"
const val AFL_TAG = "94"

data class GetProcessingOptionsResponse(
        val aipFlags: Int
) {

    fun toHexString(): String = buildTemplate(TAG,
            AIP_TAG to BigInteger.valueOf(aipFlags.toLong()).toByteArray().takeLast(2).toByteArray().toHexString(),
            AFL_TAG to "08010100") + "9000"

}
