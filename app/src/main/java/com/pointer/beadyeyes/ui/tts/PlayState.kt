package com.pointer.beadyeyes.ui.tts

enum class PlayState (val state: String) {
    PLAY("재생 중"),
    WAIT("일시정지"),
    STOP("멈춤");

    fun isStopping(): Boolean {
        return this == STOP
    }

    fun isWaiting(): Boolean {
        return this == WAIT
    }

    fun isPlaying(): Boolean {
        return this == PLAY
    }
}