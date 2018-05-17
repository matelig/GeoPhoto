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
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by alachman on 21.04.2018.
 */
class LoginActivity : BaseActivity(), UserNetworkingDelegate {

    val networking = UserNetworking(context = this)

    private fun loginUser() {
        val userData = UserData(usernameEditText.text.toString(), passwordEditText.text.toString())
        networking.login(userData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        networking.delegate = this
        setupUIForKeyboardHiding(parentLayout)
        setupButtons()
        checkPermissions()
    }

    private fun setupButtons() {
        loginButton.setOnClickListener { onLoginClicked() }
        registerButton.setOnClickListener { onRegisterClicked() }
    }

    private fun onRegisterClicked() {
        startActivity(RegisterActivity::class.java)
        finish()
    }

    private fun onLoginClicked() {
        if (validateUserInput()) {
            hideKeyboard()
            loginUser()
        } else
            displayToast(R.string.login_fill_all_fields)
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
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        checkLogin()
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>,
                                                                    token: PermissionToken) {
                        displayToast(R.string.permissions_needed)
                    }

                }).onSameThread().check()
    }

    private fun checkLogin() {
        val userData = UserDataSharedPrefsHelper(this).getAccessToken()
        if (userData != null)
            onLogin()
    }

    private fun validateUserInput(): Boolean {
        return !usernameEditText!!.text.toString().isEmpty() && !passwordEditText!!.text.toString().isEmpty()
    }

    fun onLogin() {
        displayToast(R.string.login_success)
        startActivity(TabbedActivity::class.java)
        finish()
    }

    override fun success() {
        UserDataSharedPrefsHelper(this).saveLoggedUser(UserData(usernameEditText.text.toString(), passwordEditText.text.toString()))
        onLogin()
    }

    override fun error(error: Throwable?) {
        error?.message?.let {
            displayToast(it)
        }
    }

}
