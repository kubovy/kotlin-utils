@file:Suppress("unused")
package com.poterion.utils.kotlin

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.utils.IOUtils
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.file.Path
import java.security.MessageDigest
import java.util.zip.GZIPInputStream
import javax.xml.bind.DatatypeConverter

private val LOGGER = LoggerFactory.getLogger("com.poterion.footprint.manager.utils.IOUtils")

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
	val block = ByteArray(8192)
	var length: Int
	while (inputStream.read(block).also { length = it } > 0) {
		digest.update(block, 0, length)
	}
	return DatatypeConverter.printHexBinary(digest.digest())
}

/**
 * Transforms this [InputStream] to a [GZIPInputStream].
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return [GZIPInputStream]
 */
fun InputStream.gzipped() = GZIPInputStream(BufferedInputStream(this))

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
fun InputStream.unTarTo(destinationPath: Path) = tar().extractTo(destinationPath)

/**
 * Extracts GZIPed TAR represented by this [InputStream] to given `destinationPath`.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param destinationPath Destination [Path]
 */
fun InputStream.unGzipTarTo(destinationPath: Path) = gzipped().unTarTo(destinationPath)

/**
 * Extracts this [TarArchiveInputStream] to given `destinationPath`.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param destinationPath Destination [Path]
 */
fun TarArchiveInputStream.extractTo(destinationPath: Path) = use {
	try {
		var tarEntry: TarArchiveEntry?
		while (nextTarEntry.also { tarEntry = it } != null) {
			if (tarEntry?.isDirectory != false) continue
			val outputFile = destinationPath.resolve(tarEntry!!.name).toFile()
				.also { it.parentFile.mkdirs() }
			IOUtils.copy(this, FileOutputStream(outputFile))
		}
	} catch (t: Throwable) {
		LOGGER.error(t.message, t)
	}
}