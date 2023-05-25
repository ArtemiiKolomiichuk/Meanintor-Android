package com.example.practiceeng.fragments

import android.icu.text.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.WordCard
import com.example.practiceeng.databinding.CardItemBinding
import java.util.*


class WordCardHolder(open val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root) {
    open fun bind(card: WordCard) {
        binding.apply {
            word.setText(card.word())
            partOfSpeech.setText(card.partOfSpeech)
            definitionString.setText(card.definition)
            example.setText(card.examples.toString())
            if(card.hasSynonyms()){
                synonyms.setText(card.synonyms.toString())
            } else {
                synonymsDivider.visibility = RecyclerView.GONE
                synonymsTitle.visibility = RecyclerView.GONE
                synonyms.visibility = RecyclerView.GONE
            }
            if(card.hasAntonyms()){
                antonyms.setText(card.antonyms.toString())
            } else {
                antonymsDivider.visibility = RecyclerView.GONE
                antonymsTitle.visibility = RecyclerView.GONE
                antonyms.visibility = RecyclerView.GONE
            }
        }
    }
}

class SearchListAdapter(
    private val cards: List<WordCard>,
) : RecyclerView.Adapter<WordCardHolder>() {

    override fun getItemCount(): Int = cards.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WordCardHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardItemBinding.inflate(inflater, parent, false)
        return WordCardHolder(binding)
    }

    override fun onBindViewHolder(holder: WordCardHolder, position: Int) {
        val card = cards[position]
        holder.bind(card)
    }
}