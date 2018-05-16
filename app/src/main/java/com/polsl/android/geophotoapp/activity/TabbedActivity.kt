package com.polsl.android.geophotoapp.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.fragments.GalleryPhotosFragment
import com.polsl.android.geophotoapp.fragments.MakePhotoFragment
import com.polsl.android.geophotoapp.fragments.MapFragment
import com.polsl.android.geophotoapp.fragments.PhotoListFragment
import com.polsl.android.geophotoapp.sharedprefs.UserDataSharedPrefsHelper
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_tabbed.*

class TabbedActivity : BaseActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    companion object {
        const val FILTERS_CODE: Int = 1001
    }

    val filtersClickObservable = PublishSubject.create<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed)
        setSupportActionBar(toolbar)
        setupFiltersButton()
        val tabsAdapter = SectionsPagerAdapter(supportFragmentManager, 4)
        container.adapter = tabsAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        container.pageMargin = 4
        tabs.setupWithViewPager(container)
        tabs.tabMode = TabLayout.MODE_FIXED
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                container.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == 0)
                    showFilterIcon()
                else
                    hideFilterIcon()
            }

        })
    }

    private fun setupFiltersButton() {
        filterIcon.visibility = View.VISIBLE
        filterIcon.setOnClickListener({ openFiltersActivity() })
    }

    fun showFilterIcon() {
        filterIcon.visibility = View.VISIBLE
    }

    fun hideFilterIcon() {
        filterIcon.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tabbed, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_settings -> return true
            R.id.action_logout -> {
                return onLogoutClicked()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onLogoutClicked(): Boolean {
        logout()
        displayToast(R.string.logout)
        startActivity(LoginActivity::class.java)
        finish()
        return true
    }

    private fun logout() {
        UserDataSharedPrefsHelper(this).saveLoggedUser(null)
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager, private val numberOfTabs: Int) : FragmentPagerAdapter(fm) {

        private val tabTitles = arrayOf("Photos", "Make photo", "Map", "Device photos")

        override fun getPageTitle(position: Int): CharSequence {
            return tabTitles[position]
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 ->
                    PhotoListFragment()
                1 ->
                    MakePhotoFragment()
                2 ->
                    MapFragment.newInstance(0.0, 0.0)
                3 ->
                    GalleryPhotosFragment()
                else ->
                    MakePhotoFragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }

    fun openFiltersActivity() {
        filtersClickObservable.onNext(true)
    }

}
