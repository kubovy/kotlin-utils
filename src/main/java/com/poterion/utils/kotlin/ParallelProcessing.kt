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

import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.streams.toList

/**
 * Sugar to create a parallel [stream][java.util.stream.Stream] and execute an intermediate `action` on each element
 * in that [stream][java.util.stream.Stream].
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param parallelism Parallelism (default: available cores - 2 or 3 whichever is bigger)
 * @param action Action to execute on each element
 * @return [Collection] after the processing
 */
fun <T> Collection<T>.parallelStreamIntermediate(
		parallelism: Int = kotlin.math.max(3, Runtime.getRuntime().availableProcessors() - 2),
		action: (T) -> Unit): Collection<T> = ForkJoinPool(parallelism)
		.submit(Callable<Collection<T>> { parallelStream().intermediate(action).toList() })
		.get() as Collection<T>

/**
 * Sugar to create a parallel [stream][java.util.stream.Stream] and maps each element using the given `transform`.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param parallelism Parallelism (default: available cores - 2 or 3 whichever is bigger)
 * @param transform Transform function
 * @return [Collection] after the processing
 */
fun <T, R> Collection<T>.parallelStreamMap(
		parallelism: Int = kotlin.math.max(3, Runtime.getRuntime().availableProcessors() - 2),
		transform: (T) -> R): Collection<R> = ForkJoinPool(parallelism)
		.submit(Callable<Collection<R>> { parallelStream().map(transform).toList() })
		.get() as Collection<R>

/**
 * Sugar to filter an [Iterable] in a new [Thread] in parallel.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param parallelism Parallelism (default: available cores - 2 or 3 whichever is bigger)
 * @param executor [ExecutorService]
 * @param predicate Filter predicate
 * @return [Iterable] without filtered-out elements
 */
fun <T> Iterable<T>.parallelFilter(
		parallelism: Int = kotlin.math.max(3, Runtime.getRuntime().availableProcessors() - 2),
		executor: ExecutorService = Executors.newFixedThreadPool(parallelism),
		predicate: (T) -> Boolean): List<T> {

	// default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
	val defaultSize = if (this is Collection<*>) this.size else 10
	val destination = Collections.synchronizedList(ArrayList<T>(defaultSize))

	for (item in this) {
		executor.submit { if (predicate(item)) destination.add(item) }
	}

	executor.shutdown()
	executor.awaitTermination(1, TimeUnit.DAYS)

	return ArrayList<T>(destination)
}

/**
 * Sugar to map an [Iterable] in a new [Thread] in parallel.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param parallelism Parallelism (default: available cores - 2 or 3 whichever is bigger)
 * @param executor [ExecutorService]
 * @param transform Transformation function
 * @return [Iterable] after processing
 */
fun <T, R> Iterable<T>.parallelMap(
		parallelism: Int = kotlin.math.max(3, Runtime.getRuntime().availableProcessors() - 2),
		executor: ExecutorService = Executors.newFixedThreadPool(parallelism),
		transform: (T) -> R): List<R> {

	// default size is just an inlined version of kotlin.collections.collectionSizeOrDefault
	val defaultSize = if (this is Collection<*>) this.size else 10
	val destination = Collections.synchronizedList(ArrayList<R>(defaultSize))

	for (item in this) {
		executor.submit { destination.add(transform(item)) }
	}

	executor.shutdown()
	executor.awaitTermination(1, TimeUnit.DAYS)

	return ArrayList<R>(destination)
}