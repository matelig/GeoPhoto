package com.polsl.android.geophotoapp.util

import kotlin.experimental.and

object Exif {

    fun getOrientation(jpeg: ByteArray?): Int {
        if (jpeg == null) {
            return 0
        }
        var offset = 0
        var length = 0

        while (offset + 3 < jpeg.size && (jpeg[offset++] and 0xFF.toByte()) == 0xFF.toByte()) {
            val marker =(jpeg[offset] and 0xFF.toByte())

            if (marker == 0xFF.toByte()) {
                continue
            }
            offset++

            if (marker == 0xD8.toByte() || marker == 0x01.toByte()) {
                continue
            }
            if (marker == 0xD9.toByte() || marker == 0xDA.toByte()) {
                break
            }

            length = pack(jpeg, offset, 2, false)
            if (length < 2 || offset + length > jpeg.size) {
                return 0
            }

            if (marker == 0xE1.toByte() && length >= 8 &&
                    pack(jpeg, offset + 2, 4, false) == 0x45786966 &&
                    pack(jpeg, offset + 6, 2, false) == 0) {
                offset += 8
                length -= 8
                break
            }

            offset += length
            length = 0
        }
        if (length > 8) {
            var tag = pack(jpeg, offset, 4, false)
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                return 0
            }
            val littleEndian = tag == 0x49492A00

            var count = pack(jpeg, offset + 4, 4, littleEndian) + 2
            if (count < 10 || count > length) {
                return 0
            }
            offset += count
            length -= count

            count = pack(jpeg, offset - 2, 2, littleEndian)
            while (count-- > 0 && length >= 12) {
                tag = pack(jpeg, offset, 2, littleEndian)
                if (tag == 0x0112) {
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