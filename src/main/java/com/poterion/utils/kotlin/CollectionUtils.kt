@file:Suppress("unused")
package com.poterion.utils.kotlin

import java.util.stream.Stream

/**
 * @author Jan Kubovy [jan@kubovy.eu]
 */

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
inline fun <T> Sequence<T>.intermediate(crossinline action: (T) -> Unit): Sequence<T> = map {
	action(it)
	it
}

/**
 * Iterates over and calls provided `action` on each element.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param action Action to call on each element.
 * @return This [Iterable]
 */
inline fun <T> Stream<T>.intermediate(crossinline action: (T) -> Unit): Stream<T> = map {
	action(it)
	it
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
 * @return This [Iterable]
 */
inline fun <T> Sequence<T>.intermediateIndexed(crossinline action: (Int, T) -> Unit): Sequence<T> = mapIndexed { i, it ->
	action(i, it)
	it
}

/**
 * Iterates over and calls provided `action` on each element which fulfills the given `predicate`.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param predicate Predicate
 * @param action Action to call on each element.
 * @return This [Iterable]
 */
inline fun <T> Iterable<T>.intermediateIf(predicate: (T) -> Boolean, action: (T) -> Unit): Iterable<T> {
	for (element in this) if (predicate(element)) action(element)
	return this
}

/**
 * Iterates over and calls provided `action` on each element which fulfills the given `predicate`.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param predicate Predicate
 * @param action Action to call on each element.
 * @return This [Iterable]
 */
inline fun <T> Sequence<T>.intermediateIf(crossinline predicate: (T) -> Boolean,
										  crossinline action: (T) -> Unit): Sequence<T> = map {
	if (predicate(it)) action(it)
	it
}

/**
 * Iterates over and calls provided `action` on each element which fulfills the given `predicate`.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param predicate Predicate
 * @param action Action to call on each element.
 * @return This [Iterable]
 */
inline fun <T> Stream<T>.intermediateIf(crossinline predicate: (T) -> Boolean,
										  crossinline action: (T) -> Unit): Stream<T> = map {
	if (predicate(it)) action(it)
	it
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


