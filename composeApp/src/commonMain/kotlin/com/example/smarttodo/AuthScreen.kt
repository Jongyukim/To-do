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

import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onLogin: suspend (String, String) -> Boolean,
    onRegister: suspend (String, String, String?) -> Boolean,
    onAuthenticationSuccess: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var tab by remember { mutableStateOf(AuthTab.Login) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { pad ->
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
                    onLogin = onLogin,
                    onAuthenticationSuccess = onAuthenticationSuccess,
                    onForgotPassword = onForgotPassword,
                    showSnackbar = { message ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                )
                AuthTab.Register -> RegisterForm(
                    onRegister = onRegister,
                    onAuthenticationSuccess = onAuthenticationSuccess,
                    showSnackbar = { message ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
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
    onLogin: suspend (String, String) -> Boolean,
    onAuthenticationSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    showSnackbar: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                IconButton(onClick = { showPw = !showPw }) {
                    Icon(
                        imageVector = if (showPw) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (showPw) "비밀번호 숨김" else "비밀번호 표시"
                    )
                }
            }
        )
        Button(
            onClick = {
                if (email.isNotBlank() && pw.length >= 4) {
                    coroutineScope.launch {
                        val success = onLogin(email, pw)
                        if (success) {
                            onAuthenticationSuccess()
                        } else {
                            showSnackbar("로그인 실패. 이메일 또는 비밀번호를 확인하세요.")
                        }
                    }
                } else {
                    showSnackbar("이메일과 비밀번호를 입력해주세요.")
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
    onRegister: suspend (String, String, String?) -> Boolean,
    onAuthenticationSuccess: () -> Unit,
    showSnackbar: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var pw2 by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                IconButton(onClick = { showPw = !showPw }) {
                    Icon(
                        imageVector = if (showPw) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (showPw) "비밀번호 숨김" else "비밀번호 표시"
                    )
                }
            }
        )
        Button(
            onClick = {
                if (enabled) {
                    coroutineScope.launch {
                        val success = onRegister(email, pw, name.ifBlank { null })
                        if (success) {
                            onAuthenticationSuccess()
                        } else {
                            showSnackbar("회원가입 실패. 다시 시도해주세요.")
                        }
                    }
                } else {
                    showSnackbar("모든 필드를 올바르게 입력해주세요.")
                }
            },
            enabled = enabled,
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
