@file:Suppress("unused")
package com.poterion.utils.kotlin

/**
 * No operation sugar to use, e.g., in `when` block:
 *
 *    when(/*something*/) {
 *      VALUE1 -> // ...
 *      VALUE2 -> // ...
 *      else -> noop()
 *    }
 *
 * This way some common linters will not complain.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 */
fun noop() {
	// This is a no-op helper
}