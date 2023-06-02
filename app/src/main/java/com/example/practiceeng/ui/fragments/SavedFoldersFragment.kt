package com.example.practiceeng.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practiceeng.databinding.FragmentSavedFoldersBinding
import com.example.practiceeng.ui.adapters.SavedFolderAdapter
import com.example.practiceeng.ui.viewmodels.SavedFoldersViewModel
import kotlinx.coroutines.launch
import java.util.*


class SavedFoldersFragment : Fragment() {
    private val savedFoldersViewModel: SavedFoldersViewModel by viewModels()
    private var _binding:FragmentSavedFoldersBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedFoldersBinding.inflate(inflater, container, false)
        binding.savedFoldersList.layoutManager = LinearLayoutManager(context)
        binding.savedFoldersList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                savedFoldersViewModel.folders.collect { list ->
                    binding.savedFoldersList.adapter = SavedFolderAdapter(list) { it ->
                        Toast.makeText(context, it.folderID.toString(), Toast.LENGTH_SHORT)
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