package biz.sstechnos.employeedashboard.utils

import android.app.Activity
import android.view.View
import android.widget.TextView
import biz.sstechnos.employeedashboard.R
import cn.pedant.SweetAlert.SweetAlertDialog

class SweetDialogUtil {
    companion object {
        fun makeSweetDialog(
            activity: Activity,
            alertType: Int,
            multiline: Boolean,
            title: String,
            message: String
        ): SweetAlertDialog {

            val sweetAlertDialog = SweetAlertDialog(activity, alertType)
                .setTitleText(title)
                .setContentText(message)

            if (multiline) {
                sweetAlertDialog.setOnShowListener { dialog ->
                    val sweetDialog: SweetAlertDialog = dialog as SweetAlertDialog
                    val text = sweetDialog.findViewById<TextView>(R.id.content_text)
                    text.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    text.setSingleLine(false)
                    text.maxLines = 10
                    text.setLines(6)
                }
            }
            return sweetAlertDialog
        }
    }
}