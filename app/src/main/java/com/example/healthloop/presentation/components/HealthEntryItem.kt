package com.example.healthloop.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthloop.presentation.model.HealthEntryUiModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.MaterialTheme

@Composable
fun HealthEntryItem(entry: HealthEntryUiModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Date header
            Text(
                text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(entry.date),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Metrics grid
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricItem(
                    icon = Icons.Default.WaterDrop,
                    value = "${entry.waterIntake} cups",
                    label = "Water",
                    modifier = Modifier.weight(1f)
                )
                
                MetricItem(
                    icon = Icons.Default.Bedtime,
                    value = "${entry.sleepHours} hrs",
                    label = "Sleep",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricItem(
                    icon = Icons.Default.DirectionsWalk,
                    value = "${entry.stepCount}",
                    label = "Steps",
                    modifier = Modifier.weight(1f)
                )
                
                MetricItem(
                    icon = Icons.Default.MonitorWeight,
                    value = "${entry.weight} kg",
                    label = "Weight",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mood
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Mood:",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = entry.mood,
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = value,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}