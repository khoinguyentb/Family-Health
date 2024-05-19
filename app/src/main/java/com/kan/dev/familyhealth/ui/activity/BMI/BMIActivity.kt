package com.kan.dev.familyhealth.ui.activity.BMI

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.databinding.ActivityBmiactivityBinding
import com.kan.dev.familyhealth.ui.fragment.AboutFragment
import com.kan.dev.familyhealth.ui.fragment.CalculatorFragment
import com.kan.dev.familyhealth.ui.fragment.StatisticFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BMIActivity : BaseActivity<ActivityBmiactivityBinding>() {
    override fun setViewBinding(): ActivityBmiactivityBinding {
        return ActivityBmiactivityBinding.inflate(layoutInflater)
    }

    override fun initData() {

    }

    override fun initView() {
        replaceFragment(CalculatorFragment(), "Calculator", R.drawable.home_selector, R.drawable.statics, R.drawable.about)
    }

    override fun initListener() {
        binding.icHome.setOnClickListener {
            replaceFragment(CalculatorFragment(), "Calculator", R.drawable.home_selector, R.drawable.statics, R.drawable.about)
        }
        binding.icStatics.setOnClickListener {
            replaceFragment(StatisticFragment(), "Statics", R.drawable.home, R.drawable.static_selector, R.drawable.about)
        }
        binding.icAbout.setOnClickListener {
            replaceFragment(AboutFragment(), "About", R.drawable.home, R.drawable.statics, R.drawable.about_selector)
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String, img1: Int, img2: Int, img3: Int) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val existingFragment: Fragment? = supportFragmentManager.findFragmentByTag(tag)
        binding.icHome.setImageResource(img1)
        binding.icStatics.setImageResource(img2)
        binding.icAbout.setImageResource(img3)
        if (existingFragment != null) {
            val fragments: List<Fragment> = supportFragmentManager.fragments
            if (fragments != null) {
                for (fragmentInStack in fragments) {
                    transaction.hide(fragmentInStack)
                }
            }
            transaction.show(existingFragment)
        } else {
            transaction.add(R.id.frameFragment, fragment, tag)
        }
        transaction.addToBackStack(tag)
        transaction.commit()
    }
}