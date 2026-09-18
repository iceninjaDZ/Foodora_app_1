package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.UserRole
import com.example.ui.state.FoodoraViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: FoodoraViewModel,
    onLoginSuccess: (UserRole) -> Unit = {}
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var selectedRole by remember { mutableStateOf(UserRole.MANAGER) }
    var authModeIsPin by remember { mutableStateOf(false) }

    // Form states
    var identifier by remember { mutableStateOf("manager@foodora.io") }
    var password by remember { mutableStateOf("admin123") }
    var pinCode by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    // UI Feedback
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    fun executeLogin(role: UserRole, user: String) {
        errorMessage = null
        if (authModeIsPin) {
            if (pinCode.length < 4) {
                errorMessage = when (currentLang) {
                    Language.AR -> "يرجى إدخال رمز PIN المكون من 4 أرقام على الأقل"
                    Language.FR -> "Veuillez entrer un code PIN à 4 chiffres"
                    Language.EN -> "Please enter a valid 4-digit staff PIN code"
                }
                return
            }
        } else {
            if (identifier.isBlank()) {
                errorMessage = when (currentLang) {
                    Language.AR -> "يرجى إدخال اسم المستخدم أو البريد الإلكتروني"
                    Language.FR -> "Veuillez entrer votre email ou nom d'utilisateur"
                    Language.EN -> "Please enter your username, email, or phone"
                }
                return
            }
            if (password.length < 4) {
                errorMessage = when (currentLang) {
                    Language.AR -> "كلمة المرور يجب ألا تقل عن 4 خانات"
                    Language.FR -> "Le mot de passe doit contenir au moins 4 caractères"
                    Language.EN -> "Password must be at least 4 characters"
                }
                return
            }
        }

        isLoading = true
        coroutineScope.launch {
            delay(450) // Smooth tactile response
            isLoading = false
            viewModel.login(role, user)
            onLoginSuccess(role)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF141419),
                        Color(0xFF0F0F12),
                        Color(0xFF0A0A0C)
                    )
                )
            )
    ) {
        // Language switcher chip in top corner
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Language.entries.forEach { lang ->
                val isSelected = currentLang == lang
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) FoodoraOrange else Color(0xFF24242C),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) FoodoraOrange else Color(0xFF33333E)
                    ),
                    modifier = Modifier.clickable { viewModel.setLanguage(lang) }
                ) {
                    Text(
                        text = lang.name,
                        color = if (isSelected) Color.White else Color(0xFFA0A0AB),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Main Centered Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Brand Logo & Title Header
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(FoodoraOrange, Color(0xFFFF7A33))
                        )
                    )
                    .border(2.dp, Color(0xFFFF9E66), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "FOODORA MANAGER",
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                letterSpacing = 1.5.sp,
                color = Color.White
            )

            Text(
                text = when (currentLang) {
                    Language.AR -> "نظام إدارة وتشغيل المطاعم الذكي"
                    Language.FR -> "Système de Gestion & Caisse Restauration"
                    Language.EN -> "Enterprise Restaurant Management & POS Suite"
                },
                fontSize = 12.sp,
                color = Color(0xFFA0A0AB),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Main Authentication Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1C1C24),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E2E3A)),
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Role Selector Tabs
                    Column {
                        Text(
                            text = when (currentLang) {
                                Language.AR -> "اختر الدور الوظيفي للوصول"
                                Language.FR -> "Sélectionnez votre rôle"
                                Language.EN -> "Select Staff / Role Access"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA0A0AB)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val rolesList = listOf(
                            UserRole.OWNER to (if (currentLang == Language.AR) "المدير العام" else "Admin"),
                            UserRole.MANAGER to (if (currentLang == Language.AR) "مدير الفرع" else "Manager"),
                            UserRole.CASHIER to (if (currentLang == Language.AR) "الكاشير / POS" else "Cashier"),
                            UserRole.KITCHEN to (if (currentLang == Language.AR) "المطبخ / KDS" else "Kitchen")
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF141419))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rolesList.forEach { (role, label) ->
                                val isSelected = selectedRole == role
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) FoodoraOrange else Color.Transparent)
                                        .clickable {
                                            selectedRole = role
                                            errorMessage = null
                                            when (role) {
                                                UserRole.OWNER -> {
                                                    identifier = "admin@foodora.io"
                                                    password = "admin123"
                                                    authModeIsPin = false
                                                }
                                                UserRole.MANAGER -> {
                                                    identifier = "manager@foodora.io"
                                                    password = "admin123"
                                                    authModeIsPin = false
                                                }
                                                UserRole.CASHIER -> {
                                                    identifier = "cashier@foodora.io"
                                                    pinCode = "1234"
                                                    authModeIsPin = true
                                                }
                                                UserRole.KITCHEN -> {
                                                    identifier = "kitchen@foodora.io"
                                                    pinCode = "4321"
                                                    authModeIsPin = true
                                                }
                                                else -> Unit
                                            }
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFFA0A0AB),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Demo / Presentation Quick Fill Bar
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF24242E),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF323242))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = FoodoraOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = when (currentLang) {
                                            Language.AR -> "دخول سريع للعرض التجريبي (Demo)"
                                            Language.FR -> "Mode Démo / Présentation Rapide"
                                            Language.EN -> "Quick Demo & Presentation Mode"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "1-Click",
                                    fontSize = 10.sp,
                                    color = FoodoraOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = {
                                        selectedRole = UserRole.MANAGER
                                        identifier = "manager@foodora.io"
                                        password = "admin123"
                                        authModeIsPin = false
                                        executeLogin(UserRole.MANAGER, "Demo Manager")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF363644)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f).testTag("demo_manager_btn")
                                ) {
                                    Text("Manager", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        selectedRole = UserRole.CASHIER
                                        identifier = "cashier@foodora.io"
                                        pinCode = "1234"
                                        authModeIsPin = true
                                        executeLogin(UserRole.CASHIER, "Demo Cashier")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF363644)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f).testTag("demo_cashier_btn")
                                ) {
                                    Text("POS / Cashier", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        selectedRole = UserRole.KITCHEN
                                        identifier = "kitchen@foodora.io"
                                        pinCode = "4321"
                                        authModeIsPin = true
                                        executeLogin(UserRole.KITCHEN, "Demo Kitchen")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF363644)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f).testTag("demo_kitchen_btn")
                                ) {
                                    Text("KDS / Chef", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Mode Switch: Password vs Quick PIN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (authModeIsPin) {
                                if (currentLang == Language.AR) "تسجيل الدخول برمز PIN" else "Staff PIN Access Mode"
                            } else {
                                if (currentLang == Language.AR) "بيانات الدخول" else "Credentials"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA0A0AB)
                        )

                        TextButton(
                            onClick = {
                                authModeIsPin = !authModeIsPin
                                errorMessage = null
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (authModeIsPin) Icons.Default.Password else Icons.Default.Pin,
                                contentDescription = null,
                                tint = FoodoraOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (authModeIsPin) {
                                    if (currentLang == Language.AR) "استخدام كلمة المرور" else "Use Password"
                                } else {
                                    if (currentLang == Language.AR) "استخدام رمز PIN السريع" else "Use 4-Digit PIN"
                                },
                                color = FoodoraOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Input Fields
                    if (authModeIsPin) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = pinCode,
                                onValueChange = { if (it.length <= 6) pinCode = it },
                                label = {
                                    Text(
                                        if (currentLang == Language.AR) "أدخل رمز PIN السريع (مثال: 1234)"
                                        else "Staff PIN Code (e.g., 1234)"
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Pin,
                                        contentDescription = null,
                                        tint = FoodoraOrange
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.NumberPassword,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        executeLogin(selectedRole, identifier)
                                    }
                                ),
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FoodoraOrange,
                                    unfocusedBorderColor = Color(0xFF383846),
                                    focusedContainerColor = Color(0xFF141419),
                                    unfocusedContainerColor = Color(0xFF141419)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pin_code_field")
                            )

                            // Quick Keypad helper for PIN
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("1234", "4321", "9999").forEach { samplePin ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF22222A),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF33333E)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { pinCode = samplePin }
                                    ) {
                                        Text(
                                            text = "PIN: $samplePin",
                                            color = Color(0xFFA0A0AB),
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = identifier,
                                onValueChange = { identifier = it },
                                label = {
                                    Text(
                                        if (currentLang == Language.AR) "البريد الإلكتروني / اسم المستخدم"
                                        else "Username, Email, or Phone"
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = FoodoraOrange
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FoodoraOrange,
                                    unfocusedBorderColor = Color(0xFF383846),
                                    focusedContainerColor = Color(0xFF141419),
                                    unfocusedContainerColor = Color(0xFF141419)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("username_email_field")
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = {
                                    Text(
                                        if (currentLang == Language.AR) "كلمة المرور"
                                        else "Password"
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = FoodoraOrange
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password",
                                            tint = Color(0xFFA0A0AB)
                                        )
                                    }
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FoodoraOrange,
                                    unfocusedBorderColor = Color(0xFF383846),
                                    focusedContainerColor = Color(0xFF141419),
                                    unfocusedContainerColor = Color(0xFF141419)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        executeLogin(selectedRole, identifier)
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("password_field")
                            )
                        }
                    }

                    // Remember Me & Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { rememberMe = !rememberMe }
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = FoodoraOrange,
                                    uncheckedColor = Color(0xFF6B6B78)
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (currentLang == Language.AR) "تذكر الحساب" else "Remember Me",
                                fontSize = 12.sp,
                                color = Color(0xFFA0A0AB)
                            )
                        }

                        TextButton(
                            onClick = { showForgotPasswordDialog = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (currentLang == Language.AR) "نسيت كلمة المرور؟" else "Forgot Access?",
                                fontSize = 12.sp,
                                color = FoodoraOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Inline Error Warning Banner
                    AnimatedVisibility(visible = errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StatusRed.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusRed.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = StatusRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = errorMessage ?: "",
                                    color = StatusRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Main Submit Button
                    Button(
                        onClick = { executeLogin(selectedRole, identifier) },
                        colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (currentLang == Language.AR) "جار التحقق والدخول..." else "Authenticating...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLang) {
                                    Language.AR -> "تسجيل الدخول إلى النظام"
                                    Language.FR -> "Se Connecter au Système"
                                    Language.EN -> "Log In to Foodora Manager"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer Section: Version & Cloud Server Connection Status
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF16161D),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262630))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StatusGreen)
                    )
                    Text(
                        text = "Cloud Server: Online",
                        fontSize = 11.sp,
                        color = StatusGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(modifier = Modifier.width(1.dp).height(12.dp).background(Color(0xFF33333E)))
                    Text(
                        text = "Version: Foodora Manager v2.4.0",
                        fontSize = 11.sp,
                        color = Color(0xFF888894)
                    )
                }
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Text(
                    text = if (currentLang == Language.AR) "استعادة الوصول / إعادة تعيين PIN" else "Reset Access & PIN",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (currentLang == Language.AR)
                            "لأسباب أمنية، يرجى مراجعة مشرف النظام (Super Admin) أو التواصل مع الدعم الفني لإعادة تعيين كلمة المرور أو PIN."
                        else
                            "For enterprise security, staff PINs and manager credentials can be reset by the Super Admin in the Staff Management module or via support@foodora.io.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FoodoraOrange.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Default Demo PINs: 1234 (Cashier), 4321 (Kitchen)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FoodoraOrange,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showForgotPasswordDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FoodoraOrange)
                ) {
                    Text("OK")
                }
            }
        )
    }
}
