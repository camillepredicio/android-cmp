package io.predic.cmp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import io.predic.cmp_sdk.CMP
import io.predic.cmp_sdk.models.Partner

class MainActivity : AppCompatActivity() {

    lateinit var purposes: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener { view ->
            when (view.id) {
                R.id.button -> openCMPDefault()
            }
        }
        val button2 = findViewById<Button>(R.id.button2)
        button2.setOnClickListener { view ->
            when (view.id) {
                R.id.button2 -> openCMPWorldwide()
            }
        }
        val button3 = findViewById<Button>(R.id.button3)
        button3.setOnClickListener { view ->
            when (view.id) {
                R.id.button3 -> openCMPCallback()
            }
        }
    }

    fun openCMPDefault() {
        val cmp = CMP.instance()
        cmp.initialize(this, "5d0c22a9a2ba0ed8dbb326b99e5dbabb")
        cmp.setPrivacyPolicy("http://www.predic.io/privacy")
        cmp.predicioLogoVisibility = true
        cmp.getConsent(this) { consent ->
            if (consent != null) {
                purposes = consent.purposes ?: emptyList()
            }
        }
    }

    fun openCMPWorldwide() {
        val cmp = CMP.instance()
        cmp.initialize(this, "5d0c22a9a2ba0ed8dbb326b99e5dbabb", false)
        cmp.setPrivacyPolicy("http://www.predic.io/privacy")
    }

    fun openCMPCallback() {
        val cmp = CMP.instance()
        cmp.initialize(this, "5d0c22a9a2ba0ed8dbb326b99e5dbabb") {
            Toast.makeText(this, "NOT IN EU", Toast.LENGTH_SHORT).show()
        }
        cmp.setPrivacyPolicy("http://www.predic.io/privacy")
    }
}
