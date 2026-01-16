package com.example.myapplication.view
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.DiffUtil
import com.example.myapplication.R
import com.example.myapplication.model.domain.Character

import androidx.paging.PagingDataAdapter

class CharacterAdapter(
    characters: List<Character>, // можно оставить val в конструкторе
    private val onItemClick: (Character) -> Unit
) : RecyclerView.Adapter<CharacterAdapter.ViewHolder>() {

    // Внутреннее изменяемое свойство
    private var charactersList: MutableList<Character> = characters.toMutableList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewAvatar)
        val nameTextView: TextView = view.findViewById(R.id.textViewName)
        val ageTextView: TextView = view.findViewById(R.id.textViewAge)
    }

    fun updateCharacters(newCharacters: List<Character>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = charactersList.size
            override fun getNewListSize() = newCharacters.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return charactersList[oldItemPosition].id == newCharacters[newItemPosition].id
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return charactersList[oldItemPosition] == newCharacters[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        charactersList.clear()
        charactersList.addAll(newCharacters)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return ViewHolder(view)
    }
    private var onItemLongClick: ((Character) -> Boolean)? = null

    fun setOnItemLongClickListener(listener: (Character) -> Boolean) {
        this.onItemLongClick = listener
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = charactersList[position]
        holder.nameTextView.text = character.name
        holder.ageTextView.text = "Age: ${character.age}"

        // Безопасная загрузка изображения
        val url = character.imageUrl.trim().takeIf { it.isNotEmpty() }
        Glide.with(holder.imageView.context)
            .load(url)
            .placeholder(R.drawable.placeholder_avatar)
            .error(R.drawable.error_avatar)
            .into(holder.imageView)

        holder.imageView.contentDescription = "${character.name} avatar"

        holder.itemView.setOnClickListener {
            onItemClick(character)
        }
            holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(character) ?: false
        }
    }

    override fun getItemCount() = charactersList.size // ← и здесь charactersList
}
