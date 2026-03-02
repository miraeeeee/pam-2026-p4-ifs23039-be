package org.delcom.repositories

import org.delcom.entities.Sdgs

interface ISdgsRepository {
    suspend fun getSdgs(search: String): List<Sdgs>
    suspend fun getSdgsById(id: String): Sdgs?
    suspend fun getSdgsByNomor(nomor: Int): Sdgs?
    suspend fun addSdgs(sdgs: Sdgs): String
    suspend fun updateSdgs(id: String, newSdgs: Sdgs): Boolean
    suspend fun removeSdgs(id: String): Boolean
}