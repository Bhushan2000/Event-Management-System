package com.bhushantechsolutions.eventmanagementsystem.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.bhushantechsolutions.eventmanagementsystem.data.viewModel.EventsViewModel
import com.bhushantechsolutions.eventmanagementsystem.utils.ApiState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventMapScreen(
    onEventClick: () -> Unit,
    viewModel: EventsViewModel = koinViewModel(),
    onRefresh: ((() -> Unit) -> Unit)

) {
    val context = LocalContext.current
    val eventState by viewModel.eventsState.collectAsState()
    val selectedIndex by viewModel.selectedIndex.collectAsState()
    val cameraState = rememberCameraPositionState()

    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, locationPermission)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        hasLocationPermission = it
    }

    onRefresh{
        viewModel.fetchEvents()
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(locationPermission)
        viewModel.fetchEvents()
    }

    // Camera Animation only if Success
    LaunchedEffect(selectedIndex, eventState) {
        if (eventState is ApiState.Success) {
            val events = (eventState as ApiState.Success).data
            if (events.isNotEmpty()) {
                val event = events[selectedIndex]
                cameraState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(event.latitude, event.longitude), 15f
                    ),
                    durationMs = 800
                )
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Events Map", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onEventClick() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {

            when (eventState) {
                ApiState.Empty->{
                    EmptyStateView()
                }

                ApiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                is ApiState.Error -> {
                    Text(
                        text = (eventState as ApiState.Error).message,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }

                is ApiState.Success -> {
                    val events = (eventState as ApiState.Success).data

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraState
                    ) {
                        events.forEachIndexed { index, event ->
                            Marker(
                                state = MarkerState(LatLng(event.latitude, event.longitude)),
                                title = event.event_name,
                                icon = if (index == selectedIndex)
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                                else BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                                onClick = {
                                    viewModel.setSelectedIndex(index)
                                    true
                                }
                            )
                        }
                    }

                    if (events.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .pointerInput(Unit) {
                                    detectHorizontalDragGestures { _, dragAmount ->
                                        if (dragAmount > 20) viewModel.selectPrevious()
                                        if (dragAmount < -20) viewModel.selectNext()
                                    }
                                }
                        ) {
                            EventDetailCard(
                                event = events[selectedIndex],
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

