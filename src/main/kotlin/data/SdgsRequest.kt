package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.Sdgs

@Serializable
data class SdgsRequest(
    var nomor: String = "",
    var nama: String = "",
    var label: String = "",
    var pathGambar: String = "",
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "nomor" to nomor,
        "nama" to nama,
        "label" to label,
        "pathGambar" to pathGambar,
    )

    fun toEntity(): Sdgs = Sdgs(
        nomor = nomor.toInt(),
        nama = nama,
        label = label,
        pathGambar = pathGambar,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now(),
    )
}