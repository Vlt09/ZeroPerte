package com.vlt.zeroperte.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun applyLocale(context: Context, languageTag: String): Context {
    val locale = Locale.forLanguageTag(languageTag)
    Locale.setDefault(locale)

    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(locale)

    return context.createConfigurationContext(configuration)
}
