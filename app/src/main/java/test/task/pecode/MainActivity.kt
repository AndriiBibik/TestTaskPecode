package test.task.pecode

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val SHARED_PREFERENCES_NAME = "default_app_preferences"
        private val NUMBER_FRAGMENTS_KEY = "fragments_number_key"
        private val POSITION_KEY = "position_key"
    }

    private lateinit var pagerAdapter: CreateNotificationSlidePagerAdapter
    private lateinit var mainViewPager: ViewPager2
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewPager = view_pager_main
        mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(selectedPosition: Int) {
                super.onPageSelected(selectedPosition)
                position = selectedPosition
                mainViewPager.post { updateDotIndicatorsForViewPagerAndSavePosition() }
            }
        })
        pagerAdapter = CreateNotificationSlidePagerAdapter(supportFragmentManager, lifecycle)
        mainViewPager.adapter = pagerAdapter

        initSavedDataForViewPagerFragments()
        checkIfIntentFromNotificationThenUpdatePosition()
    }

    private fun updateDotIndicatorsForViewPagerAndSavePosition() {
        updateDotIndicatorsForViewPager()
        updateViewPagerPositionInMemory()
    }

    private fun updateDotIndicatorsForViewPager() {
        view_pager_dot_indicators_container.removeAllViews()
        val numberOfFragments = pagerAdapter.itemCount
        putAllDotIndicatorsIntoContainer(numberOfFragments)
        view_pager_dot_indicators_container.invalidate()
    }

    private fun putAllDotIndicatorsIntoContainer(numberOfFragments: Int) {
        for (i in 1..numberOfFragments) {
            if (numberOfFragments != 1) {
                if (i == (position + 1)) {
                    createLargeDotIndicatorAndAddToContainer()
                } else {
                    createSmallDotIndicatorAndAddToContainer()
                }
            }
        }
    }

    private fun createLargeDotIndicatorAndAddToContainer() {
        val largeDotIndicator =
            ImageView(view_pager_dot_indicators_container.context).apply {
                setImageResource(R.drawable.large_dot_indicator)
            }
        view_pager_dot_indicators_container.addView(largeDotIndicator)
    }

    private fun createSmallDotIndicatorAndAddToContainer() {
        val smallDotIndicator =
            ImageView(view_pager_dot_indicators_container.context).apply {
                setImageResource(R.drawable.small_dot_indicator)
            }
        view_pager_dot_indicators_container.addView(smallDotIndicator)
    }

    private fun updateViewPagerPositionInMemory() {
        val sharedPreferences = getDefaultSharedPreferences()
        sharedPreferences.edit().putInt(POSITION_KEY, position).apply()
    }

    private fun initSavedDataForViewPagerFragments() {
        val numberOfFragments = getNumberFragmentsFromPreferences()
        val position = getSavedPagerAdapterPositionFromPreferences()
        initialAddAllFragments(numberOfFragments)
        initialUpdateViewPagerPosition(position)
    }

    private fun getNumberFragmentsFromPreferences(): Int {
        val sharedPreferences = getDefaultSharedPreferences()
        return sharedPreferences.getInt(NUMBER_FRAGMENTS_KEY, 1)
    }

    private fun getSavedPagerAdapterPositionFromPreferences(): Int {
        val sharedPreferences = getDefaultSharedPreferences()
        return sharedPreferences.getInt(POSITION_KEY, 0)
    }

    private fun getDefaultSharedPreferences(): SharedPreferences {
        return getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    private fun initialAddAllFragments(numberFragments: Int) {
        for (i in 1..numberFragments) {
            addNewFragment()
        }
    }

    private fun initialUpdateViewPagerPosition(position: Int) {
        mainViewPager.setCurrentItem(position, false)
    }

    private fun checkIfIntentFromNotificationThenUpdatePosition() {
        intent?.getIntExtra(CreateNotificationFragment.NOTIFICATION_FROM_POSITION_KEY, -1)
            ?.let { position ->
                if (position != -1) {
                    mainViewPager.setCurrentItem(position, true)
                }
            }
    }

    fun addNewFragmentUpdateItsNumberSlideForwardIfNeeded() {
        addNewFragment()
        slideForwardIfAtLastPosition()
        updateFragmentNumberInMemory()
        updateDotIndicatorsForViewPagerAndSavePosition()
    }

    private fun addNewFragment() = pagerAdapter.addNewFragment()
    private fun slideForwardIfAtLastPosition() {
        if (pagerAdapter.itemCount == (position + 2)) {
            mainViewPager.setCurrentItem((position + 1), false)
        }
    }

    private fun updateFragmentNumberInMemory() {
        val sharedPreferences = getDefaultSharedPreferences()
        sharedPreferences.edit().putInt(NUMBER_FRAGMENTS_KEY, pagerAdapter.itemCount).apply()
    }

    fun removeLastFragmentUpdateItsNumberSlideIfNeeded() {
        //to prevent double click remove of the first
        if (pagerAdapter.itemCount > 1) {
            slideBackIfAtLastPosition()
            removeLastFragment()
            updateFragmentNumberInMemory()
            updateDotIndicatorsForViewPagerAndSavePosition()
        }
    }

    private fun slideBackIfAtLastPosition() {
        if (pagerAdapter.itemCount == (position + 1)) {
            mainViewPager.setCurrentItem((position - 1), false)
        }
    }

    private fun removeLastFragment() = pagerAdapter.removeLastFragment()

    fun getLastFragmentNumber() = pagerAdapter.itemCount

    private inner class CreateNotificationSlidePagerAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        private val fragmentsArrayList = arrayListOf<Fragment>()
        override fun createFragment(position: Int) = fragmentsArrayList[position]
        override fun getItemCount() = fragmentsArrayList.size

        fun addNewFragment() {
            val fragment = CreateNotificationFragment(fragmentsArrayList.size + 1)
            fragmentsArrayList.add(fragment)
            notifyDataSetChanged()
        }

        fun removeLastFragment() {
            fragmentsArrayList.removeLast()
            notifyDataSetChanged()
        }
    }
}