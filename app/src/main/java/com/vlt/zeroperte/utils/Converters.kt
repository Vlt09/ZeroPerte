package com.vlt.zeroperte.utils

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class Converters {

  internal val formatter = SimpleDateFormat("yyyy-MM-dd")

  @TypeConverter
  fun fromStringFormat(value: String?): LocalDate? {
    val date = value?.let { formatter.parse(it) }

    return LocalDate.ofInstant(date?.toInstant(), ZoneId.systemDefault())
  }

  @TypeConverter
  fun dateToStringDate(date: LocalDate?): String? {
    return date?.toString()
  }
}