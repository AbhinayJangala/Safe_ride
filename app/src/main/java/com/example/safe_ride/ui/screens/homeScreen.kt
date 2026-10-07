package com.example.safe_ride.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasLocationPermission) {
            viewModel.getCurrentLocation(context)
        } else {
            viewModel.updateCurrentLocation("Location permission denied")
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            viewModel.getCurrentLocation(context)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

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
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Current Location"
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Current Location",
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = uiState.currentLocation,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

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
