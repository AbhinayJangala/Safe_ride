package com.example.safe_ride.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safe_ride.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safe Ride") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Current Location Input / Display
            OutlinedTextField(
                value = uiState.currentLocation,
                onValueChange = { viewModel.updateCurrentLocation(it) },
                label = { Text("Current Location") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Current Location Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Destination Location Input
            OutlinedTextField(
                value = uiState.destination,
                onValueChange = { viewModel.updateDestination(it) },
                label = { Text("Enter Destination") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Destination Icon",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Start Journey Button
            Button(
                onClick = {
                    viewModel.startJourney()
                },
                enabled = uiState.destination.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (uiState.journeyStarted) "Journey in Progress..." else "Start Journey",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (uiState.journeyStarted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Journey Active",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "From: ${uiState.currentLocation}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "To: ${uiState.destination}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TextButton(
                            onClick = { viewModel.resetJourney() },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Cancel Journey")
                        }
                    }
                }
            }
        }
    }
}
