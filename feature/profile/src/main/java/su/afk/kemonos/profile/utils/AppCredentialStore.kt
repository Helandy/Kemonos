package su.afk.kemonos.profile.utils

import android.app.Activity
import androidx.credentials.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCredentialStore @Inject constructor(
    private val credentialManager: CredentialManager
) {
    suspend fun pickPassword(activity: Activity): PasswordCredential? {
        val request = GetCredentialRequest(
            credentialOptions = listOf(GetPasswordOption())
        )

        val response = credentialManager.getCredential(
            context = activity,
            request = request
        )

        return response.credential as? PasswordCredential
    }

    suspend fun savePassword(activity: Activity, username: String, password: String) {
        runCatching {
            credentialManager.createCredential(
                context = activity,
                request = CreatePasswordRequest(id = username, password = password)
            )
        }
    }
}