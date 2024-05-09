/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.prochy.odesliandroid.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.prochy.odesliandroid.R
import com.prochy.odesliandroid.ui.theme.AppTheme
import com.prochy.odesliandroid.utils.Constants
import com.prochy.odesliandroid.utils.MusicProviders
import com.prochy.odesliandroid.utils.MusicProviders.Companion.getLabelFromService
import com.prochy.odesliandroid.utils.OdesliData
import com.prochy.odesliandroid.utils.Utils
import com.prochy.odesliandroid.utils.Utils.Companion.getBooleanSetting
import com.prochy.odesliandroid.utils.Utils.Companion.getStringSetting
import com.prochy.odesliandroid.utils.Utils.Companion.openLink
import com.prochy.odesliandroid.utils.Utils.Companion.regularButtonColors
import com.prochy.odesliandroid.utils.Utils.Companion.resetButtonColors
import com.prochy.odesliandroid.utils.Utils.Companion.resetIconButtonColors
import com.prochy.odesliandroid.utils.Utils.Companion.saveBooleanSetting
import com.prochy.odesliandroid.utils.Utils.Companion.saveStringSetting


class Main : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    OdesliLayout()
                }
            }
        }
    }
}

var resetOutputField = false
var resetPreferredField = false


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdesliLayout() {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var preferredService by remember { mutableStateOf(
        getStringSetting(
            "preferredService",
            "",
            context
        )
    ) }
    var outputService by remember { mutableStateOf("") }
    var receivedLinks by remember { mutableStateOf(false) }
    var triggeredRequest by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showCredits by remember { mutableStateOf(false) }
    var autoCopy by remember { mutableStateOf(
        getBooleanSetting(
            "autoCopy",
            false,
            context
        )
    ) }
    var showOnFailed by remember { mutableStateOf(
        getBooleanSetting(
            "showOnFailed",
            false,
            context
        )
    ) }
    var clickCount by remember { mutableIntStateOf(0) }
    var songData by remember { mutableStateOf(
        OdesliData(
            "",
            "",
            "",
            emptyMap(),
            emptyMap()
        )
    ) }
    val musicServices = MusicProviders.entries.map { it.service }

    fun resetOutputState() {
        text = ""
        outputService = ""
        receivedLinks = false
        songData = OdesliData("", "", "", emptyMap(), emptyMap())
    }

    fun resetPreferredState() {
        preferredService = ""
        autoCopy = false
        showOnFailed = false
        saveBooleanSetting(
            "autoCopy",
            false,
            context
        )
        saveStringSetting(
            "preferredService",
            "",
            context
        )
        saveBooleanSetting(
            "showOnFailed",
            false,
            context
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(
            left = 30,
            right = 30,
        ),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = { Text(text = stringResource(id =R.string.app_name)) },
                actions = {
                    AnimatedVisibility(
                        visible = text.isNotEmpty() && outputService.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        FilledTonalIconButton(
                            colors = resetIconButtonColors(),
                            content = { Icon(
                                painter = painterResource(id = R.drawable.ic_clear),
                                contentDescription = "Clear options"
                            ) },
                            onClick = {
                                resetOutputField = true
                                resetOutputState()
                            }
                        )
                        Utils.ButtonSpacer()
                    }
                    CustomFilledTonalIconButton(

                        onLongClick = {
                            showCredits = true
                        },
                        onClick = {
                            showSettings = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = text.isNotEmpty() && outputService.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ExtendedFloatingActionButton(
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_send),
                            contentDescription = "Convert link"
                        )
                        Utils.ButtonSpacer()
                        Text(text = stringResource(id = R.string.convert_link))
                    },
                    onClick = {
                        receivedLinks = false
                        triggeredRequest = true
                        Utils.getMusicData(text, context) { data ->
                            songData = data
                            triggeredRequest = false
                            receivedLinks = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .widthIn(1.dp, Dp.Infinity)
                            .weight(1f),
                        value = text,
                        onValueChange = {
                            text = it
                            receivedLinks = false
                            triggeredRequest = false
                        },
                        label = { Text(stringResource(id = R.string.original_url)) },
                        singleLine = true
                    )
                    Utils.ButtonSpacer()
                    DynamicSelectTextField(
                        modifier = Modifier.weight(1f),
                        selectedValue = outputService,
                        options = musicServices,
                        label = stringResource(id = R.string.output_service),
                        onValueChangedEvent = {
                            outputService = it
                        },
                        reset = resetOutputField,
                        showBackButton = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (triggeredRequest || receivedLinks) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 15.dp
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(600.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (triggeredRequest && !receivedLinks)
                            LinearProgressIndicator(
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeCap = StrokeCap.Round
                            )
                        if (receivedLinks) {
                            val thumbnail = songData.entitiesByUniqueId[outputService]?.thumbnailUrl
                            val title = songData.entitiesByUniqueId[outputService]?.title
                            val artist = songData.entitiesByUniqueId[outputService]?.artistName
                            val service = getLabelFromService(outputService)
                            val link = songData.linksByPlatform[outputService]?.url
                            val type = songData.entitiesByUniqueId[songData.entitiesByUniqueId.keys.first()]?.type ?: ""

                            Utils.SongInfo(
                                thumbnail = thumbnail.toString(),
                                title = title.toString(),
                                artist = artist.toString(),
                                service = service.toString(),
                                link = link.toString(),
                                odesliType = type
                            )
                        }
                    }

                }
            }
            if (showCredits) {
                Dialog(
                    onDismissRequest = { showCredits = false }
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 15.dp
                        ),
                        modifier = Modifier
                            .height(dimensionResource(id = R.dimen.card_height)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                            ) {
                                Row {
                                    Text(
                                        style = MaterialTheme.typography.titleLarge,
                                        text = stringResource(id = R.string.credits)
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row {
                                        Utils.ImageLoader(
                                            modifier = Modifier
                                                .clickable(
                                                    onClick = {
                                                        openLink(
                                                            Constants.DEV_GITHUB_URL,
                                                            context
                                                        )
                                                    }
                                                ),
                                            context = context,
                                            image = Constants.DEV_PFP_URL,
                                            height = 320.dp,
                                            width = 320.dp,
                                            contentDescription = "Dev profile picture"
                                        )
                                    }
                                    Row {
                                        Text(text = stringResource(id = R.string.developer))
                                    }
                                    Row {
                                        Text(
                                            text = stringResource(id = R.string.developer_substring),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(10.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.al_credits),
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.surfaceTint
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            LazyVerticalGrid(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                columns = GridCells.Fixed(2),
                                            ) {
                                                item {
                                                    FilledTonalButton(
                                                        colors = regularButtonColors(),
                                                        onClick = {
                                                            openLink(
                                                                Constants.RETROFIT_URL,
                                                                context
                                                            )
                                                        }
                                                    ) {
                                                        Text(text = "Retrofit2")
                                                    }
                                                }
                                                item {
                                                    FilledTonalButton(
                                                        colors = regularButtonColors(),
                                                        onClick = {
                                                            openLink(
                                                                Constants.ODESLI_URL,
                                                                context
                                                            )
                                                        }
                                                    ) {
                                                        Text(text = "Songlink/Odesli")
                                                    }
                                                }
                                                item {
                                                    FilledTonalButton(
                                                        colors = regularButtonColors(),
                                                        onClick = {
                                                            openLink(
                                                                Constants.IPINFO_URL,
                                                                context
                                                            )
                                                        }
                                                    ) {
                                                        Text(text = "IPinfo")
                                                    }
                                                }
                                                item {
                                                    FilledTonalButton(
                                                        colors = regularButtonColors(),
                                                        onClick = {
                                                            openLink(
                                                                Constants.COIL_URL,
                                                                context
                                                            )
                                                        }
                                                    ) {
                                                        Text(text = "Coil")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (showSettings) {
                Dialog(
                    onDismissRequest = { showSettings = false }
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 15.dp
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                        ) {
                            Text(
                                modifier = Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                ) {
                                    clickCount += 1
                                    if (clickCount == 3) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.easter_egg),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        clickCount = 0
                                    }
                                },
                                style = MaterialTheme.typography.titleLarge,
                                text = stringResource(id = R.string.share_settings)
                            )
                            Text(
                                text = stringResource(id = R.string.share_settings_desc),
                                style = MaterialTheme.typography.bodySmall
                            )
                            DynamicSelectTextField(
                                selectedValue = preferredService,
                                options = listOf("") + musicServices,
                                label = stringResource(id = R.string.preferred_share_service),
                                onValueChangedEvent = {
                                    preferredService = it
                                    saveStringSetting(
                                        "preferredService",
                                        it,
                                        context
                                    )
                                    if (it.isEmpty()) {
                                        autoCopy = false
                                        showOnFailed = false
                                        saveBooleanSetting(
                                            "autoCopy",
                                            false,
                                            context
                                        )
                                        saveBooleanSetting(
                                            "showOnFailed",
                                            false,
                                            context
                                        )
                                    }
                                },
                                reset = resetPreferredField,
                                showBackButton = true
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        preferredService.isNotEmpty()
                                    ) {
                                        saveBooleanSetting(
                                            "autoCopy",
                                            !autoCopy,
                                            context
                                        )
                                        autoCopy = !autoCopy
                                        if (!autoCopy) {
                                            showOnFailed = false
                                            saveBooleanSetting(
                                                "showOnFailed",
                                                false,
                                                context
                                            )
                                        }
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.auto_copy)
                                )
                                Switch(
                                    enabled = preferredService.isNotEmpty(),
                                    checked = autoCopy,
                                    onCheckedChange = {
                                        saveBooleanSetting(
                                            "autoCopy",
                                            it,
                                            context
                                        )
                                        autoCopy = it
                                        if (!it) {
                                            showOnFailed = false
                                            saveBooleanSetting(
                                                "showOnFailed",
                                                false,
                                                context
                                            )
                                        }
                                    }
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(
                                        preferredService.isNotEmpty() && autoCopy
                                    ) {
                                        saveBooleanSetting(
                                            "showOnFailed",
                                            !showOnFailed,
                                            context
                                        )
                                        showOnFailed = !showOnFailed
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.show_on_failed_setting)
                                )
                                Switch(
                                    enabled = preferredService.isNotEmpty() && autoCopy,
                                    checked = showOnFailed,
                                    onCheckedChange = {
                                        saveBooleanSetting(
                                            "showOnFailed",
                                            it,
                                            context
                                        )
                                        showOnFailed = it
                                    }
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                FilledTonalButton(
                                    colors = regularButtonColors(),
                                    onClick = {
                                        showSettings = false
                                    }) {
                                    Text(text = stringResource(id = R.string.ok))
                                }
                                Utils.ButtonSpacer()
                                FilledTonalButton(
                                    colors = resetButtonColors(),
                                    onClick = {
                                        resetPreferredField = true
                                        resetPreferredState()
                                    }) {
                                    Text(text = stringResource(id = R.string.reset))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelectTextField(
    modifier: Modifier = Modifier,
    selectedValue: String = "",
    options: List<String>,
    label: String,
    onValueChangedEvent: (String) -> Unit,
    reset: Boolean = false,
    showBackButton: Boolean = false
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
                value = getLabelFromService(selectedOption) ?: selectedOption,
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
                            if (showBackButton) {
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
                                    if (option.isEmpty()) {
                                        Text(text = "None")
                                    } else {
                                        getLabelFromService(option)
                                            ?.let { Text(text = it) }
                                    }
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

    if (reset) {
        selectedOption = ""
        resetOutputField = false
        resetPreferredField = false
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomFilledTonalIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    shape: Shape = IconButtonDefaults.filledShape,
    content: @Composable () -> Unit
) = Surface(
    modifier = modifier
        .padding(end = 10.dp) // Make it behave like regular filled button
        .combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ),
    shape = shape,
    color = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary
) {
    Box(
        modifier = Modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(showBackground = false)
@Composable
fun OdesliLayoutPreview() {
    AppTheme {
        OdesliLayout()
    }
}
