package com.immotef.coronavirusblockade

import android.Manifest
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.immotef.core.extensions.openSettingsPage
import com.immotef.core.permission.AppPermission
import com.immotef.core.permission.handlePermission
import com.immotef.core.permission.requestPermission
import com.immotef.dashboard.DashboardStateListener
import com.immotef.dynamic_links.DeepLinkStateListener
import com.immotef.dynamic_links.DynamicLinkManager
import com.immotef.onboarding.OnBoardingStateListener
import com.immotef.register.RegisterStateListener
import com.immotef.reportdialog.mvvm.ReportDialogInfectionViewModel
import com.immotef.reportdialog.registerInfectionStuff
import com.immotef.reportdialog.reportInfectionData
import com.immotef.scanjob.ServiceOpener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_FINE_LOCATION = 1
    private val PERMISSION_REQUEST_BACKGROUND_LOCATION = 2


    private val registerStateListener: RegisterStateListener by inject()
    private val dashboardStateListener: DashboardStateListener by inject()
    private val onBoardingStateListener: OnBoardingStateListener by inject()
    private val dynamicLinkManager: DynamicLinkManager by inject()
    private val deepLinkStateListener: DeepLinkStateListener by inject()
    private val serviceOpener: ServiceOpener by inject()

    private val reportDialogInfectionViewModel: ReportDialogInfectionViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        handleNavigation()


        lifecycleScope.launch {
            deepLinkStateListener.reactOnDeepLinkState().collect {
                reportInfectionData(reportDialogInfectionViewModel, it.id)
            }
        }

        registerInfectionStuff(reportDialogInfectionViewModel)

    }

    private fun handleNavigation() {
        lifecycleScope.launch {
            registerStateListener.userAlreadyRegistered().collect {
                handlePermissions()
                navHostFragment.findNavController().navigate(R.id.action_registerFragment_to_dashboardFragment)
                serviceOpener.openService()
            }
        }
        lifecycleScope.launch {
            registerStateListener.userRegisterWithInfectedState().collect {
                handlePermissions()
                navHostFragment.findNavController().navigate(R.id.action_registerFragment_to_dashboardFragment)
                serviceOpener.openService()
                dynamicLinkManager.handleDeepLink(intent)
            }
        }
        lifecycleScope.launch {
            registerStateListener.onBoardingOpened().collect {
                navHostFragment.findNavController().navigate(R.id.action_registerFragment_to_onboardingFragment)

            }
        }
        lifecycleScope.launch {
            onBoardingStateListener.onBoardingFinished().collect {
                navHostFragment.findNavController().navigateUp()
                handlePermissions()
            }
        }

        lifecycleScope.launch {
            dashboardStateListener.openInfectedPeople().collect {

            }
        }

        lifecycleScope.launch {
            dashboardStateListener.openUrl().collect {
                navHostFragment.findNavController().navigate(
                    R.id.action_dashboardFragment_to_webViewFragment,
                    Bundle().apply { putString(getString(R.string.url_key), it) },
                    null
                )
            }
        }

        lifecycleScope.launch {
            dashboardStateListener.openReportInfection().collect {
                navHostFragment.findNavController().navigate(
                    R.id.action_dashboardFragment_to_reportFlowFragment
                )
            }
        }
    }

    private val permissionLocation = AppPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_FINE_LOCATION)
    private val permissionBackgroundLocation = AppPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, PERMISSION_REQUEST_BACKGROUND_LOCATION)

    private val onGranted: (AppPermission) -> Unit = {
        if (navHostFragment.findNavController().currentDestination?.id != R.id.registerFragment && navHostFragment.findNavController().currentDestination?.id != R.id.onboardingFragment) {
            serviceOpener.openService()
        }
    }
    private val onGrantedFineLocation: (AppPermission) -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            handlePermission(permissionBackgroundLocation, onGranted, onRational)
        } else {
            onGranted(it)
        }
    }

    private val onRationalFine: (AppPermission) -> Unit = {
        serviceOpener.stopService()
        AlertDialog.Builder(this)
            .setTitle(R.string.rational_location_permission_title)
            .setMessage(com.immotef.reportinfection.R.string.rational_location_permission_message)
            .setPositiveButton(R.string.ok) { d, _ ->
                d.dismiss()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission(it.permissionName, it.requestCode)
                }
            }
            .show()
    }

    private val onRational: (AppPermission) -> Unit = {
        serviceOpener.stopService()
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_rational_background_location)
            .setMessage(com.immotef.reportinfection.R.string.permission_rejeceted_permamently_background_message)
            .setPositiveButton(R.string.ok) { d, _ ->
                d.dismiss()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission(it.permissionName, it.requestCode)
                }
            }
            .show()
    }

    private val onRejected: (AppPermission) -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            rejectBacgroundLocationPermission()
        } else {
            rejectLocationPermission()
        }
    }

    private fun handlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            handlePermission(permissionLocation, onGrantedFineLocation, onRationalFine)
        }
    }

    private var onOpenSettingsPage = false
    private val REJECT_KEY = "reject_key"


    private fun handleOpenSettings() {
        onOpenSettingsPage = true
        openSettingsPage()
    }

    override fun onResume() {
        super.onResume()
        if (onOpenSettingsPage) {
            handlePermissions()
            onOpenSettingsPage = false
        }
        dynamicLinkManager.handleDeepLink(intent)
        if (navHostFragment.findNavController().currentDestination?.id != R.id.registerFragment && navHostFragment.findNavController().currentDestination?.id != R.id.onboardingFragment) {
            serviceOpener.openService()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean(REJECT_KEY, onOpenSettingsPage)
        super.onSaveInstanceState(outState, outPersistentState)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        if (savedInstanceState?.getBoolean(REJECT_KEY, false) == true) {
            handlePermissions()
            onOpenSettingsPage = false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_FINE_LOCATION -> {
                handlePermission(permissionLocation, onGrantedFineLocation, onRejected, onRationalFine)
                return
            }
            PERMISSION_REQUEST_BACKGROUND_LOCATION -> {
                handlePermission(permissionBackgroundLocation, onGranted, onRejected, onRational)
                return
            }
        }
    }

    private fun rejectBacgroundLocationPermission() {
        serviceOpener.stopService()
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.permission_rejected_permmamently)
        builder.setMessage(R.string.permission_rejeceted_permamently_background_message)
        builder.setPositiveButton(android.R.string.ok, null)
        builder.setNegativeButton(R.string.permission_go_to_settings) { dialog, which ->
            handleOpenSettings()
        }
        builder.setOnDismissListener { }
        builder.show()
    }

    private fun rejectLocationPermission() {
        serviceOpener.stopService()
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.permission_rejected_permmamently)
        builder.setMessage(R.string.permission_rejeceted_permamently_message)
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.close_app) { dialog, which ->
            finish()
        }
        builder.setNegativeButton(R.string.permission_go_to_settings) { dialog, which ->
            handleOpenSettings()
        }
        builder.setOnDismissListener { }
        builder.show()
    }
}
