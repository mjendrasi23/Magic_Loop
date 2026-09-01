package com.example.magicloop.ui.sharecard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage


@Composable
fun ShareCardContent(data: ShareCardData) {
    Box(
        modifier = Modifier
            .width(360.dp)
            .height(450.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (data.coverImagePath != null) {
                    AsyncImage(
                        model = data.coverImagePath,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFD7CCC8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🧶",
                            fontSize = 64.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Text(
                    text = data.projectName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (data.completedDateText != null) {
                    Text(
                        text = "Završeno ${data.completedDateText}",
                        fontSize = 13.sp,
                        color = Color(0xFF6D4C41)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOfNotNull(data.needleSize, data.yarnInfo)
                            .joinToString(" · ")
                            .takeIf { it.isNotBlank() }
                            ?.let {
                                Text(
                                    text = it,
                                    fontSize = 12.sp,
                                    color = Color(0xFF8D6E63)
                                )
                            }
                    }

                    if (data.currentStreak > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFFFE0B2))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFEF6C00),
                                modifier = Modifier.height(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${data.currentStreak}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFEF6C00)
                            )
                        }
                    }
                }

                if (data.counterSummary != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = data.counterSummary,
                        fontSize = 13.sp,
                        color = Color(0xFF6D4C41)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Magic Loop",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFBCAAA4)
                )
            }
        }
    }
}