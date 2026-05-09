package app.kwlee.authentica.presentation

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class OtpQrScanner(activity: Activity) {
    private val scanner: GmsBarcodeScanner

    init {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
        scanner = GmsBarcodeScanning.getClient(activity, options)
    }

    fun scan(): Task<Barcode> = scanner.startScan()
}
