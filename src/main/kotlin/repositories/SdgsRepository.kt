package org.delcom.repositories

import org.delcom.dao.SdgsDAO
import org.delcom.entities.Sdgs
import org.delcom.helpers.daoToModelSdgs
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.SdgsTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class SdgsRepository : ISdgsRepository {

    override suspend fun getSdgs(search: String): List<Sdgs> = suspendTransaction {
        if (search.isBlank()) {
            SdgsDAO.all()
                .orderBy(SdgsTable.nomor to SortOrder.ASC)
                .limit(20)
                .map(::daoToModelSdgs)
        } else {
            val keyword = "%${search.lowercase()}%"
            SdgsDAO
                .find {
                    SdgsTable.nama.lowerCase() like keyword
                }
                .orderBy(SdgsTable.nomor to SortOrder.ASC)
                .limit(20)
                .map(::daoToModelSdgs)
        }
    }

    override suspend fun getSdgsById(id: String): Sdgs? = suspendTransaction {
        SdgsDAO
            .find { SdgsTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::daoToModelSdgs)
            .firstOrNull()
    }

    override suspend fun getSdgsByNomor(nomor: Int): Sdgs? = suspendTransaction {
        SdgsDAO
            .find { SdgsTable.nomor eq nomor }
            .limit(1)
            .map(::daoToModelSdgs)
            .firstOrNull()
    }

    override suspend fun addSdgs(sdgs: Sdgs): String = suspendTransaction {
        val sdgsDAO = SdgsDAO.new {
            nomor = sdgs.nomor
            nama = sdgs.nama
            label = sdgs.label
            pathGambar = sdgs.pathGambar
            createdAt = sdgs.createdAt
            updatedAt = sdgs.updatedAt
        }
        sdgsDAO.id.value.toString()
    }

    override suspend fun updateSdgs(id: String, newSdgs: Sdgs): Boolean = suspendTransaction {
        val sdgsDAO = SdgsDAO
            .find { SdgsTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (sdgsDAO != null) {
            sdgsDAO.nomor = newSdgs.nomor
            sdgsDAO.nama = newSdgs.nama
            sdgsDAO.label = newSdgs.label
            sdgsDAO.pathGambar = newSdgs.pathGambar
            sdgsDAO.updatedAt = newSdgs.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeSdgs(id: String): Boolean = suspendTransaction {
        val rowsDeleted = SdgsTable.deleteWhere {
            SdgsTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}