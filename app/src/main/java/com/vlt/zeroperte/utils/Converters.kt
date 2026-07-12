package com.vlt.zeroperte.utils

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class Converters {

  internal val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  @TypeConverter
  fun fromStringFormat(value: String?): LocalDate? {
      return value?.let { LocalDate.parse(it, formatter) }
  }
  @TypeConverter
  fun dateToStringDate(date: LocalDate?): String? {
    return date?.toString()
  }
}