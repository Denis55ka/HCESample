package com.denis55ka.hcesample

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class HCEService : HostApduService() {

    companion object {
        const val SELECT_COMMAND = "00A40400 08 F74C696361726401 00"
        const val SELECT_RESPONSE = "6F 27 84 08 F7 4C 69 63 61 72 64 01 A5 1B 50 06 4C 69 43 61 72 64 87 00 9F 38 09 9F 1D 08 9F 1A 02 9F 35 01 5F 2D 02 72 75 90 00"
        const val GET_PROCESSING_OPTIONS_COMMAND = "80 A8 00 00 0D 83 0B 00 00 00 00 00 00 00 00 06 43 22 00"
        const val GET_PROCESSING_OPTIONS_RESPONSE = "77 0A 82 02 00 00 94 04 08 01 01 00 90 00"
        const val READ_RECORD_COMMAND = "00 B2 01 0C 00"
        const val READ_RECORD_RESPONSE = "70 81 86 9F 6C 02 00 02 9F 62 06 00 00 00 00 00 70 9F 63 06 00 00 00 00 07 80 56 29 42 37 30 38 33 33 37 33 32 35 39 30 30 30 33 32 35 30 30 31 5E 20 2F 5E 32 35 31 32 36 30 31 31 30 30 30 30 30 30 30 30 30 9F 64 01 03 9F 65 02 00 0E 9F 66 02 00 F0 9F 6B 13 70 83 37 32 59 00 03 25 00 1D 25 12 60 11 00 00 00 00 0F 9F 67 01 03 9F 69 19 9F 6A 04 9F 7E 01 9F 02 06 5F 2A 02 9F 1A 02 9C 01 9A 03 9F 15 02 9F 35 01 90 00"
        const val COMPUTE_CRYPTOGRAPHIC_CHECKSUM_COMMAND = "80 2A 8E 80 16 00 00 00 01 03 00 00 00 05 00 00 06 43 06 43 00 18 07 12 00 00 22 00"
        const val COMPUTE_CRYPTOGRAPHIC_CHECKSUM_RESPONSE = "77 15 9F 60 02 06 1C 9F 61 02 06 1C 9F 36 02 02 55 DF 4B 03 00 10 00 90 00"
    }

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray? {
        Log.d("C-APDU", commandApdu.toHexString())
        return when {
            commandApdu.contentEquals(SELECT_COMMAND.toByteArray()) -> SELECT_RESPONSE.toByteArray()
            commandApdu.contentEquals(GET_PROCESSING_OPTIONS_COMMAND.toByteArray()) -> GET_PROCESSING_OPTIONS_RESPONSE.toByteArray()
            commandApdu.contentEquals(READ_RECORD_COMMAND.toByteArray()) -> READ_RECORD_RESPONSE.toByteArray()
            commandApdu.contentEquals(COMPUTE_CRYPTOGRAPHIC_CHECKSUM_COMMAND.toByteArray()) -> COMPUTE_CRYPTOGRAPHIC_CHECKSUM_RESPONSE.toByteArray()
            else -> null
        }
    }

    override fun onDeactivated(reason: Int) = Unit

}
