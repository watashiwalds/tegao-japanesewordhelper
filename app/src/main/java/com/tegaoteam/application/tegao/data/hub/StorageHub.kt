package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.database.SQLiteDatabase
import com.tegaoteam.application.tegao.domain.repo.StorageRepo

class StorageHub: StorageRepo {
    private val _dictionaryCacheDb = SQLiteDatabase.getInstance().dictionaryCacheDAO

    override suspend fun deleteSearchCaches(): Int {
        return _dictionaryCacheDb.deleteCaches()
    }
}