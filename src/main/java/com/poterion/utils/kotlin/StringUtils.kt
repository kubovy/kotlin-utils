@file:Suppress("unused")
package com.poterion.utils.kotlin

import java.net.URI
import java.net.URISyntaxException

/**
 * Trims end of this [String] if its longer than `maxLength`. If this [String] was trimmed, if will be appended with
 * `ellipsis`. The resulting string may be therefore `maxLength + ellipsis.size` long.
 *
 * Trimming respects words and cuts on spaces. A very long sequence of characters with no spaces in between will not
 * be shortened.
 *
 * In case the current [String] is shorter than `maxLength` it will not be changed.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param maxLength Requested maximum length of the resulting string (plus possible length of `ellipsis`)
 * @return [String] shorter than `maxLength` if its content allows.
 */
fun String.cutLastWords(maxLength: Int, ellipsis: String = "..."): String {
	var value = this
	while (value.length > maxLength) {
		value = value.substringBeforeLast(" ", value.substring(0, value.length - 1))
	}
	if (value != this) value += ellipsis
	return value
}

/**
 * Splits this [String] to a set of [strings][String] using the given `separator`. The resulting [Set] will not contain
 * any blank items.
 *
 *     "a,b,c" -> ["a","b","c"]
 *     "a,b,b" -> ["a","b"]
 *     "a,,c," -> ["a","c"]
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param separator Separator
 * @return [Set] of [strings][String]
 */
fun String.toSet(separator: String) = split(separator)
		.map { it.trim() }
		.filterNot { it.isBlank() }
		.toSet()

/**
 * Sugar to convert a [String] to [URI].
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return [URI]
 * @exception URISyntaxException
 */
@Throws(URISyntaxException::class)
fun String.toUri(): URI = URI(this)

/**
 * Sugar to convert a [String] to [URI].
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return [URI] or `null` if this [String] cannot be converted to [URI]
 */
fun String.toUriOrNull(): URI? = try {
	toUri()
} catch (e: URISyntaxException) {
	null
}