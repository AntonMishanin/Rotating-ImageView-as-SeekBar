package com.paint.rotatingimage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt


class RotatingByTouchImageView(context: Context, attributeSet: AttributeSet) :
    androidx.appcompat.widget.AppCompatImageView(context, attributeSet) {

    var minValue: Float = 0f
    var maxValue: Float = 0f
    var valueChanged: ((Float) -> Unit?)? = null

    private var angle = 0f
    private var previousAngle = 0f

    private var startX = 0f
    private var startY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        onTouchHandler(event)
        return true
    }

    private fun onTouchHandler(event: MotionEvent) {
        val centerX: Float = this.width.toFloat() / 2
        val centerY: Float = this.height.toFloat() / 2

        val currentX = event.x
        val currentY = event.y
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            startX = event.x
            startY = event.y
            previousAngle = angle
        } else if (event.actionMasked == MotionEvent.ACTION_MOVE) {

            //Init points
            val a = Point(centerX.toInt(), centerY.toInt())
            val b = Point(startX.toInt(), startY.toInt())
            val c = Point(currentX.toInt(), currentY.toInt())

            //Init lines.
            val ab = sqrt((b.x - a.x).toDouble().pow(2.0) + (b.y - a.y).toDouble().pow(2.0))
            val ac = sqrt((c.x - a.x).toDouble().pow(2.0) + (c.y - a.y).toDouble().pow(2.0))
            val bc = sqrt((c.x - b.x).toDouble().pow(2.0) + (c.y - b.y).toDouble().pow(2.0))

            //Calculate ratio and angle between lines.
            val ratio = (ab * ab + ac * ac - bc * bc) / (2 * ac * ab);
            angle = (acos(ratio) * (180 / Math.PI)).toFloat()


            //Верхняя четверть, ограниченная диагоналями:
            //y = 2*centerX - x
            //y = x
            if (currentY <= 2 * centerX - currentX && currentY <= currentX) {
                if (currentX >= startX) {
                    angle *= 1
                } else {
                    angle *= -1
                }
            }
            //Нижняя четверть, ограниченная диагоналями.
            else if (currentY >= 2 * centerX - currentX && currentY >= currentX) {
                if (currentX >= startX) {
                    angle *= -1
                } else {
                    angle *= 1
                }
            }
            //Правая четверть, ограниченная диагоналями.
            else if (currentY >= 2 * centerX - currentX && currentY <= currentX) {
                if (currentY >= startY) {
                    angle *= 1
                } else {
                    angle *= -1
                }
            }
            //Левая четверть, ограниченная диагоналями.
            else if (currentY <= 2 * centerX - currentX && currentY >= currentX) {
                if (currentY >= startY) {
                    angle *= -1
                } else {
                    angle *= 1
                }
            }

            angle += previousAngle

            angle %= 360

            startX = event.x
            startY = event.y
            previousAngle = angle

            onRotation(angle)
            calculateValue()
        }
    }

    private fun onRotation(angle: Float) {
        this.scaleType = ScaleType.MATRIX

        val value = FloatArray(9)
        val matrix = this.matrix
        matrix?.getValues(value)

        val coefficient = (this.width).toFloat() / (this.drawable?.intrinsicWidth ?: 1).toFloat()
        value[0] = coefficient
        value[4] = coefficient

        matrix?.setValues(value)
        matrix?.postRotate(angle, (this.width) / 2f, (this.height) / 2f)
        this.imageMatrix = matrix
    }

    private fun calculateValue() {
        if (minValue < 0) {
            minValue *= -1f
        }
        if (maxValue < 0) {
            maxValue *= -1f
        }
        val sum = minValue + maxValue

        val currentValue = angle * (sum / 360) - minValue

        valueChanged?.invoke(currentValue)
    }
}