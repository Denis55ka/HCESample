package com.denis55ka.hcesample

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.denis55ka.hcesample.apdu.AIP_FLAG_RFU
import com.denis55ka.hcesample.apdu.GetProcessingOptionsResponse
import java.io.IOException

private const val READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var nfcIntent: PendingIntent
    private lateinit var logText: TextView

    private val logReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val payload = intent.getStringExtra("PAYLOAD")
            if (payload == SELECT_COMMAND) {
                logText.append("========================================\n")
            }
            logText.append("${intent.action}: $payload\n")
            if (intent.action == "R-APDU") {
                logText.append("\n")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logText = findViewById(R.id.log_text)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        GetProcessingOptionsResponse(AIP_FLAG_RFU)
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(logReceiver, IntentFilter().apply {
                    addAction("C-APDU")
                    addAction("R-APDU")
                })
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
//        nfcAdapter.enableReaderMode(this, this, READER_FLAGS, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableReaderMode(this)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logReceiver)
        super.onDestroy()
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
        received = transceive(dep, SELECT_COMMAND)
        if (received.toHexString().takeLast(5) != "90 00") {
            return
        }
        received = transceive(dep, GET_PROCESSING_OPTIONS_COMMAND)
        if (received.toHexString().takeLast(5) != "90 00") {
            return
        }
        received = transceive(dep, READ_RECORD_COMMAND)
        if (received.toHexString().takeLast(5) != "90 00") {
            return
        }
        received = transceive(dep, COMPUTE_CRYPTOGRAPHIC_CHECKSUM_COMMAND)
    }

    private fun transceive(dep: IsoDep, hexString: String): ByteArray {
        val bytes = hexString.toByteArray()
        Log.i("HCESample", "Send: ${bytes.toHexString()}")
        val received = dep.transceive(bytes)
        Log.i("HCESample", "Received: " + received.toHexString())
        return received
    }

}
