package com.udacity


sealed class ButtonState {
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}

enum class DownloadStatus(val label: String) {
    PROGRESS("Progress"),
    SUCCESS("Success"),
    FAIL("Fail")
}