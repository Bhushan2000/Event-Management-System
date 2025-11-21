package com.bhushantechsolutions.eventmanagementsystem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.bhushantechsolutions.eventmanagementsystem.R
import com.bhushantechsolutions.eventmanagementsystem.data.viewModel.EventsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EventCard(
    title: String,
    price: String,
    date: String,
    category: String,
    onClick: (String) -> Unit,
    id: String,
    imageUrl: String,
    categoryColor: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(id) },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Event Image
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                error = painterResource(R.drawable.error),
                placeholder = painterResource(R.drawable.placeholder),
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                // Title + Category Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = title,
                        maxLines = 2,
                        modifier = Modifier.weight(1f), // Important for better visual in single line
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,

                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                color = categoryColor,
                                shape = RoundedCornerShape(12.dp)
                            ).padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = category,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            maxLines = 1,
                            fontWeight = FontWeight.SemiBold,
                         )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price + Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "₹ $price",
                        fontSize = 14.sp,
                        color = Color(0xFF676767),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = convertDate(date),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                        color = Color(0xFF9A9A9A)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }

}
fun convertDate(dateString: String): String {
    val instant = java.time.Instant.parse(dateString)

    // Convert Instant → ZonedDateTime (UTC)
    val zonedDateTime = java.time.ZonedDateTime.ofInstant(instant, java.time.ZoneId.of("UTC"))

    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d yyyy")

    return zonedDateTime.format(formatter)
}

@Preview
@Composable
fun PreviewVisitCard() {
//    EventCard()
}
