package com.example.exoplayer

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.Adapter.VideoAdapter
import com.example.exoplayer.Utils.MediaLibrary

class VideosFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_videos, container, false)

        var rv = view.findViewById<RecyclerView>(R.id.rvVideoList)

        val data =  MediaLibrary.instance.getAllVideoMediaDetails(requireContext())
       // Log.d("DATA>>>","data 2"+ audios)
        val adapter = VideoAdapter(requireContext(),data)
        rv.setLayoutManager(GridLayoutManager(requireContext(), 3))
        rv.setAdapter(adapter)

        return  view
    }
}
