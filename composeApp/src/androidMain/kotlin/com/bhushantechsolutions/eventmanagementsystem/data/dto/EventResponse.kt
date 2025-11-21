package com.bhushantechsolutions.eventmanagementsystem.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    val status: String,
    val message: String,
    val data: EventData
)
@Serializable

data class EventData(
    val num_pages: Int,
    val count: Int,
    val total_count: Int,
    val results: List<EventItem>
)
@Serializable

data class EventItem(
    val event_id: String,
    val event_name: String,
    val event_date: String,
    val event_image_url: String,
    val longitude: Double,
    val latitude: Double,
    val event_category_id: String,
    val event_category: String,
    val ticket_price: Double
)
