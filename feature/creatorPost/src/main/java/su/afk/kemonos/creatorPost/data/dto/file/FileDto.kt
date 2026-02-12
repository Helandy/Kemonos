package su.afk.kemonos.creatorPost.data.dto.file

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.creatorPost.domain.model.file.*

internal data class FileByHashResponseDto(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("data")
    val data: FileByHashDataDto? = null,
) {
    companion object {
        fun FileByHashResponseDto.toDomain(): FileByHashDomain = FileByHashDomain(
            type = type,
            data = data?.toDomain(),
        )
    }
}

internal data class FileByHashDataDto(
    @SerializedName("file")
    val file: FileInfoDto? = null,
    @SerializedName("file_list")
    val fileList: List<String>? = null,
    @SerializedName("password")
    val password: String? = null,
) {
    fun toDomain(): FileByHashDataDomain = FileByHashDataDomain(
        file = file?.toDomain(),
        fileList = fileList.orEmpty(),
        password = password,
    )
}

internal data class FileByPathResponseDto(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("data")
    val data: FileByPathDataDto? = null,
) {
    companion object {
        fun FileByPathResponseDto.toDomain(): FileByPathDomain = FileByPathDomain(
            type = type,
            data = data?.toDomain(),
        )
    }
}

internal data class FileByPathDataDto(
    @SerializedName("password")
    val password: String? = null,
) {
    fun toDomain(): FileByPathDataDomain = FileByPathDataDomain(
        password = password,
    )
}

internal data class FileInfoDto(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("hash")
    val hash: String? = null,
    @SerializedName("mtime")
    val mtime: String? = null,
    @SerializedName("ctime")
    val ctime: String? = null,
    @SerializedName("mime")
    val mime: String? = null,
    @SerializedName("ext")
    val ext: String? = null,
    @SerializedName("added")
    val added: String? = null,
    @SerializedName("size")
    val size: Long? = null,
    @SerializedName("ihash")
    val ihash: String? = null,
) {
    fun toDomain(): FileInfoDomain = FileInfoDomain(
        id = id,
        hash = hash,
        mtime = mtime,
        ctime = ctime,
        mime = mime,
        ext = ext,
        added = added,
        size = size,
        ihash = ihash,
    )
}
