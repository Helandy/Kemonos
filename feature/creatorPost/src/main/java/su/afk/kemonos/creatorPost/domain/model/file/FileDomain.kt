package su.afk.kemonos.creatorPost.domain.model.file

internal data class FileByHashDomain(
    val type: String?,
    val data: FileByHashDataDomain?,
)

internal data class FileByHashDataDomain(
    val file: FileInfoDomain?,
    val fileList: List<String>,
    val password: String?,
)

internal data class FileByPathDomain(
    val type: String?,
    val data: FileByPathDataDomain?,
)

internal data class FileByPathDataDomain(
    val password: String?,
)

internal data class FileInfoDomain(
    val id: Long?,
    val hash: String?,
    val mtime: String?,
    val ctime: String?,
    val mime: String?,
    val ext: String?,
    val added: String?,
    val size: Long?,
    val ihash: String?,
)
