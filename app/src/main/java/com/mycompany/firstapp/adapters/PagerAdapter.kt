package com.mycompany.firstapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mycompany.firstapp.activities.fragments.DoneTasksFragment
import com.mycompany.firstapp.activities.fragments.TasksFragment

class PagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val fragmentList = listOf(TasksFragment(), DoneTasksFragment())


    override fun createFragment(position: Int): Fragment {
        return fragmentList[position] as Fragment
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}