package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AudioEqualizerVisualizer
import com.example.ui.components.FactPreservationBadge
import com.example.ui.components.NeonButton
import com.example.ui.components.SecondaryStudioButton
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BrightCyan
import com.example.ui.theme.CardStroke
import com.example.ui.theme.CardStrokeGlow
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MusicPlayerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val playbackState by viewModel.playbackState.collectAsState()
    val songTitle by viewModel.activeSongTitle.collectAsState()
    val songSubject by viewModel.activeSongSubject.collectAsState()
    val songGenre by viewModel.activeSongGenre.collectAsState()
    val songLyrics by viewModel.activeSongLyrics.collectAsState()

    var volume by remember { mutableFloatStateOf(1f) }
    val lyricsListState = rememberLazyListState()

    // Smooth vinyl rotation
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    // Auto-scroll lyrics as song progresses
    LaunchedEffect(playbackState.currentLyricIndex) {
        val count = songLyrics?.timedLines?.size ?: 0
        if (count > 0 && playbackState.currentLyricIndex < count) {
            lyricsListState.animateScrollToItem(playbackState.currentLyricIndex.coerceIn(0, count - 1))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                modifier = Modifier.testTag("player_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "STUDIO PLAYER",
                    color = BrightCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$songSubject • $songGenre",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = { viewModel.startQuizForCurrentSong() },
                modifier = Modifier.testTag("player_quiz_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = "Quiz",
                    tint = AmberGold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Center Album Artwork with spinning vinyl aesthetics
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .border(2.dp, CardStrokeGlow, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.memorify_cover_art_1793377174141),
                contentDescription = "Cover Artwork",
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(if (playbackState.isPlaying) rotation else 0f),
                contentScale = ContentScale.Crop
            )

            // Center spindle hole
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(BackgroundDark)
                    .border(1.dp, CardStroke, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Song Title and Badges
        Text(
            text = songTitle,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FactPreservationBadge(score = songLyrics?.factPreservationScore ?: 96)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Animated Audio Waveform Equalizer
        AudioEqualizerVisualizer(
            barHeights = playbackState.audioVisualizerBars,
            isPlaying = playbackState.isPlaying,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Synchronized Karaoke Lyrics Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardStroke, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            val lines = songLyrics?.timedLines ?: emptyList()
            if (lines.isEmpty()) {
                // Fallback to text lines
                val fallbackLines = (songLyrics?.verse1 ?: "Light reactions charge the spark in chloroplasts.")
                    .lines().filter { it.isNotBlank() }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    items(fallbackLines.size) { i ->
                        Text(
                            text = fallbackLines[i],
                            color = if (i == 0) BrightCyan else TextSecondary,
                            fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = lyricsListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(lines) { index, line ->
                        val isActive = index == playbackState.currentLyricIndex
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isActive) NeonViolet.copy(alpha = 0.2f) else Color.Transparent)
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (line.section.isNotBlank()) {
                                    Text(
                                        text = "[${line.section}]",
                                        color = if (isActive) BrightCyan else TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = line.lineText,
                                    color = if (isActive) TextPrimary else TextSecondary,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = if (isActive) 14.sp else 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress Bar & Timestamp
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = playbackState.progress,
                onValueChange = { frac ->
                    val targetMs = (frac * playbackState.durationMs).toInt()
                    viewModel.audioEngine.seekTo(targetMs)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_seek_bar"),
                colors = SliderDefaults.colors(
                    thumbColor = BrightCyan,
                    activeTrackColor = BrightCyan,
                    inactiveTrackColor = CardStroke
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMs(playbackState.currentPositionMs),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = formatMs(playbackState.durationMs),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Playback Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.audioEngine.seekRelative(-10) },
                modifier = Modifier.testTag("rewind_10s_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Replay10,
                    contentDescription = "Rewind 10s",
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Big Play / Pause Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(NeonViolet, ElectricCyan))
                    )
                    .clickable { viewModel.audioEngine.togglePlayPause() }
                    .testTag("play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = { viewModel.audioEngine.seekRelative(10) },
                modifier = Modifier.testTag("forward_10s_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Forward10,
                    contentDescription = "Forward 10s",
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Bottom Shortcut to Quiz
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NeonButton(
                text = "Take Memory Quiz",
                icon = Icons.Default.Quiz,
                onClick = { viewModel.startQuizForCurrentSong() },
                modifier = Modifier.weight(1f),
                testTag = "player_take_quiz_button"
            )
        }
    }
}

private fun formatMs(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
