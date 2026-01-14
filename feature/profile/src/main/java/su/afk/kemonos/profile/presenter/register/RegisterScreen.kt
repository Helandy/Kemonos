package su.afk.kemonos.profile.presenter.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.R.drawable
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.common.util.findActivity
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.register.util.confirmErrorRes
import su.afk.kemonos.profile.presenter.register.util.passwordErrorRes
import su.afk.kemonos.profile.presenter.register.util.usernameErrorRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RegisterScreen(
    viewModel: RegisterViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    LaunchedEffect(viewModel, activity) {
        viewModel.effect.collect { effect ->
            val a = activity ?: return@collect
            when (effect) {
                is RegisterEffect.SavePassword -> {
                    viewModel.savePassword(a, effect.username, effect.password)

                    viewModel.onPasswordSaveFinished()
                }
            }
        }
    }

    BaseScreen(
        isScroll = false,
        isLoading = state.isLoading,
        contentAlignment = Alignment.Center,
        contentPadding = PaddingValues(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val logoRes = when (state.selectSite) {
                SelectedSite.K -> drawable.kemono_logo
                SelectedSite.C -> drawable.coomer_logo
            }

            Image(
                painter = painterResource(id = logoRes),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
                    .padding(bottom = 12.dp),
            )

            /** Заголовок */
            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            /** Карточка */
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(
                        value = state.username,
                        onValueChange = viewModel::onUsernameChange,
                        label = { Text(stringResource(R.string.register_username_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.usernameError != null,
                        supportingText = {
                            state.usernameError?.let { code ->
                                Text(stringResource(usernameErrorRes(code)))
                            }
                        }
                    )

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = { Text(stringResource(R.string.register_password_label)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.passwordError != null,
                        supportingText = {
                            state.passwordError?.let { code ->
                                Text(stringResource(passwordErrorRes(code)))
                            }
                        }
                    )

                    OutlinedTextField(
                        value = state.confirm,
                        onValueChange = viewModel::onConfirmChange,
                        label = { Text(stringResource(R.string.register_confirm_label)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.confirmError != null,
                        supportingText = {
                            state.confirmError?.let { code ->
                                Text(stringResource(confirmErrorRes(code)))
                            }
                        }
                    )

                    /** Text error */
                    state.error?.let {
                        Text(
                            text = state.error?.message ?: stringResource(R.string.login_error_unknown),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = viewModel::onRegisterClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.register_button_create))
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /** login */
            TextButton(
                onClick = viewModel::onNavigateToLoginClick
            ) {
                Text(stringResource(R.string.register_button_login))
            }
        }
    }
}
