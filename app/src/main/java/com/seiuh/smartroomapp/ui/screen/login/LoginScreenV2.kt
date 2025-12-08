package com.seiuh.smartroomapp.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seiuh.smartroomapp.ui.composable.GlassCard
import com.seiuh.smartroomapp.ui.theme.*

@Composable
fun LoginScreenV2(
    navController: NavController,
    viewModel: LoginViewModel // Nhận ViewModel từ AppNavigation
) {
    val uiState by viewModel.uiState.collectAsState()

    // Lắng nghe sự kiện đăng nhập thành công
    LaunchedEffect(Unit) {
        viewModel.loginEvent.collect { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }

    // --- GIAO DIỆN CHÍNH ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDarkGradientV2), // Nền Gradient Tối V2
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Logo / Header (Hiệu ứng Neon Text)
            Text(
                text = "SmartRoom",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonBlueV2 // Màu xanh Neon
                )
            )

            Text(
                text = "Future Living",
                style = MaterialTheme.typography.bodyLarge,
                color = TextGrayV2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
            )

            // 2. Form Container (GlassCard V2)
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp), // Padding bên trong card
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Đăng Nhập",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhiteV2,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 24.dp)
                    )

                    // Username Field
                    GlassTextField(
                        value = uiState.username,
                        onValueChange = viewModel::onUsernameChanged,
                        label = "Tài khoản",
                        icon = Icons.Default.Person
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    var passwordVisible by remember { mutableStateOf(false) }
                    GlassTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        label = "Mật khẩu",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onVisibilityToggle = { passwordVisible = !passwordVisible }
                    )

                    // Error Message
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonOrangeV2, // Màu cam cảnh báo
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Remember Me
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.rememberMe,
                            onCheckedChange = viewModel::onRememberMeChanged,
                            colors = CheckboxDefaults.colors(
                                checkedColor = NeonBlueV2,
                                uncheckedColor = TextGrayV2,
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = "Ghi nhớ đăng nhập",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextWhiteV2
                        )
                    }

                    // Login Button (Neon Button)
                    NeonButton(
                        text = "Log in",
                        onClick = viewModel::onLoginClicked,
                        isLoading = uiState.isLoading,
                        gradient = BlueCyanGradientV2
                    )
                }
            }

            // Footer
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "v2.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = TextGrayV2.copy(alpha = 0.5f)
            )
        }
    }
}

// --- COMPONENTS CỤC BỘ (LOCAL) ---

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    gradient: Brush
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = NeonBlueV2 // Đổ bóng màu Neon
            ),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent // Để hiện Gradient
        ),
        contentPadding = PaddingValues() // Xóa padding mặc định để Box tràn viền
    ) {
        // Box chứa Gradient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonBlueV2 // Icon màu Neon
            )
        },
        trailingIcon = if (isPassword && onVisibilityToggle != null) {
            {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle",
                        tint = TextGrayV2
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),

        // Cấu hình màu sắc cho TextField trên nền tối
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonBlueV2,       // Viền khi focus: Xanh Neon
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f), // Viền thường: Trắng mờ

            focusedLabelColor = NeonBlueV2,        // Label khi focus: Xanh Neon
            unfocusedLabelColor = TextGrayV2,      // Label thường: Xám

            cursorColor = NeonBlueV2,

            focusedTextColor = TextWhiteV2,
            unfocusedTextColor = TextWhiteV2,

            // Nền trong suốt hoặc hơi sáng nhẹ
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
        )
    )
}