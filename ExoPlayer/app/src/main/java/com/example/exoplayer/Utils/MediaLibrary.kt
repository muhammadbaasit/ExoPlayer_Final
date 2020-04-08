package com.example.exoplayer.Utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.exoplayer.Model.AudioEntityMediaDetail
import com.example.exoplayer.Model.VideoEntityMediaDetail
import java.io.File
import java.util.ArrayList


class MediaLibrary private constructor() {

    fun getAllVideoMediaDetails(context: Context): ArrayList<VideoEntityMediaDetail> {

        val fileModelList = ArrayList<VideoEntityMediaDetail>()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(context, "Please provide storage permission", Toast.LENGTH_SHORT).show();
            return fileModelList
        }

        val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.SIZE, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

        val selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO


        val queryUri = MediaStore.Files.getContentUri("external")

        val cr = context.contentResolver

        val cursor = cr.query(queryUri, projection, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC")

        if (cursor != null) {

            for (i in 0 until cursor.count) {

                cursor.moveToPosition(i)
                val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                val path = cursor.getString(dataColumnIndex)

                val file = File(path)
                val file_size = java.lang.Long.parseLong((file.length() / 1024).toString())

                val mediaDetail = VideoEntityMediaDetail()
                //  FileModel fileModel = new FileModel();
                mediaDetail.title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE))

                val videoDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                mediaDetail.duration = videoDuration
                mediaDetail.id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                mediaDetail.path = path
                mediaDetail.type = "Video"
                mediaDetail.actualSize = file_size
                if ((path.toUpperCase().contains(".MP4") || path.toUpperCase().contains(".M4A") || path.toUpperCase().contains(".FMP4")
                                || path.toUpperCase().contains(".WEBM") || path.toUpperCase().contains(".MATROSKA") || path.toUpperCase().contains(".MP3")
                                || path.toUpperCase().contains(".OGG") || path.toUpperCase().contains(".WAV") || path.toUpperCase().contains(".MPEG")
                                || path.toUpperCase().contains(".FLV") || path.toUpperCase().contains(".ADTS") || path.toUpperCase().contains(".FLAC")
                                || path.toUpperCase().contains(".AMR")) && file_size < 50 * 1024) {
                    fileModelList.add(mediaDetail)
                }
            }
            cursor.close()
        }
        return fileModelList
    }

    fun getAllAudioMediaDetails(context: Context) :  ArrayList<AudioEntityMediaDetail>{

        val audiofileModelList = ArrayList<AudioEntityMediaDetail>()

        val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.SIZE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)

        val selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO

        val queryUri = MediaStore.Files.getContentUri("external")

        val cr = context.contentResolver

        val cursor = cr.query(queryUri, projection, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC")

        if (cursor != null) {

            for (i in 0 until cursor.count) {

                cursor.moveToPosition(i)
                val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                val path = cursor.getString(dataColumnIndex)

                val file = File(path)
                val file_size = java.lang.Long.parseLong((file.length() / 1024).toString())

                val mediaDetail = AudioEntityMediaDetail()
                //  FileModel fileModel = new FileModel();
                mediaDetail.title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE))
                val audioDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                mediaDetail.duration = audioDuration
                mediaDetail.id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                mediaDetail.path = path
                mediaDetail.type = "Audio"
                mediaDetail.actualSize = file_size

                if (( path.toUpperCase().contains(".MP3")) && file_size < 50 * 1024) {
                    audiofileModelList.add(mediaDetail)
                }
            }
            cursor.close()
        }

        return audiofileModelList
    }

    companion object {
        val instance = MediaLibrary()
    }
}