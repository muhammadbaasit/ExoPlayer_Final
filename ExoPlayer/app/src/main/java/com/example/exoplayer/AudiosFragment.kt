package com.example.exoplayer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.Adapter.AudioAdapter
import com.example.exoplayer.Adapter.VideoAdapter
import com.example.exoplayer.Utils.MediaLibrary

class AudiosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_audios, container, false)
        setAdapter()
        return view
    }

    private fun setAdapter() {

        var rv = view?.findViewById<RecyclerView>(R.id.rvAudioList)
        val audios = MediaLibrary.instance.getAllAudioMediaDetails(requireContext())
        val adapter = AudioAdapter(requireContext(),audios)
        rv?.setLayoutManager(GridLayoutManager(requireContext(), 3))
        rv?.setAdapter(adapter)

    }


    override fun onPause() {
        Log.d("STATE","onPause")
        setAdapter()
        super.onPause()
    }

    override fun onResume() {
        Log.d("STATE","onResume")
        setAdapter()
        super.onResume()
    }
}
