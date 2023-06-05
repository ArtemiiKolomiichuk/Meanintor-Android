package com.example.practiceeng.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.*
import com.example.practiceeng.database.WordRepository
import com.example.practiceeng.databinding.FragmentSavedCardsBinding
import com.example.practiceeng.ui.adapters.CardsListAdapter
import com.example.practiceeng.ui.adapters.SavedCardsListAdapter
import com.example.practiceeng.ui.viewmodels.AddWordCardViewModel
import com.example.practiceeng.ui.viewmodels.AddWordCardViewModelFactory
import com.example.practiceeng.ui.viewmodels.SavedCardsViewModel
import com.example.practiceeng.ui.viewmodels.SavedCardsViewModelFactory
import kotlinx.coroutines.launch


class SavedCardsFragment : Fragment() {
    private var _binding: FragmentSavedCardsBinding? = null
    private val binding get() = _binding!!
    private val args: SavedCardsFragmentArgs by navArgs()
    private val cardsViewModel: SavedCardsViewModel by viewModels {
        SavedCardsViewModelFactory(args.folderId)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSavedCardsBinding.inflate(layoutInflater, container, false)
        binding.cardsList.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            cardsList.visibility = RecyclerView.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    cardsViewModel.cards.collect { list ->
                        val adapter: SavedCardsListAdapter =
                            SavedCardsListAdapter(list, { card: VisualWordCard ->
                        findNavController().navigate(
                          SavedCardsFragmentDirections.editWordCard(
                            card.cardID
                            )
                        )
                            })
                        adapter.stateRestorationPolicy =
                            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                        cardsList.adapter = adapter
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}


