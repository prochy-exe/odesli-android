package com.prochy.odesliandroid.utils

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class OdesliData(
    val entityUniqueId: String,
    val userCountry: String,
    val pageUrl: String,
    val entitiesByUniqueId: Map<String, EntitiesData>,
    val linksByPlatform: Map<String, LinksData>
)

@Keep
@Serializable
data class EntitiesData(
    val id: String,
    val type: String,
    val title: String,
    val artistName: String,
    val thumbnailUrl: String,
    val thumbnailWidth: Int,
    val thumbnailHeight: Int,
    val apiProvider: String,
    val platforms: List<String>
)

@Keep
@Serializable
data class LinksData(
    val url: String,
    val nativeAppUriMobile: String? = null,
    val nativeAppUriDesktop: String? = null,
    val entityUniqueId: String
)

@Keep
@Serializable
data class LocationData(
    val ip: String,
    val hostname: String,
    val city: String,
    val region: String,
    val country: String,
    val loc: String,
    val org: String,
    val postal: String,
    val timezone: String,
    val readme: String
)
