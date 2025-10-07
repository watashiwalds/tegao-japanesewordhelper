package com.tegaoteam.application.tegao.ui.lookup

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.configs.DictionaryConfig
import com.tegaoteam.application.tegao.databinding.ItemChipBinding

class DictionaryChipsAdapter(private val dictList: List<DictionaryConfig.Dict>): RecyclerView.Adapter<DictionaryChipsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ViewHolder private constructor(val binding: ItemChipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DictionaryConfig.Dict) {
            binding.chipButton.text = data.displayName
        }
    }
}