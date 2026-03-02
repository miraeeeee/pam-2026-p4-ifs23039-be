package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object SdgsTable : UUIDTable("sdgs") {
    val nomor = integer("nomor").uniqueIndex()
    val nama = varchar("nama", 100)
    val label = varchar("label", 255)
    val pathGambar = varchar("path_gambar", 255)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}