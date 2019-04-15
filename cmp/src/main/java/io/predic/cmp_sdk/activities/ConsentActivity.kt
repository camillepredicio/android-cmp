package io.predic.cmp_sdk.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.orhanobut.hawk.Hawk
import io.predic.cmp_sdk.R
import io.predic.cmp_sdk.databinding.ActivityConsentBinding
import io.predic.cmp_sdk.utils.CMPApp.Companion.appContext
import io.predic.cmp_sdk.utils.Constants
import io.predic.cmp_sdk.utils.Hyperlink
import io.predic.cmp_sdk.viewmodels.ConsentViewModel
import io.predic.cmp_sdk.viewmodels.ConsentViewModel.Companion.checkEUStatus
import io.predic.cmp_sdk.viewmodels.ConsentViewModel.Companion.privacyPolicyHyperlink
import io.predic.cmp_sdk.viewmodels.ConsentViewModel.Companion.privacyPolicyHyperlinkColor
import io.reactivex.disposables.CompositeDisposable

internal class ConsentActivity : AppCompatActivity() {

    private var compositeDisposable = CompositeDisposable()
    private lateinit var viewModel: ConsentViewModel

    companion object {
        lateinit var consentResources: Resources
    }

    /**
     * Lifecycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        consentResources = resources
        val binding: ActivityConsentBinding = DataBindingUtil.setContentView(this, R.layout.activity_consent)
        viewModel = ViewModelProviders.of(this).get(ConsentViewModel::class.java)
        viewModel.apiKey = appContext.appKey

        binding.viewModel = viewModel

        configObservers()
        configUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        Hawk.deleteAll()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) finish()
        }
    }

    /**
     * Used to setup all observers.
     */
    private fun configObservers() {
        val openActivityObserver = Observer<Constants.OpenSettingsStatus> { status ->
            if (status == Constants.OpenSettingsStatus.OpenSettings) {
                val bundle = Bundle()
                bundle.putInt("category", 1)
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtras(bundle)
                startActivityForResult(intent, 0)
            }
        }
        val openPredicioObserver = Observer<Constants.OpenPredicioStatus> { status ->
            if (status == Constants.OpenPredicioStatus.OpenPredicio) {
                val intent = Intent(android.content.Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://www.predic.io/")
                startActivity(intent)
            }
        }
        val closeConsentObserver = Observer<Constants.CloseConsentStatus> { status ->
            if (status == Constants.CloseConsentStatus.CloseConsent) {
                finish()
            }
        }
        val checkEUStatusObserver = Observer<Constants.EUStatus> { status ->
            viewModel.handleLocationStatus(status ?: Constants.EUStatus.Inside)
            configUI()
        }

        viewModel.openPredicio.observe(this, openPredicioObserver)
        viewModel.openSettings.observe(this, openActivityObserver)
        viewModel.closeConsent.observe(this, closeConsentObserver)
        checkEUStatus.observe(this, checkEUStatusObserver)
    }

    /**
     * Used to setup UI with configurations.
     */
    private fun configUI() {
        setHyperlink(
            privacyPolicyHyperlink ?: "",
            privacyPolicyHyperlinkColor ?: resources.getColor(R.color.buttonColor)
        )
    }

    /**
     * Used to set hyperlink in text.
     * Privacy policy text doesn't change, so we look for "privacy policy" part and set hyperlink for it.
     *
     * @param url Sets what url will open when hyperlink pressed.
     */
    private fun setHyperlink(url: String, color: Int) {
        val view = findViewById<TextView>(R.id.privacyPolicyTextView)
        view.text = viewModel.consentText
        if (viewModel.consentText.contains("privacy policy") && !url.isEmpty()) {
            Hyperlink.setClickableHighLightedText(view, "privacy policy", color, View.OnClickListener {
                val intent = Intent(android.content.Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            })
        }
    }
}
