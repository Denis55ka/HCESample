package com.denis55ka.hcesample

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.IOException

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private val READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK

    private val nfcTechFilter = arrayOf(arrayOf(NfcA::class.java.name))
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var nfcIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableReaderMode(this, this, READER_FLAGS, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag) {
        val dep: IsoDep = IsoDep.get(tag)
        try {
            dep.connect()
            readCard(dep)
        } catch (e: IOException) {
            Log.i("HCESample", "Error tagcomm: " + e.message)
        } finally {
            dep.close()
        }
    }

    private fun readCard(dep: IsoDep) {
        var received: ByteArray?
        received = transceive(dep, HCEService.SELECT_COMMAND)
        if (received.toHexString().takeLast(5) != "90 00") {
            return
        }
        received = transceive(dep, HCEService.GET_PROCESSING_OPTIONS_COMMAND)
        if (received.toHexString().takeLast(5) != "90 00") {
            return
        }
        received = transceive(dep, HCEService.READ_RECORD_COMMAND)
        if (received.toHexString().takeLast(5) != "90 00") {
            return
        }
        received = transceive(dep, HCEService.COMPUTE_CRYPTOGRAPHIC_CHECKSUM_COMMAND)
    }

    private fun transceive(dep: IsoDep, hexString: String): ByteArray {
        val bytes = hexString.toByteArray()
        Log.i("HCESample", "Send: ${bytes.toHexString()}")
        val received = dep.transceive(bytes)
        Log.i("HCESample", "Received: " + received.toHexString())
        return received
    }

}
