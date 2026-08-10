package com.dantruong.smartvehicletelemetrydashboard_mobile.presentation.weatherforecast

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dantruong.smartvehicletelemetrydashboard_mobile.R
import com.dantruong.smartvehicletelemetrydashboard_mobile.domain.model.HourlyWeatherForecast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HourlyWeatherAdapter : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {

    private val items = mutableListOf<HourlyWeatherForecast>()

    fun submitList(newItems: List<HourlyWeatherForecast>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        return HourlyWeatherViewHolder(createRow(parent))
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    private fun createRow(parent: ViewGroup): LinearLayout {
        val density = parent.resources.displayMetrics.density
        val horizontalPadding = (16 * density).toInt()
        val verticalPadding = (14 * density).toInt()
        val rowMargin = (5 * density).toInt()

        return LinearLayout(parent.context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
            background = GradientDrawable().apply {
                cornerRadius = 18 * density
                setColor(0x9E1E2026.toInt())
                setStroke((1 * density).toInt(), 0x10FFFFFF)
            }
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, rowMargin, 0, rowMargin)
            }

            addView(
                createConditionBadge(context, density)
            )

            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                        marginStart = (14 * density).toInt()
                    }

                    addView(createTextView(context, textSize = 16f, color = Color.WHITE, style = Typeface.BOLD).apply {
                        id = View.generateViewId()
                        tag = TIME_TAG
                    })
                    addView(createTextView(context, textSize = 12f, color = SECONDARY_TEXT).apply {
                        id = View.generateViewId()
                        tag = DESCRIPTION_TAG
                    })
                }
            )

            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.END
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    addView(createTextView(context, textSize = 20f, color = Color.WHITE, style = Typeface.BOLD).apply {
                        gravity = Gravity.END
                        id = View.generateViewId()
                        tag = TEMP_TAG
                    })
                    addView(createTextView(context, textSize = 12f, color = SECONDARY_TEXT, style = Typeface.BOLD).apply {
                        gravity = Gravity.END
                        id = View.generateViewId()
                        tag = WIND_TAG
                    })
                }
            )
        }
    }

    private fun createConditionBadge(context: Context, density: Float): FrameLayout {
        return FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams((48 * density).toInt(), (48 * density).toInt())
            background = GradientDrawable().apply {
                cornerRadius = 24 * density
                setColor(0x2E00B4D8)
                setStroke((1 * density).toInt(), 0x6600B4D8)
            }
            addView(
                ImageView(context).apply {
                    tag = CONDITION_ICON_TAG
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    layoutParams = FrameLayout.LayoutParams(
                        (30 * density).toInt(),
                        (30 * density).toInt(),
                        Gravity.CENTER
                    )
                }
            )
        }
    }

    private fun createTextView(
        context: Context,
        textSize: Float,
        color: Int,
        style: Int = Typeface.NORMAL
    ): TextView {
        return TextView(context).apply {
            this.textSize = textSize
            setTextColor(color)
            typeface = Typeface.DEFAULT_BOLD.takeIf { style == Typeface.BOLD } ?: Typeface.DEFAULT
            includeFontPadding = false
        }
    }

    class HourlyWeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: HourlyWeatherForecast) {
            itemView.findImage(CONDITION_ICON_TAG).setImageResource(weatherIconRes(item.weatherCode))
            itemView.findText(TIME_TAG).text = formatHour(item.time)
            itemView.findText(DESCRIPTION_TAG).text = weatherDescription(item.weatherCode)
            itemView.findText(TEMP_TAG).text = "${item.temperature.toInt()}°C"
            itemView.findText(WIND_TAG).text = "Wind ${item.windSpeed.toInt()} km/h"
        }

        private fun View.findText(tag: String): TextView {
            return findViewWithTag(tag)
        }

        private fun View.findImage(tag: String): ImageView {
            return findViewWithTag(tag)
        }
    }

    companion object {
        private const val TIME_TAG = "time"
        private const val DESCRIPTION_TAG = "description"
        private const val TEMP_TAG = "temperature"
        private const val WIND_TAG = "wind"
        private const val CONDITION_ICON_TAG = "condition_icon"
        private const val SECONDARY_TEXT = 0xFF9E9E9E.toInt()
    }
}

private fun formatHour(time: String): String {
    return runCatching {
        LocalDateTime.parse(time).format(DateTimeFormatter.ofPattern("HH:mm"))
    }.getOrDefault(time)
}

private fun weatherDescription(code: Int): String {
    return when (code) {
        0 -> "Clear Sky"
        1, 2, 3 -> "Partly Cloudy"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rainy"
        80, 81, 82 -> "Showers"
        else -> "Sunny"
    }
}

private fun weatherIconRes(code: Int): Int {
    return when (code) {
        0 -> R.drawable.ic_weather_sunny
        1, 2, 3 -> R.drawable.ic_weather_cloudy
        45, 48 -> R.drawable.ic_weather_foggy
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> R.drawable.ic_weather_rainy
        else -> R.drawable.ic_weather_sunny
    }
}
