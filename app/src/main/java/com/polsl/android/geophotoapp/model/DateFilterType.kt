package com.polsl.android.geophotoapp.model

import java.io.Serializable

/**
 * Created by alachman on 06.05.2018.
 */
enum class DateFilterType constructor(val typeCode: Int): Serializable {
    ASCENDING(1),
    DESCENDING(2);

    companion object {
        fun getTypeByCode(typeCode: Int): DateFilterType? {
            when (typeCode) {
                1 -> return ASCENDING
                2 -> return DESCENDING
                else -> return null
            }
        }
    }
}
