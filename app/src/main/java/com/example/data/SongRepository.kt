package com.example.data

import com.example.model.MusicGenre
import com.example.model.SongDuration
import com.example.model.SongLyrics
import com.example.model.TimedLyricLine
import kotlinx.coroutines.flow.Flow

class SongRepository(private val songDao: SongDao) {

    val allSongs: Flow<List<StudySongEntity>> = songDao.getAllSongsFlow()
    val favoriteSongs: Flow<List<StudySongEntity>> = songDao.getFavoriteSongsFlow()

    suspend fun getSongById(id: Long): StudySongEntity? = songDao.getSongById(id)

    suspend fun insertSong(
        title: String,
        subject: String,
        genre: MusicGenre,
        language: String,
        mood: String,
        duration: SongDuration,
        originalText: String,
        lyrics: SongLyrics,
        audioPath: String
    ): Long {
        val entity = StudySongEntity(
            title = title,
            subject = subject,
            genreId = genre.id,
            genreTitle = genre.title,
            language = language,
            mood = mood,
            durationSeconds = duration.durationSecs,
            originalStudyText = originalText,
            factPreservationScore = lyrics.factPreservationScore,
            preservedFactsCsv = lyrics.preservedFacts.joinToString("|"),
            verse1 = lyrics.verse1,
            chorus = lyrics.chorus,
            verse2 = lyrics.verse2,
            bridge = lyrics.bridge,
            outro = lyrics.outro,
            audioFilePath = audioPath,
            isFavorite = false,
            playCount = 1
        )
        return songDao.insertSong(entity)
    }

    suspend fun toggleFavorite(songId: Long, currentFav: Boolean) {
        songDao.updateFavorite(songId, !currentFav)
    }

    suspend fun recordPlay(songId: Long) {
        songDao.incrementPlayCount(songId)
    }

    suspend fun deleteSong(song: StudySongEntity) {
        songDao.deleteSong(song)
    }

    suspend fun ensureSampleSongs() {
        if (songDao.getCount() == 0) {
            // Seed a starter sample song: Photosynthesis Light & Dark Reaction
            val photosynthesis = StudySongEntity(
                title = "Light to Life: The Photosynthesis Beat",
                subject = "Science",
                genreId = "lofi",
                genreTitle = "Lo-Fi Study Beats",
                language = "English",
                mood = "Deep Study Focus",
                durationSeconds = 120,
                originalStudyText = """
                    Photosynthesis occurs in plant chloroplasts using sunlight, water (H2O), and carbon dioxide (CO2).
                    Formula: 6CO2 + 6H2O + Light Energy -> C6H12O6 + 6O2.
                    Light reactions take place in the thylakoid membranes, producing ATP and NADPH while releasing Oxygen.
                    The Calvin Cycle (Dark Reaction) takes place in the stroma, using ATP and NADPH to fix CO2 into glucose.
                    Key enzyme: RuBisCO catalyzes the first major step of carbon fixation.
                    Chlorophyll a and b pigments absorb blue and red light while reflecting green light.
                """.trimIndent(),
                factPreservationScore = 98,
                preservedFactsCsv = "6CO2 + 6H2O -> C6H12O6 + 6O2|Chloroplasts|Thylakoid Membranes|ATP & NADPH|Calvin Cycle in Stroma|RuBisCO Enzyme|Chlorophyll a and b",
                verse1 = "Inside the chloroplast green leaf zone,\nSolar photon waves hitting chlorophyll alone.\nSix CO2 plus six H2O,\nWater splitting up, watch the oxygen flow!\nThylakoid membranes charging up the spark,\nATP and NADPH lit before the dark!",
                chorus = "Sunlight to sugar, turn the study into rhyme,\nPhotosynthesis working through all of time!\nSix carbon dioxide, six waters in the line,\nGlucose and oxygen, nature by design!",
                verse2 = "Now slide to the stroma where the dark reaction turns,\nCalvin cycle spinning while the RuBisCO enzyme burns.\nFixing carbon atoms into C-6-H-12-O-6,\nEnergy stored deep in every covalent mix!",
                bridge = "Red and blue absorbed, green reflection shines,\nPlants feed the planet through chemical designs!",
                outro = "Thylakoid light, stroma Calvin flight,\nMemorize the formula and ace it on the sight!",
                audioFilePath = "",
                isFavorite = true,
                playCount = 3
            )

            val quadratic = StudySongEntity(
                title = "Roots of Math: The Quadratic Flow",
                subject = "Mathematics",
                genreId = "rap",
                genreTitle = "Rap & Hip-Hop",
                language = "English",
                mood = "Energetic & Upbeat",
                durationSeconds = 110,
                originalStudyText = """
                    Quadratic standard form: ax^2 + bx + c = 0.
                    Quadratic formula: x = (-b ± √(b^2 - 4ac)) / (2a).
                    The discriminant Δ = b^2 - 4ac determines roots:
                    If Δ > 0: two distinct real roots.
                    If Δ = 0: one repeated real root.
                    If Δ < 0: two complex conjugate roots.
                """.trimIndent(),
                factPreservationScore = 96,
                preservedFactsCsv = "ax^2 + bx + c = 0|x = (-b ± √(b^2 - 4ac))/(2a)|Discriminant Δ = b^2 - 4ac|Real distinct roots|Complex conjugate roots",
                verse1 = "A-x squared plus b-x plus c equals zero,\nWhen you need to find the roots, formula is the hero!\nDrop down the negative b at the head,\nPlus or minus radical where the truth is spread!",
                chorus = "Negative b, plus or minus square root of b-squared minus 4ac,\nAll over 2a, that's the algebraic key!\nDiscriminant Δ tells you where the roots will be,\nMemorify the math and set your answers free!",
                verse2 = "Inside the radical, b-squared minus 4ac stands,\nGreater than zero? Two real roots on demand!\nEquals zero? Just one repeated on the line,\nNegative discriminant? Complex conjugate design!",
                bridge = "From standard form to parabola crest,\nPlug in the coefficients and pass the test!",
                outro = "Ax squared, bx, plus c is done,\nQuadratic formula, battle won!",
                audioFilePath = "",
                isFavorite = false,
                playCount = 2
            )

            songDao.insertSong(photosynthesis)
            songDao.insertSong(quadratic)
        }
    }
}
