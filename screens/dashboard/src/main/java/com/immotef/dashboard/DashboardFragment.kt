package com.immotef.dashboard

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.immotef.core.CoroutineUtils
import com.immotef.core.extensions.*
import com.immotef.dashboard.mvvm.DashboardViewModel
import com.immotef.dashboardrepository.data.InfectionState
import com.immotef.featureflag.FeatureViewManager
import com.immotef.reportrecoverydialog.mvvm.ReportRecoveryViewModel
import com.immotef.reportrecoverydialog.registerRecoveryDialog
import com.immotef.reportrecoverydialog.reportRecovery
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module
import java.util.*
import org.koin.androidx.scope.lifecycleScope as koinScope

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {


    private val viewModel: DashboardViewModel by viewModel()
    private val reportRecoveryViewModel: ReportRecoveryViewModel by viewModel()
    private val dashboardStateTrigger: DashboardStateTrigger by inject()
    private val dashboardStateListener: DashboardStateListener by inject()
    private val coroutineUtils: CoroutineUtils by inject()


    private val featureViewManager: FeatureViewManager by koinScope.inject()

    private val firstLine by lazy { getString(R.string.dasboard_first_line_title) }
    private val firstLineLength by lazy { firstLine.length }
    private val endOfSecondLine by lazy { getString(R.string.dashboard_end_of_second_line) }
    private val thirdLine by lazy { getString(R.string.dashboard_third_line) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        featureViewManager.handleView(view)
        dashboardShareButton.setOnClickListener {
            requireActivity().createShareIntent(getString(R.string.share_text), getString(R.string.share_title), getString(R.string.choose_app_title))
        }
        observeViewModelStreams()
        dashboardWhatCanYouDoLabel.fromHtml(R.string.what_can_you_do)
        dashboardRefreshLayout.setOnRefreshListener {
            viewModel.realodDashboardData()
        }

        dashboardWhatCanYouDoLabel.setOnClickListener {
            coroutineUtils.globalScope.launch {
                dashboardStateTrigger.openUrl(getString(R.string.what_can_you_do_url, Locale.getDefault().language))
            }
        }

        dashboardInfectionLayout.setOnClickListener {
            coroutineUtils.globalScope.launch {
                dashboardStateTrigger.openInfectedPeopleList()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            dashboardStateListener.openRecoveryFlow().collect {
                reportRecovery(reportRecoveryViewModel)
            }
        }
        registerRecoveryDialog(reportRecoveryViewModel)
    }

    private fun observeViewModelStreams() {
        observe(viewModel.dashboardInfectionStateDataStream) { risk ->
            handleIncomingInfectionStatus(risk)
        }
        observe(viewModel.showProgressStream) {
            dashboardRefreshLayout.isRefreshing = it
        }

        observe(viewModel.errorWrapperStream) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.error_occured)
                .setMessage(it.text)
                .setPositiveButton(R.string.ok) { d, w ->
                    d.dismiss()
                    it.action?.invoke(this)
                }
                .show()
        }
    }

    private val animationsList = mutableListOf<ValueAnimator>()
    private val pivotX by lazy { (dashboardHandClock.width / 2).toFloat() }
    private var pivotYValue = 0.0f
    private val pivotY: Float
        get() {
            if (pivotYValue < 2)
                pivotYValue = (dashboardHandClock.height - 10).toFloat()
            return pivotYValue
        }


    private fun handleIncomingInfectionStatus(infectionState: InfectionState) {
        cancelAllAnimation()
        if (infectionState.shouldShowReportButton) {
            dashboardReportInfectionButton2.visibility = View.VISIBLE
            dashboardReportInfectionButton2.setOnClickListener { coroutineUtils.globalScope.launch { infectionState.reportButtonClick(dashboardStateTrigger) } }
            dashboardReportInfectionButton2.setText(infectionState.reportInfectionButtonText)
        } else {
            dashboardReportInfectionButton2.setOnClickListener(null)
            dashboardReportInfectionButton2.visibility = View.GONE
        }


        animateColor(infectionState.circleColorFrom, infectionState.circleColorTo) {
            numberOfInfectedPeople.background.setTint(it)
        }.apply { animationsList.add(this) }

        animateTitle(infectionState)
        animateColor(infectionState.colorFrom, infectionState.colorTo) {
            background.setBackgroundColor(it)
        }.apply { animationsList.add(this) }
        riskMeter.setCurrentValues(infectionState.riskLevel.toFloat())
        animateNumber(infectionState)
        dashboardTotalNumberPeople.text = getString(R.string.dashboard_number_of_people_met, infectionState.allPeopleMet)
        dashboardDocBackIcon.setImageResource(infectionState.backgroundIcon)
        animateInt(riskMeter.lastAngle.toInt() - 90, infectionState.rotation) {
            dashboardHandClock.rotation = it.toFloat()
            dashboardHandClock.pivotX = pivotX
            dashboardHandClock.pivotY = pivotY
        }.apply { animationsList.add(this) }
    }

    private fun cancelAllAnimation() {
        animationsList.forEach { it.cancel() }
        animationsList.removeAll { true }
    }

    private fun animateNumber(infectionState: InfectionState) {
        val previous = try {
            numberOfInfectedPeople.text.toString().toInt()
        } catch (e: Exception) {
            0
        }
        animateInt(previous, infectionState.infectedPeopleMet) {
            numberOfInfectedPeople.text = it.toString()
        }.apply { animationsList.add(this) }
    }

    private fun animateTitle(infectionState: InfectionState) {
        val infectedString = getString(infectionState.textOfRiskBeingInfected)
        if (!infectionState.shouldShowMultilineTitle) {
            dashboardTitleText.text = infectedString
            animateColor(infectionState.riskTextFromColor, infectionState.riskTextColor) {
                ImageViewCompat.setImageTintList(dashboardShareButton, ColorStateList.valueOf(it))
                dashboardTitleText.setTextColor(it)
            }.apply { animationsList.add(this) }
        } else {
            animateColor(infectionState.riskTextFromColor, infectionState.riskTextColor) {
                ImageViewCompat.setImageTintList(dashboardShareButton, ColorStateList.valueOf(it))
                animateSpannable(infectedString, it)
            }.apply { animationsList.add(this) }
        }
    }

    private fun animateSpannable(infectedString: String, it: Int) {
        val spannable = createSpannableString(infectedString, it)
        dashboardTitleText.text = spannable
    }

    private fun createSpannableString(infectedString: String, it: Int): SpannableString =
        SpannableString("$firstLine\n$infectedString $endOfSecondLine\n$thirdLine")
            .apply {
                setSpan(
                    ForegroundColorSpan(it),
                    firstLineLength, firstLineLength + infectedString.length + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

    override fun onDestroyView() {
        cancelAllAnimation()
        super.onDestroyView()
    }
}


internal val dashboardViewModule = module {
    viewModel { DashboardViewModel(get(), get()) }
    scope<DashboardFragment> {
        factory<FeatureViewManager> { DashboardViewFeatureManager(get(), get()) }
    }

}


