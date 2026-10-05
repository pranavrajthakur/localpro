package com.example.ca4localpro.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ca4localpro.data.model.UserRole
import com.example.ca4localpro.ui.components.LocalProButton
import com.example.ca4localpro.viewmodel.ProfileViewModel
import com.example.ca4localpro.viewmodel.ProfileUiState

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var activeDialog by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    val user = if (uiState is ProfileUiState.Success) {
        (uiState as ProfileUiState.Success).user
    } else null

    var editName by remember(user) { mutableStateOf(user?.name ?: "") }
    var editPhone by remember(user) { mutableStateOf(user?.phone ?: "") }
    var editAddress by remember(user) { mutableStateOf(user?.primaryAddress ?: "") }

    if (activeDialog != null) {
        AlertDialog(
            onDismissRequest = { activeDialog = null },
            title = {
                Text(
                    text = when (activeDialog) {
                        "addresses" -> "Saved Addresses"
                        "history" -> "Booking History"
                        "payments" -> "Payment Methods"
                        else -> "Help & Support"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    when (activeDialog) {
                        "addresses" -> {
                            Text(text = "Primary Address: ${user?.primaryAddress}", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "• Home: 42 Park Street, Connaught Place", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "• Office: Sector 14, Metro Area", style = MaterialTheme.typography.bodyMedium)
                        }
                        "history" -> {
                            Text(text = "• Raj AC Services - Scheduled Today, 4:00 PM (Active)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "• SparkFix Electrical - Completed 2 days ago (₹299)", style = MaterialTheme.typography.bodyMedium)
                        }
                        "payments" -> {
                            Text(text = "• UPI: rahul@okicici (Default)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "• Credit Card: **** **** **** 4021", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "• Cash on Delivery: Enabled", style = MaterialTheme.typography.bodyMedium)
                        }
                        else -> {
                            Text(text = "📞 Customer Helpline: +91 1800-LOCALPRO", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "✉️ Support Email: support@localpro.com", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "24/7 dedicated local marketplace support team.", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { activeDialog = null }) {
                    Text(text = "Close")
                }
            }
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "My Profile",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User Info Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.name?.take(1) ?: "U",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editPhone,
                    onValueChange = { editPhone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editAddress,
                    onValueChange = { editAddress = it },
                    label = { Text("Primary Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = user?.name ?: "Guest User",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (!isEditing) {
                Text(
                    text = user?.email ?: "guest@example.com",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!isEditing) {
                Button(onClick = { isEditing = true }) {
                    Text("Edit Profile")
                }
            } else {
                Row {
                    OutlinedButton(onClick = { 
                        isEditing = false
                        editName = user?.name ?: ""
                        editPhone = user?.phone ?: ""
                        editAddress = user?.primaryAddress ?: ""
                    }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { 
                        viewModel.updateProfile(editName, editPhone, editAddress)
                        isEditing = false 
                    }) {
                        Text("Save")
                    }
                }
            }

            if (uiState is ProfileUiState.Saving) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator()
            }
            if (uiState is ProfileUiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (uiState as ProfileUiState.Error).message, 
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Role: " + (if (user?.role == UserRole.PROVIDER) "Service Provider" else "Customer"),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Removed Role Switcher for real backend integration

            Spacer(modifier = Modifier.height(24.dp))

            // Option Items
            ProfileOptionItem(
                icon = Icons.Default.LocationOn,
                title = "Saved Addresses",
                subtitle = user?.primaryAddress ?: "Manage delivery & service locations",
                onClick = { activeDialog = "addresses" }
            )

            ProfileOptionItem(
                icon = Icons.Default.History,
                title = "Booking History",
                subtitle = "View active and completed job bookings",
                onClick = { activeDialog = "history" }
            )

            ProfileOptionItem(
                icon = Icons.Default.Payment,
                title = "Payment Methods",
                subtitle = "UPI, Cards, Cash on Service",
                onClick = { activeDialog = "payments" }
            )

            ProfileOptionItem(
                icon = Icons.Default.Help,
                title = "Help & Support",
                subtitle = "24/7 customer assistance & FAQs",
                onClick = { activeDialog = "support" }
            )

            Spacer(modifier = Modifier.height(32.dp))

            LocalProButton(
                text = "Sign Out",
                onClick = onLogout,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error,
                icon = Icons.Default.ExitToApp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
