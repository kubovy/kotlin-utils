package com.poterion.utils.kotlin

import java.time.Duration

/**
 * Transforms this [Number], representing number if milliseconds, to a [Duration].
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return [Duration]
 */
fun Number.toDuration(): Duration = Duration.ofMillis(toLong())

/**
 * Formats this [Number], representing number of milliseconds, to duration.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return Formated duration
 */
fun Number.toFormattedDuration(): String = toDuration().formatString()

/**
 * Uses this [Number] as number of miliseconds and formats it as duration.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return [String] formatted duration
 */
fun Duration.formatString(): String {
	val days = toDays()
		.takeIf { it > 0 }?.let { "${it} days " }
	val hours = toHours()
		.takeIf { days != null || it > 0 }
		?.let { "%02d:".format(it) }
	val minutes = toMinutes()
		.takeIf { hours != null || it > 0 }
		?.let { "%02d:".format(it) }
	val seconds = seconds.rem(60)
		.takeIf { minutes != null || it > 0 }
		?.let { "%0${if (minutes != null) 2 else 1}d.".format(it) }
	val millis = toMillis().rem(1000)
		.takeIf { seconds != null || it > 0 }
		?.let { "%0${if (seconds != null) 3 else 1}d".format(it) }
	val suffix = when {
		days == null && hours == null && minutes == null && seconds == null -> "ms"
		days == null && hours == null && minutes == null -> "s"
		else -> null
	}
	return listOfNotNull(days, hours, minutes, seconds, millis, suffix).joinToString("")
}
