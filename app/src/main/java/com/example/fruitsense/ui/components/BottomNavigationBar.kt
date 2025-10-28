package com.example.fruitsense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.offset
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
        colors = CardDefaults.cardColors(containerColor = FruitSenseColors.GreenDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.CameraAlt,
                label = "Scan",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )

            BottomNavItem(
                icon = Icons.Default.Archive,
                label = "Inventory",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )

            BottomNavItem(
                icon = Icons.Default.History,
                label = "History",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )

            BottomNavItem(
                icon = Icons.Default.AccountCircle,
                label = "Profile",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = if (isSelected) FruitSenseColors.White else Color.Transparent,
                    shape = CircleShape
                ),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = if (isSelected) FruitSenseColors.GreenDark else FruitSenseColors.GrayDark
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) FruitSenseColors.GreenDark else FruitSenseColors.GrayDark
        )
    }
}