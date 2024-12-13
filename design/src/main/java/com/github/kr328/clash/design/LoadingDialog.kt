package com.github.kr328.clash.design

import android.app.ProgressDialog
import android.content.Context

class LoadingDialog(context: Context) {
    private val progressDialog: ProgressDialog = ProgressDialog(context).apply {
        setMessage("Loading...")
        setCancelable(false)
    }

    fun show() {
        progressDialog.show()
    }

    fun dismiss() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }
}
