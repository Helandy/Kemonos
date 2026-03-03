package su.afk.kemonos.profile.presenter.auth

import android.app.Activity
import androidx.credentials.*

internal suspend fun pickPasswordCredential(activity: Activity): PasswordCredential? {
    val request = GetCredentialRequest(
        credentialOptions = listOf(
            GetPasswordOption()
        ),
        preferImmediatelyAvailableCredentials = true
    )

    val response = CredentialManager.create(activity).getCredential(
        context = activity,
        request = request
    )

    return response.credential as? PasswordCredential
}

internal suspend fun savePasswordCredential(
    activity: Activity,
    username: String,
    password: String,
): Result<Unit> {
    return runCatching {
        CredentialManager.create(activity).createCredential(
            context = activity,
            request = CreatePasswordRequest(id = username, password = password)
        )
    }
}
