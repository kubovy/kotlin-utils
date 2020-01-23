@file:Suppress("unused")
package com.poterion.utils.kotlin

/**
 * Iterates over and calls provided `action` on each element.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param action Action to call on each element.
 * @return This [Iterable]
 */
inline fun <T> Iterable<T>.intermediate(action: (T) -> Unit): Iterable<T> = apply {
	for (element in this) action(element)
}

/**
 * Iterates over and calls provided `action` on each element.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param action Action to call on each element.
 * @return This [Iterable]
 */
inline fun <T> Iterable<T>.intermediateIndexed(action: (index: Int, T) -> Unit): Iterable<T> = apply {
	var index = 0
	for (element in this) action(index++, element)
}

/**
 * Iterates over and calls provided `action` on each element.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param action Action to call on each element.
 * @return This [Map]
 */
inline fun <K, V> Map<out K, V>.intermediate(action: (Map.Entry<K, V>) -> Unit): Map<out K, V> = apply {
	for (element in this) action(element)
}

/**
 * Appends an [Iterable] provided by `provider` to this [Iterable].
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param provider Provider of an [Iterable] to be appended
 * @return This [Iterable] with appended [Iterable] provided by the `provider`
 */
inline fun <T> Iterable<T>.append(provider: () -> Iterable<T>): Iterable<T> = this + provider()


