package com.example.magicloop.ui.pattern

import androidx.compose.ui.geometry.Offset


object PathEncoding {

    fun encode(points: List<Offset>): String =
        points.joinToString(";") { "${it.x},${it.y}" }

    fun decode(data: String): List<Offset> {
        if (data.isBlank()) return emptyList()
        return data.split(";").mapNotNull { pair ->
            val parts = pair.split(",")
            if (parts.size == 2) {
                val x = parts[0].toFloatOrNull()
                val y = parts[1].toFloatOrNull()
                if (x != null && y != null) Offset(x, y) else null
            } else null
        }
    }
}