package com.utigernils.autofuely

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.radiobutton.MaterialRadioButton
import com.utigernils.autofuely.data.repository.PreferenceRepository

class SettingsActivity : AppCompatActivity() {

    private lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        preferenceRepository = PreferenceRepository(this)

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

        setupAccordions()
        setupThemeSelection()

        val tvFooter = findViewById<TextView>(R.id.tvFooterMadeBy)
        tvFooter.text = HtmlCompat.fromHtml(
            getString(R.string.footer_made_by),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        tvFooter.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupThemeSelection() {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupTheme)
        val radioSystem = findViewById<MaterialRadioButton>(R.id.radioThemeSystem)
        val radioDark = findViewById<MaterialRadioButton>(R.id.radioThemeDark)
        val radioLight = findViewById<MaterialRadioButton>(R.id.radioThemeLight)

        when (preferenceRepository.getThemeMode()) {
            PreferenceRepository.THEME_LIGHT -> radioLight.isChecked = true
            PreferenceRepository.THEME_DARK -> radioDark.isChecked = true
            else -> radioSystem.isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val (newMode, nightMode) = when (checkedId) {
                R.id.radioThemeLight -> PreferenceRepository.THEME_LIGHT to AppCompatDelegate.MODE_NIGHT_NO
                R.id.radioThemeDark -> PreferenceRepository.THEME_DARK to AppCompatDelegate.MODE_NIGHT_YES
                else -> PreferenceRepository.THEME_SYSTEM to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            if (preferenceRepository.getThemeMode() != newMode) {
                preferenceRepository.setThemeMode(newMode)
                AppCompatDelegate.setDefaultNightMode(nightMode)
            }
        }
    }

    private fun setupAccordions() {
        val settingsContentLayout = findViewById<ViewGroup>(R.id.settings_content_layout)

        val headerAppearance = findViewById<View>(R.id.headerAppearance)
        val contentAppearance = findViewById<View>(R.id.contentAppearance)
        val arrowAppearance = findViewById<ImageView>(R.id.ivArrowAppearance)

        val headerPermissions = findViewById<View>(R.id.headerPermissions)
        val contentPermissions = findViewById<View>(R.id.contentPermissions)
        val arrowPermissions = findViewById<ImageView>(R.id.ivArrowPermissions)

        val headerAbout = findViewById<View>(R.id.headerAbout)
        val contentAbout = findViewById<View>(R.id.contentAbout)
        val arrowAbout = findViewById<ImageView>(R.id.ivArrowAbout)

        data class Accordion(val header: View, val content: View, val arrow: ImageView)

        val accordions = listOf(
            Accordion(headerAppearance, contentAppearance, arrowAppearance),
            Accordion(headerPermissions, contentPermissions, arrowPermissions),
            Accordion(headerAbout, contentAbout, arrowAbout)
        )

        accordions.forEach { accordion ->
            accordion.header.setOnClickListener {
                TransitionManager.beginDelayedTransition(settingsContentLayout)
                val isCurrentlyExpanded = accordion.content.visibility == View.VISIBLE

                accordions.forEach { other ->
                    if (other == accordion) {
                        if (isCurrentlyExpanded) {
                            other.content.visibility = View.GONE
                            other.arrow.animate().rotation(0f).setDuration(200).start()
                        } else {
                            other.content.visibility = View.VISIBLE
                            other.arrow.animate().rotation(180f).setDuration(200).start()
                        }
                    } else {
                        other.content.visibility = View.GONE
                        other.arrow.animate().rotation(0f).setDuration(200).start()
                    }
                }
            }
        }
    }
}
