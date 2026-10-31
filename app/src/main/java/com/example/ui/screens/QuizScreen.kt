package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuizQuestionType
import com.example.model.QuizSession
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.NeonButton
import com.example.ui.components.SecondaryStudioButton
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BrightCyan
import com.example.ui.theme.CardStroke
import com.example.ui.theme.CoralRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QuizScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val quizSession by viewModel.quizSession.collectAsState()
    val isGenerating by viewModel.isGeneratingQuiz.collectAsState()
    val quizError by viewModel.quizError.collectAsState()

    var activeQuestionIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp)
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
                onClick = { viewModel.navigateTo(AppScreen.MUSIC_PLAYER) },
                modifier = Modifier.testTag("quiz_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "AI MEMORY QUIZ",
                    color = AmberGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = quizSession?.songTitle ?: "Testing Original Curriculum Facts",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(48.dp))
        }

        if (isGenerating) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(54.dp),
                        color = AmberGold,
                        strokeWidth = 5.dp
                    )
                    Text(
                        text = "Gemini Generating Cognitive Quiz...",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Extracting verified curriculum facts to test your memory retention",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (quizSession == null || quizSession!!.questions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = quizError ?: "Quiz could not be generated. Please retry.",
                        color = CoralRed,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NeonButton(
                        text = "Retry Quiz Generation",
                        icon = Icons.Default.Refresh,
                        onClick = { viewModel.startQuizForCurrentSong() }
                    )
                }
            }
        } else {
            val session = quizSession!!
            if (session.isSubmitted) {
                QuizResultView(session, viewModel) {
                    activeQuestionIndex = 0
                    viewModel.retryQuiz()
                }
            } else {
                val questions = session.questions
                val currentQ = questions.getOrNull(activeQuestionIndex) ?: questions.first()
                val totalQ = questions.size

                // Progress Indicator
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Question ${activeQuestionIndex + 1} of $totalQ",
                            color = BrightCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${session.userAnswers.size}/$totalQ Answered",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (activeQuestionIndex + 1).toFloat() / totalQ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AmberGold,
                        trackColor = SurfaceCard
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question Area
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, CardStroke, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val typeLabel = when (currentQ.type) {
                                    QuizQuestionType.MULTIPLE_CHOICE -> "MULTIPLE CHOICE"
                                    QuizQuestionType.FILL_IN_THE_BLANK -> "FILL IN THE BLANK"
                                    QuizQuestionType.TRUE_FALSE -> "TRUE OR FALSE"
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AmberGold.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = typeLabel,
                                        color = AmberGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = currentQ.question,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    // Options
                    val selectedAnswer = session.userAnswers[currentQ.id]
                    val hasAnswered = selectedAnswer != null

                    itemsIndexed(currentQ.options) { index, optionText ->
                        val isSelected = selectedAnswer == index
                        val isCorrect = index == currentQ.correctIndex

                        val (cardBg, borderColor, textColor) = when {
                            !hasAnswered -> Triple(SurfaceCard, CardStroke, TextPrimary)
                            isSelected && isCorrect -> Triple(EmeraldGreen.copy(alpha = 0.2f), EmeraldGreen, EmeraldGreen)
                            isSelected && !isCorrect -> Triple(CoralRed.copy(alpha = 0.2f), CoralRed, CoralRed)
                            !isSelected && isCorrect -> Triple(EmeraldGreen.copy(alpha = 0.15f), EmeraldGreen, EmeraldGreen)
                            else -> Triple(SurfaceCard, CardStroke, TextSecondary)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                                .clickable(enabled = !hasAnswered) {
                                    viewModel.answerQuizQuestion(currentQ.id, index)
                                }
                                .testTag("quiz_option_${currentQ.id}_$index"),
                            colors = CardDefaults.cardColors(containerColor = cardBg)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected || (hasAnswered && isCorrect)) borderColor else SurfaceCardElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (hasAnswered && isCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else if (hasAnswered && isSelected && !isCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Text(
                                            text = ('A'.code + index).toChar().toString(),
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = optionText,
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected || (hasAnswered && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Fact Explanation Card (Revealed after selection)
                    if (hasAnswered) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, BrightCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = BrightCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Fact Explanation & Recall Key",
                                            color = BrightCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = currentQ.explanation,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                    if (currentQ.testedFact.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Tested Curriculum Fact: ${currentQ.testedFact}",
                                            color = AmberGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Next / Submit Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (activeQuestionIndex > 0) {
                        SecondaryStudioButton(
                            text = "Previous",
                            onClick = { activeQuestionIndex-- },
                            modifier = Modifier.weight(1f),
                            testTag = "quiz_prev_button"
                        )
                    }

                    if (activeQuestionIndex < totalQ - 1) {
                        NeonButton(
                            text = "Next Question",
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            onClick = { activeQuestionIndex++ },
                            modifier = Modifier.weight(1f),
                            testTag = "quiz_next_button"
                        )
                    } else {
                        NeonButton(
                            text = "Submit Quiz & See Score",
                            icon = Icons.Default.EmojiEvents,
                            onClick = { viewModel.submitQuiz() },
                            modifier = Modifier.weight(1f),
                            testTag = "quiz_submit_button"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizResultView(
    session: QuizSession,
    viewModel: MainViewModel,
    onRetry: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(AmberGold, NeonViolet))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        item {
            Text(
                text = session.gradeTitle,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${session.score} of ${session.questions.size} Questions Correct (${session.percentage}%)",
                color = BrightCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardStroke, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cognitive Retention Analysis:",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when {
                            session.percentage == 100 -> "Incredible memory retention! Music-reinforced learning has anchored these facts firmly in your long-term memory."
                            session.percentage >= 80 -> "Strong cognitive retention! Listening to the song once more will cement the remaining formulas."
                            else -> "Good baseline! Replay the song and pay close attention to the Chorus to lock in definitions."
                        },
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryStudioButton(
                    text = "Retry Quiz",
                    icon = Icons.Default.Refresh,
                    onClick = onRetry,
                    modifier = Modifier.weight(1f),
                    testTag = "quiz_retry_button"
                )

                NeonButton(
                    text = "Back to Player",
                    icon = Icons.Default.PlayArrow,
                    onClick = { viewModel.navigateTo(AppScreen.MUSIC_PLAYER) },
                    modifier = Modifier.weight(1f),
                    testTag = "quiz_back_to_player_button"
                )
            }
        }
    }
}
