package com.example.practiceeng.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.Folder
import com.example.practiceeng.databinding.ItemFolderBinding


class SavedFolderHolder(open val binding: ItemFolderBinding) : RecyclerView.ViewHolder(binding.root) {
    open fun bind(folder: Folder, navigateFunction: (Folder) -> Unit) {
        binding.apply {
            folderName.text = folder.title
            folderDescription.text = folder.description
            cardsCount.text = "0"
            startTrainingButton.setOnClickListener {navigateFunction(folder)}
        }
    }
}

class SavedFolderAdapter(
    private val folders: List<Folder>,
    private val navigateFunction: (Folder) -> Unit
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
        holder.bind(folder, navigateFunction)
    }
}