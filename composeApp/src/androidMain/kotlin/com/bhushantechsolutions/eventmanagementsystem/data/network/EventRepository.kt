package com.bhushantechsolutions.eventmanagementsystem.data.network

import com.bhushantechsolutions.eventmanagementsystem.data.dto.EventCategoryResponse
import com.bhushantechsolutions.eventmanagementsystem.data.dto.EventDetailsResponse
import com.bhushantechsolutions.eventmanagementsystem.data.dto.EventResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType

class EventRepository(private val client: HttpClient) {
    suspend fun getEvents(page: Int, pageSize:Int): EventResponse {
        return client.get("http://18.208.147.119/events"){
            parameter("page", page)
            parameter("items_per_page", pageSize)
            contentType(ContentType.Application.Json)
        }.body()
    }
    suspend fun getEventDetails(eventId: String): EventDetailsResponse{
        return client.get("http://18.208.147.119/events/$eventId"){
            contentType(ContentType.Application.Json)
        }.body()
    }

    suspend fun eventCategories(): EventCategoryResponse {
        return client.get("http://18.208.147.119/event-categories"){
            contentType(ContentType.Application.Json)
        }.body()
    }
}