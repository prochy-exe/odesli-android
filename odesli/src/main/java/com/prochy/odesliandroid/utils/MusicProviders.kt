package com.prochy.odesliandroid.utils

import androidx.annotation.Keep

// Enum class representing different music services
@Keep
enum class MusicProviders(val label: String, val service: String) {
    AmazonMusic("Amazon Music", "amazonMusic"),
    AmazonStore("Amazon Appstore", "amazonStore"),
    Anghami("Anghami", "anghami"),
    AppleMusic("Apple Music", "appleMusic"),
    Audiamack("Audiomack", "audiomack"),
    Audius("Audius", "audius"),
    Boomplay("Boomplay", "boomplay"),
    Deezer("Deezer", "deezer"),
    Itunes("iTunes", "itunes"),
    Napster("Napster", "napster"),
    Pandora("Pandora", "pandora"),
    Soundcloud("SoundCloud", "soundcloud"),
    Spotify("Spotify", "spotify"),
    Spinrilla("Spinrilla", "spinrilla"),
    Tidal("Tidal", "tidal"),
    Yandex("Yandex", "yandex"),
    Youtube("YouTube", "youtube"),
    YoutubeMusic("YouTube Music", "youtubeMusic");

    companion object {
        fun getLabelFromService(service: String): String? {
            return entries.find { it.service == service }?.label
        }
    }
}