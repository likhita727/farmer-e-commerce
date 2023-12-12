package org.likesyou.bensalcie.pushharder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

internal class PagerViewAdapter constructor(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    public override fun getItem(i: Int): Fragment {
        when (i) {
            0 -> {
                val profileFragment: ProfileFragment = ProfileFragment()
                return profileFragment
            }

            1 -> {
                val usersFragment: UsersFragment = UsersFragment()
                return usersFragment
            }

            2 -> {
                val notificationFragment: NotificationFragment = NotificationFragment()
                return notificationFragment
            }
        }
        return ProfileFragment()
    }

    public override fun getCount(): Int {
        return 3
    }
}