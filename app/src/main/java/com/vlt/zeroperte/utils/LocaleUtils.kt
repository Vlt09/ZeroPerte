package com.vlt.zeroperte.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

/**
 * Wraps [context] so its resources resolve strings in [languageTag], while keeping
 * [ContextWrapper.getBaseContext] pointing at the real base context. Hilt (and other
 * libraries) walk that chain to find the hosting Activity when resolving ViewModels;
 * handing them a bare Context from createConfigurationContext() breaks that walk since
 * it isn't a ContextWrapper.
 */
fun applyLocale(context: Context, languageTag: String): Context {
    val locale = Locale.forLanguageTag(languageTag)
    Locale.setDefault(locale)

    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(locale)

    val localizedResources = context.createConfigurationContext(configuration).resources
    return LocaleContextWrapper(context, localizedResources)
}

private class LocaleContextWrapper(
    base: Context,
    private val localizedResources: Resources
) : ContextWrapper(base) {
    override fun getResources(): Resources = localizedResources
}
