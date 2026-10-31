package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.MusicGenre
import com.example.model.SongDuration
import com.example.model.SongLyrics
import com.example.model.StudyLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

private const val TAG = "MusicAudioEngine"

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val progress: Float = 0f,
    val currentLyricIndex: Int = 0,
    val audioVisualizerBars: List<Float> = List(16) { 0.1f }
)

class MusicAudioEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var mediaPlayer: MediaPlayer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var currentSongLyrics: SongLyrics? = null
    private var activeAudioFile: File? = null

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            textToSpeech?.setSpeechRate(1.05f)
            textToSpeech?.setPitch(1.0f)
        } else {
            Log.w(TAG, "TextToSpeech init status: $status")
        }
    }

    /**
     * Generates a multi-track educational song WAV file.
     * Combines procedural rhythmic beats, basslines, and melodic instruments
     * with synthesized vocal singing/rhythmic lyric reciting.
     */
    suspend fun generateSongAudio(
        lyrics: SongLyrics,
        genre: MusicGenre,
        language: StudyLanguage,
        duration: SongDuration,
        onProgress: (Float, String) -> Unit
    ): File = withContext(Dispatchers.IO) {
        onProgress(0.15f, "Preparing rhythm sequencer & acoustic soundstage...")

        val outputDir = File(context.filesDir, "study_songs").apply { mkdirs() }
        val songFile = File(outputDir, "song_${System.currentTimeMillis()}.wav")

        onProgress(0.35f, "Synthesizing ${genre.title} beat, 808 bass & chord harmony...")

        // Render backing audio track (PCM 16-bit 44.1kHz stereo)
        val sampleRate = 44100
        val targetSeconds = minOf(duration.durationSecs, 75) // Optimal high-quality length
        val totalSamples = sampleRate * targetSeconds

        val backingPcm = generateBackingTrackPcm(genre, targetSeconds, sampleRate) { prog, msg ->
            onProgress(0.35f + prog * 0.35f, msg)
        }

        onProgress(0.75f, "Rendering vocal melody track with educational lyrics...")

        // Synthesize vocals if TTS is available
        val vocalPcm = if (isTtsReady) {
            synthesizeVocalPcm(lyrics, language)
        } else null

        onProgress(0.90f, "Mixing vocal master & mastering WAV audio...")

        // Mix backing track and vocals together
        val mixedPcm = if (vocalPcm != null && vocalPcm.isNotEmpty()) {
            mixAudioTracks(backingPcm, vocalPcm)
        } else {
            backingPcm
        }

        // Write standard 16-bit PCM WAV with proper RIFF header
        writeWavFile(songFile, mixedPcm, sampleRate, 2)

        onProgress(1.0f, "Audio generation complete!")
        activeAudioFile = songFile
        currentSongLyrics = lyrics
        songFile
    }

    private suspend fun synthesizeVocalPcm(lyrics: SongLyrics, language: StudyLanguage): ByteArray? = withContext(Dispatchers.IO) {
        val tts = textToSpeech ?: return@withContext null
        if (!isTtsReady) return@withContext null

        try {
            when (language) {
                StudyLanguage.HINDI -> {
                    val hiLocale = Locale.forLanguageTag("hi-IN")
                    if (tts.isLanguageAvailable(hiLocale) >= TextToSpeech.LANG_AVAILABLE) {
                        tts.language = hiLocale
                    }
                }
                StudyLanguage.HINGLISH -> {
                    val inLocale = Locale.forLanguageTag("en-IN")
                    if (tts.isLanguageAvailable(inLocale) >= TextToSpeech.LANG_AVAILABLE) {
                        tts.language = inLocale
                    }
                }
                else -> {
                    tts.language = Locale.US
                }
            }

            val vocalFile = File(context.cacheDir, "temp_vocals_${System.currentTimeMillis()}.wav")
            val utteranceId = "vocal_track_${System.currentTimeMillis()}"

            val vocalScript = buildString {
                append(lyrics.title).append(". ")
                append("Verse one. ").append(lyrics.verse1.replace("\n", ". ")).append(". ")
                append("Chorus. ").append(lyrics.chorus.replace("\n", ". ")).append(". ")
                append("Verse two. ").append(lyrics.verse2.replace("\n", ". ")).append(". ")
                if (lyrics.bridge.isNotBlank()) {
                    append("Bridge. ").append(lyrics.bridge.replace("\n", ". ")).append(". ")
                }
                append("Chorus. ").append(lyrics.chorus.replace("\n", ". ")).append(". ")
                if (lyrics.outro.isNotBlank()) {
                    append("Outro. ").append(lyrics.outro.replace("\n", ". "))
                }
            }

            val success = suspendCancellableCoroutine<Boolean> { cont ->
                val listener = object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(id: String?) {
                        if (id == utteranceId && cont.isActive) {
                            cont.resume(true)
                        }
                    }
                    override fun onError(id: String?) {
                        if (id == utteranceId && cont.isActive) {
                            cont.resume(false)
                        }
                    }
                }
                tts.setOnUtteranceProgressListener(listener)
                val params = Bundle()
                val result = tts.synthesizeToFile(vocalScript, params, vocalFile, utteranceId)
                if (result != TextToSpeech.SUCCESS && cont.isActive) {
                    cont.resume(false)
                }
            }

            if (success && vocalFile.exists() && vocalFile.length() > 44) {
                val bytes = vocalFile.readBytes()
                vocalFile.delete()
                // Strip WAV header (first 44 bytes) to get raw PCM
                return@withContext bytes.copyOfRange(44, bytes.size)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error synthesizing vocals", e)
        }
        return@withContext null
    }

    /**
     * Generates stereo 16-bit PCM multi-track audio:
     * Kick, Snare, Hi-hat, Bassline, and Chords matching the genre's tempo and rhythm.
     */
    private fun generateBackingTrackPcm(
        genre: MusicGenre,
        durationSeconds: Int,
        sampleRate: Int,
        onProgress: (Float, String) -> Unit
    ): ByteArray {
        val totalSamples = sampleRate * durationSeconds
        val pcm = ShortArray(totalSamples * 2) // Stereo (L, R)

        val bpm = genre.bpm
        val secondsPerBeat = 60.0 / bpm
        val samplesPerBeat = (sampleRate * secondsPerBeat).toInt()
        val totalBeats = (durationSeconds / secondsPerBeat).toInt()

        // Chord frequencies (Root notes for chord progression: C, G, Am, F)
        val chordFrequencies = when (genre) {
            MusicGenre.RAP_HIPHOP -> listOf(55.0, 51.9, 49.0, 46.2) // Dark minor: A, G#, G, F
            MusicGenre.BOLLYWOOD_FUSION -> listOf(65.4, 73.4, 82.4, 87.3) // C, D, E, F
            MusicGenre.LOFI_STUDY -> listOf(65.4, 82.4, 73.4, 58.3) // Cmaj7, Em7, Dm7, Bb
            MusicGenre.ELECTRONIC_EDM -> listOf(65.4, 55.0, 43.6, 58.3) // C, A, F, G
            else -> listOf(65.4, 49.0, 55.0, 43.6) // C, G, A, F
        }

        for (beat in 0 until totalBeats) {
            val beatStartSample = beat * samplesPerBeat
            val chordIndex = (beat / 4) % chordFrequencies.size
            val rootFreq = chordFrequencies[chordIndex]

            // 1. Kick Drum on beats 0 and 2 (or on 0, 1, 2, 3 for EDM)
            val isKickBeat = when (genre) {
                MusicGenre.ELECTRONIC_EDM -> true
                MusicGenre.RAP_HIPHOP -> beat % 4 == 0 || beat % 4 == 3
                else -> beat % 4 == 0 || beat % 4 == 2
            }
            if (isKickBeat) {
                renderKickDrum(pcm, beatStartSample, sampleRate)
            }

            // 2. Snare / Clap on beats 1 and 3 (2 & 4 in musical terms)
            if (beat % 4 == 1 || beat % 4 == 3) {
                renderSnare(pcm, beatStartSample, sampleRate)
            }

            // 3. Hi-hat on every 8th note
            renderHiHat(pcm, beatStartSample, sampleRate)
            renderHiHat(pcm, beatStartSample + samplesPerBeat / 2, sampleRate)

            // 4. Bassline following root frequency
            renderBassline(pcm, beatStartSample, samplesPerBeat, rootFreq, sampleRate)

            // 5. Melodic Chords & Arpeggios
            renderChord(pcm, beatStartSample, samplesPerBeat, rootFreq, sampleRate, genre)
        }

        // Convert ShortArray to ByteArray (little-endian)
        val byteBuffer = ByteBuffer.allocate(pcm.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        for (sample in pcm) {
            byteBuffer.putShort(sample)
        }
        return byteBuffer.array()
    }

    private fun renderKickDrum(pcm: ShortArray, startSample: Int, sampleRate: Int) {
        val durationSamples = (sampleRate * 0.25).toInt() // 250ms kick
        for (i in 0 until durationSamples) {
            val index = (startSample + i) * 2
            if (index + 1 >= pcm.size) break
            val t = i.toDouble() / sampleRate
            // Sine pitch drop from 140Hz down to 40Hz
            val freq = 140.0 * exp(-t * 22.0) + 40.0
            val envelope = exp(-t * 12.0)
            val value = (sin(2.0 * PI * freq * t) * envelope * 14000.0).toInt().coerceIn(-30000, 30000)
            pcm[index] = (pcm[index] + value).coerceIn(-32768, 32767).toShort()
            pcm[index + 1] = (pcm[index + 1] + value).coerceIn(-32768, 32767).toShort()
        }
    }

    private fun renderSnare(pcm: ShortArray, startSample: Int, sampleRate: Int) {
        val durationSamples = (sampleRate * 0.18).toInt()
        for (i in 0 until durationSamples) {
            val index = (startSample + i) * 2
            if (index + 1 >= pcm.size) break
            val t = i.toDouble() / sampleRate
            val noise = (Math.random() * 2.0 - 1.0)
            val tone = sin(2.0 * PI * 180.0 * t) * 0.4
            val envelope = exp(-t * 20.0)
            val value = ((noise * 0.7 + tone) * envelope * 9000.0).toInt()
            pcm[index] = (pcm[index] + value).coerceIn(-32768, 32767).toShort()
            pcm[index + 1] = (pcm[index + 1] + value).coerceIn(-32768, 32767).toShort()
        }
    }

    private fun renderHiHat(pcm: ShortArray, startSample: Int, sampleRate: Int) {
        val durationSamples = (sampleRate * 0.05).toInt()
        for (i in 0 until durationSamples) {
            val index = (startSample + i) * 2
            if (index + 1 >= pcm.size) break
            val t = i.toDouble() / sampleRate
            val noise = (Math.random() * 2.0 - 1.0)
            val envelope = exp(-t * 70.0)
            val value = (noise * envelope * 4500.0).toInt()
            pcm[index] = (pcm[index] + value).coerceIn(-32768, 32767).toShort()
            pcm[index + 1] = (pcm[index + 1] + value).coerceIn(-32768, 32767).toShort()
        }
    }

    private fun renderBassline(pcm: ShortArray, startSample: Int, durationSamples: Int, rootFreq: Double, sampleRate: Int) {
        for (i in 0 until durationSamples) {
            val index = (startSample + i) * 2
            if (index + 1 >= pcm.size) break
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 3.5)
            // Sub-bass fundamental + 2nd harmonic
            val bass = sin(2.0 * PI * rootFreq * t) * 0.7 + sin(2.0 * PI * rootFreq * 2.0 * t) * 0.3
            val value = (bass * envelope * 8000.0).toInt()
            pcm[index] = (pcm[index] + value).coerceIn(-32768, 32767).toShort()
            pcm[index + 1] = (pcm[index + 1] + value).coerceIn(-32768, 32767).toShort()
        }
    }

    private fun renderChord(
        pcm: ShortArray,
        startSample: Int,
        durationSamples: Int,
        rootFreq: Double,
        sampleRate: Int,
        genre: MusicGenre
    ) {
        // Form triad: Root, Major/Minor Third (1.2 or 1.25), Fifth (1.5), Seventh (1.875)
        val chordFreqs = listOf(rootFreq * 2.0, rootFreq * 2.4, rootFreq * 3.0, rootFreq * 3.6)
        for (i in 0 until durationSamples) {
            val index = (startSample + i) * 2
            if (index + 1 >= pcm.size) break
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 2.0)

            var chordSample = 0.0
            for (f in chordFreqs) {
                chordSample += sin(2.0 * PI * f * t)
            }
            chordSample /= chordFreqs.size

            val valueL = (chordSample * envelope * 4000.0).toInt()
            val valueR = (chordSample * envelope * 3800.0).toInt()
            pcm[index] = (pcm[index] + valueL).coerceIn(-32768, 32767).toShort()
            pcm[index + 1] = (pcm[index + 1] + valueR).coerceIn(-32768, 32767).toShort()
        }
    }

    private fun mixAudioTracks(backing: ByteArray, vocalPcm: ByteArray): ByteArray {
        val backingShorts = ByteBuffer.wrap(backing).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        val vocalShorts = ByteBuffer.wrap(vocalPcm).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()

        val mixedLength = backingShorts.capacity()
        val resultShorts = ShortArray(mixedLength)

        val vocalLen = vocalShorts.capacity()
        var vocalIndex = 0

        for (i in 0 until mixedLength step 2) {
            val backL = backingShorts.get(i).toInt()
            val backR = backingShorts.get(i + 1).toInt()

            val voc = if (vocalIndex < vocalLen) {
                vocalShorts.get(vocalIndex).toInt().also { vocalIndex++ }
            } else 0

            // Mix: 70% backing + 90% vocals (vocal emphasis for study clarity)
            val mixedL = ((backL * 0.65) + (voc * 0.95)).toInt().coerceIn(-32768, 32767).toShort()
            val mixedR = ((backR * 0.65) + (voc * 0.95)).toInt().coerceIn(-32768, 32767).toShort()

            resultShorts[i] = mixedL
            resultShorts[i + 1] = mixedR
        }

        val outBuffer = ByteBuffer.allocate(resultShorts.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        for (s in resultShorts) {
            outBuffer.putShort(s)
        }
        return outBuffer.array()
    }

    private fun writeWavFile(file: File, pcmData: ByteArray, sampleRate: Int, channels: Int) {
        val byteRate = sampleRate * channels * 2
        val totalAudioLen = pcmData.size
        val totalDataLen = totalAudioLen + 36

        FileOutputStream(file).use { out ->
            val header = ByteArray(44)
            // RIFF chunk descriptor
            header[0] = 'R'.code.toByte()
            header[1] = 'I'.code.toByte()
            header[2] = 'F'.code.toByte()
            header[3] = 'F'.code.toByte()
            header[4] = (totalDataLen and 0xff).toByte()
            header[5] = ((totalDataLen shr 8) and 0xff).toByte()
            header[6] = ((totalDataLen shr 16) and 0xff).toByte()
            header[7] = ((totalDataLen shr 24) and 0xff).toByte()
            header[8] = 'W'.code.toByte()
            header[9] = 'A'.code.toByte()
            header[10] = 'V'.code.toByte()
            header[11] = 'E'.code.toByte()

            // "fmt " sub-chunk
            header[12] = 'f'.code.toByte()
            header[13] = 'm'.code.toByte()
            header[14] = 't'.code.toByte()
            header[15] = ' '.code.toByte()
            header[16] = 16 // SubChunk1Size (16 for PCM)
            header[17] = 0
            header[18] = 0
            header[19] = 0
            header[20] = 1 // AudioFormat (1 for PCM)
            header[21] = 0
            header[22] = channels.toByte()
            header[23] = 0
            header[24] = (sampleRate and 0xff).toByte()
            header[25] = ((sampleRate shr 8) and 0xff).toByte()
            header[26] = ((sampleRate shr 16) and 0xff).toByte()
            header[27] = ((sampleRate shr 24) and 0xff).toByte()
            header[28] = (byteRate and 0xff).toByte()
            header[29] = ((byteRate shr 8) and 0xff).toByte()
            header[30] = ((byteRate shr 16) and 0xff).toByte()
            header[31] = ((byteRate shr 24) and 0xff).toByte()
            header[32] = (channels * 2).toByte() // BlockAlign
            header[33] = 0
            header[34] = 16 // BitsPerSample
            header[35] = 0

            // "data" sub-chunk
            header[36] = 'd'.code.toByte()
            header[37] = 'a'.code.toByte()
            header[38] = 't'.code.toByte()
            header[39] = 'a'.code.toByte()
            header[40] = (totalAudioLen and 0xff).toByte()
            header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
            header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
            header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

            out.write(header)
            out.write(pcmData)
        }
    }

    // ---------------- Playback Controls ----------------

    fun playSong(file: File, lyrics: SongLyrics? = null) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        currentPositionMs = 0,
                        progress = 0f
                    )
                }
            }
            activeAudioFile = file
            currentSongLyrics = lyrics
            val duration = mediaPlayer?.duration ?: 0
            _playbackState.value = _playbackState.value.copy(
                isPlaying = true,
                durationMs = duration,
                currentPositionMs = 0
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play audio file", e)
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            player.pause()
            _playbackState.value = _playbackState.value.copy(isPlaying = false)
        } else {
            player.start()
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        val duration = mediaPlayer?.duration ?: 1
        _playbackState.value = _playbackState.value.copy(
            currentPositionMs = positionMs,
            progress = (positionMs.toFloat() / duration).coerceIn(0f, 1f)
        )
    }

    fun seekRelative(deltaSeconds: Int) {
        val player = mediaPlayer ?: return
        val newPos = (player.currentPosition + deltaSeconds * 1000).coerceIn(0, player.duration)
        seekTo(newPos)
    }

    fun setVolume(volume: Float) {
        val v = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(v, v)
    }

    fun updatePlaybackTick() {
        val player = mediaPlayer ?: return
        if (!player.isPlaying) return
        val pos = player.currentPosition
        val duration = player.duration
        val progress = if (duration > 0) (pos.toFloat() / duration).coerceIn(0f, 1f) else 0f

        // Calculate synchronized lyric index
        val currentSec = pos / 1000f
        val lines = currentSongLyrics?.timedLines ?: emptyList()
        val lyricIndex = lines.indexOfFirst { currentSec >= it.startSec && currentSec <= it.endSec }.let {
            if (it >= 0) it else 0
        }

        // Generate synthetic visualizer bars
        val visualizerBars = List(16) { i ->
            val freq = (i + 1) * 3.0
            val phase = (pos / 200.0)
            ((sin(phase * freq) * 0.5 + 0.5) * 0.8 + 0.2).toFloat().coerceIn(0.1f, 1.0f)
        }

        _playbackState.value = _playbackState.value.copy(
            isPlaying = player.isPlaying,
            currentPositionMs = pos,
            durationMs = duration,
            progress = progress,
            currentLyricIndex = lyricIndex,
            audioVisualizerBars = visualizerBars
        )
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}
