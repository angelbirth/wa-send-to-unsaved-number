package angelbirth.org.whatsappsendtounsaved

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText

class Activity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var num = intent?.takeIf { it.action == Intent.ACTION_VIEW && it.data != null }?.data?.host
        num?.let {
            when {
                it.matches("[1-9][0-9]{1,15}".toRegex()) -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$num")))
                    finish()
                }
                it.matches("""08[1-9]\d{7,10}""".toRegex()) -> {
                    num = it.replace(Regex("""^0(.*)"""), "62$1")
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$num")))
                    finish()
                }
                it.matches(Regex("""\+(\d+)""")) -> {
                    num = it.replace(Regex("""\+(\d+)"""), "$1")
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$num")))
                }
            }
        }
        val wa = packageManager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
                .firstOrNull {
                    it.packageName == "com.whatsapp"
                }
        if (wa == null) {
            AlertDialog.Builder(this).run {
                setMessage("You don't have whatsapp installed. Press OK to download")
                setPositiveButton(android.R.string.ok) { _, _ ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")))
                }
                setOnCancelListener { finish() }
                setOnDismissListener { finish() }
                create()
            }.show()
            finish()
        } else {
            AlertDialog.Builder(this).apply {
                val ed = EditText(this@Activity).apply {
                    inputType = EditorInfo.TYPE_CLASS_PHONE
                }
                setView(ed)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    var num1 = num ?: ed.text.toString()
                    if (num1.matches("[1-9][0-9]{1,15}".toRegex())) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$num1")))
                    } else if (num1.matches("""08[1-9]\d{7,10}""".toRegex())) {
                        num1 = num1.replace(Regex("""^0(\d*)"""), "62$1")
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$num1")))
                    } else if (num1.matches(Regex("""\+(\d+)"""))) {
                        num1 = num1.replace(Regex("""\+(\d+)"""), "$1")
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$num1")))
                    }
                    finish()
                }
                setOnCancelListener { finish() }
                setOnDismissListener { finish() }
            }.show()
        }
    }
}