package com.example.mykotlinapp.ui.components.drawer

import android.graphics.Color
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.mykotlinapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.math.MathUtils
import com.google.android.material.navigation.NavigationView

abstract class BottomDrawerUI {

    abstract val scrimView: View

    abstract val navigationView: NavigationView

    lateinit var bottomSheetBehavior: BottomSheetBehavior<NavigationView>

    fun initializeNavigationDrawer() {
        bottomSheetBehavior = BottomSheetBehavior.from(navigationView)
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        hideDrawer()
    }

    fun showDrawer() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        scrimView.visibility = View.VISIBLE
    }

    fun hideDrawer() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        scrimView.visibility = View.GONE
    }

    private val bottomSheetCallback by lazy {
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val baseColor = Color.BLACK
                // 87% opacity
                val baseAlpha = ResourcesCompat.getFloat(
                    navigationView.context.resources,
                    R.dimen.material_emphasis_high_type
                )
                // Map slideOffset from [-1.0, 1.0] to [0.0, 1.0]
                val offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f
                val alpha = MathUtils.lerp(0f, 255f, offset * baseAlpha).toInt()
                val color = Color.argb(alpha, baseColor.red, baseColor.green, baseColor.blue)
                scrimView.setBackgroundColor(color)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {}
        }
    }

}