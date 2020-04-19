package com.immotef.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.immotef.onboarding.mvvm.OnBoardingViewModel
import kotlinx.android.synthetic.main.fragment_onboarding.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module


class OnboardingFragment : Fragment() {

    private val viewModel: OnBoardingViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    private val adapter by lazy {
        OnboardingAdapter(requireActivity())
    }

    private val onbardingPages = 3;
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = adapter

        dotsIndicator.setViewPager2(viewPager)

        fabOnboarding.setOnClickListener {
            reactOnFabClick()
        }
    }

    private fun reactOnFabClick() {
        if (viewPager.currentItem == onbardingPages - 1) {
            viewModel.finishOnBoarding()
        } else {
            viewPager.setCurrentItem(viewPager.currentItem + 1, true)
        }
    }

    private data class FragmentInfo(val title: Int, val text: Int, val image: String)

    private inner class OnboardingAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        private val listOfTexts = listOf(
            FragmentInfo(R.string.title1, R.string.onboarding_text_1, "file:///android_asset/ic_onboarding_1.png"),
            FragmentInfo(R.string.title2, R.string.onboarding_text_2, "file:///android_asset/ic_onboarding_2.png"),
            FragmentInfo(R.string.title3, R.string.onboarding_text_3, "file:///android_asset/ic_onboarding_3.png")
        )

        override fun getItemCount(): Int = onbardingPages

        override fun createFragment(position: Int): Fragment =
            BoardingFragment.newInstance(listOfTexts[position].title, listOfTexts[position].text, listOfTexts[position].image)
    }
}


val onboardingModule = module {
    viewModel { OnBoardingViewModel(get(), get()) }
    single { OnBoardingState() }
    factory<OnBoardingStateTrigger> { get<OnBoardingState>() }
    factory<OnBoardingStateListener> { get<OnBoardingState>() }
}