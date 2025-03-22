
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import androidx.gridlayout.widget.GridLayout
import lab03.GameStates
import lab03.MemoryGameEvent
import lab03.Tile
import pl.wsei.pam.lab01.R

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()
    private val icons: List<Int> = listOf(
        R.drawable.baseline_rocket_launch_24,
        R.drawable.baseline_school_24,
        R.drawable.baseline_sailing_24,
        R.drawable.baseline_sports_basketball_24,
        R.drawable.baseline_alarm_24,
        R.drawable.baseline_anchor_24,
        R.drawable.baseline_airplanemode_active_24,
        R.drawable.baseline_airport_shuttle_24,
        R.drawable.baseline_attach_money_24,
        R.drawable.baseline_auto_awesome_24,
        R.drawable.baseline_back_hand_24,
        R.drawable.baseline_bar_chart_24,
        R.drawable.baseline_beach_access_24,
        R.drawable.baseline_bedtime_24,
        R.drawable.baseline_brush_24,
        R.drawable.baseline_bubble_chart_24,
        R.drawable.baseline_call_24,
        R.drawable.baseline_camera_alt_24
    )
    private val deckResource: Int = R.drawable.deck
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = { _ -> }
    private val matchedPair: ArrayDeque<Tile> = ArrayDeque()
    private val logic: MemoryGameLogic = MemoryGameLogic(cols * rows / 2)

    init {
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().apply {

            addAll(icons.subList(0, cols * rows / 2))
            addAll(icons.subList(0, cols * rows / 2))
            shuffle()
        }
        gridLayout.columnCount = cols
        gridLayout.rowCount = rows

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val btn = ImageButton(gridLayout.context).apply {
                    tag = "$row $col"
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        setGravity(Gravity.CENTER)
                        columnSpec = GridLayout.spec(col, 1, 1f)
                        rowSpec = GridLayout.spec(row, 1, 1f)
                    }

                }

                val resourceImage = shuffledIcons.removeAt(0)
                val tile = Tile(btn, resourceImage, deckResource)
                tiles[btn.tag.toString()] = tile
                gridLayout.addView(btn)

                btn.setOnClickListener { onClickTile(it) }
            }
        }
    }

    private fun onClickTile(v: View) {
        val tile = tiles[v.tag] ?: return
        if (tile.revealed) return
        tile.revealed = true
        tile.button.setImageResource(tile.tileResource)

        matchedPair.addFirst(tile)
        val matchResult = logic.process { tile.tileResource }

        val event = MemoryGameEvent(matchedPair.toList(), matchResult)
        onGameChangeStateListener.invoke(event)

        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
            android.os.Handler().postDelayed({
                matchedPair.forEach { it.revealed = false; it.button.setImageResource(deckResource) }
                matchedPair.clear()
            }, 1000)
        }
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }
}
