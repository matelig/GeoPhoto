package com.polsl.android.geophotoapp.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.Unbinder

abstract class BaseActivity: AppCompatActivity() {

    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unbinder = ButterKnife.bind(this)
    }

    fun startActivity(className: Class<*>) {
        val intent = Intent(this, className)
        startActivity(intent)
    }

    fun startService(className: Class<*>) {
        val intent = Intent(this, className)
        startService(intent)
    }

    fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun setupUIForKeyboardHiding(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                hideKeyboard()
                false
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUIForKeyboardHiding(innerView)
            }
        }
    }

    fun displayToast(resId: Int) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show()
    }

    fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private var progress: ProgressDialog?=null

    fun showProgressDialog(title: String, message: String) {
        progress = ProgressDialog.show(this, title,
                message, true);
    }

    fun hideProgressDialog() {
        progress?.dismiss()
    }
}
