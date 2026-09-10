package com.vlt.zeroperte.data.model.domain

enum class AppLanguage(val tag: String, val displayName: String) {
    French("fr", "Français"),
    English("en", "English");

    companion object {
        val allLanguages = listOf(French, English)

        fun fromTag(tag: String): AppLanguage = allLanguages.firstOrNull { it.tag == tag } ?: French
    }
}
