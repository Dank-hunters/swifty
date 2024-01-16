package com.example.swiftycompanion

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

class NonScrollListView(context: Context, attrs: AttributeSet) : ListView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMeasureSpecCustom = MeasureSpec.makeMeasureSpec(
            Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST
        )
        super.onMeasure(widthMeasureSpec, heightMeasureSpecCustom)
        layoutParams.height = measuredHeight
    }
}