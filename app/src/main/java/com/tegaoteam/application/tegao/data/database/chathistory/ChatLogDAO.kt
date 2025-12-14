package com.tegaoteam.application.tegao.data.database.chathistory

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatLogDAO {
    @Query("""
        select * from ${ChatLogEntity.TABLE_NAME} order by ${ChatLogEntity.COL_ID} DESC limit 20
    """)
    fun getRecentChat(): Flow<List<ChatLogEntity>>

    @Query("""
        select * from ${ChatLogEntity.TABLE_NAME} order by ${ChatLogEntity.COL_ID} DESC limit :startIndex, 20
    """)
    fun getOlderRecentChatAfterIndex(startIndex: Long): Flow<List<ChatLogEntity>>

    @Upsert
    suspend fun upsertChat(chatLog: ChatLogEntity): Long
}