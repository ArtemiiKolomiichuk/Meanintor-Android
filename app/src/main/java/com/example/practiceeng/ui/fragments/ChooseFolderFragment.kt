package com.example.practiceeng.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practiceeng.Folder
import com.example.practiceeng.R
import com.example.practiceeng.database.WordRepository
import com.example.practiceeng.databinding.FragmentChooseFolderBinding
import com.example.practiceeng.ui.adapters.ChooseFolderAdapter
import com.example.practiceeng.ui.viewmodels.ChooseFolderViewModel
import kotlinx.coroutines.launch
import java.util.*

class ChooseFolderFragment : Fragment() {

    companion object {
        val REQUEST_KEY_FOLDER: String = "REQUEST_KEY_FOLDER"
        val BUNDLE_KEY_FOLDER = "BUNDLE_KEY_FOLDER"
        lateinit var RESULT_FOLDER_ID: UUID
        fun newInstance() = ChooseFolderFragment()
    }

    private lateinit var viewModel: ChooseFolderViewModel
    private var _binding: FragmentChooseFolderBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseFolderBinding.inflate(inflater, container, false)
        binding.foldersList.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            newFolder.setOnClickListener {
                newFolderDialog()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.foldersList.collect{list ->
                    val adapter: ChooseFolderAdapter =
                        ChooseFolderAdapter(list) { setResult() }
                    foldersList.adapter = adapter
                    adapter.notifyDataSetChanged()
                    }
                }
            }
        }

    }

    private fun newFolderDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("New folder")
        val alertDialog: AlertDialog = builder.create()
        val dialog_layout: View =
            layoutInflater.inflate(R.layout.fragment_new_folder_dialog, null)
        // Create the text field in the alert dialog...
        val text1 = dialog_layout.findViewById<EditText>(R.id.text1)
        // Create the text field in the alert dialog...
        val text2 = dialog_layout.findViewById<EditText>(R.id.text2)
        alertDialog.setView(dialog_layout)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Create",
            { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.addFolder(Folder(text1.text.toString(), text2.text.toString()))
                    binding.foldersList.adapter?.notifyDataSetChanged()
                }
            }
        )
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "Cancel", DialogInterface.OnClickListener { _, _ -> }
        )
        alertDialog.show()
    }

    public fun setResult() {
        setFragmentResult(
            ChooseFolderFragment.REQUEST_KEY_FOLDER,
            bundleOf(ChooseFolderFragment.BUNDLE_KEY_FOLDER to ChooseFolderFragment.RESULT_FOLDER_ID)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChooseFolderViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}