package com.bhushantechsolutions.eventmanagementsystem.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bhushantechsolutions.eventmanagementsystem.data.dto.EventCategoryResponse
import com.bhushantechsolutions.eventmanagementsystem.data.dto.EventDetails
import com.bhushantechsolutions.eventmanagementsystem.data.network.EventRepository
import com.bhushantechsolutions.eventmanagementsystem.data.dto.EventItem
import com.bhushantechsolutions.eventmanagementsystem.utils.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class EventsViewModel(
    private val repo: EventRepository
) : ViewModel() {

    private val _eventsState = MutableStateFlow<ApiState<List<EventItem>>>(ApiState.Loading)
    val eventsState: StateFlow<ApiState<List<EventItem>>> = _eventsState

    private val _paginationLoading = MutableStateFlow(false)
    val paginationLoading: StateFlow<Boolean> = _paginationLoading

    private val _refreshLoading = MutableStateFlow(false)
    val refreshLoading: StateFlow<Boolean> = _refreshLoading.asStateFlow()

    private val _events = mutableListOf<EventItem>()        // Visible List (Paged UI)
    private val _allEvents = mutableListOf<EventItem>()     // Global Master List for Sorting/Filtering

    private var currentPage = 1
    private var totalPages = Int.MAX_VALUE
    private var apiPageData: List<EventItem> = emptyList() // single backend page stored
    private var clientOffset = 0
    private val clientPageSize = 20

    fun fetchEvents(isRefresh: Boolean = false) {
        if (_paginationLoading.value && !isRefresh) return
        if (_refreshLoading.value && isRefresh) return

        viewModelScope.launch {
            try {
                if (isRefresh) {
                    _refreshLoading.value = true

                    // RESET EVERYTHING
                    _events.clear()
                    _allEvents.clear()
                    currentPage = 1
                    totalPages = Int.MAX_VALUE
                    apiPageData = emptyList()
                    clientOffset = 0
                } else {
                    _paginationLoading.value = true
                }

                // Fetch next backend page if required
                if (clientOffset >= apiPageData.size) {

                    if (currentPage > totalPages) {
                        return@launch // no more pages
                    }

                    val response = repo.getEvents(currentPage, 260)
                    apiPageData = response.data.results
                    totalPages = response.data.num_pages
                    currentPage++
                    clientOffset = 0

                    // Add loaded data to master list
                    _allEvents.addAll(apiPageData)
                }

                // Load 20 items at a time into UI list
                val end = minOf(clientOffset + clientPageSize, apiPageData.size)
                val chunk = apiPageData.subList(clientOffset, end)
                clientOffset = end

                _events.addAll(chunk)

                _eventsState.value =
                    if (_events.isEmpty()) ApiState.Empty
                    else ApiState.Success(_events.toList())

            } catch (e: Exception) {
                _eventsState.value =
                    ApiState.Error(e.localizedMessage ?: "Something went wrong")
            } finally {
                _paginationLoading.value = false
                _refreshLoading.value = false
            }
        }
    }




    private val _eventDetailState = MutableStateFlow<ApiState<EventDetails>>(ApiState.Loading)
    val eventDetailState: StateFlow<ApiState<EventDetails>> = _eventDetailState

    fun fetchEventDetail(eventId: String) {
        viewModelScope.launch {
            _eventDetailState.value = ApiState.Loading

            try {
                val response = repo.getEventDetails(eventId)
                _eventDetailState.value = ApiState.Success(response.data)

            } catch (e: Exception) {
                _eventDetailState.value = ApiState.Error(
                    e.localizedMessage ?: "Failed to load details"
                )
            }
        }
    }


    // Map part: Marker Selection
    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex = _selectedIndex.asStateFlow()

    fun setSelectedIndex(index: Int) {
        _selectedIndex.value = index
    }

    fun selectNext() {
        val current = _selectedIndex.value
        val list = (eventsState.value as? ApiState.Success)?.data ?: return
        if (current < list.lastIndex) {
            _selectedIndex.value = current + 1
        }
    }

    fun selectPrevious() {
        val current = _selectedIndex.value
        if (current > 0) {
            _selectedIndex.value = current - 1
        }
    }

    private val _eventCategoriesState =
        MutableStateFlow<ApiState<EventCategoryResponse>>(ApiState.Loading)
    val eventCategoriesState = _eventCategoriesState.asStateFlow()

    fun fetchEventCategories() {
        viewModelScope.launch {
            _eventCategoriesState.value = ApiState.Loading
            try {
                val response = repo.eventCategories()
                _eventCategoriesState.value = ApiState.Success(response)
            } catch (e: Exception) {
                _eventCategoriesState.value =
                    ApiState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }
}
