package com.example.practiceeng.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.APIHandler
import com.example.practiceeng.DictionaryAPI
import com.example.practiceeng.R
import com.example.practiceeng.WordCard
import com.example.practiceeng.databinding.FragmentWordSearchBinding
import java.util.concurrent.Callable
import java.util.concurrent.Executors


class WordSearchFragment : Fragment() {
    private var _binding: FragmentWordSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


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
            wordSearch.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    binding.textView.text = "Text submit: " + query
                    //TODO: remove logging

                    var result = mutableListOf<WordCard>()

                    val executor = Executors.newSingleThreadExecutor()
                    val future = executor.submit(Callable {
                        APIHandler.getCards(
                            query.toString(),
                            DictionaryAPI.FreeDictionaryAPI
                        )
                    })
                    result += future.get()
                    val executor2 = Executors.newSingleThreadExecutor()
                    val future2 = executor2.submit(Callable {
                        APIHandler.getCards(
                            query.toString(),
                            DictionaryAPI.WordsAPI
                        )
                    })
                    result += future2.get()
                    for (card in result)
                        Log.d("WordSearchFragment", card.toString())
                    if (result.size == 0) {
                        cardsList.visibility = RecyclerView.GONE
                        textView.visibility = RecyclerView.VISIBLE
                        textView.setText(R.string.search_error)
                    } else {
                        textView.visibility = RecyclerView.GONE
                        cardsList.visibility = RecyclerView.VISIBLE
                        cardsList.adapter = SearchListAdapter(result)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //Log.d("SEARCH", "Query: " + newText)
                    return true
                }

            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}


