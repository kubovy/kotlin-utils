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

import java.util.stream.Stream

/**
 * @author Jan Kubovy [jan@kubovy.eu]
 */

/**
 * Replace the whole collection `with` the given iterable.
 *
 * This will remove all items from the collection which are not present in the `with` replacement and add all
 * items from the `with` replacement which are not present in this collection
 *
 * @param with Replacement.
 * @return Whether success or not.
 * @author Jan Kubovy [jan@kubovy.eu]
 */
fun <T> MutableCollection<T>.setAll(with: Iterable<T>): Boolean {
	val new = with - this
	val old = this - with
	val removed = removeAll(old)
	val added = addAll(new)
	return removed && added
}

/**
 * Replace the whole map `with` the given map.
 *
 * This will remove all mappings from the map which are not present in the `with` replacement, add all mappings
 * from the `with` replacement which are not present in this collection and update all mappings which are different.
 *
 * @param with Replacement.
 * @return Whether success or not.
 * @author Jan Kubovy [jan@kubovy.eu]
 */
fun <K, V> MutableMap<K, V>.setAll(with: Map<K, V>) {
	val newKeys = with.keys - this.keys
	val oldKeys = this.keys - with.keys
	val updated = with.filter { (k, v) -> this.keys.contains(k) && this[k] != v }

	for (key in oldKeys) remove(key)
	putAll(with.filterKeys { it in newKeys })
	putAll(updated)
}

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

/**
 * Checks if this [Collection] contains exactly all elements from the given `other` [Collection]. The order is not
 * important.
 *
 * @param other The other [Collection]
 * @return Whether this and the `other` [Collection]'s contain exactly the same elements.
 */
fun <T> Collection<T>.containsExactly(other: Collection<T>): Boolean = size == other.size && containsAll(other)

/**
 * Checks if this [Iterable] contains exactly all elements from the given `other` [Iterable]. The order is not
 * important.
 *
 * @param other The other [Iterable]
 * @return Whether this and the `other` [Iterable]'s contain exactly the same elements.
 */
fun <T> Iterable<T>.containsExactly(other: Iterable<T>): Boolean =
		all { other.contains(it) } && other.all { contains(it) }

/**
 * Checks if this [Iterable] contains exactly all elements from the given `other` [Iterable]. The order is not
 * important.
 *
 * @param other The other [Iterable]
 * @param mapper Mapper to be applied to all elements in both [iterables][Iterable] before the comparison.
 * @return Whether this and the `other` [Iterable]'s contain exactly the same elements.
 */
fun <T, R> Iterable<T>.containsExactly(other: Iterable<T>, mapper: (T?) -> R?): Boolean =
		map(mapper).containsExactly(other.map(mapper))

