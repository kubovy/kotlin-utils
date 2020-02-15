/******************************************************************************
 * Copyright (C) 2020 Jan Kubovy (jan@kubovy.eu)                              *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify it    *
 * under the terms of the GNU Lesser General Public License as published      *
 * by the Free Software Foundation, either version 3 of the License, or (at   *
 * your option) any later version.                                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Lesser General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this program.  If not, see                              *
 * <http://www.gnu.org/licenses/>.                                            *
 ******************************************************************************/
@file:Suppress("unused")
package com.poterion.utils.kotlin

import java.net.URI
import java.net.URISyntaxException
import java.nio.charset.Charset
import java.util.*

/**
 * Ensures this [String] has the given `suffix`.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param suffix Suffix to ensure
 * @param ignoreCase Ignore `suffix` case
 * @return [String] with given `suffix`
 */
fun String.ensureSuffix(suffix: String, ignoreCase: Boolean = false) =
		if (endsWith(suffix, ignoreCase)) this else "${this}${suffix}"

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

/**
 * Encode this [String] with [Base64]
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return [Base64] encoded [String]
 */
fun String.base64Encode(charset: Charset = Charsets.UTF_8): String = toByteArray(charset).base64Encode()

/**
 * Decode this [Base64] encoded [String]
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return Decoded [String]
 */
fun String.base64Decode(): ByteArray = Base64.getDecoder().decode(this)
