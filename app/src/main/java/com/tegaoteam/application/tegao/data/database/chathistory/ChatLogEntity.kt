package com.tegaoteam.application.tegao.data.database.chathistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ChatLogEntity.TABLE_NAME)
data class ChatLogEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COL_ID) val id: Long,
    @ColumnInfo(name = COL_SENDTIME) val sendAtTime: String,
    @ColumnInfo(name = COL_SENDTEXT) val sendText: String,
    @ColumnInfo(name = COL_REPLYTIME) var replyAtTime: String,
    @ColumnInfo(name = COL_REPLYTEXT) var replyText: String
) {
    companion object {
        const val TABLE_NAME = "chat_histories"
        const val COL_ID = "chatId"
        const val COL_SENDTIME = "userSendTime"
        const val COL_SENDTEXT = "userText"
        const val COL_REPLYTIME = "botReplyTime"
        const val COL_REPLYTEXT = "botText"
    }
}