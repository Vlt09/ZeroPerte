package com.zeroperte.config

import org.jetbrains.exposed.v1.jdbc.Database

object DbConfig {
    fun setup(jdbcUrl: String, username: String, password: String) {
        Database.connect(jdbcUrl, "org.h2.Driver", username, password)
    }
}