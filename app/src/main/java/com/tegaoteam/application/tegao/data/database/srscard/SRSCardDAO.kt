package com.tegaoteam.application.tegao.data.database.srscard

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SRSCardDAO {
    //region [Normal GET functions]
    @Query("""select * from ${SRSCardGroup.TABLE_NAME}""")
    fun getCardGroups(): Flow<List<SRSCardGroup>>

    @Query("""select * from ${SRSCardEntity.TABLE_NAME} where ${SRSCardGroup.COL_ID} = :groupId""")
    fun getCardsByGroupId(groupId: Long): Flow<List<SRSCardEntity>>

    @Query("""select * from ${SRSCardRepeat.TABLE_NAME} where ${SRSCardEntity.COL_ID} in (:cardIds)""")
    fun getCardRepeatsByCardIds(cardIds: List<Long>): Flow<List<SRSCardRepeat>>
    //endregion

    //region [Normal INSERT/UPSERT functions]
    @Upsert
    suspend fun upsertCardGroup(newGroup: SRSCardGroup): Long

    @Upsert
    suspend fun upsertCard(newCard: SRSCardEntity): Long

    @Upsert
    suspend fun upsertCardRepeat(cardRepeat: SRSCardRepeat): Long
    //endregion

    //region [Raw functions to form TRANSACTION for respective DELETE functions]
    @Query("""delete from ${SRSCardGroup.TABLE_NAME} where ${SRSCardGroup.COL_ID} = :groupId""")
    suspend fun rawDeleteCardGroupById(groupId: Long): Int
    @Query("""select ${SRSCardEntity.COL_ID} from ${SRSCardEntity.TABLE_NAME} where ${SRSCardGroup.COL_ID} = :groupId""")
    fun rawGetCardIdsByGroupId(groupId: Long): List<Long>
    @Query("""delete from ${SRSCardRepeat.TABLE_NAME} where ${SRSCardEntity.COL_ID} in (:cardIds)""")
    suspend fun rawDeleteCardRepeatsByCardIds(cardIds: List<Long>): Int
    @Query("""delete from ${SRSCardEntity.TABLE_NAME} where ${SRSCardEntity.COL_ID} in (:cardIds)""")
    suspend fun rawDeleteCardsByIds(cardIds: List<Long>): Int

    @Transaction
    suspend fun deleteCardGroupById(groupId: Long): Int {
        val relatedCardIds = rawGetCardIdsByGroupId(groupId)
        if (relatedCardIds.isEmpty()) return rawDeleteCardGroupById(groupId)
        rawDeleteCardRepeatsByCardIds(relatedCardIds)
        return rawDeleteCardsByIds(relatedCardIds) + 1
    }

    @Transaction
    suspend fun deleteCardById(cardId: Long): Int {
        rawDeleteCardRepeatsByCardIds(listOf(cardId))
        return rawDeleteCardsByIds(listOf(cardId))
    }
    //endregion
}