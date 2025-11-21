package com.bhushantechsolutions.eventmanagementsystem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhushantechsolutions.eventmanagementsystem.data.dto.EventDetails
import com.bhushantechsolutions.eventmanagementsystem.data.viewModel.EventsViewModel
import com.bhushantechsolutions.eventmanagementsystem.utils.ApiState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    onEventClick: () -> Unit,
    eventId: String,
    viewModel: EventsViewModel = koinViewModel(),
    onRefresh: ((() -> Unit) -> Unit)
) {

    val detailState by viewModel.eventDetailState.collectAsState()

    onRefresh {
        viewModel.fetchEventDetail(eventId)
    }

    // Load only once per event
    LaunchedEffect(eventId) {
        viewModel.fetchEventDetail(eventId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val title = when (detailState) {
                        is ApiState.Success -> (detailState as ApiState.Success<EventDetails>).data.eventName
                        else -> "Event Details"
                    }
                    Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onEventClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        when (detailState) {

            ApiState.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ApiState.Error -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (detailState as ApiState.Error).message,
                        color = Color.Red
                    )
                }
            }

            ApiState.Empty -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No event details available", color = Color.Gray)
                }
            }

            is ApiState.Success -> {
                val event = (detailState as ApiState.Success<EventDetails>).data

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {

                    // 🔹 Event Images Carousel
                    ImageCarousel(event.eventImageUrls)

                    Spacer(Modifier.height(20.dp))

                    Column(Modifier.padding(horizontal = 16.dp)) {

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                event.eventName,
                                maxLines = 2,
                                modifier = Modifier.weight(1f), // 👈 Important
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color(0xFFFFE5E5),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    event.eventCategory,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color(0xFFD47A7A)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "₹ ${event.ticketPrice}",
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                convertDate(event.eventDate),
                                color = Color.Gray
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(event.eventDescription, color = Color.Gray)

                        Spacer(Modifier.height(20.dp))

                        Text("Reach out to learn more", fontWeight = FontWeight.SemiBold)
                        Text(event.organizerContact, color = Color.Gray)

                        Spacer(Modifier.height(20.dp))

                        Text(
                            "600 others are attending!",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color(0xFF4F7CF0),
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { /* TODO */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Buy Your Ticket")
                        }

                        Spacer(Modifier.height(60.dp))
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewEventDetails() {
    EventDetailsScreen(
        onEventClick = {},
        eventId = "",
        onRefresh = {}
    )
}
