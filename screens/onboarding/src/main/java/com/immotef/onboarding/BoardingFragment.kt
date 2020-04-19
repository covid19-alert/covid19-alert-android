package com.immotef.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.immotef.imageloading.ImageLoader
import kotlinx.android.synthetic.main.fragment_boarding.*
import org.koin.android.ext.android.inject


class BoardingFragment : Fragment() {
    companion object {
        private val TITLE_KEY = "first_key"
        private val TEXT_KEY = "text_key"
        private val ICON_KEY = "icon_key"

        fun newInstance(idTitle: Int, idInformation: Int, idIcon: String): BoardingFragment {
            return BoardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(TITLE_KEY, idTitle)
                    putInt(TEXT_KEY, idInformation)
                    putString(ICON_KEY, idIcon)
                }
            }
        }
    }

    val imageLoader: ImageLoader by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_boarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt(TITLE_KEY)?.apply {
            textInformativeTitle.setText(this)
        }
        arguments?.getInt(TEXT_KEY)?.apply {
            textInformative1.setText(this)
        }
        arguments?.getString(ICON_KEY)?.apply {
            imageLoader.loadImage(this, iconImageView)
        }

    }
}
