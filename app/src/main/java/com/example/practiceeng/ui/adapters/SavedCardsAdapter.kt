package com.example.practiceeng.ui.adapters

import com.example.practiceeng.VisualWordCard
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceeng.databinding.CardItemBinding


class VisualWordCardHolder(open val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root) {
    open fun bind(card: VisualWordCard, onCardClicked: (card: VisualWordCard) -> Unit) {
        binding.apply {
            word.setText(card.word.word)
            partOfSpeech.setText(card.partOfSpeech)
            definitionString.setText(card.definition)
            if(card.examples.isNotEmpty()) {
                example.setText(card.examples.joinToString("\n"))
            } else
                example.visibility = RecyclerView.GONE
            if(card.synonyms.isNotEmpty()){
                synonyms.setText(card.synonyms.joinToString())
            } else {
                synonymsDivider.visibility = RecyclerView.GONE
                synonymsTitle.visibility = RecyclerView.GONE
                synonyms.visibility = RecyclerView.GONE
            }
            if(card.antonyms.isNotEmpty()){
                antonyms.setText(card.antonyms.joinToString())
            } else {
                antonymsDivider.visibility = RecyclerView.GONE
                antonymsTitle.visibility = RecyclerView.GONE
                antonyms.visibility = RecyclerView.GONE
            }
            root.setOnClickListener {
                onCardClicked(card)
            }
        }
    }
}

class SavedCardsListAdapter(
    private val cards: List<VisualWordCard>, private val onCardClicked: (card: VisualWordCard) -> Unit
) : RecyclerView.Adapter<VisualWordCardHolder>() {

    override fun getItemCount(): Int = cards.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VisualWordCardHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardItemBinding.inflate(inflater, parent, false)
        return VisualWordCardHolder(binding)
    }

    override fun onBindViewHolder(holder: VisualWordCardHolder, position: Int) {
        val card = cards[position]
        holder.bind(card, onCardClicked)
    }
}