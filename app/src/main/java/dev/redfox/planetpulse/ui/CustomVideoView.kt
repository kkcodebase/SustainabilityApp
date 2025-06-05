package dev.redfox.planetpulse.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class CustomVideoView(context:Context,attrs:AttributeSet) :VideoView(context, attrs){
    // Override onMeasure to adjust the height of the VideoView
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val statusBarHeight = getStatusBarHeight()
        setMeasuredDimension(height * 16 / 9, height + statusBarHeight)
    }

    // Helper method to get the height of the status bar
    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }
}