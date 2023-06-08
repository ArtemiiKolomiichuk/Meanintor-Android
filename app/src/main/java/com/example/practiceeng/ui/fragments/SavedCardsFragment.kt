package com.example.practiceeng.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
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
import com.example.practiceeng.databinding.FragmentSavedCardsBinding
import com.example.practiceeng.ui.adapters.SavedCardsListAdapter
import com.example.practiceeng.ui.viewmodels.SavedCardsViewModel
import com.example.practiceeng.ui.viewmodels.SavedCardsViewModelFactory
import kotlinx.coroutines.launch


class SavedCardsFragment : Fragment() {
    private var _binding: FragmentSavedCardsBinding? = null
    private val binding get() = _binding!!
    private val args: SavedCardsFragmentArgs by navArgs()
    private lateinit var editDialog: AlertDialog
    private var deleteDialog: AlertDialog? = null
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
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit folder")
        editDialog = builder.create()
        editDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "Cancel", DialogInterface.OnClickListener { _, _ -> }
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            cardsList.visibility = RecyclerView.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    cardsViewModel.folder.collect {
                        it?.let { folder ->
                            folderName.text = folder.title
                            folderDescription.text = folder.description

                            editFolderButton.setOnClickListener(object:OnClickListener{
                                override fun onClick(v: View?) {
                                    editFolderDialog(folder)
                                    editDialog.show()
                                }
                            })

                            deleteFolderButton.setOnClickListener(object:OnClickListener{
                                override fun onClick(v: View?) {
                                    if(deleteDialog==null)
                                        deleteDialog = deleteFolderDialog(folder)
                                    deleteDialog!!.show()
                                }
                            })

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
        }
    }

    fun deleteFolderDialog(folder: Folder):AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete folder")
        val alertDialog: AlertDialog = builder.create()
       alertDialog.setMessage("The folder with all its cards will be deleted\nAre you sure?")
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Delete",
            { _, _ ->
                cardsViewModel.deleteFolder(folder.folderID)
                findNavController().navigateUp()
            }
        )
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "Cancel", DialogInterface.OnClickListener { _, _ -> }
        )

       return alertDialog
    }

    fun editFolderDialog(folder: Folder) {

        val dialog_layout: View =
            layoutInflater.inflate(R.layout.dialog_new_folder, null)
        val text1 = dialog_layout.findViewById<EditText>(R.id.text1)
        val text2 = dialog_layout.findViewById<EditText>(R.id.text2)
        text1.setText(folder.title)
        text2.setText(folder.description)
        editDialog.setView(dialog_layout)
        editDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Save",
            { _, _ ->
                folder.description = text2.text.toString()
                folder.title = text1.text.toString()
                cardsViewModel.updateFolder(folder)
                binding.folderName.text=folder.title
                binding.folderDescription.text=folder.description
            }
        )

        editDialog.setOnShowListener(object: DialogInterface.OnShowListener {
            override fun onShow(dialog: DialogInterface?) {
                val saveButton: Button = editDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                text1.doOnTextChanged { text, _, _, _ -> disableIfEmpty(saveButton, text.toString(), text2.text.toString())  }
                text2.doOnTextChanged { text, _, _, _ -> disableIfEmpty(saveButton, text1.text.toString(), text.toString())  }
            }
        });
    }

    fun disableIfEmpty(button:Button, name:String, description: String){
        button.setEnabled( !(name.isEmpty() || description.isEmpty()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}


