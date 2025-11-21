package com.bhushantechsolutions.eventmanagementsystem.ui


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhushantechsolutions.eventmanagementsystem.R
import com.bhushantechsolutions.eventmanagementsystem.data.dto.EventCategoryResponse
import com.bhushantechsolutions.eventmanagementsystem.data.dto.EventItem
import com.bhushantechsolutions.eventmanagementsystem.data.viewModel.EventsViewModel
import com.bhushantechsolutions.eventmanagementsystem.utils.ApiState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    onEventClick: (String) -> Unit,
    goToEventMap: () -> Unit,
    viewModel: EventsViewModel = koinViewModel(),
    onRefresh: ((() -> Unit) -> Unit)
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isUserTyping by remember { mutableStateOf(true) }

    val eventState by viewModel.eventsState.collectAsState()
    val paginationLoading by viewModel.paginationLoading.collectAsState()
    val categoryState by viewModel.eventCategoriesState.collectAsState()
    val refreshLoading by viewModel.refreshLoading.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by remember { mutableStateOf(false) }

    onRefresh {
        viewModel.fetchEvents() // 👈 API refresh
    }

    // initial load
    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
    }

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                val events = (eventState as? ApiState.Success<List<EventItem>>)?.data ?: return@collect
                if (lastVisibleIndex == events.lastIndex && !paginationLoading) {
                    viewModel.fetchEvents()
                }
            }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Discover", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = goToEventMap) {
                        Icon(
                            painter = painterResource(R.drawable.page),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        isSheetOpen = true
                        viewModel.fetchEventCategories()  // trigger category API call
                    }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (isSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isSheetOpen = false },
                sheetState = sheetState,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.LightGray)
                    )
                }
            ) {

                when (categoryState) {

                    is ApiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is ApiState.Success -> {
                        val categories =
                            (categoryState as ApiState.Success<EventCategoryResponse>).data.data.results

                        Column(modifier = Modifier.fillMaxWidth()) {

                            Text(
                                text = "Sort Events by Category",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )

                            LazyColumn {
                                items(categories) { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                // filter based on name event category name matching with events list
                                                 isSheetOpen = false
                                            }
                                            .padding(horizontal = 20.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.eventCategory,
                                            fontSize = 16.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = null
                                        )
                                    }
                                    Divider()
                                }
                            }
                            TextButton(
                                onClick = {
                                     isSheetOpen = false
                                },
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text("Clear Sorting")
                            }
                        }
                    }

                    is ApiState.Error -> {
                        Text(
                            text = "Failed to load categories",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Red
                        )
                    }

                    is ApiState.Empty -> {
                        EmptyStateView()
                    }
                }
            }
        }


        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = refreshLoading),
            onRefresh = { viewModel.fetchEvents(isRefresh = true) }
        ) {
            Column(Modifier.padding(paddingValues)) {


                when (eventState) {

                    is ApiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is ApiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: ${(eventState as ApiState.Error).message}",
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    is ApiState.Empty -> {
                        EmptyStateView() // Create custom UI if you want
                    }

                    is ApiState.Success -> {
                        val eventList = (eventState as ApiState.Success<List<EventItem>>).data

                        // 🔍 Search Bar UI
                        SearchBar(
                            query = query,
                            onQueryChange = { newText ->
                                query = newText
                                if (newText.isNotEmpty()) {
                                    isUserTyping = true
                                    expanded = true
                                }
                            },
                            onSearch = { expanded = false },
                            active = expanded,
                            onActiveChange = { expanded = it },
                            placeholder = { Text("Search…") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (expanded && query.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            query = ""
                                            expanded = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close")
                                    }
                                }
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {

                            if (query.isBlank()) {
                                SearchInfo()
                                return@SearchBar
                            }

                            val filtered = eventList.filter {
                                it.event_name.contains(query, ignoreCase = true) ||
                                        it.event_category.contains(query, ignoreCase = true)
                            }

                            LazyColumn(Modifier.padding(8.dp)) {
                                items(filtered) { item ->
                                    val categoryColor = getDynamicCategoryColor(item.event_category)

                                    EventCard(
                                        id = item.event_id,
                                        title = item.event_name,
                                        price = item.ticket_price.toString(),
                                        date = item.event_date,
                                        category = item.event_category,
                                        imageUrl = item.event_image_url,
                                        categoryColor = categoryColor,
                                        onClick = {
                                            expanded = false
                                            onEventClick(item.event_id)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        //  Default list
                        LazyColumn(state = listState) {
                            items(eventList) { item ->
                                EventCard(
                                    id = item.event_id,
                                    title = item.event_name,
                                    price = item.ticket_price.toString(),
                                    date = item.event_date,
                                    category = item.event_category,
                                    imageUrl = item.event_image_url,
                                    categoryColor = getDynamicCategoryColor(item.event_category),
                                    onClick = { onEventClick(item.event_id) }
                                )
                            }

                            item {
                                if (paginationLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    // 🔙 Handle Back for SearchBar
    BackHandler(enabled = expanded || query.isNotEmpty()) {
        isUserTyping = false
        query = ""
        expanded = false
    }
}


val categoryColorMap = mutableMapOf<String, Color>()
var nextColorIndex = 0

val categoryColorPool = listOf(
    Color(0xFFF8BBD0), // Light Pink
    Color(0xFFC8E6C9), // Light Green
    Color(0xFFBBDEFB), // Light Blue
    Color(0xFFFFE0B2), // Light Orange
    Color(0xFFE1BEE7), // Light Purple
    Color(0xFFC5CAE9), // Light Indigo
    Color(0xFFFFCCBC), // Light Deep Orange
    Color(0xFFB2DFDB), // Light Teal
    Color(0xFFD7CCC8), // Light Brown
    Color(0xFFCFD8DC)  // Light Blue Grey
)


fun getDynamicCategoryColor(category: String): Color {
    if (categoryColorMap.containsKey(category)) {
        return categoryColorMap[category]!!
    }

    // Assign new color
    val color = categoryColorPool[nextColorIndex % categoryColorPool.size]
    categoryColorMap[category] = color

    nextColorIndex++

    return color
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(
        onEventClick = {},
        goToEventMap = {},
        onRefresh = {}
    )
}