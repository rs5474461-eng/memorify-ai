package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.graphics.vector.ImageVector

enum class SubjectCategory(
    val displayName: String,
    val iconName: String,
    val defaultTitle: String,
    val sampleTopic: String,
    val sampleContent: String
) {
    SCIENCE(
        displayName = "Science",
        iconName = "Biotech",
        defaultTitle = "Photosynthesis & Cellular Respiration",
        sampleTopic = "Photosynthesis Light & Dark Reactions",
        sampleContent = """
            Photosynthesis occurs in plant chloroplasts using sunlight, water (H2O), and carbon dioxide (CO2).
            Formula: 6CO2 + 6H2O + Light Energy -> C6H12O6 + 6O2.
            Light reactions take place in the thylakoid membranes, producing ATP and NADPH while releasing Oxygen.
            The Calvin Cycle (Dark Reaction) takes place in the stroma, using ATP and NADPH to fix CO2 into glucose.
            Key enzyme: RuBisCO catalyzes the first major step of carbon fixation.
            Chlorophyll a and b pigments absorb blue and red light while reflecting green light.
        """.trimIndent()
    ),
    MATHEMATICS(
        displayName = "Mathematics",
        iconName = "Calculate",
        defaultTitle = "Quadratic Equations & Calculus Derivatives",
        sampleTopic = "Quadratic Formula & Power Rule",
        sampleContent = """
            Quadratic standard form: ax^2 + bx + c = 0.
            Quadratic formula: x = (-b ± √(b^2 - 4ac)) / (2a).
            The discriminant Δ = b^2 - 4ac determines roots:
            If Δ > 0: two distinct real roots.
            If Δ = 0: one repeated real root.
            If Δ < 0: two complex conjugate roots.
            Calculus Power Rule: d/dx(x^n) = n * x^(n-1).
            Product Rule: (u*v)' = u'*v + u*v'.
            Pythagorean Theorem: a^2 + b^2 = c^2 in a right-angled triangle.
        """.trimIndent()
    ),
    HISTORY(
        displayName = "History",
        iconName = "HistoryEdu",
        defaultTitle = "Indian Freedom Struggle & Dandi March",
        sampleTopic = "Salt March & Independence Milestones",
        sampleContent = """
            The Dandi Salt March was led by Mahatma Gandhi in 1930 from Sabarmati Ashram to coastal Dandi.
            Covered 240 miles over 24 days to protest the British salt tax monopoly.
            This sparked the Civil Disobedience Movement across India.
            Quit India Movement was launched on 8 August 1942 at Gowalia Tank Maidan with the slogan 'Do or Die'.
            India achieved Independence on 15 August 1947.
            Constitution of India was adopted on 26 November 1949 and came into effect on 26 January 1950, drafted by Dr. B.R. Ambedkar.
        """.trimIndent()
    ),
    GEOGRAPHY(
        displayName = "Geography",
        iconName = "Public",
        defaultTitle = "Plate Tectonics & Earth's Layers",
        sampleTopic = "Crust, Mantle, Core & Fault Lines",
        sampleContent = """
            Earth consists of three primary concentric layers: Crust, Mantle, and Core.
            Crust: Oceanic (basalt, denser, 5-10 km) and Continental (granite, 30-50 km).
            Mantle: Silicate rocky shell extending to 2,900 km depth; includes asthenosphere.
            Core: Outer liquid nickel-iron core generates Earth's geomagnetic field; Inner solid iron core at 5,400°C.
            Plate boundaries: Divergent (spreading, mid-ocean ridges), Convergent (subduction, mountain building), and Transform (San Andreas fault).
            Continental Drift theory was proposed by Alfred Wegener in 1912.
        """.trimIndent()
    ),
    COMPUTER_SCIENCE(
        displayName = "Computer Science",
        iconName = "Code",
        defaultTitle = "Data Structures & Big-O Notation",
        sampleTopic = "Arrays, HashMaps, Trees & Complexity",
        sampleContent = """
            Big-O notation quantifies algorithm time and space complexity in the worst-case scenario.
            O(1) Constant time: Array index lookup and HashMap average key retrieval.
            O(log n) Logarithmic: Binary Search on sorted array.
            O(n) Linear: Linear search through an unsorted array or linked list.
            O(n log n) Linearithmic: Merge Sort and Quick Sort average time.
            O(n^2) Quadratic: Bubble Sort and nested iteration.
            Stack operates on LIFO (Last-In-First-Out); Queue operates on FIFO (First-In-First-Out).
            Binary Search Tree (BST): Left child is strictly smaller, right child is strictly greater than the root.
        """.trimIndent()
    ),
    ENGLISH(
        displayName = "English",
        iconName = "Language",
        defaultTitle = "Literary Devices & Figures of Speech",
        sampleTopic = "Metaphor, Simile, Personification & Alliteration",
        sampleContent = """
            Figures of speech enhance creative and academic writing:
            Simile: Direct comparison using 'like' or 'as' (e.g., 'As brave as a lion').
            Metaphor: Implied comparison without 'like' or 'as' (e.g., 'Time is a thief').
            Personification: Giving human characteristics to inanimate objects or abstract ideas.
            Hyperbole: Deliberate extreme exaggeration for emphasis.
            Alliteration: Repetition of identical initial consonant sounds in neighboring words.
            Onomatopoeia: Words that phonetically imitate the sound they describe (buzz, sizzle).
            Oxymoron: Juxtaposition of two contradictory terms (e.g., 'deafening silence').
        """.trimIndent()
    ),
    HINDI(
        displayName = "Hindi",
        iconName = "Translate",
        defaultTitle = "Hindi Vyakaran - Alankaar aur Sandhi",
        sampleTopic = "Shabdalankaar aur Swar Sandhi",
        sampleContent = """
            हिंदी व्याकरण में अलंकार और संधि के मुख्य नियम:
            अलंकार काव्य की शोभा बढ़ाने वाले तत्व हैं।
            १. अनुप्रास अलंकार: जहां समान वर्णों की आवृत्ति बार-बार हो (चारु चंद्र की चंचल किरणें)।
            २. यमक अलंकार: जहां एक ही शब्द दो या अधिक बार आए और अर्थ भिन्न हो (कनक कनक ते सौ गुनी)।
            ३. रूपक अलंकार: जहां उपमेय और उपमान में अभेद स्थापित किया जाए (चरण कमल बंदौ हरिराई)।
            संधि दो वर्णों के मेल से होने वाला विकार है:
            स्वर संधि: दीर्घ संधि (अ+अ=आ जैसे विद्या+आलय = विद्यालय), गुण संधि (देव+इंद्र = देवेंद्र), वृद्धि संधि (एक+एक = एकैक)।
        """.trimIndent()
    ),
    SOCIAL_SCIENCE(
        displayName = "Social Science",
        iconName = "School",
        defaultTitle = "Democracy & Fundamental Rights",
        sampleTopic = "Indian Constitution & Democratic Organs",
        sampleContent = """
            Democracy is a system of government by the whole population, typically through elected representatives.
            Preamble declares India a Sovereign, Socialist, Secular, Democratic Republic.
            Three organs of state: Legislature (makes laws), Executive (implements laws), Judiciary (interprets laws).
            Six Fundamental Rights in Indian Constitution (Part III, Articles 12-35):
            Right to Equality (Articles 14-18), Right to Freedom (Articles 19-22),
            Right against Exploitation (Articles 23-24), Right to Freedom of Religion (Articles 25-28),
            Cultural and Educational Rights (Articles 29-30), Right to Constitutional Remedies (Article 32, 'Heart and Soul' of Constitution).
        """.trimIndent()
    ),
    GENERAL_KNOWLEDGE(
        displayName = "General Knowledge",
        iconName = "MenuBook",
        defaultTitle = "Solar System & Space Exploration",
        sampleTopic = "Planets, Moon Missions & Astronomical Units",
        sampleContent = """
            The Solar System consists of the Sun and 8 major planets.
            Terrestrial inner rocky planets: Mercury, Venus, Earth, Mars.
            Gas giants: Jupiter (largest, Great Red Spot) and Saturn (prominent ring system).
            Ice giants: Uranus (rotates on its side) and Neptune (strongest winds).
            Venus is the hottest planet due to runaway greenhouse effect (96% CO2 atmosphere).
            First human in space: Yuri Gagarin on Vostok 1 in 1961.
            First moon landing: Apollo 11 on 20 July 1969 with Neil Armstrong and Buzz Aldrin.
            ISRO Chandrayaan-3 landed on lunar south polar region on 23 August 2023.
        """.trimIndent()
    ),
    CUSTOM(
        displayName = "Custom Topics",
        iconName = "Create",
        defaultTitle = "Custom Study Topic",
        sampleTopic = "Paste any notes, formula sheet or textbook chapter",
        sampleContent = ""
    )
}

enum class MusicGenre(
    val id: String,
    val title: String,
    val bpm: Int,
    val vibe: String,
    val emoji: String,
    val description: String
) {
    RAP_HIPHOP(
        id = "rap",
        title = "Rap & Hip-Hop",
        bpm = 96,
        vibe = "Fast Cadence, 808 Bass & Punchy Rhymes",
        emoji = "🎤",
        description = "High retention punchy flows with deep bass and crisp snare rhythms."
    ),
    LOFI_STUDY(
        id = "lofi",
        title = "Lo-Fi Study Beats",
        bpm = 78,
        vibe = "Mellow Vinyl, Chill Grooves & Smooth Rhodes",
        emoji = "☕",
        description = "Relaxing, focused ambient lo-fi beats designed for long study sessions."
    ),
    POP_ANTHEM(
        id = "pop",
        title = "Pop Anthem",
        bpm = 120,
        vibe = "Catchy Melodic Hooks & Driving Synths",
        emoji = "🌟",
        description = "Earworm chorus melodies that stick formulas and definitions in your head."
    ),
    ACOUSTIC(
        id = "acoustic",
        title = "Acoustic Warmth",
        bpm = 85,
        vibe = "Warm Guitar Plucks & Folk Harmonies",
        emoji = "🎸",
        description = "Organic acoustic strums with storytelling lyrics for deep conceptual clarity."
    ),
    ELECTRONIC_EDM(
        id = "edm",
        title = "Electronic Pulse",
        bpm = 128,
        vibe = "Hypnotic Arpeggios & Energetic Bass Drops",
        emoji = "⚡",
        description = "High adrenaline study banger with pulsing synths to energize exam revision."
    ),
    BOLLYWOOD_FUSION(
        id = "bollywood",
        title = "Bollywood Fusion",
        bpm = 104,
        vibe = "Desi Tabla, Sitar Riffs & Rhythmic Hooks",
        emoji = "🪘",
        description = "Vibrant Hindi/Hinglish cinematic melodies with infectious desi rhythm."
    ),
    MOTIVATIONAL(
        id = "motivational",
        title = "Heroic Motivation",
        bpm = 112,
        vibe = "Orchestral Brass, Heavy Percussion & Triumphant",
        emoji = "🔥",
        description = "Epic cinematic build-ups inspiring mastery and unbeatable exam confidence."
    ),
    SOFT_BALLAD(
        id = "ballad",
        title = "Soft Melodic",
        bpm = 72,
        vibe = "Gentle Piano Chords & Atmospheric Vocals",
        emoji = "🎹",
        description = "Soothing ballad arrangements for nighttime revision and stress-free memory."
    )
}

enum class StudyLanguage(val code: String, val displayName: String, val flag: String) {
    ENGLISH("en", "English", "🇺🇸"),
    HINDI("hi", "Hindi", "🇮🇳"),
    HINGLISH("hinglish", "Hinglish (Hindi+English)", "🇮🇳")
}

enum class SongMood(val displayName: String, val description: String) {
    ENERGETIC("Energetic & Upbeat", "Fast-paced, vibrant, high-tempo memory booster"),
    CHILL("Chill & Relaxed", "Laid back, relaxing, calm flow for anxiety reduction"),
    FOCUS("Deep Study Focus", "Repetitive rhythmic pulse optimal for concentration"),
    EPIC("Epic & Heroic", "Powerful, grand, cinematic climaxes for memorable facts")
}

enum class SongDuration(val displayName: String, val durationSecs: Int, val description: String) {
    QUICK_REVISION("Quick Blitz (1 Min)", 60, "Concise summary of top formulas & core definitions"),
    STANDARD("Standard Song (2 Min)", 120, "Complete structured song: Verse 1, Chorus, Verse 2, Outro"),
    DEEP_DIVE("Deep Dive (3 Min)", 180, "Full breakdown with Bridge, mnemonics, and extensive facts")
}

data class TimedLyricLine(
    val section: String,
    val lineText: String,
    val startSec: Float,
    val endSec: Float
)

data class SongLyrics(
    val title: String,
    val subject: String,
    val genre: String,
    val language: String,
    val factPreservationScore: Int,
    val preservedFacts: List<String>,
    val verse1: String,
    val chorus: String,
    val verse2: String,
    val bridge: String,
    val outro: String,
    val timedLines: List<TimedLyricLine> = emptyList()
) {
    val fullLyricsText: String
        get() = buildString {
            appendLine("=== [Verse 1] ===")
            appendLine(verse1)
            appendLine()
            appendLine("=== [Chorus] ===")
            appendLine(chorus)
            appendLine()
            appendLine("=== [Verse 2] ===")
            appendLine(verse2)
            appendLine()
            if (bridge.isNotBlank()) {
                appendLine("=== [Bridge] ===")
                appendLine(bridge)
                appendLine()
            }
            appendLine("=== [Chorus] ===")
            appendLine(chorus)
            appendLine()
            if (outro.isNotBlank()) {
                appendLine("=== [Outro] ===")
                appendLine(outro)
            }
        }
}

enum class QuizQuestionType {
    MULTIPLE_CHOICE,
    FILL_IN_THE_BLANK,
    TRUE_FALSE
}

data class QuizQuestion(
    val id: Int,
    val question: String,
    val type: QuizQuestionType,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val testedFact: String
)

data class QuizSession(
    val songTitle: String,
    val subject: String,
    val questions: List<QuizQuestion>,
    val userAnswers: MutableMap<Int, Int> = mutableMapOf(),
    var isSubmitted: Boolean = false
) {
    val score: Int
        get() = questions.count { q -> userAnswers[q.id] == q.correctIndex }

    val percentage: Int
        get() = if (questions.isEmpty()) 0 else (score * 100) / questions.size

    val gradeTitle: String
        get() = when {
            percentage == 100 -> "Flawless Memory! S-Tier"
            percentage >= 80 -> "Excellent Retention! A-Tier"
            percentage >= 60 -> "Good Recall! B-Tier"
            else -> "Needs Revision! Keep Listening"
        }
}
