package com.denis55ka.hcesample

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.denis55ka.hcesample.apdu.AIP_FLAG_SDA_SUPPORTED
import com.denis55ka.hcesample.apdu.GetProcessingOptionsResponse
import java.nio.charset.Charset

const val SELECT_PPSE_COMMAND = "00A404000E325041592E5359532E444446303100"
const val SELECT_PPSE_RESPONSE = "6F24840E325041592E5359532E4444463031A512BF0C0F610D4F08F74C6963617264018701019000"
const val SELECT_AID_COMMAND = "00A4040008F74C69636172640100"
const val SELECT_AID_RESPONSE = "6F278408F74C696361726401A51B50064C694361726487009F38099F1D089F1A029F35015F2D0272759000"

const val GET_PROCESSING_OPTIONS_COMMAND = "80 A8 00 00 0D 83 0B 00 00 00 00 00 00 00 00 06 43 22 00"
const val GET_PROCESSING_OPTIONS_RESPONSE = "77 0A 82 02 00 00 94 04 08 01 01 00 90 00"

const val READ_RECORD_COMMAND = "00 B2 01 0C 00"
const val READ_RECORD_RESPONSE = "70 81 86 9F 6C 02 00 02 9F 62 06 00 00 00 00 00 70 9F 63 06 00 00 00 00 07 80 56 29 42 37 30 38 33 33 37 33 32 35 39 30 30 30 33 32 35 30 30 31 5E 20 2F 5E 32 35 31 32 36 30 31 31 30 30 30 30 30 30 30 30 30 9F 64 01 03 9F 65 02 00 0E 9F 66 02 00 F0 9F 6B 13 70 83 37 32 59 00 03 25 00 1D 25 12 60 11 00 00 00 00 0F 9F 67 01 03 9F 69 19 9F 6A 04 9F 7E 01 9F 02 06 5F 2A 02 9F 1A 02 9C 01 9A 03 9F 15 02 9F 35 01 90 00"

const val COMPUTE_CRYPTOGRAPHIC_CHECKSUM_COMMAND = "80 2A 8E 80 00"
const val COMPUTE_CRYPTOGRAPHIC_CHECKSUM_COMMAND_PREFIX = "80 2A 8E 80"
const val COMPUTE_CRYPTOGRAPHIC_CHECKSUM_RESPONSE = "77 15 9F 60 02 06 1C 9F 61 02 06 1C 9F 36 02 02 55 DF 4B 03 00 10 00 90 00"

const val PAN = "7083 3732 5900 0325 001"
const val EXPIRATION_DATE = "2512"

class HCEService : HostApduService() {

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray? {
        logApdu("C-APDU", commandApdu.toHexString())
        val response = when {
            commandApdu.contentEquals(SELECT_PPSE_COMMAND.toByteArray()) -> SELECT_PPSE_RESPONSE.toByteArray()
            commandApdu.contentEquals(SELECT_AID_COMMAND.toByteArray()) -> SELECT_AID_RESPONSE.toByteArray()
            commandApdu.contentEquals(GET_PROCESSING_OPTIONS_COMMAND.toByteArray()) -> GET_PROCESSING_OPTIONS_RESPONSE.toByteArray()
            commandApdu.contentEquals(READ_RECORD_COMMAND.toByteArray()) -> buildReadRecordResponse(PAN, EXPIRATION_DATE)
            commandApdu.toHexString().startsWith(COMPUTE_CRYPTOGRAPHIC_CHECKSUM_COMMAND_PREFIX) -> COMPUTE_CRYPTOGRAPHIC_CHECKSUM_RESPONSE.toByteArray()
            else -> null
        }
        logApdu("R-APDU", response?.toHexString())
        return response
    }

    override fun onDeactivated(reason: Int) = Unit

    private fun logApdu(action: String, value: String?) {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(Intent(action).putExtra("PAYLOAD", value))
        Log.d(action, value.toString())
    }

}

private fun buildGetProcessingOptionsResponse(): ByteArray {
    return GetProcessingOptionsResponse(AIP_FLAG_SDA_SUPPORTED).toHexString().toByteArray()
}

private fun buildReadRecordResponse(pan: String, expirationDate: String): ByteArray {
    val template = buildTemplate("7081",
            "9F6C" to "0002",
            "9F62" to "000000000070",
            "9F63" to "000000000780",
            "56" to buildTrack1(pan, expirationDate),
            "9F64" to "03",
            "9F65" to "000E",
            "9F66" to "00F0",
            "9F6B" to buildTrack2(pan, expirationDate),
            "9F67" to "03",
            "9F69" to "9F6A049F7E019F02065F2A029F1A029C019A039F15029F3501"
    ) + "9000"
    return template.toByteArray()
}

private fun buildTrack1(pan: String, expirationDate: String, serviceCode: String = "601"): String {
    return "B${pan.replace(" ", "")}^ /^$expirationDate${serviceCode}1000000000".toByteArray(Charset.defaultCharset()).toHexString()
}

private fun buildTrack2(pan: String, expirationDate: String, serviceCode: String = "601"): String {
    return "${pan.replace(" ", "")}D$expirationDate${serviceCode}1000000000F"
}
