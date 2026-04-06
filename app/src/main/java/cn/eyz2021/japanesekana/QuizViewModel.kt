package cn.eyz2021.japanesekana

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class QuizMode {
    AUDIO_TO_CHAR,   // 听音选字
    CHAR_TO_ROMAJI,  // 看字选罗马音
    ROMAJI_TO_CHAR   // 看罗马音选字
}

enum class QuizTarget {
    HIRAGANA,
    KATAKANA,
    MIXED
}

data class QuizState(
    val currentQuestion: Question? = null,
    val score: Int = 0,
    val totalCount: Int = 0,
    val maxQuestions: Int = 10, // -1 表示无限模式
    val isFinished: Boolean = false,
    val selectedRows: Set<String> = kanaRows.keys, // 默认选中所有行
    val targetType: QuizTarget = QuizTarget.HIRAGANA,
    val playAudioBefore: Boolean = true, // 题目自动发音
    val playAudioAfter: Boolean = true   // 点击选项发音
)

data class Question(
    val correctKana: Kana,
    val options: List<Kana>,
    val mode: QuizMode,
    val displayAsKatakana: Boolean = false
)

class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizState())
    val uiState: StateFlow<QuizState> = _uiState.asStateFlow()

    private var currentMode: QuizMode = QuizMode.CHAR_TO_ROMAJI

    init {
        startNewQuiz(QuizMode.CHAR_TO_ROMAJI, 10, kanaRows.keys, QuizTarget.HIRAGANA)
    }

    /**
     * 开始一轮新的测试
     */
    fun startNewQuiz(
        mode: QuizMode, 
        maxCount: Int = _uiState.value.maxQuestions,
        selectedRows: Set<String> = _uiState.value.selectedRows,
        targetType: QuizTarget = _uiState.value.targetType,
        playAudioBefore: Boolean = _uiState.value.playAudioBefore,
        playAudioAfter: Boolean = _uiState.value.playAudioAfter
    ) {
        currentMode = mode
        _uiState.update { 
            it.copy(
                score = 0, 
                totalCount = 0, 
                maxQuestions = maxCount,
                selectedRows = selectedRows,
                targetType = targetType,
                playAudioBefore = playAudioBefore,
                playAudioAfter = playAudioAfter,
                isFinished = false,
                currentQuestion = null
            ) 
        }
        generateQuestion()
    }

    /**
     * 生成新题目
     */
    fun generateQuestion() {
        val activeKana = if (_uiState.value.selectedRows.isEmpty()) {
            allKanaData
        } else {
            _uiState.value.selectedRows.flatMap { kanaRows[it] ?: emptyList() }
        }.filter { it.char.isNotEmpty() }

        if (activeKana.size < 4) return

        // 定义同音/易混淆组
        val conflictSets = listOf(
            setOf("ず", "づ"),
            setOf("じ", "ぢ"),
            setOf("お", "を")
        )

        // 1. 随机选择正确答案
        val correct = activeKana.random()

        // 2. 准备干扰项池
        // 排除正确答案本身，以及与其同音的冲突项
        val forbiddenChars = mutableSetOf(correct.char)
        conflictSets.find { correct.char in it }?.let { 
            forbiddenChars.addAll(it) 
        }

        val basePool = if (activeKana.size >= 10) activeKana else allKanaData
        val shuffledPool = basePool.filter { it.char !in forbiddenChars }.shuffled()

        // 3. 抽取 3 个互不冲突的干扰项
        val distractors = mutableListOf<Kana>()
        for (kana in shuffledPool) {
            if (distractors.size >= 3) break
            
            // 检查当前候选假名是否与已选中的干扰项冲突
            val hasConflict = conflictSets.any { set ->
                kana.char in set && distractors.any { it.char in set }
            }
            
            if (!hasConflict) {
                distractors.add(kana)
            }
        }

        // 4. 决定当前题目显示为平假名还是片假名
        val displayAsKatakana = when (_uiState.value.targetType) {
            QuizTarget.HIRAGANA -> false
            QuizTarget.KATAKANA -> true
            QuizTarget.MIXED -> (0..1).random() == 1
        }

        // 5. 更新当前题目状态
        _uiState.update { 
            it.copy(
                currentQuestion = Question(
                    correctKana = correct,
                    options = (distractors + correct).shuffled(),
                    mode = currentMode,
                    displayAsKatakana = displayAsKatakana
                )
            )
        }
    }

    /**
     * 提交答案
     */
    fun submitAnswer(selectedKana: Kana) {
        val currentState = _uiState.value
        if (currentState.isFinished || currentState.currentQuestion == null) return

        val isCorrect = selectedKana == currentState.currentQuestion.correctKana
        
        _uiState.update { 
            val newTotalCount = it.totalCount + 1
            val shouldFinish = if (it.maxQuestions == -1) false else newTotalCount >= it.maxQuestions
            
            it.copy(
                score = if (isCorrect) it.score + 1 else it.score,
                totalCount = newTotalCount,
                isFinished = shouldFinish
            )
        }

        // 如果测试没结束，自动生成下一题
        if (!_uiState.value.isFinished) {
            generateQuestion()
        }
    }
}
