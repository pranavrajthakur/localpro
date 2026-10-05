package com.example.ca4localpro.ui.screens.provider

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ca4localpro.viewmodel.ProviderDashboardUiState
import com.example.ca4localpro.viewmodel.ProviderDashboardViewModel

@Composable
fun ProviderDashboardScreen(
    viewModel: ProviderDashboardViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val state = uiState) {
            is ProviderDashboardUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProviderDashboardUiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Provider profile not found.")
                }
            }
            is ProviderDashboardUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is ProviderDashboardUiState.Saving -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProviderDashboardUiState.Success -> {
                val provider = state.provider

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Provider Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // User Info Header
                    var isEditing by remember { mutableStateOf(false) }

                    if (isEditing) {
                        var editName by remember { mutableStateOf(provider.name) }
                        var editDesc by remember { mutableStateOf(provider.bio) }
                        var editPhone by remember { mutableStateOf(provider.phone) }
                        var editExp by remember { mutableStateOf(provider.yearsExperience.toString()) }
                        var editAddress by remember { mutableStateOf(provider.locationAddress) }

                        OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Business Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(value = editDesc, onValueChange = { editDesc = it }, label = { Text("Bio / Description") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(value = editPhone, onValueChange = { editPhone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(value = editExp, onValueChange = { editExp = it }, label = { Text("Experience (Years)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(value = editAddress, onValueChange = { editAddress = it }, label = { Text("Location Address") }, modifier = Modifier.fillMaxWidth())
                        
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedButton(onClick = { isEditing = false }) { Text("Cancel") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { 
                                viewModel.updateProfile(
                                    businessName = editName,
                                    description = editDesc,
                                    phone = editPhone,
                                    experienceYears = editExp.toIntOrNull() ?: provider.yearsExperience,
                                    serviceRadius = provider.distanceKm.toInt(), // keeping old
                                    locationAddress = editAddress
                                )
                                isEditing = false
                            }) { Text("Save") }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = provider.name.take(1),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Good morning, ${provider.name}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        TextButton(onClick = { isEditing = true }) {
                            Text("Edit Profile")
                        }
                    }
                    
                    if (provider.isVerified) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verified Professional", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        Text("Verification Pending", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Availability Toggle
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Availability", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(if (provider.isEmergencyAvailable) "Online & Accepting Jobs" else "Offline", style = MaterialTheme.typography.bodyMedium)
                            }
                            Switch(
                                checked = provider.isEmergencyAvailable,
                                onCheckedChange = { isAvailable ->
                                    viewModel.updateAvailability(isAvailable)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dashboard overview
                    Text(
                        text = "Today's Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OverviewCard(title = "New Requests", value = "0", modifier = Modifier.weight(1f))
                        OverviewCard(title = "Active Jobs", value = "0", modifier = Modifier.weight(1f))
                        OverviewCard(title = "Completed", value = "0", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Services section
                    var showAddServiceDialog by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Services",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { showAddServiceDialog = true }) {
                            Text("Add New")
                        }
                    }

                    if (showAddServiceDialog) {
                        var newTitle by remember { mutableStateOf("") }
                        var newDesc by remember { mutableStateOf("") }
                        var newPrice by remember { mutableStateOf("") }
                        var newDuration by remember { mutableStateOf("60 mins") }

                        AlertDialog(
                            onDismissRequest = { showAddServiceDialog = false },
                            title = { Text("Add Service") },
                            text = {
                                Column {
                                    OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Service Title") }, modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(value = newDesc, onValueChange = { newDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(value = newPrice, onValueChange = { newPrice = it }, label = { Text("Price (₹)") }, modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(value = newDuration, onValueChange = { newDuration = it }, label = { Text("Duration (e.g. 60 mins)") }, modifier = Modifier.fillMaxWidth())
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    val priceDouble = newPrice.toDoubleOrNull()
                                    if (newTitle.isNotBlank() && priceDouble != null && priceDouble >= 0) {
                                        viewModel.addService(newTitle, newDesc, priceDouble, newDuration)
                                        showAddServiceDialog = false
                                    }
                                }) { Text("Save") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAddServiceDialog = false }) { Text("Cancel") }
                            }
                        )
                    }

                    if (provider.servicesOffered.isEmpty()) {
                        Text("No services listed. Add a service to get bookings.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        provider.servicesOffered.forEach { service ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(service.title, fontWeight = FontWeight.Bold)
                                        Text("₹${service.price} • ${service.durationText}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    IconButton(onClick = { viewModel.removeService(service.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove Service", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Logout")
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun OverviewCard(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}
