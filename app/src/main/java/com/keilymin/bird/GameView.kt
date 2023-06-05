package com.keilymin.bird

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GameView(context: Context) : SurfaceView(context), Runnable {
    private var gameThread: Thread? = null
    private var surfaceHolder: SurfaceHolder = holder
    private var isPlaying = false
    private var isPause = false

    private var bird: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
    private var pipeTop: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.pipe_top)
    private var pipeBottom: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.pipe_bot)
    private var background: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.background)

    private var birdX = 0f
    private var birdY = 0f
    private var birdVelocity = 0f

    private var pipeX = 0f
    private var pipeGapY = 0f
    private var pipeOffset = 0f

    private var score = 0

    private val scorePaint = Paint().apply {
        color = Color.BLACK
        textSize = 48f
        typeface = Typeface.DEFAULT_BOLD
    }

    private var gameStarted = false
    private val startButtonRect = RectF()
    private val startButtonPaint = Paint().apply {
        color = Color.GREEN
    }
    private val startButtonTextPaint = Paint().apply {
        color = Color.BLACK
        textSize = 72f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private var isGameOver = false

    private val birdRect = Rect()
    private val pipeTopRect = Rect()
    private val pipeBottomRect = Rect()

    init {
        birdX = 100f
        birdY = (resources.displayMetrics.heightPixels / 2).toFloat()
        pipeX = resources.displayMetrics.widthPixels.toFloat()
        pipeGapY = resources.displayMetrics.heightPixels.toFloat() / 4
        pipeOffset = resources.displayMetrics.heightPixels.toFloat() / 8

        val pipeFactorWidth = 0.8f
        val pipeFactorHeight = 1.2f

        pipeBottom = Bitmap.createScaledBitmap(
            pipeBottom,
            (pipeBottom.width * pipeFactorWidth).toInt(),
            (pipeBottom.height * pipeFactorHeight).toInt(),
            false
        )

        pipeTop = Bitmap.createScaledBitmap(
            pipeTop,
            (pipeTop.width * pipeFactorWidth).toInt(),
            (pipeTop.height * pipeFactorHeight).toInt(),
            false
        )

        val factor: Float = resources.displayMetrics.heightPixels / background.height.toFloat()

        background = Bitmap.createScaledBitmap(
            background,
            (background.width * factor).toInt(), (background.height * factor).toInt(), false
        )

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val touchX = event.x
                val touchY = event.y
                if (isGameOver) {
                    isGameOver = false
                    gameStarted = true
                    score = 0
                    false
                } else if (isPause){
                    isPause = false
                    gameStarted = true
                    false
                } else if (!gameStarted && startButtonRect.contains(touchX, touchY)) {
                    gameStarted = true
                    false
                }
            }
            false
        }
    }

    override fun run() {
        GlobalScope.launch {
            while (isPlaying) {
                if (surfaceHolder.surface.isValid) {
                    val canvas = surfaceHolder.lockCanvas()
                    canvas.drawColor(Color.WHITE)
                    canvas.drawBitmap(background, 0f, 0f, null)

                    if (isGameOver || isPause){
                        val centerX = canvas.width / 2f
                        val centerY = canvas.height / 2f
                        var upText: String
                        var downText: String
                        if (isGameOver){
                            upText = "Game Over"
                            downText = "Tap to Restart"
                        } else{
                            upText = "Game Paused"
                            downText = "Tap to Continue"
                        }
                        canvas.drawText(upText, centerX, centerY, startButtonTextPaint)
                        canvas.drawText(downText, centerX, centerY + startButtonTextPaint.textSize, startButtonTextPaint)

                        canvas.drawText("Score: $score", centerX, centerY - startButtonTextPaint.textSize * 5, startButtonTextPaint)
                    } else if (!gameStarted) {
                        // Draw start button
                        val centerX = canvas.width / 2f
                        val centerY = canvas.height / 2f
                        val buttonWidth = 400f
                        val buttonHeight = 200f
                        startButtonRect.set(
                            centerX - buttonWidth / 2, centerY - buttonHeight / 2,
                            centerX + buttonWidth / 2, centerY + buttonHeight / 2
                        )
                        canvas.drawRect(startButtonRect, startButtonPaint)
                        canvas.drawText(
                            "Start",
                            centerX,
                            centerY + startButtonPaint.textSize,
                            startButtonTextPaint
                        )
                    } else if (gameStarted) {
                        canvas.drawBitmap(bird, birdX, birdY, null)
                        canvas.drawBitmap(
                            pipeTop,
                            pipeX,
                            pipeGapY - pipeTop.height - pipeOffset,
                            null
                        )
                        canvas.drawBitmap(pipeBottom, pipeX, pipeGapY + pipeOffset, null)

                        canvas.drawText("Score: $score", 20f, 60f, scorePaint)
                    }

                    surfaceHolder.unlockCanvasAndPost(canvas)
                }

                if (gameStarted) {
                    birdY += birdVelocity
                    birdVelocity += 1f

                    pipeX -= 10f

                    if (pipeX + pipeTop.width < 0) {
                        pipeX = resources.displayMetrics.widthPixels.toFloat()
                        pipeGapY = randomFloat(
                            pipeOffset,
                            resources.displayMetrics.heightPixels.toFloat() - pipeOffset
                        )
                        score++
                    }

                    if (birdY + bird.height > resources.displayMetrics.heightPixels || birdY < 0) {
                        gameOver()
                    }

                    birdRect.set(
                        birdX.toInt(),
                        birdY.toInt(),
                        birdX.toInt() + bird.width,
                        birdY.toInt() + bird.height
                    )

                    pipeTopRect.set(
                        pipeX.toInt(),
                        (pipeGapY - pipeTop.height - pipeOffset).toInt(),
                        pipeX.toInt() + pipeTop.width,
                        (pipeGapY - pipeOffset).toInt()
                    )

                    pipeBottomRect.set(
                        pipeX.toInt(),
                        (pipeGapY + pipeOffset).toInt(),
                        pipeX.toInt() + pipeBottom.width,
                        (pipeGapY + pipeBottom.height + pipeOffset).toInt()
                    )

                    if (Rect.intersects(birdRect, pipeTopRect) || Rect.intersects(
                            birdRect,
                            pipeBottomRect
                        )
                    ) {
                        gameOver()
                    }
                }
            }
        }
    }

    fun resume() {
        isPlaying = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    fun pause() {
        isPause = true
        isPlaying = false
        gameStarted = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> birdVelocity = -20f
        }
        return true
    }

    private fun gameOver() {
        isGameOver = true
        gameStarted = false
        birdX = 100f
        birdY = (resources.displayMetrics.heightPixels / 2).toFloat()
        pipeX = resources.displayMetrics.widthPixels.toFloat()
        pipeGapY = randomFloat(
            pipeOffset,
            resources.displayMetrics.heightPixels.toFloat() - pipeOffset
        )
    }

    private fun randomFloat(min: Float, max: Float): Float {
        return min + (Math.random() * (max - min)).toFloat()
    }
}