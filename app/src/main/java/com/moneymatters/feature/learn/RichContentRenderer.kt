package com.moneymatters.feature.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moneymatters.core.designsystem.*

/**
 * Parses and renders rich Markdown blocks including tables, callout boxes, and key takeaways.
 */
@Composable
fun RichContentRenderer(content: String) {
    val blocks = remember(content) { parseContentBlocks(content) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        blocks.forEach { block ->
            when (block) {
                is ContentBlock.Header -> {
                    Text(
                        text = block.text,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PwElectricBlue,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                is ContentBlock.Table -> {
                    RenderMarkdownTable(tableData = block)
                }
                is ContentBlock.CalloutBox -> {
                    RenderCalloutBox(title = block.title, text = block.text, type = block.type)
                }
                is ContentBlock.Paragraph -> {
                    Text(
                        text = block.text,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

sealed class ContentBlock {
    data class Header(val text: String) : ContentBlock()
    data class Paragraph(val text: String) : ContentBlock()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : ContentBlock()
    data class CalloutBox(val title: String, val text: String, val type: CalloutType) : ContentBlock()
}

enum class CalloutType { MISTAKE, TAKEAWAY, MISCONCEPTION, EXAMPLE, NOTE }

fun parseContentBlocks(content: String): List<ContentBlock> {
    val blocks = mutableListOf<ContentBlock>()
    val lines = content.lines()

    var i = 0
    while (i < lines.size) {
        val line = lines[i].trim()

        if (line.isEmpty()) {
            i++
            continue
        }

        // Table Detection (Line contains | and next line contains ---)
        if (line.contains("|") && i + 1 < lines.size && lines[i + 1].contains("---")) {
            val rawHeaders = line.split("|")
            val headers = rawHeaders.map { cell -> cell.trim() }.filter { cell -> cell.isNotEmpty() }
            i += 2 // Skip header and divider
            val rows = mutableListOf<List<String>>()

            while (i < lines.size && lines[i].trim().contains("|")) {
                val rowCells = lines[i].split("|").map { cell -> cell.trim() }.filter { cell -> cell.isNotEmpty() }
                if (rowCells.isNotEmpty()) {
                    rows.add(rowCells)
                }
                i++
            }
            blocks.add(ContentBlock.Table(headers, rows))
            continue
        }

        // Callout Box Detection
        when {
            line.startsWith("WHERE PEOPLE MAKE MISTAKES", ignoreCase = true) -> {
                val textBuf = StringBuilder()
                i++
                while (i < lines.size && !lines[i].trim().startsWith("KEY TAKEAWAYS") && !lines[i].trim().startsWith("COMMON MISCONCEPTIONS")) {
                    textBuf.append(lines[i]).append("\n")
                    i++
                }
                blocks.add(ContentBlock.CalloutBox("WHERE PEOPLE MAKE MISTAKES", textBuf.toString().trim(), CalloutType.MISTAKE))
                continue
            }
            line.startsWith("KEY TAKEAWAYS", ignoreCase = true) -> {
                val textBuf = StringBuilder()
                i++
                while (i < lines.size && !lines[i].trim().startsWith("COMMON MISCONCEPTIONS") && !lines[i].trim().startsWith("THE JOURNEY AHEAD")) {
                    textBuf.append(lines[i]).append("\n")
                    i++
                }
                blocks.add(ContentBlock.CalloutBox("KEY TAKEAWAYS", textBuf.toString().trim(), CalloutType.TAKEAWAY))
                continue
            }
            line.startsWith("COMMON MISCONCEPTIONS", ignoreCase = true) -> {
                val textBuf = StringBuilder()
                i++
                while (i < lines.size && !lines[i].trim().startsWith("THE JOURNEY AHEAD")) {
                    textBuf.append(lines[i]).append("\n")
                    i++
                }
                blocks.add(ContentBlock.CalloutBox("COMMON MISCONCEPTIONS", textBuf.toString().trim(), CalloutType.MISCONCEPTION))
                continue
            }
            line.startsWith("Real Indian Example:", ignoreCase = true) -> {
                blocks.add(ContentBlock.CalloutBox("REAL INDIAN CASE STUDY 🇮🇳", line, CalloutType.EXAMPLE))
                i++
                continue
            }
            line.startsWith("#") || line.matches(Regex("""^\d+\.\d+.*""")) -> {
                blocks.add(ContentBlock.Header(line.replace("#", "").trim()))
                i++
                continue
            }
            else -> {
                blocks.add(ContentBlock.Paragraph(line))
                i++
            }
        }
    }

    return blocks
}

@Composable
fun RenderMarkdownTable(tableData: ContentBlock.Table) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(InstagramDarkSurface)
            .border(0.8.dp, InstagramBorderDark, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InstagramElevatedSurface)
                    .padding(8.dp)
            ) {
                tableData.headers.forEach { header ->
                    Text(
                        text = header,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PwAmberGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(color = InstagramBorderDark)

            tableData.rows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (index % 2 == 0) InstagramDarkSurface else InstagramElevatedSurface.copy(alpha = 0.5f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { cell ->
                        Text(
                            text = cell,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                HorizontalDivider(color = InstagramBorderDark.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun RenderCalloutBox(title: String, text: String, type: CalloutType) {
    val (borderColor, icon, titleColor) = when (type) {
        CalloutType.MISTAKE -> Triple(InstagramHeartRed, Icons.Default.ErrorOutline, InstagramHeartRed)
        CalloutType.TAKEAWAY -> Triple(PwEmeraldGreen, Icons.Default.CheckCircle, PwEmeraldGreen)
        CalloutType.MISCONCEPTION -> Triple(PwAmberGold, Icons.Default.Lightbulb, PwAmberGold)
        CalloutType.EXAMPLE -> Triple(PwElectricBlue, Icons.Default.Star, PwElectricBlue)
        CalloutType.NOTE -> Triple(IndigoSecondary, Icons.Default.Star, IndigoSecondary)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InstagramDarkSurface)
            .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = titleColor, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 20.sp
            )
        }
    }
}
