// AuthScreen.kt
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smarttodo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

private enum class AuthTab { Login, Register }

@Composable
fun AuthScreen(
    users: MutableList<AuthUser>,
    onAuthenticated: (User) -> Unit,
    onForgotPassword: () -> Unit
) {
    var tab by remember { mutableStateOf(AuthTab.Login) }
    var message by remember { mutableStateOf<String?>(null) }

    Scaffold { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            AppBadge()
            Text(
                text = "SmartTodo",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "로그인하거나 계정을 만들어\n나만의 할 일 관리를 시작해 보세요.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            AuthTabs(tab = tab) {
                tab = it
                message = null
            }

            Spacer(Modifier.height(24.dp))

            when (tab) {
                AuthTab.Login -> LoginForm(
                    users = users,
                    onAuthenticated = onAuthenticated,
                    onForgotPassword = onForgotPassword,
                    onError = { message = it }
                )
                AuthTab.Register -> RegisterForm(
                    existing = users,
                    onRegistered = { user ->
                        users.add(user)
                        message = "회원가입이 완료되었습니다. 로그인 탭에서 로그인해 주세요."
                    },
                    onError = { message = it }
                )
            }

            message?.takeIf { it.isNotBlank() }?.let { text ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AuthTabs(
    tab: AuthTab,
    onChange: (AuthTab) -> Unit
) {
    val titles = listOf("로그인", "회원가입")
    val selectedIndex = if (tab == AuthTab.Login) 0 else 1
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent
    ) {
        titles.forEachIndexed { idx, title ->
            Tab(
                selected = selectedIndex == idx,
                onClick = { onChange(if (idx == 0) AuthTab.Login else AuthTab.Register) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
private fun LoginForm(
    users: List<AuthUser>,
    onAuthenticated: (User) -> Unit,
    onForgotPassword: () -> Unit,
    onError: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                onError("")
            },
            label = { Text("이메일") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = pw,
            onValueChange = {
                pw = it
                onError("")
            },
            label = { Text("비밀번호") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPw = !showPw }) {
                    Text(if (showPw) "숨김" else "표시")
                }
            }
        )
        Button(
            onClick = {
                val trimmedEmail = email.trim()
                if (trimmedEmail.isBlank() || pw.isBlank()) {
                    onError("이메일과 비밀번호를 입력해 주세요.")
                    return@Button
                }
                val user = users.firstOrNull { it.email == trimmedEmail }
                if (user == null || user.password != pw) {
                    onError("이메일 또는 비밀번호가 올바르지 않습니다.")
                } else {
                    onError("")
                    onAuthenticated(
                        User(
                            id = user.email,
                            email = user.email,
                            name = user.name
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text("로그인")
        }

        Spacer(Modifier.height(4.dp))
        TextButton(onClick = onForgotPassword) {
            Text("비밀번호를 잊으셨나요?", textDecoration = TextDecoration.Underline)
        }
    }
}

@Composable
private fun RegisterForm(
    existing: List<AuthUser>,
    onRegistered: (AuthUser) -> Unit,
    onError: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var pw2 by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                onError("")
            },
            label = { Text("이름") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                onError("")
            },
            label = { Text("이메일") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = pw,
            onValueChange = {
                pw = it
                onError("")
            },
            label = { Text("비밀번호 (8자 이상)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = pw2,
            onValueChange = {
                pw2 = it
                onError("")
            },
            label = { Text("비밀번호 확인") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPw = !showPw }) {
                    Text(if (showPw) "숨김" else "표시")
                }
            }
        )
        Button(
            onClick = {
                val trimmedName = name.trim()
                val trimmedEmail = email.trim()

                when {
                    trimmedName.isBlank() || trimmedEmail.isBlank() ||
                            pw.isBlank() || pw2.isBlank() -> {
                        onError("모든 항목을 입력해 주세요.")
                    }
                    pw.length < 8 -> {
                        onError("비밀번호는 8자 이상이어야 합니다.")
                    }
                    pw != pw2 -> {
                        onError("비밀번호와 비밀번호 확인이 일치하지 않습니다.")
                    }
                    existing.any { it.email == trimmedEmail } -> {
                        onError("이미 가입된 이메일입니다.")
                    }
                    else -> {
                        onError("")
                        onRegistered(AuthUser(trimmedName, trimmedEmail, pw))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text("회원가입")
        }
    }
}

@Composable
private fun AppBadge() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Color(0xFF6C63FF)),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
    }
}
