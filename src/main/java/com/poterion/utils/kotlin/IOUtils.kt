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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path
import java.security.MessageDigest
import java.util.zip.GZIPInputStream
import javax.xml.bind.DatatypeConverter

private val LOGGER = LoggerFactory.getLogger("com.poterion.footprint.manager.utils.IOUtils")
const val BUFFER_SIZE = 8192

/**
 * Assumes this [String] represents a filename and tries to remove its extension.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return Filename without extension
 */
fun String.fileNameWithoutExtension(): String = substringBeforeLast(".")

/**
 * Returns this [Path]'s filename without extension
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return Filename without extension
 */
fun Path.fileNameWithoutExtension(): String = fileName.toString().fileNameWithoutExtension()

/**
 * Assumes this [String] represent a filename and tries to detect its extension.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return Filename's extension
 */
fun String.fileExtension(): String = substringAfterLast(".", "").takeIf { it.length < 5 } ?: ""

/**
 * Calculates a hash of this [File]
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param algorithm Hash algorithm
 * @return Calculated hash
 */
fun File.calculateHash(algorithm: String) = FileInputStream(this).calculateHash(algorithm)

/**
 * Calculates a hash of this [ByteArray]
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param algorithm Hash algorithm
 * @return Calculated hash
 */
fun ByteArray.calculateHash(algorithm: String): String {
	val digest = MessageDigest.getInstance(algorithm)
	digest.update(this)
	return DatatypeConverter.printHexBinary(digest.digest())
}

/**
 * Calculates a hash of this [InputStream]
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param algorithm Hash algorithm
 * @return Calculated hash
 */
fun InputStream.calculateHash(algorithm: String): String = BufferedInputStream(this).use { inputStream ->
	val digest = MessageDigest.getInstance(algorithm)
	val block = ByteArray(BUFFER_SIZE)
	var length: Int
	while (inputStream.read(block).also { length = it } > 0) {
		digest.update(block, 0, length)
	}
	return DatatypeConverter.printHexBinary(digest.digest())
}

/**
 * Copies the content of this InputStream into an OutputStream
 *
 * @param output Target [OutputStream]
 * @param bufferSize Buffer size to use, must be bigger than 0
 * @param reporter Reporter of copied bytes
 * @return the number of bytes copied
 */
fun InputStream.copyTo(output: OutputStream, bufferSize: Int = BUFFER_SIZE, reporter: (Long) -> Unit = {}): Long {
	require(bufferSize >= 1) { "Buffer size must be bigger than 0" }
	val buffer = ByteArray(bufferSize)
	var n: Int
	var count: Long = 0
	while (this.read(buffer).also { n = it } != -1) {
		output.write(buffer, 0, n)
		count += n.toLong()
		reporter(count)
	}
	return count
}

/**
 * Transforms this [InputStream] to a [GZIPInputStream].
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return [GZIPInputStream]
 */
fun InputStream.gzipped() = GZIPInputStream(this)

/**
 * Transforms this [InputStream] to [TarArchiveInputStream]
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return [TarArchiveInputStream]
 */
fun InputStream.tar() = TarArchiveInputStream(this)

/**
 * Extracts TAR represented by this [InputStream] to given `destinationPath`.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param destinationPath Destination [Path]
 */
fun InputStream.unTarTo(destinationPath: Path,
						bufferSize: Int = BUFFER_SIZE,
						reporter: (Long) -> Unit = {}) = tar()
		.extractTo(destinationPath, bufferSize, reporter)

/**
 * Extracts GZIPed TAR represented by this [InputStream] to given `destinationPath`.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param destinationPath Destination [Path]
 */
fun InputStream.unGzipTarTo(destinationPath: Path,
							bufferSize: Int = BUFFER_SIZE,
							reporter: (Long) -> Unit = {}) =
		gzipped().unTarTo(destinationPath, bufferSize, reporter)

/**
 * Extracts this [TarArchiveInputStream] to given `destinationPath`.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param destinationPath Destination [Path]
 */
fun TarArchiveInputStream.extractTo(destinationPath: Path,
									bufferSize: Int = BUFFER_SIZE,
									reporter: (Long) -> Unit = {}) = use {
	try {
		var tarEntry: TarArchiveEntry?
		while (nextTarEntry.also { tarEntry = it } != null) {
			if (tarEntry?.isDirectory != false) continue
			val outputFile = destinationPath.resolve(tarEntry!!.name).toFile()
				.also { it.parentFile.mkdirs() }
			outputFile.outputStream().buffered(bufferSize).use { outputStream ->
				reporter(this.copyTo(outputStream, bufferSize))
			}
		}
	} catch (t: Throwable) {
		LOGGER.error(t.message, t)
	}
}