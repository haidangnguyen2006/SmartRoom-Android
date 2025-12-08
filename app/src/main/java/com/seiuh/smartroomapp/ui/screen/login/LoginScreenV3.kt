package com.seiuh.smartroomapp.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.seiuh.smartroomapp.R // Đảm bảo import R
import com.seiuh.smartroomapp.ui.composable.LimeButton
import com.seiuh.smartroomapp.ui.theme.*
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun LoginScreenV3(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loginEvent.collect { event ->
            if (event is LoginEvent.NavigateToHome) {
                navController.navigate("home") { popUpTo("login") { inclusive = true } }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. BACKGROUND IMAGE (FULLSCREEN)
        Image(
            // Bạn nhớ thêm ảnh bg_login vào drawable nhé
            // Nếu chưa có ảnh, tạm thời dùng một Box màu đen hoặc gradient
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            contentScale = ContentScale.Crop, // Cắt ảnh để lấp đầy màn hình
            modifier = Modifier.fillMaxSize()
        )

        // 2. GRADIENT OVERLAY (Lớp phủ mờ)
        // Giúp text dễ đọc hơn bằng cách làm tối phần dưới và sáng phần trên
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,        // Trên cùng trong suốt để thấy ảnh
                            RichBlack.copy(alpha = 0.6f), // Giữa hơi tối
                            RichBlack.copy(alpha = 0.95f) // Dưới cùng gần như đen đặc để làm nền cho form
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // 3. CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding() // Tránh tai thỏ/status bar
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(0.4f)) // Đẩy Logo xuống một chút

            // --- LOGO AREA ---
            // Logo Icon
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo_login_foregound),
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp),
                // Nếu logo là SVG đen, dùng dòng dưới để tô màu Neon. Nếu là PNG màu thì xóa dòng này.
                colorFilter = ColorFilter.tint(LimeGreen)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Smart Room",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = White
            )

            Text(
                text = "Future living experience",
                style = MaterialTheme.typography.bodyMedium,
                color = LightGray.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.weight(0.6f)) // Khoảng cách linh hoạt

            // --- FORM AREA ---
            // Form nằm trực tiếp trên nền (đã có Gradient Overlay đỡ phía sau)

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Username
                NeoTextField(
                    value = uiState.username,
                    onValueChange = viewModel::onUsernameChanged,
                    placeholder = "Username or Email",
                    icon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // Password
                NeoTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChanged,
                    placeholder = "Password",
                    icon = Icons.Default.Lock,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(image, contentDescription = null, tint = MutedGray)
                        }
                    }
                )

                // Forgot Password & Remember Me Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Remember Me (Custom Checkbox Row)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.onRememberMeChanged(!uiState.rememberMe) }
                    ) {
                        Checkbox(
                            checked = uiState.rememberMe,
                            onCheckedChange = viewModel::onRememberMeChanged,
                            colors = CheckboxDefaults.colors(
                                checkedColor = LimeGreen,
                                checkmarkColor = RichBlack,
                                uncheckedColor = MutedGray
                            ),
                            modifier = Modifier.scale(0.8f) // Nhỏ lại chút cho đẹp
                        )
                        Text(
                            text = "Remember me",
                            style = MaterialTheme.typography.bodySmall,
                            color = LightGray
                        )
                    }

                    // Forgot Password
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.labelMedium,
                        color = LimeGreen,
                        modifier = Modifier.clickable { /* TODO */ }
                    )
                }
            }

            // Error Message
            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    color = Danger,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button (Nút to, full width)
            LimeButton(
                text = "LOGIN TO SYSTEM",
                onClick = viewModel::onLoginClicked,
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(text = "New user? ", color = MutedGray, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Create Account",
                    color = White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
        }
    }
}

// Helper để scale Checkbox nếu chưa có
private fun Modifier.scale(scale: Float) = this.then(
    graphicsLayer(scaleX = scale, scaleY = scale)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    // [THÊM MỚI] Các tham số để xử lý mật khẩu
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null // Slot cho nút mắt
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = MutedGray) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MutedGray) },
        // [SỬA] Dùng tham số truyền vào
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Charcoal, // Màu nền đậm hơn chút
            unfocusedContainerColor = Charcoal,
            focusedBorderColor = LimeGreen,
            unfocusedBorderColor = Color.Transparent, // Không viền khi chưa focus cho sạch
            focusedTextColor = White,
            unfocusedTextColor = White,
            cursorColor = LimeGreen
        )
    )
}