package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.ai.AiLyricQuizEngine
import com.example.audio.MusicAudioEngine
import com.example.audio.PlaybackState
import com.example.data.MemorifyDatabase
import com.example.data.SongRepository
import com.example.data.StudySongEntity
import com.example.model.MusicGenre
import com.example.model.QuizQuestion
import com.example.model.QuizSession
import com.example.model.SongDuration
import com.example.model.SongLyrics
import com.example.model.SongMood
import com.example.model.StudyLanguage
import com.example.model.SubjectCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

enum class AppScreen {
    DASHBOARD,
    CREATE_WIZARD,
    MUSIC_PLAYER,
    LIBRARY,
    MEMORY_QUIZ,
    SETTINGS
}

data class WizardState(
    val step: Int = 1,
    val studyText: String = SubjectCategory.SCIENCE.sampleContent,
    val selectedSubject: SubjectCategory = SubjectCategory.SCIENCE,
    val selectedLanguage: StudyLanguage = StudyLanguage.ENGLISH,
    val selectedGenre: MusicGenre = MusicGenre.LOFI_STUDY,
    val selectedMood: SongMood = SongMood.FOCUS,
    val selectedDuration: SongDuration = SongDuration.STANDARD,
    val isGeneratingLyrics: Boolean = false,
    val lyricsError: String? = null,
    val generatedLyrics: SongLyrics? = null,
    val isGeneratingAudio: Boolean = false,
    val audioProgress: Float = 0f,
    val audioProgressText: String = "",
    val generatedAudioFile: File? = null,
    val savedSongId: Long? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MemorifyDatabase.getDatabase(application)
    private val repository = SongRepository(database.songDao())
    val audioEngine = MusicAudioEngine(application)
    private val aiEngine = AiLyricQuizEngine()

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _wizardState = MutableStateFlow(WizardState())
    val wizardState: StateFlow<WizardState> = _wizardState.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = audioEngine.playbackState

    private val _activeSongLyrics = MutableStateFlow<SongLyrics?>(null)
    val activeSongLyrics: StateFlow<SongLyrics?> = _activeSongLyrics.asStateFlow()

    private val _activeSongTitle = MutableStateFlow("Light to Life: The Photosynthesis Beat")
    val activeSongTitle: StateFlow<String> = _activeSongTitle.asStateFlow()

    private val _activeSongSubject = MutableStateFlow("Science")
    val activeSongSubject: StateFlow<String> = _activeSongSubject.asStateFlow()

    private val _activeSongGenre = MutableStateFlow("Lo-Fi Study Beats")
    val activeSongGenre: StateFlow<String> = _activeSongGenre.asStateFlow()

    // Library search & filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSubjectFilter = MutableStateFlow<String?>(null)
    val selectedSubjectFilter: StateFlow<String?> = _selectedSubjectFilter.asStateFlow()

    val librarySongs: StateFlow<List<StudySongEntity>> = combine(
        repository.allSongs,
        _searchQuery,
        _selectedSubjectFilter
    ) { songs, query, subjectFilter ->
        songs.filter { song ->
            val matchesQuery = query.isBlank() ||
                    song.title.contains(query, ignoreCase = true) ||
                    song.subject.contains(query, ignoreCase = true) ||
                    song.genreTitle.contains(query, ignoreCase = true)
            val matchesSubject = subjectFilter == null || song.subject.equals(subjectFilter, ignoreCase = true)
            matchesQuery && matchesSubject
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Memory Quiz State
    private val _quizSession = MutableStateFlow<QuizSession?>(null)
    val quizSession: StateFlow<QuizSession?> = _quizSession.asStateFlow()

    private val _isGeneratingQuiz = MutableStateFlow(false)
    val isGeneratingQuiz: StateFlow<Boolean> = _isGeneratingQuiz.asStateFlow()

    private val _quizError = MutableStateFlow<String?>(null)
    val quizError: StateFlow<String?> = _quizError.asStateFlow()

    private var playbackTickerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureSampleSongs()
        }
        startPlaybackTicker()
    }

    private fun startPlaybackTicker() {
        playbackTickerJob?.cancel()
        playbackTickerJob = viewModelScope.launch {
            while (isActive) {
                audioEngine.updatePlaybackTick()
                delay(100)
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // --- Wizard Actions ---

    fun setWizardStep(step: Int) {
        _wizardState.value = _wizardState.value.copy(step = step.coerceIn(1, 8))
    }

    fun setStudyText(text: String) {
        _wizardState.value = _wizardState.value.copy(studyText = text)
    }

    fun selectSubject(subject: SubjectCategory) {
        _wizardState.value = _wizardState.value.copy(
            selectedSubject = subject,
            studyText = if (subject.sampleContent.isNotBlank()) subject.sampleContent else _wizardState.value.studyText
        )
    }

    fun selectLanguage(language: StudyLanguage) {
        _wizardState.value = _wizardState.value.copy(selectedLanguage = language)
    }

    fun selectGenre(genre: MusicGenre) {
        _wizardState.value = _wizardState.value.copy(selectedGenre = genre)
    }

    fun selectMood(mood: SongMood) {
        _wizardState.value = _wizardState.value.copy(selectedMood = mood)
    }

    fun selectDuration(duration: SongDuration) {
        _wizardState.value = _wizardState.value.copy(selectedDuration = duration)
    }

    fun startGeneratingLyrics() {
        val state = _wizardState.value
        _wizardState.value = state.copy(
            isGeneratingLyrics = true,
            lyricsError = null,
            step = 5
        )

        viewModelScope.launch {
            try {
                val lyrics = aiEngine.generateLyrics(
                    studyText = state.studyText,
                    subject = state.selectedSubject.displayName,
                    genre = state.selectedGenre,
                    language = state.selectedLanguage,
                    mood = state.selectedMood.displayName,
                    duration = state.selectedDuration
                )
                _wizardState.value = _wizardState.value.copy(
                    isGeneratingLyrics = false,
                    generatedLyrics = lyrics,
                    step = 6
                )
            } catch (e: Exception) {
                _wizardState.value = _wizardState.value.copy(
                    isGeneratingLyrics = false,
                    lyricsError = e.localizedMessage ?: "Failed to generate educational lyrics"
                )
            }
        }
    }

    fun updateLyricsSection(verse1: String, chorus: String, verse2: String, bridge: String, outro: String) {
        val current = _wizardState.value.generatedLyrics ?: return
        _wizardState.value = _wizardState.value.copy(
            generatedLyrics = current.copy(
                verse1 = verse1,
                chorus = chorus,
                verse2 = verse2,
                bridge = bridge,
                outro = outro
            )
        )
    }

    fun generateFullSong() {
        val state = _wizardState.value
        val lyrics = state.generatedLyrics ?: return

        _wizardState.value = state.copy(
            isGeneratingAudio = true,
            audioProgress = 0.05f,
            audioProgressText = "Starting audio generation...",
            step = 7
        )

        viewModelScope.launch {
            try {
                val audioFile = audioEngine.generateSongAudio(
                    lyrics = lyrics,
                    genre = state.selectedGenre,
                    language = state.selectedLanguage,
                    duration = state.selectedDuration
                ) { progress, statusText ->
                    _wizardState.value = _wizardState.value.copy(
                        audioProgress = progress,
                        audioProgressText = statusText
                    )
                }

                // Save to Room Library
                val savedId = repository.insertSong(
                    title = lyrics.title,
                    subject = lyrics.subject,
                    genre = state.selectedGenre,
                    language = lyrics.language,
                    mood = state.selectedMood.displayName,
                    duration = state.selectedDuration,
                    originalText = state.studyText,
                    lyrics = lyrics,
                    audioPath = audioFile.absolutePath
                )

                _wizardState.value = _wizardState.value.copy(
                    isGeneratingAudio = false,
                    generatedAudioFile = audioFile,
                    savedSongId = savedId,
                    step = 8
                )

                // Load into active player
                loadSongIntoPlayer(
                    title = lyrics.title,
                    subject = lyrics.subject,
                    genre = state.selectedGenre.title,
                    lyrics = lyrics,
                    audioFile = audioFile
                )
            } catch (e: Exception) {
                _wizardState.value = _wizardState.value.copy(
                    isGeneratingAudio = false,
                    audioProgressText = "Generation error: ${e.localizedMessage}"
                )
            }
        }
    }

    // --- Player Actions ---

    fun loadSongIntoPlayer(
        title: String,
        subject: String,
        genre: String,
        lyrics: SongLyrics,
        audioFile: File?
    ) {
        _activeSongTitle.value = title
        _activeSongSubject.value = subject
        _activeSongGenre.value = genre
        _activeSongLyrics.value = lyrics

        if (audioFile != null && audioFile.exists()) {
            audioEngine.playSong(audioFile, lyrics)
        }
    }

    fun playSongFromLibrary(song: StudySongEntity) {
        viewModelScope.launch {
            repository.recordPlay(song.id)
            val audioFile = if (song.audioFilePath.isNotBlank()) File(song.audioFilePath) else null

            val lyrics = SongLyrics(
                title = song.title,
                subject = song.subject,
                genre = song.genreTitle,
                language = song.language,
                factPreservationScore = song.factPreservationScore,
                preservedFacts = song.preservedFactsCsv.split("|").filter { it.isNotBlank() },
                verse1 = song.verse1,
                chorus = song.chorus,
                verse2 = song.verse2,
                bridge = song.bridge,
                outro = song.outro
            )

            // If audio file doesn't exist yet (e.g. sample song), synthesize it on the fly
            val playableFile = if (audioFile != null && audioFile.exists()) {
                audioFile
            } else {
                val genre = MusicGenre.entries.find { it.id == song.genreId } ?: MusicGenre.LOFI_STUDY
                audioEngine.generateSongAudio(
                    lyrics = lyrics,
                    genre = genre,
                    language = StudyLanguage.ENGLISH,
                    duration = SongDuration.STANDARD
                ) { _, _ -> }
            }

            loadSongIntoPlayer(
                title = song.title,
                subject = song.subject,
                genre = song.genreTitle,
                lyrics = lyrics,
                audioFile = playableFile
            )
            navigateTo(AppScreen.MUSIC_PLAYER)
        }
    }

    fun toggleFavorite(song: StudySongEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(song.id, song.isFavorite)
        }
    }

    fun deleteSong(song: StudySongEntity) {
        viewModelScope.launch {
            repository.deleteSong(song)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSubjectFilter(subject: String?) {
        _selectedSubjectFilter.value = subject
    }

    // --- Quiz Actions ---

    fun startQuizForCurrentSong() {
        val currentLyrics = _activeSongLyrics.value ?: _wizardState.value.generatedLyrics
        val studyText = _wizardState.value.studyText
        val subject = _activeSongSubject.value

        _isGeneratingQuiz.value = true
        _quizError.value = null
        navigateTo(AppScreen.MEMORY_QUIZ)

        viewModelScope.launch {
            try {
                val questions = aiEngine.generateQuiz(
                    studyText = studyText.ifBlank { currentLyrics?.fullLyricsText ?: "Photosynthesis light and dark reactions" },
                    subject = subject
                )
                _quizSession.value = QuizSession(
                    songTitle = _activeSongTitle.value,
                    subject = subject,
                    questions = questions
                )
                _isGeneratingQuiz.value = false
            } catch (e: Exception) {
                _isGeneratingQuiz.value = false
                _quizError.value = e.localizedMessage ?: "Failed to generate memory quiz"
            }
        }
    }

    fun answerQuizQuestion(questionId: Int, selectedIndex: Int) {
        val session = _quizSession.value ?: return
        if (session.isSubmitted) return
        session.userAnswers[questionId] = selectedIndex
        // Trigger recomposition
        _quizSession.value = session.copy(userAnswers = HashMap(session.userAnswers))
    }

    fun submitQuiz() {
        val session = _quizSession.value ?: return
        _quizSession.value = session.copy(isSubmitted = true)
    }

    fun retryQuiz() {
        val session = _quizSession.value ?: return
        _quizSession.value = session.copy(userAnswers = mutableMapOf(), isSubmitted = false)
    }

    override fun onCleared() {
        super.onCleared()
        playbackTickerJob?.cancel()
        audioEngine.release()
    }
}
