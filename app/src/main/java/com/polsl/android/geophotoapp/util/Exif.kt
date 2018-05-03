package com.polsl.android.geophotoapp.util

import kotlin.experimental.and

/**
 * Created by alachman on 03.05.2018.
 */
object Exif {

    // Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
    fun getOrientation(jpeg: ByteArray?): Int {
        if (jpeg == null) {
            return 0
        }

        var offset = 0
        var length = 0

        // ISO/IEC 10918-1:1993(E)
        while (offset + 3 < jpeg.size && (jpeg[offset++] and 0xFF.toByte()) == 0xFF.toByte()) {
            val marker =(jpeg[offset] and 0xFF.toByte())

            // Check if the marker is a padding.
            if (marker == 0xFF.toByte()) {
                continue
            }
            offset++

            // Check if the marker is SOI or TEM.
            if (marker == 0xD8.toByte() || marker == 0x01.toByte()) {
                continue
            }
            // Check if the marker is EOI or SOS.
            if (marker == 0xD9.toByte() || marker == 0xDA.toByte()) {
                break
            }

            // Get the length and check if it is reasonable.
            length = pack(jpeg, offset, 2, false)
            if (length < 2 || offset + length > jpeg.size) {

                return 0
            }

            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1.toByte() && length >= 8 &&
                    pack(jpeg, offset + 2, 4, false) == 0x45786966 &&
                    pack(jpeg, offset + 6, 2, false) == 0) {
                offset += 8
                length -= 8
                break
            }

            // Skip other markers.
            offset += length
            length = 0
        }

        // JEITA CP-3451 Exif Version 2.2
        if (length > 8) {
            // Identify the byte order.
            var tag = pack(jpeg, offset, 4, false)
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                return 0
            }
            val littleEndian = tag == 0x49492A00

            // Get the offset and check if it is reasonable.
            var count = pack(jpeg, offset + 4, 4, littleEndian) + 2
            if (count < 10 || count > length) {
                return 0
            }
            offset += count
            length -= count

            // Get the count and go through all the elements.
            count = pack(jpeg, offset - 2, 2, littleEndian)
            while (count-- > 0 && length >= 12) {
                // Get the tag and check if it is orientation.
                tag = pack(jpeg, offset, 2, littleEndian)
                if (tag == 0x0112) {
                    // We do not really care about type and count, do we?
                    val orientation = pack(jpeg, offset + 8, 2, littleEndian)
                    when (orientation) {
                        1 -> return 0
                        3 -> return 180
                        6 -> return 90
                        8 -> return 270
                    }
                    return 0
                }
                offset += 12
                length -= 12
            }
        }
        return 0
    }

    private fun pack(bytes: ByteArray?, offset: Int, length: Int,
                     littleEndian: Boolean): Int {
        var offset = offset
        var length = length
        var step = 1
        if (littleEndian) {
            offset += length - 1
            step = -1
        }

        var value = 0
        while (length-- > 0) {
            value = value shl 8 or ((bytes!![offset] and 0xFF.toByte()).toInt())
            offset += step
        }
        return value
    }
}