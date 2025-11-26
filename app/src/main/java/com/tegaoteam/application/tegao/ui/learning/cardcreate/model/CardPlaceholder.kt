package com.tegaoteam.application.tegao.ui.learning.cardcreate.model

import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.utils.Time

data class CardPlaceholder(
    var cardId: Long = 0,
    var groupId: Long,
    var type: Int,
    var dateCreated: String? = null,
    var front: String = "",
    var answer: String? = null,
    var back: String = ""
) {
    companion object {
        fun toDomainCardEntry(plc: CardPlaceholder) = CardEntry(
            plc.cardId,
            plc.groupId,
            plc.type,
            plc.dateCreated?: Time.getCurrentTimestamp().toString(),
            plc.front,
            plc.answer,
            plc.back
        )
        fun fromDomainCardEntry(entry: CardEntry) = CardPlaceholder(
            entry.cardId,
            entry.groupId,
            entry.type,
            entry.dateCreated,
            entry.front,
            entry.answer,
            entry.back
        )
        fun parseFromSelectedMaterials(
            materials: CardMaterial,
            groupIds: List<Long>,
            type: Int,
            frontKeys: List<Pair<String, Int>>,
            answer: String?,
            backKeys: List<Pair<String, Int>>
        ): CardPlaceholder {
            val contentPacks = materials.contents
            val groupId = groupIds.first()
            var frontStrings = mutableListOf<String>()
            var backStrings = mutableListOf<String>()
            contentPacks.forEach { pack ->
                val front = frontKeys.filter { it.first == pack.key }.map { pack.value[it.second] }
                val back = backKeys.filter { it.first == pack.key }.map { pack.value[it.second] }
                when (pack.key) {
                    "reading", "furigana", "kunyomi", "onyomi" -> {
                        front.joinToString("、").takeUnless { it.isEmpty() }?.let { frontStrings.add(it) }
                        back.joinToString("、").takeUnless { it.isEmpty() }?.let { backStrings.add(it) }
                    }
                    else -> {
                        frontStrings.addAll(front)
                        backStrings.addAll(back)
                    }
                }
            }
            return CardPlaceholder(
                groupId = groupId,
                type = type,
                front = frontStrings.joinToString("\n"),
                answer = answer,
                back = backStrings.joinToString("\n")
            )
        }
    }
}
