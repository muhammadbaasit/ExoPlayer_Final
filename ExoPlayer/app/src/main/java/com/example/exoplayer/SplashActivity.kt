package com.example.exoplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.Utils.IsPlaying
import com.example.exoplayer.Utils.Utility

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var isPlaying = IsPlaying(this)
        isPlaying.updatePlayerStatus(false)
        Log.d("_STATUS","Splash update >> "+isPlaying.isPlayerRunning())

        funInit()
    }

    private fun funInit() {
        Handler().postDelayed(Runnable {
            if (!Utility.checkStoragePermission(this))
                Utility.requestStoragePermission(this)
            else {
                val gotoNext = Intent(this, MediaListActivity::class.java)
                startActivity(gotoNext)
                finish()
            }
        }, 3000)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            402 -> {
                if (grantResults.size > 0) {
                    val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (permissionGranted) {
                        val gotoNext = Intent(this, MediaListActivity::class.java)
                        startActivity(gotoNext)
                        finish()
                    } else {
                        Toast.makeText(this, "Permission Denied! Cannot load images.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}