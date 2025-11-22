package com.tegaoteam.application.tegao.ui.shared

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager

object BehaviorPreset {
    fun cancelInputWhenTouchOutside(
        motionEvent: MotionEvent?,
        inputView: View,
        focusedView: View?,
        imm: InputMethodManager,
        vararg allowedViews: View?
    ) {
        if (inputView != focusedView) return
        if (motionEvent?.action == MotionEvent.ACTION_DOWN) {
            //all Rect involved with editText and input event
            val allowedRegions = mutableListOf<Rect>()
            val exportedRect = Rect()

            // get allowed region for touch event
            // 'cause each time is a record of different region, exportedRect need to make a new copy each time adding to allowedRegion
            allowedViews.forEach {
                it?.let { view ->
                    view.getGlobalVisibleRect(exportedRect)
                    allowedRegions.add(Rect(exportedRect))
                }
            }

            // check if touch event is inside the excluded zone
            val touchEventOutsideExcludedZones = allowedRegions.none { it.contains(motionEvent.rawX.toInt(), motionEvent.rawY.toInt()) }
            if (touchEventOutsideExcludedZones) {
                // strip the focus out of input view
                inputView.clearFocus()
                // Hide virtual keyboard 'cause Android don't do it automatically
                imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
            }
        }
    }
}