package cn.eyz2021.japanesekana

enum class KanaType { HIRAGANA, KATAKANA }

data class Kana(
    val char: String,
    val romaji: String,
    val type: KanaType,
    val katakana: String = "", // 用于平假名显示对应的片假名
    val audioRes: Int = 0
)

/**
 * 按行定义的假名数据，方便按行选择进行测试
 */
val kanaRows = mapOf(
    "あ行" to listOf(
        Kana("あ", "a", KanaType.HIRAGANA, "ア"), Kana("い", "i", KanaType.HIRAGANA, "イ"),
        Kana("う", "u", KanaType.HIRAGANA, "ウ"), Kana("え", "e", KanaType.HIRAGANA, "エ"),
        Kana("お", "o", KanaType.HIRAGANA, "オ")
    ),
    "か行" to listOf(
        Kana("か", "ka", KanaType.HIRAGANA, "カ"), Kana("き", "ki", KanaType.HIRAGANA, "キ"),
        Kana("く", "ku", KanaType.HIRAGANA, "ク"), Kana("け", "ke", KanaType.HIRAGANA, "ケ"),
        Kana("こ", "ko", KanaType.HIRAGANA, "コ")
    ),
    "さ行" to listOf(
        Kana("さ", "sa", KanaType.HIRAGANA, "サ"), Kana("し", "shi", KanaType.HIRAGANA, "シ"),
        Kana("す", "su", KanaType.HIRAGANA, "ス"), Kana("せ", "se", KanaType.HIRAGANA, "セ"),
        Kana("そ", "so", KanaType.HIRAGANA, "ソ")
    ),
    "た行" to listOf(
        Kana("た", "ta", KanaType.HIRAGANA, "タ"), Kana("ち", "chi", KanaType.HIRAGANA, "チ"),
        Kana("つ", "tsu", KanaType.HIRAGANA, "ツ"), Kana("て", "te", KanaType.HIRAGANA, "テ"),
        Kana("と", "to", KanaType.HIRAGANA, "ト")
    ),
    "な行" to listOf(
        Kana("な", "na", KanaType.HIRAGANA, "ナ"), Kana("に", "ni", KanaType.HIRAGANA, "ニ"),
        Kana("ぬ", "nu", KanaType.HIRAGANA, "ヌ"), Kana("ね", "ne", KanaType.HIRAGANA, "ネ"),
        Kana("の", "no", KanaType.HIRAGANA, "ノ")
    ),
    "は行" to listOf(
        Kana("は", "ha", KanaType.HIRAGANA, "ハ"), Kana("ひ", "hi", KanaType.HIRAGANA, "ヒ"),
        Kana("ふ", "fu", KanaType.HIRAGANA, "フ"), Kana("へ", "he", KanaType.HIRAGANA, "ヘ"),
        Kana("ほ", "ho", KanaType.HIRAGANA, "ホ")
    ),
    "ま行" to listOf(
        Kana("ま", "ma", KanaType.HIRAGANA, "マ"), Kana("み", "mi", KanaType.HIRAGANA, "ミ"),
        Kana("む", "mu", KanaType.HIRAGANA, "ム"), Kana("め", "me", KanaType.HIRAGANA, "メ"),
        Kana("も", "mo", KanaType.HIRAGANA, "モ")
    ),
    "や行" to listOf(
        Kana("や", "ya", KanaType.HIRAGANA, "ヤ"), Kana("ゆ", "yu", KanaType.HIRAGANA, "ユ"), Kana("よ", "yo", KanaType.HIRAGANA, "ヨ")
    ),
    "ら行" to listOf(
        Kana("ら", "ra", KanaType.HIRAGANA, "ラ"), Kana("り", "ri", KanaType.HIRAGANA, "リ"),
        Kana("る", "ru", KanaType.HIRAGANA, "ル"), Kana("れ", "re", KanaType.HIRAGANA, "レ"),
        Kana("ろ", "ro", KanaType.HIRAGANA, "ロ")
    ),
    "わ/ん" to listOf(
        Kana("わ", "wa", KanaType.HIRAGANA, "ワ"), Kana("を", "wo", KanaType.HIRAGANA, "ヲ"),
        Kana("ん", "n", KanaType.HIRAGANA, "ン")
    ),
    "浊音/半浊音" to listOf(
        Kana("が", "ga", KanaType.HIRAGANA, "ガ"), Kana("ぎ", "gi", KanaType.HIRAGANA, "ギ"),
        Kana("ぐ", "gu", KanaType.HIRAGANA, "グ"), Kana("げ", "ge", KanaType.HIRAGANA, "ゲ"),
        Kana("ご", "go", KanaType.HIRAGANA, "ゴ"),
        Kana("ざ", "za", KanaType.HIRAGANA, "ザ"), Kana("じ", "ji", KanaType.HIRAGANA, "ジ"),
        Kana("ず", "zu", KanaType.HIRAGANA, "ズ"), Kana("ぜ", "ze", KanaType.HIRAGANA, "ゼ"),
        Kana("ぞ", "zo", KanaType.HIRAGANA, "ゾ"),
        Kana("だ", "da", KanaType.HIRAGANA, "ダ"), Kana("ぢ", "di", KanaType.HIRAGANA, "ヂ"),
        Kana("づ", "du", KanaType.HIRAGANA, "ヅ"), Kana("で", "de", KanaType.HIRAGANA, "デ"),
        Kana("ど", "do", KanaType.HIRAGANA, "ド"),
        Kana("ば", "ba", KanaType.HIRAGANA, "バ"), Kana("び", "bi", KanaType.HIRAGANA, "ビ"),
        Kana("ぶ", "bu", KanaType.HIRAGANA, "ブ"), Kana("べ", "be", KanaType.HIRAGANA, "ベ"),
        Kana("ぼ", "bo", KanaType.HIRAGANA, "ボ"),
        Kana("ぱ", "pa", KanaType.HIRAGANA, "パ"), Kana("ぴ", "pi", KanaType.HIRAGANA, "ピ"),
        Kana("ぷ", "pu", KanaType.HIRAGANA, "プ"), Kana("ぺ", "pe", KanaType.HIRAGANA, "ペ"),
        Kana("ぽ", "po", KanaType.HIRAGANA, "ポ")
    ),
    "拗音" to listOf(
        Kana("きゃ", "kya", KanaType.HIRAGANA, "キャ"), Kana("きゅ", "kyu", KanaType.HIRAGANA, "キュ"), Kana("きょ", "kyo", KanaType.HIRAGANA, "キョ"),
        Kana("しゃ", "sha", KanaType.HIRAGANA, "シャ"), Kana("しゅ", "shu", KanaType.HIRAGANA, "シュ"), Kana("しょ", "sho", KanaType.HIRAGANA, "ショ"),
        Kana("ちゃ", "cha", KanaType.HIRAGANA, "チャ"), Kana("ちゅ", "chu", KanaType.HIRAGANA, "チュ"), Kana("ちょ", "cho", KanaType.HIRAGANA, "チョ"),
        Kana("にゃ", "nya", KanaType.HIRAGANA, "ニャ"), Kana("にゅ", "nyu", KanaType.HIRAGANA, "ニュ"), Kana("にょ", "nyo", KanaType.HIRAGANA, "ニョ"),
        Kana("ひゃ", "hya", KanaType.HIRAGANA, "ヒャ"), Kana("ひゅ", "hyu", KanaType.HIRAGANA, "ヒュ"), Kana("ひょ", "hyo", KanaType.HIRAGANA, "ヒョ"),
        Kana("みゃ", "mya", KanaType.HIRAGANA, "ミャ"), Kana("みゅ", "myu", KanaType.HIRAGANA, "ミュ"), Kana("みょ", "myo", KanaType.HIRAGANA, "ミョ"),
        Kana("りゃ", "rya", KanaType.HIRAGANA, "リャ"), Kana("りゅ", "ryu", KanaType.HIRAGANA, "リュ"), Kana("りょ", "ryo", KanaType.HIRAGANA, "リョ"),
        Kana("ぎゃ", "gya", KanaType.HIRAGANA, "ギャ"), Kana("ぎゅ", "gyu", KanaType.HIRAGANA, "ギュ"), Kana("ぎょ", "gyo", KanaType.HIRAGANA, "ギョ"),
        Kana("じゃ", "ja", KanaType.HIRAGANA, "ジャ"), Kana("じゅ", "ju", KanaType.HIRAGANA, "ジュ"), Kana("じょ", "jo", KanaType.HIRAGANA, "ジョ"),
        Kana("びゃ", "bya", KanaType.HIRAGANA, "ビャ"), Kana("びゅ", "byu", KanaType.HIRAGANA, "ビュ"), Kana("びょ", "byo", KanaType.HIRAGANA, "ビョ"),
        Kana("ぴゃ", "pya", KanaType.HIRAGANA, "ピャ"), Kana("ぴゅ", "pyu", KanaType.HIRAGANA, "ピュ"), Kana("ぴょ", "pyo", KanaType.HIRAGANA, "ピョ")
    )
)

/**
 * 为兼容以前代码保留的列表
 */
val gojuonData = listOf(
    "あ行", "か行", "さ行", "た行", "な行", "は行", "ま行", "や行", "ら行", "わ/ん"
).flatMap { groupName ->
    val list = kanaRows[groupName]!!
    // 补齐 5 列用于 Grid 显示（ya, wa 行特殊处理）
    when (groupName) {
        "や行" -> listOf(list[0], Kana("", "", KanaType.HIRAGANA), list[1], Kana("", "", KanaType.HIRAGANA), list[2])
        "わ/ん" -> listOf(list[0], Kana("", "", KanaType.HIRAGANA), Kana("", "", KanaType.HIRAGANA), Kana("", "", KanaType.HIRAGANA), list[1], list[2])
        else -> list
    }
}

val dakuonData = kanaRows["浊音/半浊音"]!!
val yoonData = kanaRows["拗音"]!!

val allKanaData = kanaRows.values.flatten().filter { it.char.isNotEmpty() }
