package com.utigernils.autofuely

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout)) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
            toolbar.setPadding(
                toolbar.paddingLeft,
                systemBars.top,
                toolbar.paddingRight,
                toolbar.paddingBottom
            )
            
            val contentLayout = findViewById<View>(R.id.settings_content_layout)
            contentLayout?.setPadding(
                (20 * resources.displayMetrics.density).toInt(),
                (20 * resources.displayMetrics.density).toInt(),
                (20 * resources.displayMetrics.density).toInt(),
                systemBars.bottom + (20 * resources.displayMetrics.density).toInt()
            )

            val rootLayout = findViewById<View>(R.id.root_layout)
            rootLayout.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                0
            )
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val tvFooter = findViewById<TextView>(R.id.tvFooterMadeBy)
        tvFooter.text = HtmlCompat.fromHtml(
            getString(R.string.footer_made_by),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        tvFooter.movementMethod = LinkMovementMethod.getInstance()
    }
}
