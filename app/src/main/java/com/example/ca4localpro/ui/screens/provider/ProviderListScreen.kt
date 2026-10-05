package com.example.ca4localpro.ui.screens.provider

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ca4localpro.ui.components.ProviderCard
import com.example.ca4localpro.ui.components.SearchBarCard
import com.example.ca4localpro.viewmodel.ProviderViewModel

@Composable
fun ProviderListScreen(
    viewModel: ProviderViewModel,
    initialCategory: String?,
    initialQuery: String?,
    onProviderClick: (String) -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val providers by viewModel.providersList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LaunchedEffect(initialCategory, initialQuery) {
        if (!initialCategory.isNullOrEmpty()) {
            viewModel.filterByCategory(initialCategory)
        } else if (!initialQuery.isNullOrEmpty()) {
            viewModel.search(initialQuery)
        } else {
            viewModel.loadAllProviders()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (!initialCategory.isNullOrEmpty()) "Local ${initialCategory.replaceFirstChar { it.uppercase() }} Pros" else "Verified Professionals",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${providers.size} verified professionals near you",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SearchBarCard(
                query = searchQuery,
                onQueryChange = { viewModel.search(it) },
                onSearch = { },
                placeholder = "Search by provider name or service..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (providers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No service professionals found matching your search.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 28.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(providers, key = { it.id }) { provider ->
                        ProviderCard(
                            provider = provider,
                            onClick = { onProviderClick(provider.id) }
                        )
                    }
                }
            }
        }
    }
}
