package com.immotef.register

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.immotef.core.extensions.fromHtml
import com.immotef.core.extensions.observe
import com.immotef.network.getApi
import com.immotef.register.mvvm.RegisterUseCase
import com.immotef.register.mvvm.RegisterUseCaseImp
import com.immotef.register.mvvm.RegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val viewModel: RegisterViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showProgressStream.observe(viewLifecycleOwner, Observer {
            registerRefreshLayout.isRefreshing = it
        })

        observe(viewModel.errorWrapperStream) { errorWrapper ->
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.error_occured)
                .setMessage(errorWrapper.text)
                .setPositiveButton(R.string.ok) { d, w ->
                    d.dismiss()
                    errorWrapper.action?.invoke(this)
                }
                .show()
        }
        registerButton.apply {
            setOnClickListener { viewModel.register() }
            isEnabled = acceptPolicySwitch.isChecked
        }
        acceptPolicySwitch.setOnCheckedChangeListener { _, checked -> registerButton.isEnabled = checked }
        registerRefreshLayout.isEnabled = false

        privacy_text.fromHtml(R.string.register_switch_text)
        privacy_text.movementMethod = LinkMovementMethod.getInstance()
    }
}


internal val registrationFragmentModule = module {
    viewModel { RegisterViewModel(get(), get(), get(), get(), get()) }
    factory<RegisterUseCase> { RegisterUseCaseImp(getApi(), get(), get(), get()) }
}