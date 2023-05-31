package com.example.practiceeng.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.APIHandler
import com.example.practiceeng.DictionaryAPI
import com.example.practiceeng.R
import com.example.practiceeng.WordCard
import com.example.practiceeng.databinding.FragmentWordSearchBinding
import com.example.practiceeng.ui.SearchListAdapter
import com.example.practiceeng.ui.viewmodels.AddWordCardViewModel
import com.example.practiceeng.ui.viewmodels.SearchViewModel
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.math.log


class WordSearchFragment : Fragment() {
    private var _binding: FragmentWordSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWordSearchBinding.inflate(layoutInflater, container, false)
        binding.cardsList.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            addWordsButton.setOnClickListener {
               val action = WordSearchFragmentDirections.addWordCard(binding.wordSearch.query.toString(), null, null,null,null,null, null, null)
                findNavController().navigate(action)
            }

            wordSearch.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //TODO: remove logging
                    val executor = Executors.newSingleThreadExecutor()
                    val future = executor.submit(Callable {
                        APIHandler.getCards(
                            query.toString(),
                            DictionaryAPI.ALL
                        )
                    })
                    val result = future.get()
                    Log.d("WordSearchFragment", result[0].toString())
                    searchViewModel.setList(result)
                    if (result.size == 0) {
                        cardsList.visibility = RecyclerView.GONE
                        textView.visibility = RecyclerView.VISIBLE
                        textView.setText(R.string.search_error)
                    } else {
                        showResultsAtRecyclerView()
                    }

                    val audio = MediaPlayer.create(
                        context,
                        "https://download.xfd.plus/xfed/audio/33965_en-us-extraordinary.ogg".toUri()
                    )
                    audio?.setOnPreparedListener {
                        audio?.start()
                    }
                    audio?.setOnCompletionListener {
                        audio?.stop()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
        }
        showResultsAtRecyclerView()
    }

    private fun showResultsAtRecyclerView() {
        binding.apply {
            textView.visibility = RecyclerView.GONE
            cardsList.visibility = RecyclerView.VISIBLE
            searchViewModel.getList()?.let {
                val adapter : SearchListAdapter =
                    SearchListAdapter(it.toList(), { card: WordCard ->
                        findNavController().navigate(
                            WordSearchFragmentDirections.addWordCard(
                                card.wordString(),
                                card.partOfSpeech,
                                card.definition,
                                card.examples,
                                card.synonyms,
                                card.antonyms,
                                card.folderID,
                                card.cardID
                            )
                        )
                    })
                adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                cardsList.adapter = adapter
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
       _binding=null
    }

}


