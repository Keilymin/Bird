package com.keilymin.bird.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.keilymin.bird.R

class InternetTroubleDialog(val onClose: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val nullParent: ViewGroup? = null
        builder.setView(inflater.inflate(R.layout.internet_dialog, nullParent))
            .setPositiveButton(
                R.string.ok
            ) { dialog: DialogInterface, _: Int ->
                onClose()
                dialog.cancel()
            }
        return builder.create()
    }
}