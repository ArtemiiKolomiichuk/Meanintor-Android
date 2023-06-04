package com.example.practiceeng.ui

import android.app.Activity
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.Folder
import com.example.practiceeng.R
import com.example.practiceeng.ui.adapters.StringAdapter
import kotlinx.coroutines.launch

class StringListAdapter (private val context: Activity, val listName: String, private val arrayList: MutableList<String>):
     ArrayAdapter<String>(context, R.layout.view_string_array_list, arrayOf("")) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.view_string_array_list, null)

        val label : TextView = view.findViewById(R.id.list_header)
        val addButton : ImageButton = view.findViewById(R.id.add_button)
        val listView : RecyclerView = view.findViewById(R.id.dynamic_list)

        label.text = listName
        listView.layoutManager = LinearLayoutManager(context)
        val adapter: StringAdapter = StringAdapter(arrayList)
        listView.adapter = adapter

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Add a new element")
        val alertDialog: AlertDialog = builder.create()
        val dialog_layout: View =
            inflater.inflate(R.layout.dialog_new_string, null)
        val newString = dialog_layout.findViewById<EditText>(R.id.text)
        newString.hint = listName
        alertDialog.setView(dialog_layout)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "Create",
            { _, _ ->
                if(newString.text.isNotEmpty()) {
                    arrayList.add(newString.text.toString())
                    adapter.notifyItemInserted(arrayList.size-1)
                }
            }
        )
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            "Cancel", DialogInterface.OnClickListener { _, _ -> }
        )

        addButton.setOnTouchListener { _, _ ->
            alertDialog.show()
        true}

        return view
    }

}