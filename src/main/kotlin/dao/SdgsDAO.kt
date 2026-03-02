package org.delcom.dao

import org.delcom.tables.SdgsTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class SdgsDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, SdgsDAO>(SdgsTable)

    var nomor by SdgsTable.nomor
    var nama by SdgsTable.nama
    var label by SdgsTable.label
    var pathGambar by SdgsTable.pathGambar
    var createdAt by SdgsTable.createdAt
    var updatedAt by SdgsTable.updatedAt
}