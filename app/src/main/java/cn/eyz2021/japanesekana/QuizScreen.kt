package cn.eyz2021.japanesekana

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: QuizViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val ttsHelper = remember { TTSHelper(context) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        ttsHelper.initTTS { }
        onDispose { ttsHelper.release() }
    }

    var selectedOption by remember { mutableStateOf<Kana?>(null) }

    if (showSettingsDialog) {
        QuizSettingsDialog(
            currentMax = uiState.maxQuestions,
            currentSelectedRows = uiState.selectedRows,
            currentTargetType = uiState.targetType,
            currentPlayBefore = uiState.playAudioBefore,
            currentPlayAfter = uiState.playAudioAfter,
            onDismiss = { showSettingsDialog = false },
            onConfirm = { newMax, newRows, newTarget, playBefore, playAfter ->
                showSettingsDialog = false
                viewModel.startNewQuiz(
                    viewModel.uiState.value.currentQuestion?.mode ?: QuizMode.CHAR_TO_ROMAJI,
                    newMax,
                    newRows,
                    newTarget,
                    playBefore,
                    playAfter
                )
            }
        )
    }

    if (uiState.isFinished) {
        QuizResultScreen(
            score = uiState.score,
            total = uiState.totalCount,
            onRestart = { viewModel.startNewQuiz(viewModel.uiState.value.currentQuestion?.mode ?: QuizMode.CHAR_TO_ROMAJI) },
            modifier = modifier
        )
        return
    }

    val question = uiState.currentQuestion

    LaunchedEffect(question) {
        if (question != null && !uiState.isFinished) {
            // 听音模式强制发音，其他模式根据开关决定
            if (question.mode == QuizMode.AUDIO_TO_CHAR || uiState.playAudioBefore) {
                ttsHelper.speakKana(question.correctKana.romaji)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    QuizMode.entries.forEachIndexed { index, mode ->
                        val label = when (mode) {
                            QuizMode.CHAR_TO_ROMAJI -> "看字"
                            QuizMode.ROMAJI_TO_CHAR -> "看音"
                            QuizMode.AUDIO_TO_CHAR -> "听音"
                        }
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = QuizMode.entries.size),
                            onClick = { viewModel.startNewQuiz(mode) },
                            selected = question?.mode == mode,
                            label = { Text(label, fontSize = 10.sp) }
                        )
                    }
                }
                
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "设置")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.maxQuestions != -1) {
                val progress = if (uiState.totalCount == 0) 0f else uiState.totalCount.toFloat() / uiState.maxQuestions
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    strokeCap = StrokeCap.Round
                )
            } else {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(2.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            val modeLabel = if (uiState.maxQuestions == -1) "无限" else "目标:${uiState.maxQuestions}"
            val typeLabel = when(uiState.targetType) {
                QuizTarget.HIRAGANA -> "平假名"
                QuizTarget.KATAKANA -> "片假名"
                QuizTarget.MIXED -> "混合"
            }
            Text(
                text = "得分: ${uiState.score}/${uiState.totalCount} ($typeLabel | $modeLabel)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (question != null) {
                val displayText = if (question.displayAsKatakana) question.correctKana.katakana else question.correctKana.char
                when (question.mode) {
                    QuizMode.AUDIO_TO_CHAR -> {
                        FilledIconButton(
                            onClick = { ttsHelper.speakKana(question.correctKana.romaji) },
                            modifier = Modifier.size(100.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(50.dp))
                        }
                    }
                    QuizMode.CHAR_TO_ROMAJI -> {
                        Text(text = displayText, fontSize = 100.sp, fontWeight = FontWeight.Bold)
                    }
                    QuizMode.ROMAJI_TO_CHAR -> {
                        Text(text = question.correctKana.romaji, fontSize = 80.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (question != null) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
            ) {
                items(question.options) { kana ->
                    val isCorrect = kana == question.correctKana
                    val isSelected = kana == selectedOption
                    val buttonColor by animateColorAsState(
                        targetValue = when {
                            selectedOption == null -> MaterialTheme.colorScheme.secondaryContainer
                            isCorrect -> Color(0xFF4CAF50)
                            isSelected -> Color(0xFFF44336)
                            else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        }, label = ""
                    )

                    val optionText = if (question.mode == QuizMode.CHAR_TO_ROMAJI) {
                        kana.romaji
                    } else {
                        if (question.displayAsKatakana) kana.katakana else kana.char
                    }

                    Button(
                        onClick = {
                            if (selectedOption == null) {
                                if (uiState.playAudioAfter) {
                                    ttsHelper.speakKana(kana.romaji)
                                }
                                selectedOption = kana
                                scope.launch {
                                    delay(600)
                                    viewModel.submitAnswer(kana)
                                    selectedOption = null
                                }
                            }
                        },
                        modifier = Modifier.height(80.dp).fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor, 
                            contentColor = if (selectedOption != null && (isCorrect || isSelected)) Color.White else MaterialTheme.colorScheme.onSecondaryContainer),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = optionText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuizSettingsDialog(
    currentMax: Int, 
    currentSelectedRows: Set<String>, 
    currentTargetType: QuizTarget,
    currentPlayBefore: Boolean,
    currentPlayAfter: Boolean,
    onDismiss: () -> Unit, 
    onConfirm: (Int, Set<String>, QuizTarget, Boolean, Boolean) -> Unit
) {
    var isInfinite by remember { mutableStateOf(currentMax == -1) }
    var sliderValue by remember { mutableFloatStateOf(if (currentMax == -1) 10f else currentMax.toFloat()) }
    var selectedRows by remember { mutableStateOf(currentSelectedRows) }
    var targetType by remember { mutableStateOf(currentTargetType) }
    var playBefore by remember { mutableStateOf(currentPlayBefore) }
    var playAfter by remember { mutableStateOf(currentPlayAfter) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("测试设置") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("假名类型", style = MaterialTheme.typography.titleSmall)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    QuizTarget.entries.forEachIndexed { index, target ->
                        val label = when(target) {
                            QuizTarget.HIRAGANA -> "平假"
                            QuizTarget.KATAKANA -> "片假"
                            QuizTarget.MIXED -> "混合"
                        }
                        SegmentedButton(
                            selected = targetType == target,
                            onClick = { targetType = target },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = QuizTarget.entries.size),
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("发音设置", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("出现题目时发音", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = playBefore, onCheckedChange = { playBefore = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("点击选项时发音", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = playAfter, onCheckedChange = { playAfter = it })
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("题目数量", style = MaterialTheme.typography.titleSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(selected = !isInfinite, onClick = { isInfinite = false }, label = { Text("有限") })
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(selected = isInfinite, onClick = { isInfinite = true }, label = { Text("无限") })
                }
                if (!isInfinite) {
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 5f..100f,
                        steps = 18
                    )
                    Text("${sliderValue.roundToInt()} 题", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("测试行范围", style = MaterialTheme.typography.titleSmall)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedRows.size == kanaRows.size,
                        onCheckedChange = { all ->
                            selectedRows = if (all) kanaRows.keys.toSet() else emptySet()
                        }
                    )
                    Text("全选", style = MaterialTheme.typography.bodyMedium)
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    kanaRows.keys.forEach { rowName ->
                        FilterChip(
                            selected = selectedRows.contains(rowName),
                            onClick = {
                                selectedRows = if (selectedRows.contains(rowName)) {
                                    selectedRows - rowName
                                } else {
                                    selectedRows + rowName
                                }
                            },
                            label = { Text(rowName, fontSize = 12.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(if (isInfinite) -1 else sliderValue.roundToInt(), selectedRows, targetType, playBefore, playAfter) },
                enabled = selectedRows.isNotEmpty()
            ) {
                Text("开始新测试")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
fun QuizResultScreen(score: Int, total: Int, onRestart: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("测试结束", style = MaterialTheme.typography.headlineMedium)
        Text("得分: $score / $total", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRestart) { Text("重新开始") }
    }
}
