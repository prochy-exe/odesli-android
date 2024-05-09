package com.prochy.odesliandroid.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.prochy.odesliandroid.R
import com.prochy.odesliandroid.ui.theme.AppTheme
import com.prochy.odesliandroid.utils.MusicProviders
import com.prochy.odesliandroid.utils.MusicProviders.Companion.getLabelFromService
import com.prochy.odesliandroid.utils.OdesliData
import com.prochy.odesliandroid.utils.Utils
import com.prochy.odesliandroid.utils.Utils.Companion.getBooleanSetting
import com.prochy.odesliandroid.utils.Utils.Companion.getStringSetting

class Share : ComponentActivity() {
    private fun displayOdesliPopUp(receivedLink: String) {
        // Dynamically invoke dim behind
        val layoutParams = window.attributes
        layoutParams.dimAmount = 0.75f
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = layoutParams
        setContent {
            AppTheme {
                Surface {
                    ShareActivityLayout(receivedLink)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var receivedLink = ""
        when {
            intent?.action == Intent.ACTION_SEND -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    receivedLink = it
                }
            }
        }
        if (receivedLink.isBlank()) {
            finish()
            return
        }
        val urlPattern = Regex("""https?://\S+""")
        if (urlPattern.containsMatchIn(receivedLink)) {
            receivedLink = urlPattern.find(receivedLink)!!.value
        }
        val autoCopy = getBooleanSetting(
            "autoCopy",
            false,
            this
        )
        val showOnFailed = getBooleanSetting(
            "showOnFailed",
            false,
            this
        )
        val service = getStringSetting(
            "preferredService",
            "",
            this
        )
        if (service.isNotBlank() && autoCopy) {
            Utils.getMusicData(receivedLink, this) { data ->
                val type = data.entitiesByUniqueId[data.entitiesByUniqueId.keys.first()]?.type ?: ""
                val errorString: String = if (type == "song") {
                    this.getString(R.string.song_not_found_error_popup)
                } else {
                    this.getString(R.string.album_not_found_error_popup)
                }
                if (type.isBlank()) {
                    Toast.makeText(
                        this,
                        this.getString(R.string.unexpected_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                val platformLink = data.linksByPlatform[service]?.url
                if (!platformLink.isNullOrBlank()) {
                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Link", platformLink)
                    clipboard.setPrimaryClip(clip)
                    finish()
                } else {
                    if (showOnFailed) {
                        displayOdesliPopUp(receivedLink)
                    } else {
                        Toast.makeText(
                            this,
                            errorString + " " + getLabelFromService(service),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        } else {
            displayOdesliPopUp(receivedLink)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelectTextFieldPopUp(
    modifier: Modifier = Modifier,
    selectedValue: String = "",
    options: List<String>,
    label: String,
    onValueChangedEvent: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(selectedValue) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(expanded) {
        if (!expanded) {
            focusManager.clearFocus()
        }
    }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        CompositionLocalProvider(
            LocalTextInputService provides null
        ) {
            OutlinedTextField(
                readOnly = true,
                value = getLabelFromService(selectedOption)
                    ?: selectedOption,
                onValueChange = {},
                label = { Text(label) },
                singleLine = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
        }
        if (expanded) {
            Dialog(
                onDismissRequest = { expanded = false }
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 15.dp
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    expanded = false
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back),
                                    contentDescription = "Back",
                                )
                            }
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        options.forEach { option: String ->
                            DropdownMenuItem(
                                contentPadding = PaddingValues(
                                    start = 0.dp,
                                    top = 10.dp,
                                    end = 0.dp,
                                    bottom = 10.dp
                                ) ,
                                text = {
                                    getLabelFromService(option)
                                        ?.let { Text(text = it) }
                                },
                                onClick = {
                                    expanded = false
                                    selectedOption = option
                                    onValueChangedEvent(option)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShareActivityLayout(receivedLink: String) {
    val context = LocalContext.current
    val shouldDismiss = remember {
        mutableStateOf(false)
    }
    val triggeredRequest = remember {
        mutableStateOf(true)
    }
    val receivedLinks = remember {
        mutableStateOf(false)
    }
    if (shouldDismiss.value) return
    val receivedData = remember {
        mutableStateOf(OdesliData(
            "",
            "",
            "",
            emptyMap(),
            emptyMap()
        )
    ) }
    Utils.getMusicData(receivedLink, LocalContext.current) { data ->
        triggeredRequest.value = false
        receivedLinks.value = true
        receivedData.value = data
    }
    Dialog(
        onDismissRequest = {
            shouldDismiss.value = true
            (context as? ComponentActivity)?.finish() // Finish the activity
        }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 15.dp
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .height(dimensionResource(id = R.dimen.card_height))
        ) {
            if (triggeredRequest.value) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    LinearProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                }
            } else if (receivedLinks.value) {
                if (receivedData.value.entitiesByUniqueId.isEmpty()) {
                    (context as? ComponentActivity)?.finish()
                }
                var outputService by remember {
                    mutableStateOf(
                        getStringSetting(
                            "preferredService",
                            "",
                            context
                        ).ifBlank {
                            receivedData.value.entitiesByUniqueId.keys.first()
                        }
                    )
                }

                val musicServices = MusicProviders.entries.map { it.service }
                val thumbnail = receivedData.value.entitiesByUniqueId[outputService]?.thumbnailUrl
                val title = receivedData.value.entitiesByUniqueId[outputService]?.title
                val artist = receivedData.value.entitiesByUniqueId[outputService]?.artistName
                val service = getLabelFromService(outputService)
                val link = receivedData.value.linksByPlatform[outputService]?.url
                val type = receivedData.value.entitiesByUniqueId[receivedData.value.entitiesByUniqueId.keys.first()]?.type ?: ""
                Utils.SongInfo(
                    thumbnail = thumbnail.toString(),
                    title = title.toString(),
                    artist = artist.toString(),
                    service = service.toString(),
                    link = link.toString(),
                    odesliType = type,
                    element = {
                        DynamicSelectTextFieldPopUp(
                            modifier = Modifier.width(350.dp),
                            selectedValue = outputService,
                            options = musicServices,
                            label = stringResource(id = R.string.output_service),
                            onValueChangedEvent = {
                                outputService = it
                            }
                        )
                    }
                )
            }
        }
    }
}
