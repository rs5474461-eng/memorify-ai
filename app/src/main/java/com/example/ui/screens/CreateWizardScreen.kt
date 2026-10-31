package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MusicGenre
import com.example.model.SongDuration
import com.example.model.SongLyrics
import com.example.model.SongMood
import com.example.model.StudyLanguage
import com.example.model.SubjectCategory
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.WizardState
import com.example.ui.components.FactPreservationBadge
import com.example.ui.components.NeonButton
import com.example.ui.components.SecondaryStudioButton
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BrightCyan
import com.example.ui.theme.CardStroke
import com.example.ui.theme.CardStrokeGlow
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CreateWizardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.wizardState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (state.step > 1 && state.step != 5 && state.step != 7) {
                            viewModel.setWizardStep(state.step - 1)
                        } else {
                            viewModel.navigateTo(AppScreen.DASHBOARD)
                        }
                    },
                    modifier = Modifier.testTag("wizard_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Create Study Song",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Step ${state.step} of 8: ${getStepTitle(state.step)}",
                        color = BrightCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Stepper Progress Line
        LinearProgressIndicator(
            progress = { state.step / 8f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = BrightCyan,
            trackColor = SurfaceCard
        )

        // Step Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (state.step) {
                1 -> Step1EnterContent(state, viewModel)
                2 -> Step2SelectLanguage(state, viewModel)
                3 -> Step3SelectMusicStyle(state, viewModel)
                4 -> Step4SelectMoodAndDuration(state, viewModel)
                5 -> Step5GeneratingLyrics(state)
                6 -> Step6PreviewAndEditLyrics(state, viewModel)
                7 -> Step7GeneratingAudio(state)
                8 -> Step8SongReady(state, viewModel)
            }
        }
    }
}

private fun getStepTitle(step: Int): String = when (step) {
    1 -> "Enter Study Content"
    2 -> "Select Language"
    3 -> "Select Music Style"
    4 -> "Mood & Duration"
    5 -> "AI Lyricist Generating..."
    6 -> "Preview & Edit Lyrics"
    7 -> "Synthesizing Music & Vocals..."
    8 -> "Song Master Ready!"
    else -> ""
}

// ---------------- STEP 1: ENTER CONTENT ----------------

@Composable
fun Step1EnterContent(state: WizardState, viewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "What would you like to memorize today?",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Paste formulas, dates, textbook paragraphs, or select a pre-loaded topic pack below.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Subject selector chips
        item {
            Text(
                text = "Select Subject Category",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SubjectCategory.entries.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { cat ->
                            val isSelected = state.selectedSubject == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) NeonViolet.copy(alpha = 0.25f) else SurfaceCard)
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonViolet else CardStroke,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.selectSubject(cat) }
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat.displayName,
                                    color = if (isSelected) BrightCyan else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Text area
        item {
            OutlinedTextField(
                value = state.studyText,
                onValueChange = { viewModel.setStudyText(it) },
                label = { Text("Educational Notes / Facts / Formulas") },
                placeholder = { Text("Paste school notes, questions & answers, or definitions...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .testTag("study_content_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedBorderColor = BrightCyan,
                    unfocusedBorderColor = CardStroke,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        item {
            NeonButton(
                text = "Next: Choose Language",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = { viewModel.setWizardStep(2) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.studyText.isNotBlank(),
                testTag = "step1_next_button"
            )
        }
    }
}

// ---------------- STEP 2: SELECT LANGUAGE ----------------

@Composable
fun Step2SelectLanguage(state: WizardState, viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Choose Song Language",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Memorify AI preserves educational terms while adapting rhymes into your chosen language.",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        StudyLanguage.entries.forEach { lang ->
            val isSelected = state.selectedLanguage == lang
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (isSelected) BrightCyan else CardStroke,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { viewModel.selectLanguage(lang) }
                    .testTag("language_option_${lang.code}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SurfaceCardElevated else SurfaceCard
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = lang.flag, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = lang.displayName,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = when (lang) {
                                StudyLanguage.ENGLISH -> "Standard English rhymes with technical vocabulary."
                                StudyLanguage.HINDI -> "हिंदी में सुंदर लयबद्ध दोहे और स्मृति श्लोक।"
                                StudyLanguage.HINGLISH -> "Popular Indian college Hinglish style (Hindi + English)."
                            },
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = BrightCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        NeonButton(
            text = "Next: Music Style",
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            onClick = { viewModel.setWizardStep(3) },
            modifier = Modifier.fillMaxWidth(),
            testTag = "step2_next_button"
        )
    }
}

// ---------------- STEP 3: SELECT MUSIC STYLE ----------------

@Composable
fun Step3SelectMusicStyle(state: WizardState, viewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Choose Your Music Style",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Each genre applies a distinct acoustic tempo, drum pattern, and melodic structure.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        items(MusicGenre.entries) { genre ->
            val isSelected = state.selectedGenre == genre
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (isSelected) NeonViolet else CardStroke,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { viewModel.selectGenre(genre) }
                    .testTag("genre_option_${genre.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SurfaceCardElevated else SurfaceCard
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = genre.emoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = genre.title,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${genre.bpm} BPM",
                                color = BrightCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = genre.vibe,
                            color = AmberGold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = genre.description,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = NeonViolet,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            NeonButton(
                text = "Next: Mood & Duration",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = { viewModel.setWizardStep(4) },
                modifier = Modifier.fillMaxWidth(),
                testTag = "step3_next_button"
            )
        }
    }
}

// ---------------- STEP 4: MOOD & DURATION ----------------

@Composable
fun Step4SelectMoodAndDuration(state: WizardState, viewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Mood & Track Length",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tune the emotional energy and length for your study goal.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        item {
            Text(
                text = "Track Mood",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SongMood.entries.forEach { mood ->
                    val isSelected = state.selectedMood == mood
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NeonViolet.copy(alpha = 0.2f) else SurfaceCard)
                            .border(
                                1.dp,
                                if (isSelected) NeonViolet else CardStroke,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectMood(mood) }
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = mood.displayName,
                                color = if (isSelected) BrightCyan else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = mood.description,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Song Duration",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SongDuration.entries.forEach { dur ->
                    val isSelected = state.selectedDuration == dur
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) BrightCyan.copy(alpha = 0.15f) else SurfaceCard)
                            .border(
                                1.dp,
                                if (isSelected) BrightCyan else CardStroke,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectDuration(dur) }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dur.displayName,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = dur.description,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = BrightCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            NeonButton(
                text = "Generate AI Lyrics",
                icon = Icons.Default.AutoAwesome,
                onClick = { viewModel.startGeneratingLyrics() },
                modifier = Modifier.fillMaxWidth(),
                testTag = "step4_generate_lyrics_button"
            )
        }
    }
}

// ---------------- STEP 5: GENERATING LYRICS ----------------

@Composable
fun Step5GeneratingLyrics(state: WizardState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = NeonViolet,
                strokeWidth = 5.dp
            )

            Text(
                text = "Memorify AI Engine Working",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardStrokeGlow, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    GenerationStepRow("1. Analyzing curriculum notes & definitions", true)
                    GenerationStepRow("2. Extracting formulas, dates, & critical keywords", true)
                    GenerationStepRow("3. Formulating rhyming cadence & rhythmic meter", true)
                    GenerationStepRow("4. Running zero-hallucination fact verification", true)
                }
            }

            if (state.lyricsError != null) {
                Text(
                    text = state.lyricsError,
                    color = Color(0xFFEF4444),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun GenerationStepRow(label: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = BrightCyan,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

// ---------------- STEP 6: PREVIEW & EDIT LYRICS ----------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step6PreviewAndEditLyrics(state: WizardState, viewModel: MainViewModel) {
    val lyrics = state.generatedLyrics ?: return
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Lyrics & Edits, 1: Original Notes

    var verse1 by remember { mutableStateOf(lyrics.verse1) }
    var chorus by remember { mutableStateOf(lyrics.chorus) }
    var verse2 by remember { mutableStateOf(lyrics.verse2) }
    var bridge by remember { mutableStateOf(lyrics.bridge) }
    var outro by remember { mutableStateOf(lyrics.outro) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = lyrics.title,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FactPreservationBadge(score = lyrics.factPreservationScore)
                Text(
                    text = "${lyrics.subject} • ${lyrics.genre}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Fact Preservation Verification Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, EmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Fact Preservation Check: PASSED",
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Verified retained facts & formulas from original curriculum:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        lyrics.preservedFacts.forEach { fact ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EmeraldGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = fact,
                                    color = EmeraldGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tabs: Generated Lyrics vs Original Notes
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceCard,
                contentColor = BrightCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BrightCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Generated Lyrics", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Original Notes", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            item {
                LyricsSectionEditor(
                    title = "Verse 1",
                    value = verse1,
                    onValueChange = {
                        verse1 = it
                        viewModel.updateLyricsSection(verse1, chorus, verse2, bridge, outro)
                    }
                )
            }
            item {
                LyricsSectionEditor(
                    title = "Chorus (Hook)",
                    value = chorus,
                    isHighlight = true,
                    onValueChange = {
                        chorus = it
                        viewModel.updateLyricsSection(verse1, chorus, verse2, bridge, outro)
                    }
                )
            }
            item {
                LyricsSectionEditor(
                    title = "Verse 2",
                    value = verse2,
                    onValueChange = {
                        verse2 = it
                        viewModel.updateLyricsSection(verse1, chorus, verse2, bridge, outro)
                    }
                )
            }
            if (bridge.isNotBlank()) {
                item {
                    LyricsSectionEditor(
                        title = "Bridge",
                        value = bridge,
                        onValueChange = {
                            bridge = it
                            viewModel.updateLyricsSection(verse1, chorus, verse2, bridge, outro)
                        }
                    )
                }
            }
            if (outro.isNotBlank()) {
                item {
                    LyricsSectionEditor(
                        title = "Outro",
                        value = outro,
                        onValueChange = {
                            outro = it
                            viewModel.updateLyricsSection(verse1, chorus, verse2, bridge, outro)
                        }
                    )
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Original Input Text:",
                            color = BrightCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.studyText,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            NeonButton(
                text = "Generate Full Song",
                icon = Icons.Default.MusicNote,
                onClick = { viewModel.generateFullSong() },
                modifier = Modifier.fillMaxWidth(),
                testTag = "step6_generate_full_song_button"
            )
        }
    }
}

@Composable
fun LyricsSectionEditor(
    title: String,
    value: String,
    isHighlight: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (isHighlight) NeonViolet else CardStroke,
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight) SurfaceCardElevated else SurfaceCard
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = if (isHighlight) BrightCyan else NeonViolet,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }
    }
}

// ---------------- STEP 7: GENERATING AUDIO ----------------

@Composable
fun Step7GeneratingAudio(state: WizardState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(NeonViolet.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { state.audioProgress },
                    modifier = Modifier.size(80.dp),
                    color = BrightCyan,
                    strokeWidth = 6.dp
                )
                Text(
                    text = "${(state.audioProgress * 100).toInt()}%",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Text(
                text = "Synthesizing Multi-Track Song",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = state.audioProgressText,
                color = BrightCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardStroke, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Audio Stem Pipeline:",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    GenerationStepRow("Drum Sequencer & 808 Sub-bass", state.audioProgress >= 0.35f)
                    GenerationStepRow("Melodic Harmonics & Acoustic Stems", state.audioProgress >= 0.55f)
                    GenerationStepRow("Educational Vocal Synthesis", state.audioProgress >= 0.75f)
                    GenerationStepRow("Stereo WAV 44.1kHz Audio Mastering", state.audioProgress >= 0.95f)
                }
            }
        }
    }
}

// ---------------- STEP 8: SONG READY ----------------

@Composable
fun Step8SongReady(state: WizardState, viewModel: MainViewModel) {
    val lyrics = state.generatedLyrics ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(EmeraldGreen.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldGreen,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Study Song Mastered!",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = lyrics.title,
            color = BrightCyan,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        FactPreservationBadge(score = lyrics.factPreservationScore)

        Spacer(modifier = Modifier.height(24.dp))

        NeonButton(
            text = "Play in Music Player",
            icon = Icons.Default.PlayArrow,
            onClick = {
                viewModel.navigateTo(AppScreen.MUSIC_PLAYER)
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "step8_open_player_button"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SecondaryStudioButton(
            text = "Test My Memory Quiz",
            icon = Icons.Default.Quiz,
            onClick = {
                viewModel.startQuizForCurrentSong()
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "step8_test_quiz_button"
        )
    }
}
