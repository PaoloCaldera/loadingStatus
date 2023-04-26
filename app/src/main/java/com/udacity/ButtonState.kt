package com.udacity

// State of the custom button
sealed class ButtonState {
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}

// Status of the download
enum class DownloadStatus(val label: String) {
    PROGRESS("Progress"),
    SUCCESS("Success"),
    FAIL("Fail")
}