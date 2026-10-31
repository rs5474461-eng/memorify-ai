package com.example

import com.example.ai.AiLyricQuizEngine
import com.example.model.MusicGenre
import com.example.model.QuizQuestion
import com.example.model.QuizQuestionType
import com.example.model.QuizSession
import com.example.model.SongDuration
import com.example.model.StudyLanguage
import com.example.model.SubjectCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testLyricSynthesisAndFactPreservation() {
        val engine = AiLyricQuizEngine()
        val studyText = SubjectCategory.SCIENCE.sampleContent
        val lyrics = engine.synthesizeLocalLyrics(
            studyText = studyText,
            subject = "Science",
            genre = MusicGenre.LOFI_STUDY,
            language = StudyLanguage.ENGLISH,
            mood = "Deep Study Focus",
            duration = SongDuration.STANDARD
        )

        assertNotNull(lyrics)
        assertTrue(lyrics.verse1.isNotBlank())
        assertTrue(lyrics.chorus.isNotBlank())
        assertTrue(lyrics.verse2.isNotBlank())
        assertTrue(lyrics.factPreservationScore >= 90)
        assertTrue(lyrics.preservedFacts.isNotEmpty())
    }

    @Test
    fun testQuizSessionScoring() {
        val q1 = QuizQuestion(
            id = 1,
            question = "What is the formula of glucose?",
            type = QuizQuestionType.MULTIPLE_CHOICE,
            options = listOf("C6H12O6", "CO2", "H2O", "ATP"),
            correctIndex = 0,
            explanation = "Glucose is C6H12O6",
            testedFact = "Chemical formula"
        )
        val q2 = QuizQuestion(
            id = 2,
            question = "Is chlorophyll green?",
            type = QuizQuestionType.TRUE_FALSE,
            options = listOf("True", "False"),
            correctIndex = 0,
            explanation = "Chlorophyll reflects green light",
            testedFact = "Pigment reflection"
        )

        val session = QuizSession(
            songTitle = "Photosynthesis",
            subject = "Science",
            questions = listOf(q1, q2)
        )

        session.userAnswers[1] = 0 // correct
        session.userAnswers[2] = 0 // correct

        assertEquals(2, session.score)
        assertEquals(100, session.percentage)
        assertEquals("Flawless Memory! S-Tier", session.gradeTitle)
    }
}
