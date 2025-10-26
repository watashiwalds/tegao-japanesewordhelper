package com.tegaoteam.application.tegao.ui.shared

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tegaoteam.application.tegao.TegaoApplication

object DisplayHelper {

    object FlexboxLayoutManagerMaker {
        fun rowStart(context: Context): FlexboxLayoutManager {
            return FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
                alignItems = AlignItems.FLEX_START
            }
        }
        fun gridEven(context: Context): FlexboxLayoutManager {
            return FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.SPACE_EVENLY
                alignItems = AlignItems.FLEX_START
            }
        }
    }

    class LinearDividerItemDecoration private constructor(
        private val vertical: Int,
        private val horizontal: Int
    ): RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {

            outRect.bottom = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                vertical.toFloat(),
                TegaoApplication.instance.applicationContext.resources.displayMetrics
            ).toInt()
            outRect.right = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                horizontal.toFloat(),
                TegaoApplication.instance.applicationContext.resources.displayMetrics
            ).toInt()
            //Try remove this if not work
//            super.getItemOffsets(outRect, view, parent, state)
        }

        companion object {
            fun make(verticalDp: Int, horizontalDp: Int): LinearDividerItemDecoration {
                return LinearDividerItemDecoration(verticalDp, horizontalDp)
            }
        }
    }
}