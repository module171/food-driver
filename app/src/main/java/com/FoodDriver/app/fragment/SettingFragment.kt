package com.FoodDriver.app.fragment

import android.view.View
import com.FoodDriver.app.activity.EditProfileActivity
import com.FoodDriver.app.R
import com.FoodDriver.app.activity.ChangePasswordActivity
import com.FoodDriver.app.activity.DashboardActivity
import com.FoodDriver.app.base.BaseFragmnet
import com.FoodDriver.app.utils.Common.getCurrentLanguage
import com.FoodDriver.app.utils.SharePreference
import kotlinx.android.synthetic.main.fragment_home.ivMenu
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment: BaseFragmnet() {
    override fun setView(): Int {
        return R.layout.fragment_setting
    }
    override fun Init(view: View) {
        getCurrentLanguage(activity!!,false)
        ivMenu.setOnClickListener {
            (activity as DashboardActivity?)!!.onDrawerToggle()
        }

        cvBtnEditProfile.setOnClickListener {
            openActivity(EditProfileActivity::class.java)
        }

        cvBtnPassword.setOnClickListener {
            openActivity(ChangePasswordActivity::class.java)
        }

        llArabic.setOnClickListener {
            SharePreference.setStringPref(activity!!, SharePreference.SELECTED_LANGUAGE,activity!!.resources.getString(R.string.language_hindi))
            getCurrentLanguage(activity!!, true)
        }

        llEnglish.setOnClickListener {
            SharePreference.setStringPref(activity!!, SharePreference.SELECTED_LANGUAGE,activity!!.resources.getString(R.string.language_english))
            getCurrentLanguage(activity!!, true)
        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(activity!!,false)
    }
}