import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import androidx.gridlayout.widget.GridLayout
import lab03.GameStates
import lab03.MemoryGameEvent
import lab03.Tile
import pl.wsei.pam.lab01.R
import java.util.Random

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

    fun getState(): IntArray {
        return tiles.values.map {
            if (it.revealed) it.tileResource else -1
        }.toIntArray()
    }

    fun setState(state: IntArray) {
        val shuffledIcons = state.toMutableList()
        tiles.values.forEachIndexed { index, tile ->
            val resource = shuffledIcons.removeAt(0)
            tile.revealed = resource != -1
            tile.button.setImageResource(if (tile.revealed) resource else deckResource)
        }
    }

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
    private fun animatePairedButtons(firstButton: ImageButton, secondButton: ImageButton, action: Runnable) {
        val set = AnimatorSet()

        val rotationFirst = ObjectAnimator.ofFloat(firstButton, "rotation", 0f, 360f)
        val rotationSecond = ObjectAnimator.ofFloat(secondButton, "rotation", 0f, 360f)

        val scaleXFirst = ObjectAnimator.ofFloat(firstButton, "scaleX", 1f, 1.2f, 1f)
        val scaleXSecond = ObjectAnimator.ofFloat(secondButton, "scaleX", 1f, 1.2f, 1f)

        val scaleYFirst = ObjectAnimator.ofFloat(firstButton, "scaleY", 1f, 1.2f, 1f)
        val scaleYSecond = ObjectAnimator.ofFloat(secondButton, "scaleY", 1f, 1.2f, 1f)

        set.playTogether(rotationFirst, rotationSecond, scaleXFirst, scaleXSecond, scaleYFirst, scaleYSecond)
        set.duration = 800
        set.interpolator = DecelerateInterpolator()

        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                action.run()
            }
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        set.start()
    }



    private fun animateIncorrectPair(button1: ImageButton, button2: ImageButton, action: Runnable) {
        val set = AnimatorSet()

        val rotation1 = ObjectAnimator.ofFloat(button1, "rotation", -10f, 10f, -5f, 5f, 0f)
        val rotation2 = ObjectAnimator.ofFloat(button2, "rotation", 10f, -10f, 5f, -5f, 0f)

        set.playTogether(rotation1, rotation2)
        set.duration = 500
        set.interpolator = DecelerateInterpolator()

        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}

            override fun onAnimationEnd(animator: Animator) {
                action.run()
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })

        set.start()
    }


    private fun onClickTile(v: View) {
        val tile = tiles[v.tag] ?: return
        if (tile.revealed) return

        tile.revealed = true
        tile.button.setImageResource(tile.tileResource)

        if (matchedPair.isEmpty()) {
            matchedPair.addFirst(tile)
        } else if (matchedPair.size == 1) {
            matchedPair.addLast(tile)
        }

        if (matchedPair.size == 2) {
            val firstTile = matchedPair[0]
            val secondTile = matchedPair[1]

            val matchResult = if (firstTile.tileResource == secondTile.tileResource) {
                GameStates.Matching
            } else {
                GameStates.NoMatch
            }

            val event = MemoryGameEvent(matchedPair.toList(), matchResult)
            onGameChangeStateListener.invoke(event)

            if (matchResult == GameStates.Matching) {
                animatePairedButtons(matchedPair[0].button, matchedPair[1].button) {
                    matchedPair.clear()
                }
            } else {
                animateIncorrectPair(firstTile.button, secondTile.button) {
                    firstTile.revealed = false
                    secondTile.revealed = false
                    firstTile.button.setImageResource(deckResource)
                    secondTile.button.setImageResource(deckResource)
                    matchedPair.clear()
                }
            }
        }
    }


    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }
}


