package com.example.exoplayer.Utils

import android.content.Context

class IsPlaying(context: Context)  {

    var _context = context

     fun updatePlayerStatus(isAudioPlaying: Boolean) {

        val sharedPref = _context.getSharedPreferences("isAudioPlaying", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("isPlaying", isAudioPlaying)
        editor.commit()
    }

     fun isPlayerRunning(): Boolean {
        val sharedPref = _context.getSharedPreferences("isAudioPlaying", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("isPlaying", false)
    }
}