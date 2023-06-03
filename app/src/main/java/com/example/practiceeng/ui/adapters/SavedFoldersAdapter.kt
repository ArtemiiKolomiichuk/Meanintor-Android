package com.example.practiceeng.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.CountedFolder
import com.example.practiceeng.Folder
import com.example.practiceeng.database.WordRepository
import com.example.practiceeng.databinding.ItemFolderBinding
import java.util.*


class SavedFolderHolder(open val binding: ItemFolderBinding) : RecyclerView.ViewHolder(binding.root) {
    open fun bind(
        folder: CountedFolder,
        navigateFunction: (UUID, Int) -> Unit,
        trainingFunction: (UUID, Int) -> Unit
    ) {
        binding.apply {
            folderName.text = folder.title
            folderDescription.text = folder.description
            cardsCount.text = folder.amount.toString()

            if(folder.amount==1)
            cardsCountLabel.text = "card"

            startTrainingButton.setOnClickListener {
                trainingFunction(
                    folder.folderID,
                    folder.amount
                )
            }
            binding.root.setOnClickListener {
                navigateFunction(folder.folderID, folder.amount)
            }
        }
    }
}

class SavedFolderAdapter(
    private val folders: List<CountedFolder>,
    private val navigateFunction: (UUID, Int) -> Unit,
    private val trainingFunction: (UUID, Int) -> Unit
) : RecyclerView.Adapter<SavedFolderHolder>() {

    override fun getItemCount(): Int = folders.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavedFolderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFolderBinding.inflate(inflater, parent, false)
        return SavedFolderHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedFolderHolder, position: Int) {
        val folder = folders[position]
        holder.bind(folder, navigateFunction, trainingFunction)
    }
}