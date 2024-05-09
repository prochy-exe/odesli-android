package com.prochy.odesliandroid.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.TelephonyManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.prochy.odesliandroid.R

class Utils {
    companion object {
        @Composable
        fun SongInfo(
            thumbnail: String,
            title: String,
            artist: String,
            service: String,
            link: String,
            odesliType: String,
            element: @Composable () -> Unit = {}
        ) {
            val finalThumbnail = thumbnail.replace("http://", "https://") //make sure we are using https for images
            val context = LocalContext.current
            val errorString: String = if (odesliType == "song") {
                stringResource(id = R.string.song_not_found_error)
            } else {
                stringResource(id = R.string.album_not_found_error)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize()
            ) {
                element()
                if (link.isEmpty() || link == "null") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorString,
                        )
                    }
                } else {
                    ImageLoader(
                        context = context,
                        image = finalThumbnail,
                        height = 350.dp,
                        width = 350.dp,
                    )
                    Text(
                        title,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Visible,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        artist,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        service,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Row(
                    ) {
                        FilledTonalButton(
                            colors = regularButtonColors(),
                            content = { Text(text = stringResource(id = R.string.share )) },
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, link)
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                startActivity(context, shareIntent, null)
                            }
                        )
                        ButtonSpacer()
                        FilledTonalButton(
                            colors = regularButtonColors(),
                            content = { Text(text = stringResource(id = R.string.copy)) },
                            onClick = {
                                val clipboard: ClipboardManager? =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                                val clip = ClipData.newPlainText(link, link)
                                clipboard?.setPrimaryClip(clip)
                            }
                        )
                        ButtonSpacer()
                        FilledTonalButton(
                            colors = regularButtonColors(),
                            content = { Text(text = stringResource(id = R.string.open )) },
                            onClick = {
                                openLink(link, context)
                            }
                        )
                    }
                }
            }
        }

        @Composable
        fun ImageLoader(
            modifier: Modifier = Modifier,
            context: Context,
            image: String,
            width: Dp,
            height: Dp,
            contentDescription: String? = null
        ){
            var imageLoaded by remember { mutableStateOf(false) }
            var imageError by remember { mutableStateOf(false) }
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(image)
                    .size(
                        Size.ORIGINAL
                    )
                    .build(),
                contentScale = ContentScale.Crop,
                onState = { state ->
                    when (state) {
                        is AsyncImagePainter.State.Success -> {
                            imageLoaded = true
                            imageError = false
                        }
                        is AsyncImagePainter.State.Error -> {
                            Log.e("CoilImageLoading", "Image loading failed: ${state.result.throwable}")
                            imageLoaded = false
                            imageError = true
                        }
                        else -> {
                            imageLoaded = false
                            imageError = false
                        }
                    }
                }
            )

            if (imageLoaded) {
                Image(
                    modifier = modifier
                        .size(
                            height = height,
                            width = width
                        )
                        .clip(
                            shape = RoundedCornerShape(16.dp),
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp),
                        ),
                    painter = painter,
                    contentScale = ContentScale.Crop,
                    contentDescription = contentDescription,
                )
            } else if (imageError) {
                Box(
                    modifier = Modifier
                        .size(
                            height = height,
                            width = width
                        )
                        .clip(
                            shape = RoundedCornerShape(16.dp),
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(stringResource(id = R.string.image_error))
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(
                            height = height,
                            width = width
                        )
                        .clip(
                            shape = RoundedCornerShape(16.dp),
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp),
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        @Composable
        fun ButtonSpacer() {
            Spacer(modifier = Modifier.width(10.dp))
        }

        fun openLink(link: String, context: Context) {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(context, browserIntent, null)
        }

        fun getMusicData(link: String, context: Context, callback: (OdesliData) -> Unit) {

            fun retroFitRequest(countryCode: String) {
                var songData: OdesliData
                RetrofitClient().getData(context, link, countryCode) { data ->
                    val entitiesByUniqueId = mutableMapOf<String, EntitiesData>()

                    data.entitiesByUniqueId.forEach { (_, song) -> // Make sure entities are keyed as service names
                        song.platforms.forEach { platform ->
                            val songForPlatform = song.copy(platforms = listOf(platform))
                            entitiesByUniqueId[platform] = songForPlatform
                        }
                    }

                    songData = OdesliData(
                        data.entityUniqueId,
                        data.userCountry,
                        data.pageUrl,
                        entitiesByUniqueId,
                        data.linksByPlatform
                    )
                    callback(songData)
                }
            }

            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            var countryCode = tm!!.networkCountryIso
            if (countryCode.isNullOrEmpty()) { //use a 3rd party service to get country code if device doesn't have SIM
                RetrofitClient().getCountry(context) { locationData ->
                    countryCode = locationData.country
                    retroFitRequest(countryCode)
                }
            } else {
                retroFitRequest(countryCode)
            }
        }

        @Composable
        fun regularButtonColors(): ButtonColors {
            return ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        }

        @Composable
        fun resetButtonColors(): ButtonColors {
            return ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            )
        }

        @Composable
        fun resetIconButtonColors(): IconButtonColors {
            return IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            )
        }

        fun saveStringSetting(key: String, value: String, context: Context) {
            val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getStringSetting(key: String, defaultValue: String, context: Context): String {
            val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, defaultValue) ?: defaultValue
        }

        fun saveBooleanSetting(key: String, value: Boolean, context: Context) {
            val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        fun getBooleanSetting(key: String, defaultValue: Boolean, context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(key, defaultValue)
        }
    }
}