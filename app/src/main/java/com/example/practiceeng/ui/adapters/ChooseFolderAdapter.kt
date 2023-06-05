package com.example.practiceeng.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.Folder
import com.example.practiceeng.databinding.ChooseFolderItemBinding
import com.example.practiceeng.ui.fragments.ChooseFolderFragment
import java.util.*


class ChooseFolderHolder(open val binding: ChooseFolderItemBinding) : RecyclerView.ViewHolder(binding.root) {
    open fun bind(folder: Folder, position: Int, selectedPosition: Int, update: (Boolean) -> Unit) {
        binding.apply {
            folderName.text = folder.title
            folderName.isChecked = (position == selectedPosition)
            folderName.setOnCheckedChangeListener { _, isChecked ->
                update(isChecked)
            }
        }
    }
}

class ChooseFolderAdapter(
    private val folders: List<Folder>,
    private val setResult: () -> Unit,
    val folderID: UUID?
) : RecyclerView.Adapter<ChooseFolderHolder>() {
    var selectedPosition = -1

    init {
        val position = folderID?.let { folders.indexOfFirst { it -> it.folderID == folderID } }
        position?.let { selectedPosition = it }
    }

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
        holder.bind(folder, position, selectedPosition) { isChecked ->
            if (isChecked) {
                notifyItemChanged(selectedPosition)
                selectedPosition = holder.bindingAdapterPosition
                ChooseFolderFragment.RESULT_FOLDER_ID = folders[selectedPosition].folderID
                notifyItemChanged(selectedPosition)
                setResult()
            }
        }

    }
}