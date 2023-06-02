package com.example.practiceeng.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.Folder
import com.example.practiceeng.databinding.ChooseFolderItemBinding
import com.example.practiceeng.databinding.ItemFolderBinding
import com.example.practiceeng.ui.fragments.ChooseFolderFragment


class ChooseFolderHolder(open val binding: ChooseFolderItemBinding) : RecyclerView.ViewHolder(binding.root) {
    open fun bind(folder: Folder, position: Int, selectedPosition: Int) {
        binding.apply {
            folderName.text = folder.title
            folderName.isChecked = (position == selectedPosition)
        }
    }
}

class ChooseFolderAdapter(
    private val folders: List<Folder>,
    private val setResult: () -> Unit
) : RecyclerView.Adapter<ChooseFolderHolder>() {
    var selectedPosition = -1

    override fun getItemCount(): Int = folders.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseFolderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChooseFolderItemBinding.inflate(inflater, parent, false)
        return ChooseFolderHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseFolderHolder, position: Int) {
        val folder = folders[position]
        holder.bind(folder, position, selectedPosition)
        holder.binding.folderName.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val oldPos = selectedPosition
                selectedPosition = holder.bindingAdapterPosition
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPosition)

                ChooseFolderFragment.RESULT_FOLDER_ID = folders[selectedPosition].folderID
                setResult()
            }
        }
    }
}