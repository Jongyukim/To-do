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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var tab by remember { mutableStateOf(AuthTab.Login) }
    Scaffold { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))
            AppBadge()
            Spacer(Modifier.height(12.dp))
            Text("스마트 To-Do", style = MaterialTheme.typography.titleLarge)
            Text("효율적인 일정 관리의 시작", color = Color.Gray)

            Spacer(Modifier.height(20.dp))
            AuthTabs(tab = tab, onChange = { tab = it })

            Spacer(Modifier.height(16.dp))
            when (tab) {
                AuthTab.Login -> LoginForm(onAuthenticated, onForgotPassword)
                AuthTab.Register -> RegisterForm(onAuthenticated)
            }
        }
    }
}

private enum class AuthTab { Login, Register }

@Composable
private fun AuthTabs(tab: AuthTab, onChange: (AuthTab) -> Unit) {
    val titles = listOf("로그인", "회원가입")
    val selectedIndex = if (tab == AuthTab.Login) 0 else 1
    TabRow(selectedTabIndex = selectedIndex, containerColor = Color.Transparent) {
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
    onAuthenticated: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("이메일") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("example@email.com") }
        )
        OutlinedTextField(
            value = pw, onValueChange = { pw = it },
            label = { Text("비밀번호") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPw = !showPw }) { Text(if (showPw) "숨김" else "표시") }
            }
        )
        Button(
            onClick = { if (email.isNotBlank() && pw.length >= 4) onAuthenticated() },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) { Text("로그인") }

        Spacer(Modifier.height(4.dp))
        TextButton(onClick = onForgotPassword) {
            Text("비밀번호를 잊으셨나요?", textDecoration = TextDecoration.Underline)
        }
    }
}

@Composable
private fun RegisterForm(
    onAuthenticated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var pw2 by remember { mutableStateOf("") }
    var showPw by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("이름") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("이메일") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("example@email.com") }
        )
        OutlinedTextField(
            value = pw, onValueChange = { pw = it },
            label = { Text("비밀번호 (8자 이상)") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
        )
        OutlinedTextField(
            value = pw2, onValueChange = { pw2 = it },
            label = { Text("비밀번호 확인") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPw = !showPw }) { Text(if (showPw) "숨김" else "표시") }
            }
        )
        val enabled = name.isNotBlank() && email.isNotBlank() && pw.length >= 8 && pw2 == pw
        Button(
            onClick = { if (enabled) onAuthenticated() },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) { Text("회원가입") }
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