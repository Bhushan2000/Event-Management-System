package com.bhushantechsolutions.eventmanagementsystem.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventCategoryResponse(
    val status: String,
    val message: String,
    val data: EventCategoryData
)

@Serializable
data class EventCategoryData(
    val count: Int,
    val results: List<EventCategoryItem>
)

@Serializable
data class EventCategoryItem(
    @SerialName("event_category_id")
    val eventCategoryId: String,

    @SerialName("event_category")
    val eventCategory: String
)
