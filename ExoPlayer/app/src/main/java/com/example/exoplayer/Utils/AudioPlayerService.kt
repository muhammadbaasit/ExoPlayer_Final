package com.example.exoplayer.Utils

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import com.example.exoplayer.Model.AudioEntityMediaDetail
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.createWithNotificationChannel
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util



class AudioPlayerService : Service() {

    val myBinder = MyLocalBinder()
    var player: SimpleExoPlayer? = null
    var shouldAutoPlay: Boolean = false
    var dta = java.util.ArrayList<AudioEntityMediaDetail>()
    var position = 0
    var playerNotificationManager:PlayerNotificationManager ?= null
    var notificationId:Int = 1234
    var isPlaying = IsPlaying(this)

    override fun onBind(intent: Intent): IBinder? {
        return myBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Send a notification that service is started
        val data =  MediaLibrary.instance.getAllAudioMediaDetails(this)
        dta = data
        val _intent = intent
        position = _intent.getIntExtra("postion_id",0)

         if(isPlaying.isPlayerRunning() == false){
            Log.d("_STATUS","if false >> "+isPlaying.isPlayerRunning())
             startPlayer()
             isPlaying.updatePlayerStatus(true)
        }else{
            Log.d("_STATUS","if true >> "+isPlaying.isPlayerRunning())
             releasePlayer()
             startPlayer()
        }

        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
       /* Toast.makeText(this,"Service destroyed.", Toast.LENGTH_LONG).show()
        stopForeground(true)*/
    }

    fun getplayerInstance(): SimpleExoPlayer? {
        return player
    }

    fun startPlayer() {

        shouldAutoPlay = true
        val bandwidthMeter = DefaultBandwidthMeter()
        var trackSelector: DefaultTrackSelector ?= null
        val dataSourceFactory =
            DefaultDataSourceFactory(this, Util.getUserAgent(this, "Exo-Player"))
        val AudioTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val mediaDataSourceFactory = ConcatenatingMediaSource()

        for(items in dta){
            var path = items.path
            val mediaSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(path))
            mediaDataSourceFactory.addMediaSource(mediaSource)
        }
        trackSelector = DefaultTrackSelector(AudioTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        player?.playWhenReady = shouldAutoPlay
        player?.prepare(mediaDataSourceFactory)
        player?.seekTo(position, C.TIME_UNSET)
        player?.addListener(object : Player.EventListener{
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
                val latestWindowIndex = player?.currentWindowIndex
                if(latestWindowIndex != position){
                    position = latestWindowIndex!!
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {

            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {

                }

            }
        })

        playerNotificationManager = createWithNotificationChannel(this
            ,"notification_id"
            ,R.string.exo_controls_play_description
            ,notificationId
            ,object : PlayerNotificationManager.MediaDescriptionAdapter{

                override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                    return null
                }

                override fun getCurrentContentText(player: Player?): String? {
                    return dta.get(player!!.currentWindowIndex).artistName
                }

                override fun getCurrentContentTitle(player: Player?): String? {
                    return dta.get(player!!.currentWindowIndex).title
                }

                override fun getCurrentLargeIcon(player: Player?, callback: PlayerNotificationManager.BitmapCallback?): Bitmap? {
                    return null
                }


            })
        playerNotificationManager?.setFastForwardIncrementMs(0);
        playerNotificationManager?.setRewindIncrementMs(0);
        playerNotificationManager?.setPlayer(player)
    }

    fun releasePlayer(){
           // player?.release()
            shouldAutoPlay = player!!.playWhenReady
            //player?.stop()
            player = null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    inner class MyLocalBinder : Binder() {
        fun getService() : AudioPlayerService {
            return this@AudioPlayerService
        }
    }
}

