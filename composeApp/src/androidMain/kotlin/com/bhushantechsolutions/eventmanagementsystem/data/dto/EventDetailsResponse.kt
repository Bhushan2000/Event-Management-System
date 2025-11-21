package com.bhushantechsolutions.eventmanagementsystem.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDetailsResponse(
    val status: String,
    val message: String,
    val data: EventDetails
)

@Serializable
data class EventDetails(
    @SerialName("event_id")
    val eventId: String,

    @SerialName("event_name")
    val eventName: String,

    @SerialName("event_description")
    val eventDescription: String,

    @SerialName("event_date")
    val eventDate: String,

    @SerialName("event_image_urls")
    val eventImageUrls: List<String>,

    val longitude: Double,
    val latitude: Double,

    @SerialName("event_category_id")
    val eventCategoryId: String,

    @SerialName("event_category")
    val eventCategory: String,

    @SerialName("num_attendees")
    val numAttendees: Int,

    @SerialName("ticket_price")
    val ticketPrice: Double,

    @SerialName("organizer_contact")
    val organizerContact: String
)
