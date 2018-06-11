package com.polsl.android.geophotoapp.fragments

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.activity.TakenPhotoActivity
import kotlinx.android.synthetic.main.fragment_make_photo.*

class MakePhotoFragment : Fragment() {

    private val TAKE_PHOTO_REQUEST = 101
    private var mCurrentPhotoPath: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_make_photo, container, false)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cameraPreview.setOnClickListener { validatePermission() }
    }

    private fun validatePermission() {

        Dexter.withActivity(this.activity).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(object : MultiplePermissionsListener {

            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    launchCamera()
                } else {
                    Snackbar.make(cameraMainContainer,
                            R.string.storage_permission_denied_message,
                            Snackbar.LENGTH_LONG)
                            .show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                AlertDialog.Builder(this@MakePhotoFragment.context)
                        .setTitle(R.string.storage_permission_rationale_title)
                        .setMessage(R.string.storage_permition_rationale_message)
                        .setNegativeButton(android.R.string.cancel,
                                { dialog, _ ->
                                    dialog.dismiss()
                                    token?.cancelPermissionRequest()
                                })
                        .setPositiveButton(android.R.string.ok,
                                { dialog, _ ->
                                    dialog.dismiss()
                                    token?.continuePermissionRequest()
                                })
                        .setOnDismissListener({ token?.cancelPermissionRequest() })
                        .show()
            }

        }).check()
    }

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = this.activity.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(this.activity.packageManager) != null) {
            mCurrentPhotoPath = fileUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            var intent = Intent(context, TakenPhotoActivity::class.java)
            intent.putExtra("photoUrl", mCurrentPhotoPath)
            startActivity(intent)

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}