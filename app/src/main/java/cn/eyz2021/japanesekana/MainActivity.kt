package cn.eyz2021.japanesekana

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.eyz2021.japanesekana.ui.theme.JapaneseKanaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JapaneseKanaTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var currentTab by remember { mutableStateOf(0) }
    val quizViewModel: QuizViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.AutoMirrored.Filled.LibraryBooks, contentDescription = "学习") },
                    label = { Text("学习") }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = "测试") },
                    label = { Text("测试") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                0 -> KanaTable()
                1 -> QuizScreen(viewModel = quizViewModel)
            }
        }
    }
}

@Composable
fun KanaTable(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val ttsHelper = remember { TTSHelper(context) }
    var isKatakanaFirst by remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        ttsHelper.initTTS { }
        onDispose { ttsHelper.release() }
    }

    Column(modifier = modifier.padding(8.dp).verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("五十音图", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { isKatakanaFirst = !isKatakanaFirst }) {
                Text(if (isKatakanaFirst) "切换为平假名优先" else "切换为片假名优先")
            }
        }
        KanaGrid(gojuonData, ttsHelper, isKatakanaFirst = isKatakanaFirst)
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("浊音 / 半浊音", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
        KanaGrid(dakuonData, ttsHelper, isKatakanaFirst = isKatakanaFirst)
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("拗音", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
        KanaGrid(yoonData, ttsHelper, columns = 3, isKatakanaFirst = isKatakanaFirst)
    }
}

@Composable
fun KanaGrid(data: List<Kana>, ttsHelper: TTSHelper, columns: Int = 5, isKatakanaFirst: Boolean) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.heightIn(max = 2000.dp), // 增加最大高度以容纳更多内容
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false // 由外部 ScrollState 控制
    ) {
        items(data) { kana ->
            KanaCard(kana, isKatakanaFirst = isKatakanaFirst, onClick = { ttsHelper.speakKana(kana.romaji) })
        }
    }
}

@Composable
fun KanaCard(kana: Kana, isKatakanaFirst: Boolean, onClick: () -> Unit) {
    if (kana.char.isEmpty()) {
        Box(modifier = Modifier.size(60.dp))
        return
    }

    val primaryText = if (isKatakanaFirst) kana.katakana else kana.char
    val secondaryText = if (isKatakanaFirst) kana.char else kana.katakana

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = primaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = secondaryText,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = kana.romaji,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KanaTablePreview() {
    JapaneseKanaTheme {
        KanaTable()
    }
}
