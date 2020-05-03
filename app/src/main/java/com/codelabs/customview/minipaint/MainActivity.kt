package com.codelabs.customview.minipaint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myCanvasView = MyCanvasView(this)

        // Request the full screen for the layout
        myCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        // Setup content description
        myCanvasView.contentDescription = getString(R.string.canvasContentDescription)
        // Set content view to myCanvasView.
        setContentView(myCanvasView)

    }
}
