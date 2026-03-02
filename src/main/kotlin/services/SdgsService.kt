package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.SdgsRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ISdgsRepository
import java.io.File
import java.util.*

class SdgsService(private val sdgsRepository: ISdgsRepository) {

    // Mengambil semua data SDGs
    suspend fun getAllSdgs(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val sdgsList = sdgsRepository.getSdgs(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar SDGs",
            mapOf(Pair("sdgs", sdgsList))
        )
        call.respond(response)
    }

    // Mengambil data SDGs berdasarkan id
    suspend fun getSdgsById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID SDGs tidak boleh kosong!")

        val sdgs = sdgsRepository.getSdgsById(id)
            ?: throw AppException(404, "Data SDGs tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data SDGs",
            mapOf(Pair("sdgs", sdgs))
        )
        call.respond(response)
    }

    // Ambil data request dari multipart form
    private suspend fun getSdgsRequest(call: ApplicationCall): SdgsRequest {
        val sdgsReq = SdgsRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nomor"  -> sdgsReq.nomor  = part.value.trim()
                        "nama"   -> sdgsReq.nama   = part.value.trim()
                        "label"  -> sdgsReq.label  = part.value.trim()
                    }
                }

                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/sdgs/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    sdgsReq.pathGambar = filePath
                }

                else -> {}
            }
            part.dispose()
        }

        return sdgsReq
    }

    // Validasi request
    private fun validateSdgsRequest(sdgsReq: SdgsRequest) {
        val validatorHelper = ValidatorHelper(sdgsReq.toMap())
        validatorHelper.required("nomor", "Nomor SDGs tidak boleh kosong")
        validatorHelper.required("nama", "Nama SDGs tidak boleh kosong")
        validatorHelper.required("label", "Label SDGs tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar SDGs tidak boleh kosong")
        validatorHelper.validate()

        // Validasi nomor harus angka 1–17
        val nomorInt = sdgsReq.nomor.toIntOrNull()
        if (nomorInt == null || nomorInt < 1 || nomorInt > 17) {
            throw AppException(400, "nomor: Nomor SDGs harus berupa angka antara 1 hingga 17")
        }

        val file = File(sdgsReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar SDGs gagal diupload!")
        }
    }

    // Menambahkan data SDGs
    suspend fun createSdgs(call: ApplicationCall) {
        val sdgsReq = getSdgsRequest(call)

        validateSdgsRequest(sdgsReq)

        // Periksa apakah nomor SDGs sudah terdaftar
        val existSdgs = sdgsRepository.getSdgsByNomor(sdgsReq.nomor.toInt())
        if (existSdgs != null) {
            val tmpFile = File(sdgsReq.pathGambar)
            if (tmpFile.exists()) tmpFile.delete()
            throw AppException(409, "SDGs dengan nomor ini sudah terdaftar!")
        }

        val sdgsId = sdgsRepository.addSdgs(sdgsReq.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambahkan data SDGs",
            mapOf(Pair("sdgsId", sdgsId))
        )
        call.respond(response)
    }

    // Mengubah data SDGs
    suspend fun updateSdgs(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID SDGs tidak boleh kosong!")

        val oldSdgs = sdgsRepository.getSdgsById(id)
            ?: throw AppException(404, "Data SDGs tidak tersedia!")

        val sdgsReq = getSdgsRequest(call)

        if (sdgsReq.pathGambar.isEmpty()) {
            sdgsReq.pathGambar = oldSdgs.pathGambar
        }

        validateSdgsRequest(sdgsReq)

        // Periksa nomor duplikat jika nomor diubah
        if (sdgsReq.nomor.toInt() != oldSdgs.nomor) {
            val existSdgs = sdgsRepository.getSdgsByNomor(sdgsReq.nomor.toInt())
            if (existSdgs != null) {
                val tmpFile = File(sdgsReq.pathGambar)
                if (tmpFile.exists()) tmpFile.delete()
                throw AppException(409, "SDGs dengan nomor ini sudah terdaftar!")
            }
        }

        // Hapus gambar lama jika mengupload gambar baru
        if (sdgsReq.pathGambar != oldSdgs.pathGambar) {
            val oldFile = File(oldSdgs.pathGambar)
            if (oldFile.exists()) oldFile.delete()
        }

        val isUpdated = sdgsRepository.updateSdgs(id, sdgsReq.toEntity())
        if (!isUpdated) {
            throw AppException(400, "Gagal memperbarui data SDGs!")
        }

        val response = DataResponse(
            "success",
            "Berhasil mengubah data SDGs",
            null
        )
        call.respond(response)
    }

    // Menghapus data SDGs
    suspend fun deleteSdgs(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID SDGs tidak boleh kosong!")

        val oldSdgs = sdgsRepository.getSdgsById(id)
            ?: throw AppException(404, "Data SDGs tidak tersedia!")

        val oldFile = File(oldSdgs.pathGambar)

        val isDeleted = sdgsRepository.removeSdgs(id)
        if (!isDeleted) {
            throw AppException(400, "Gagal menghapus data SDGs!")
        }

        if (oldFile.exists()) oldFile.delete()

        val response = DataResponse(
            "success",
            "Berhasil menghapus data SDGs",
            null
        )
        call.respond(response)
    }

    // Mengambil gambar SDGs
    suspend fun getSdgsImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val sdgs = sdgsRepository.getSdgsById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(sdgs.pathGambar)

        if (!file.exists()) {
            return call.respond(HttpStatusCode.NotFound)
        }

        call.respondFile(file)
    }
}