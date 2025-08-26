package com.mycompany.firstapp.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycompany.firstapp.adapters.PagerAdapter
import com.mycompany.firstapp.databinding.FragmentPagerBinding


class PagerFragment : Fragment() {

    private lateinit var _binding: FragmentPagerBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPagerBinding.inflate(inflater, container, false)

        binding.viewPager.adapter = PagerAdapter(this)

        return binding.root
    }


}