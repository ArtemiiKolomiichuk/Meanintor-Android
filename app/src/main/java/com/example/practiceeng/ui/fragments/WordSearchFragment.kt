package com.example.practiceeng.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.*
import com.example.practiceeng.database.WordRepository
import com.example.practiceeng.databinding.FragmentWordSearchBinding
import com.example.practiceeng.ui.adapters.CardsListAdapter
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
               val action = WordSearchFragmentDirections.addWordCard(
                   binding.wordSearch.query.toString(),
                   null,
                   null,
                   null,
                   null,
                   null,
                   null,
                   null
               )
                findNavController().navigate(action)
            }
            addWordsButton.visibility = View.GONE

            wordSearch.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //TODO: remove logging
                    if(query.isNullOrBlank())
                        return false
                    binding.addWordsButton.visibility=View.VISIBLE

                    var result: Array<WordCard> = arrayOf()
                    val executor = Executors.newSingleThreadExecutor()
                    val future = executor.submit(Callable {
                        APIHandler.getCards(
                            query.toString().trim(),
                            DictionaryAPI.ALL
                        )
                    })
                    result = future.get()
                    executor.shutdown()

                    searchViewModel.setList(result)
                    if (result.isEmpty()) {
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
                    /*
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
                    */
                    context?.let { Utils.playAudio("33965_en-us-extraordinary.ogg", it) }
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
                val adapter : CardsListAdapter =
                    CardsListAdapter(it.toList(), { card: WordCard ->
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


