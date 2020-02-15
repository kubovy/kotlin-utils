@file:Suppress("unused")

package com.poterion.utils.kotlin

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

fun <T> readLock(lock: ReentrantReadWriteLock?, procedure: () -> T): T =
		if (lock == null) procedure() else lock.read { procedure() }

fun <T> writeLock(lock: ReentrantReadWriteLock?, procedure: () -> T): T =
		if (lock == null) procedure() else lock.write { procedure() }
