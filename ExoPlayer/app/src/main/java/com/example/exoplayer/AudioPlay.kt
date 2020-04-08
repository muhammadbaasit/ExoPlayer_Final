package com.example.exoplayer

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.Utils.AudioPlayerService
import com.example.exoplayer.Utils.IsPlaying
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_audio_play.*

class AudioPlay : AppCompatActivity() {

    var myService: AudioPlayerService? = null
    var isBound = false
    var player: SimpleExoPlayer? = null
    val serviceClass = AudioPlayerService::class.java
    var serviceIntent= Intent()
    var isPlaying = IsPlaying(this)
    var pos = 0

    private val myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AudioPlayerService.MyLocalBinder
            myService = binder.getService()
            isBound = true
          //  GetBackgroundNotification(applicationContext, myService).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

            initializePlayer()
           // isPlaying = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_play)

        val intent= intent
        pos = intent.getIntExtra("postion_id",0)
        var playing = intent.getBooleanExtra("isPlaying",false)
        Log.d("IS_PLAYING",">>>>"+playing)
        //Log.d("postion_id","id1 "+pos)

        serviceIntent = Intent(applicationContext, serviceClass)
        serviceIntent.putExtra("postion_id",pos)

        if (!isServiceRunning(serviceClass)) {
            toast("Service is starting .")
            startService(serviceIntent)
            bindService(serviceIntent, myConnection, Context.BIND_AUTO_CREATE)
        } else {
            toast("Service already running.")
            bindService(serviceIntent, myConnection, Context.BIND_AUTO_CREATE)
        }

       /* if(isPlaying.isPlayerRunning() == false){
            Log.d("_STATUS","if false >> "+isPlaying.isPlayerRunning())
            startService(serviceIntent)
            bindService(serviceIntent, myConnection, Context.BIND_AUTO_CREATE)
            isPlaying.updatePlayerStatus(true)
        }else{
            Log.d("_STATUS","if true >> "+isPlaying.isPlayerRunning())
         //   unbindService(myConnection)
            stopService(serviceIntent)
            myService?.stopSelf()
            startService(serviceIntent)
            bindService(serviceIntent, myConnection, Context.BIND_AUTO_CREATE)
        }*/



    }

    override fun onStart() {
        super.onStart()

        if (isServiceRunning(serviceClass)) {
            toast("Service is running.")
          //  bindService(serviceIntent, myConnection, Context.BIND_AUTO_CREATE)
        } else {
            toast("Service is stopped.")
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        isPlaying.updatePlayerStatus(true)
        Log.d("_STATUS","onStop >>"+isPlaying.isPlayerRunning())
        super.onStop()
    }

    override fun onDestroy() {
        val serviceClass = AudioPlayerService::class.java
        val serviceIntent = Intent(applicationContext, serviceClass)
        try {
            unbindService(myConnection)
        } catch (e: IllegalArgumentException) {
            Log.w("MainActivity", "Error Unbinding Service.")
        }
        if (isServiceRunning(AudioPlayerService::class.java)) {
            stopService(serviceIntent)
        }
        //isPlaying.updatePlayerStatus(false)
        Log.d("_STATUS","onDestroy >>"+isPlaying.isPlayerRunning())
        super.onDestroy()
    }

    private fun initializePlayer() {
        if(isBound ){
            player = myService?.getplayerInstance()
            playerView2?.player =  player
            player?.playWhenReady = true
            playerView2.showController()
            playerView2?.useController = true
            playerView2?.requestFocus()
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
fun Context.toast(message:String){
    Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
}

