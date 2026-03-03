package su.afk.kemonos.profile.domain

import su.afk.kemonos.profile.domain.login.LoginRemoteResult
import su.afk.kemonos.profile.domain.register.RegisterRemoteResult

interface IAuthRepository {
    suspend fun register(username: String, password: String): RegisterRemoteResult
    suspend fun login(username: String, password: String): LoginRemoteResult
    suspend fun logout(): Boolean
}
