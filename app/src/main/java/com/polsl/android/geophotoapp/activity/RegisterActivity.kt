package com.polsl.android.geophotoapp.activity

import android.Manifest
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.Services.networking.UserNetworking
import com.polsl.android.geophotoapp.Services.networking.UserNetworkingDelegate
import com.polsl.android.geophotoapp.model.UserData
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity(), UserNetworkingDelegate {

    var networking = UserNetworking(context = this)

    override fun registerSuccess() {
        onRegistered()
    }

    override fun error(error: Throwable?) {
        displayToast(getString(R.string.register_error, error?.message))
    }

    fun onRegisterClicked() {
        if (validateFields()) {
            hideKeyboard()
            register()
        } else
            displayToast(R.string.login_fill_all_fields)
    }

    private fun register() {
        networking.register(UserData(usernameEditText.text.toString(), passwordEditText.text.toString()))
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
        networking.delegate = this
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

    fun onRegistered() {
        displayToast(R.string.register_success)
        loginUser()
    }

    private fun loginUser() {
        val userData = UserData(usernameEditText.text.toString(), passwordEditText.text.toString())
        networking.login(userData)
    }

    fun onLogin() {
        displayToast(R.string.login_success)
        startActivity(TabbedActivity::class.java)
        finish()
    }

    override fun loginSuccess() {
        UserDataSharedPrefsHelper(this).saveLoggedUser(UserData(usernameEditText.text.toString(), passwordEditText.text.toString()))
        onLogin()
    }
}

