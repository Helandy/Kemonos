package su.afk.kemonos.profile.presenter.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import su.afk.kemonos.common.R.drawable.coomer_logo
import su.afk.kemonos.common.R.drawable.kemono_logo
import su.afk.kemonos.common.presenter.baseScreen.BaseScreen
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.profile.R
import su.afk.kemonos.profile.presenter.login.util.loginPasswordErrorRes
import su.afk.kemonos.profile.presenter.login.util.loginUsernameErrorRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                SelectedSite.C -> coomer_logo
                SelectedSite.K -> kemono_logo
            }

            Image(
                painter = painterResource(id = logoRes),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
                    .padding(bottom = 12.dp),
            )

            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            /** Карточка логина */
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
                    /** Username */
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = viewModel::onUsernameChange,
                        label = { Text(stringResource(R.string.login_username_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = state.usernameError != null,
                        supportingText = {
                            state.usernameError?.let { code ->
                                Text(
                                    text = stringResource(loginUsernameErrorRes(code)),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    )

                    /** Password с переключением видимости */
                    var passwordVisible by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
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
                                    contentDescription = null
                                )
                            }
                        },
                        supportingText = {
                            state.passwordError?.let { code ->
                                Text(
                                    text = stringResource(loginPasswordErrorRes(code)),
                                    style = MaterialTheme.typography.bodySmall
                                )
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

                    /** Кнопка "Войти" */
                    Button(
                        onClick = viewModel::onLoginClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.login_button_sign_in))
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /** Register */
            TextButton(
                onClick = viewModel::onNavigateToRegisterClick
            ) {
                Text(stringResource(R.string.login_button_register))
            }
        }
    }
}