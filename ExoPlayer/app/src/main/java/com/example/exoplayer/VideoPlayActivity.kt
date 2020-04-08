package com.example.exoplayer

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.GestureDetector
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.Model.VideoEntityMediaDetail
import com.example.exoplayer.Utils.MediaLibrary
import com.example.exoplayer.Utils.OnSwipeTouchListener
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*


class VideoPlayActivity : AppCompatActivity(){

    private lateinit var player: SimpleExoPlayer
    private lateinit var trackSelector: DefaultTrackSelector
    var gestureDetector:GestureDetector ?= null
    private var shouldAutoPlay: Boolean = false
    var dta = ArrayList<VideoEntityMediaDetail>()
    var position = 0
    var orientation:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data =  MediaLibrary.instance.getAllVideoMediaDetails(this)
        dta = data
        val _intent = intent
        position = _intent.getIntExtra("postion_id",0)

    }

    fun releasePlayer(){
        player.release()
        shouldAutoPlay = player.playWhenReady
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }

    override fun onStart() {
        initializePlayer()
        super.onStart()
    }
    private fun initializePlayer() {

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        shouldAutoPlay = true
        val bandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory =
            DefaultDataSourceFactory(this, Util.getUserAgent(this, "Exo-Player"))
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val mediaDataSourceFactory = ConcatenatingMediaSource()
        for(items in dta){
            var path = items.path
            val mediaSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(path))
            mediaDataSourceFactory.addMediaSource(mediaSource)
        }
        playerView?.requestFocus()
        trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
        playerView?.player = player
        playerView.showController()
        playerView.useController = true
        player.playWhenReady = shouldAutoPlay;
        player.prepare(mediaDataSourceFactory)
        player.seekTo(position, C.TIME_UNSET)

        player.addListener(object : Player.EventListener{
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            }
            override fun onSeekProcessed() {
            }
            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            }
            override fun onPlayerError(error: ExoPlaybackException?) {
            }
            override fun onLoadingChanged(isLoading: Boolean) {
            }
            override fun onPositionDiscontinuity(reason: Int) {

                val latestWindowIndex = player.currentWindowIndex
                if(latestWindowIndex != position){
                    position = latestWindowIndex
                }

            }
            override fun onRepeatModeChanged(repeatMode: Int) {
            }
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            }
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            }
        })

        exo_fullScreen.setOnClickListener(View.OnClickListener {

            if(orientation == false){
                requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                var rotation = this.getResources().getConfiguration().orientation
                if(rotation == Configuration.ORIENTATION_LANDSCAPE){
                    window.decorView.apply {
                        systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    }
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
                } else{
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
                }
                orientation = true
            }

            else if(orientation == true){

                window.decorView.apply {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                }
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
                orientation = false
            }


        })

        exo_ScreenLock.setOnClickListener(View.OnClickListener {

            var rotation = this.getResources().getConfiguration().orientation
            if(rotation == Configuration.ORIENTATION_LANDSCAPE){
                window.decorView.apply {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                }
                requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }
            if(rotation == Configuration.ORIENTATION_PORTRAIT){
                window.decorView.apply {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                }
                requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            }

        })

       llPrev.setOnTouchListener(object : OnSwipeTouchListener(this@VideoPlayActivity){

           override fun onDoubleTap() {
               player.seekTo(player.currentPosition - 12000)
           }
           override fun onSwipeTop() {
               val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
               var maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
               var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
               if(currentVolume < maxVolume){
                   audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume+2,0)
               }
           }

           @RequiresApi(Build.VERSION_CODES.P)
           override fun onSwipeBottom() {
               val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
               var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
               var minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)
               if(currentVolume > minVolume){
                   audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                   audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume-2,0)
               }
           }
       })

        llFfd.setOnTouchListener(object : OnSwipeTouchListener(this@VideoPlayActivity){

            override fun onDoubleTap() {
                player.seekTo(player.currentPosition + 12000)
            }
            override fun onSwipeTop() {
                val curBrightnessValue = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                val SysBackLightValue = curBrightnessValue + 25
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, SysBackLightValue)
            }
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onSwipeBottom() {
                val curBrightnessValue = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                val SysBackLightValue = curBrightnessValue - 25
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, SysBackLightValue)
            }
        })
    }
}
