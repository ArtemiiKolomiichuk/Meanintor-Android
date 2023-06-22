package com.example.practiceeng.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.databinding.ItemArrayStringBinding


class StringHolder(open val binding: ItemArrayStringBinding) : RecyclerView.ViewHolder(binding.root) {
    open fun bind(
        string: String,
        deleteFunction: () -> Unit,
        editFunction: (String) -> Unit
    ) {
        binding.apply {
           name.text = string
            delete.setOnClickListener { deleteFunction() }
            edit.setOnClickListener { editFunction(string) }
        }
    }
}

class StringAdapter(
    private val strings: MutableList<String>,
    private val editFunction: (String, Int, StringAdapter) -> Unit
) : RecyclerView.Adapter<StringHolder>() {

    override fun getItemCount(): Int = strings.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StringHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemArrayStringBinding.inflate(inflater, parent, false)
        return StringHolder(binding)
    }

    override fun onBindViewHolder(holder: StringHolder, position: Int) {
        val string = strings[position]
        holder.bind(string, {
            strings.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }, {oldValue:String -> editFunction(oldValue, position, this)})
    }
}