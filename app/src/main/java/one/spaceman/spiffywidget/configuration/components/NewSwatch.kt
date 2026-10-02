package one.spaceman.spiffywidget.configuration.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun NewSwatch(
    color1: Color = Color.Red,
    color2: Color = Color.Green,
    selected: Boolean = true,
    onClick: () -> Unit = {},
) {
//    val outlineColor = LocalContentColor.current
    val outlineColor = LocalContentColor.current
    Box(modifier = Modifier
        .size(65.dp)
        .padding(5.dp)
        .clickable { onClick() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            scale(0.8f) {
                drawCircle(color1)
                drawArc(
                    color = color2,
                    startAngle = 270f,
                    sweepAngle = 180f,
                    useCenter = true,
                )
            }
            if (selected) {
                drawArc(
                    color = outlineColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = true,
                    style = Stroke(10f),
                )

            }
        }
    }
}