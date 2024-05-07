package net.kodein.cup.widget.material3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public class BulletPointsBuilder internal constructor() {
    internal val contents = ArrayList<Pair<Boolean, @Composable () -> Unit>>()

    public fun BulletPoint(visible: Boolean = true, content: @Composable () -> Unit) {
        contents += visible to content
    }
}

private enum class BulletPointsSlotsEnum { Main, Dependent }

@Composable
private fun BulletPointsContent(
    contents: List<Pair<Boolean, @Composable () -> Unit>>,
    horizontalAlignment: Alignment.Horizontal,
    spacedBy: Dp,
) {
    Column(
        horizontalAlignment = horizontalAlignment,
    ) {
        contents.forEachIndexed { index, (visible, content) ->
            AnimatedVisibility(visible) {
                Column {
                    if (index != 0) Spacer(Modifier.height(spacedBy))
                    Row {
                        Text("• ")
                        content()
                    }
                }
            }
        }
    }
}

@Composable
public fun BulletPoints(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    spacedBy: Dp = 8.dp,
    builder: BulletPointsBuilder.() -> Unit,
) {
    val contents = BulletPointsBuilder().apply(builder).contents
    SubcomposeLayout(modifier) { constraints ->
        val mainPlaceable = subcompose(BulletPointsSlotsEnum.Main) {
            BulletPointsContent(contents.map { true to it.second }, horizontalAlignment, spacedBy)
        }.first().measure(constraints)

        val sizedPlaceable = subcompose(BulletPointsSlotsEnum.Dependent) {
            BulletPointsContent(contents, horizontalAlignment, spacedBy)
        }.first().measure(constraints)

        layout(mainPlaceable.width, sizedPlaceable.height) {
            sizedPlaceable.place(0, 0)
        }
    }
}
