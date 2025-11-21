package com.bhushantechsolutions.eventmanagementsystem.ui
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bhushantechsolutions.eventmanagementsystem.R
import com.bhushantechsolutions.eventmanagementsystem.data.dto.EventItem

@Composable
fun EventDetailCard(event: EventItem, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = event.event_image_url,
                contentDescription = null,
                error = painterResource(R.drawable.error),
                placeholder = painterResource(R.drawable.placeholder),
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(event.event_name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(event.event_category, color = Color.Gray)
                Text("₹ ${event.ticket_price}", fontWeight = FontWeight.SemiBold)
                Text(convertDate(event.event_date), color = Color.DarkGray)
            }
        }
    }
}
