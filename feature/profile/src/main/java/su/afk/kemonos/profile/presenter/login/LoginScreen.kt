package su.afk.kemonos.profile.presenter.login

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.auth.pickPasswordCredential
import su.afk.kemonos.profile.presenter.auth.savePasswordCredential
import su.afk.kemonos.profile.presenter.login.LoginState.*
import su.afk.kemonos.profile.presenter.login.LoginState.State
import su.afk.kemonos.profile.presenter.login.util.loginPasswordErrorRes
import su.afk.kemonos.profile.presenter.login.util.loginUsernameErrorRes
import su.afk.kemonos.ui.R.drawable.coomer_logo
import su.afk.kemonos.ui.R.drawable.kemono_logo
import su.afk.kemonos.ui.presenter.baseScreen.BaseScreen
import su.afk.kemonos.ui.presenter.baseScreen.CenterBackTopBar
import su.afk.kemonos.ui.preview.KemonosPreviewScreen
import su.afk.kemonos.ui.uiUtils.findActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginScreen(
    state: State,
    effect: Flow<Effect>,
    onEvent: (Event) -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    LaunchedEffect(activity) {
        if (activity != null) onEvent(Event.RequestSavedCredentials)
    }

    HandleLoginEffects(
        effect = effect,
        activity = activity,
        onEvent = onEvent,
    )

    BaseScreen(
        isScroll = false,
        isLoading = state.isLoading,
        contentAlignment = Alignment.Center,
        contentPadding = PaddingValues(24.dp),
        customTopBar = { scrollBehavior ->
            CenterBackTopBar(
                title = stringResource(R.string.login_title),
                onBack = { onEvent(Event.Back) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthLogo(selectSite = state.selectSite)

            Spacer(Modifier.height(12.dp))

            LoginFormCard(state = state, onEvent = onEvent)

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = { onEvent(Event.NavigateToRegisterClick) }
            ) {
                Text(stringResource(R.string.login_button_register))
            }
        }
    }
}

@Composable
private fun HandleLoginEffects(
    effect: Flow<Effect>,
    activity: Activity?,
    onEvent: (Event) -> Unit,
) {
    LaunchedEffect(effect, activity) {
        effect.collect { item ->
            when (item) {
                Effect.PickPassword -> {
                    val a = activity ?: return@collect
                    val cred = runCatching { pickPasswordCredential(a) }.getOrNull() ?: return@collect
                    onEvent(Event.CredentialsPicked(cred.id, cred.password))
                }

                is Effect.SavePasswordAndNavigate -> {
                    val a = activity ?: return@collect
                    runCatching { savePasswordCredential(a, item.username, item.password) }
                    onEvent(Event.PasswordSaveFinished)
                }

                Effect.NavigateToProfile -> onEvent(Event.NavigateToProfile)
            }
        }
    }
}

@Composable
private fun AuthLogo(selectSite: SelectedSite) {
    val logoRes = when (selectSite) {
        SelectedSite.C -> coomer_logo
        SelectedSite.K -> kemono_logo
    }
    Image(
        painter = painterResource(id = logoRes),
        contentDescription = null,
        modifier = Modifier
            .size(72.dp)
            .padding(bottom = 12.dp),
    )
}

@Composable
private fun LoginFormCard(
    state: State,
    onEvent: (Event) -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = state.username,
                onValueChange = { onEvent(Event.UsernameChanged(it)) },
                label = { Text(stringResource(R.string.login_username_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.usernameError != null,
                supportingText = {
                    state.usernameError?.let { code ->
                        Text(
                            text = stringResource(loginUsernameErrorRes(code)),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                },
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = { onEvent(Event.PasswordChanged(it)) },
                label = { Text(stringResource(R.string.login_password_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.passwordError != null,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    val icon = if (passwordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    }
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                        )
                    }
                },
                supportingText = {
                    state.passwordError?.let { code ->
                        Text(
                            text = stringResource(loginPasswordErrorRes(code)),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                },
            )

            state.error?.let {
                Text(
                    text = it.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Button(
                onClick = { onEvent(Event.LoginClick) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(stringResource(R.string.login_button_sign_in))
                }
            }
        }
    }
}

@Preview(name = "LoginScreen")
@Composable
private fun PreviewLoginScreen() {
    KemonosPreviewScreen {
        LoginScreen(
            state = State(
                username = "demo_user",
                password = "password123",
            ),
            effect = emptyFlow(),
            onEvent = {},
        )
    }
}
