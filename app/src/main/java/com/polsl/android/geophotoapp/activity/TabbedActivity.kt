package com.polsl.android.geophotoapp.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.polsl.android.geophotoapp.R
import com.polsl.android.geophotoapp.fragments.MakePhotoFragment
import com.polsl.android.geophotoapp.fragments.MapFragment
import kotlinx.android.synthetic.main.activity_tabbed.*

class TabbedActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed)

        setSupportActionBar(toolbar)

        val tabsAdapter = SectionsPagerAdapter(supportFragmentManager, 2)

        container.adapter = tabsAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        container.pageMargin = 3
        tabs.setupWithViewPager(container)
        tabs.tabMode = TabLayout.MODE_FIXED
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                container.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tabbed, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager, private val numberOfTabs: Int) : FragmentPagerAdapter(fm) {

        private val tabTitles = arrayOf("Make photo", "Map")

        override fun getPageTitle(position: Int): CharSequence {
            return tabTitles[position]
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> MakePhotoFragment()
                1 -> MapFragment.newInstance(0.0,0.0)
                else -> MakePhotoFragment()
            }
        }

        override fun getCount(): Int {
            return numberOfTabs
        }
    }

}
