package com.polsl.android.geophotoapp.activity

import android.Manifest
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.model.UserData
import com.polsl.android.geophotoapp.rest.GeoPhotoEndpoints
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity() {

    fun onRegisterClicked() {
        if (validateFields()) {
            hideKeyboard()
            register()
        } else
            displayToast(R.string.login_fill_all_fields)
    }

    private fun register() {
        //todo handle registration on server
        val userData = UserData(usernameEditText.text.toString(), passwordEditText.text.toString())
        UserDataSharedPrefsHelper(this).saveLoggedUser(userData)
        val requestHandle = GeoPhotoEndpoints.create()
        requestHandle.register(UserData(usernameEditText.text.toString(), passwordEditText.text.toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    onRegistered()
                }, { error ->
                    onError(error)
                })
    }

    private fun validateFields(): Boolean {
        return (!usernameEditText!!.text.toString().isEmpty()
                && !passwordEditText!!.text.toString().isEmpty()
                && !confirmPasswordEditText!!.text.toString().isEmpty()
                && passwordEditText!!.text.toString() == confirmPasswordEditText!!.text.toString())
    }

    private fun onLoginClicked() {
        startActivity(LoginActivity::class.java)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupUIForKeyboardHiding(parentLayout!!)
        setupButtons()
        checkPermissions()
    }

    private fun setupButtons() {
        loginButton.setOnClickListener { onLoginClicked() }
        registerButton.setOnClickListener { onRegisterClicked() }
    }

    private fun checkPermissions() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {}

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>,
                                                                    token: PermissionToken) {
                        displayToast(R.string.permissions_needed)
                    }

                }).check()
    }

    fun onError(throwable: Throwable) {
        displayToast(getString(R.string.register_error, throwable.message))
    }

    fun onRegistered() {
        displayToast(R.string.register_success)
        startActivity(TabbedActivity::class.java)
        finish()
    }

}

