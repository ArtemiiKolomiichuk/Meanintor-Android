package com.example.practiceeng.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.*
import com.example.practiceeng.database.WordRepository
import com.example.practiceeng.databinding.FragmentWordSearchBinding
import com.example.practiceeng.ui.SearchListAdapter
import com.example.practiceeng.ui.viewmodels.SearchViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.Callable
import java.util.concurrent.Executors


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
               val action = com.example.practiceeng.ui.fragments.WordSearchFragmentDirections.addWordCard(binding.wordSearch.query.toString(), null, null,null,null,null, null, null)
                findNavController().navigate(action)
            }
            addWordsButton.visibility = View.GONE

            wordSearch.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //TODO: remove logging
                    if(query.isNullOrBlank())
                        return false
                    binding.addWordsButton.visibility=View.VISIBLE
                    val executor = Executors.newSingleThreadExecutor()
                    val future = executor.submit(Callable {
                        APIHandler.getCards(
                            query.toString(),
                            DictionaryAPI.ALL
                        )
                    })
                    val result = future.get()
                    searchViewModel.setList(result)
                    if (result.size == 0) {
                        cardsList.visibility = RecyclerView.GONE
                        textView.visibility = RecyclerView.VISIBLE
                        textView.setText(R.string.search_error)
                        viewLifecycleOwner.lifecycleScope.launch {
                            WordRepository.get().addOrUpdateWord(Word(query))
                        }
                    } else {
                    Log.d("WordSearchFragment", result[0].toString())
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
                if(it.size>0) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        WordRepository.get().addOrUpdateWord(it.get(0).word)
                    }
                }
                val adapter : SearchListAdapter =
                    SearchListAdapter(it.toList(), { card: WordCard ->
                        findNavController().navigate(
                            com.example.practiceeng.ui.fragments.WordSearchFragmentDirections.addWordCard(
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


