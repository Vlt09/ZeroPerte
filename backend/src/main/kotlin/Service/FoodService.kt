package com.zeroperte.Service

import com.zeroperte.model.Food
import com.zeroperte.model.FoodDto
import io.ktor.server.routing.RoutingCall
import io.ktor.util.logging.KtorSimpleLogger
import kotlin.collections.associate
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

internal val LOGGER = KtorSimpleLogger("com.zeroperte.FoodServiceLogger")


class FoodService {

    /**
     * Cette méthode identifie tout les paramètres de filtrage de food dans la route get(/foods?...)
     * et leur valeur. Utilise la réflection pour retrouver toutes les propriétés de l'entité food dynamiquement
     * afin d'éviter de devoir les écrire en dure.
     * Retourne une map avec comme clé le nom du paramètre de filtrage correspondant au propriétés de l'entité food
     * et comme valeur la valeur associé au paramètre de filtrage
     */
    fun getFilterParameterFromUrl(call: RoutingCall): Map<String, String> {
        val map = HashMap<String, String>()

        // Add url parameters which is not food member properties but additional info on expiryDate filter member
        map["days"] = call.parameters["days"] ?: "0"

        LOGGER.info("map days : $map")

        Food::class.declaredMemberProperties.mapNotNull { p -> call.parameters[p.name]?.let { map[p.name] = it } }
        return map

    }
}