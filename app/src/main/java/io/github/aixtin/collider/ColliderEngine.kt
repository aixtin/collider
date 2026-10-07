package io.github.aixtin.collider

import kotlin.random.Random

/** 碰撞引擎：领域词库 + 概念词库 + 句式模板（移植自云端 collider.py） */
object ColliderEngine {

    val DOMAINS = listOf(
        "Android", "本地AI", "记忆系统", "隐私", "内容创作", "硬件", "园艺", "香水", "麻将",
        "折纸", "昆虫", "天文", "烹饪", "音乐", "物流", "生物进化", "游戏机制", "金融",
        "建筑", "交通", "教育", "医疗", "天气", "社交", "手工艺", "密码学", "语言学",
        "考古", "体育", "零售"
    )

    val CONCEPTS = mapOf(
        "生物进化" to listOf("自然选择", "变异池", "趋同演化", "诚实信号", "生态位", "灭绝", "共生", "适应辐射"),
        "香水" to listOf("前调中调后调", "普鲁斯特效应", "信息素", "定香剂", "嗅觉记忆", "调香金字塔"),
        "教育" to listOf("变异-选择-遗传", "情境记忆", "遗忘曲线", "脚手架", "教学相长"),
        "记忆系统" to listOf("索引", "衰减", "回放", "巩固", "关联唤醒"),
        "本地AI" to listOf("离线推理", "隐私边界", "个人知识库", "工具调用"),
        "Android" to listOf("生命周期", "广播", "意图框架", "桌面小部件", "通知渠道"),
        "游戏机制" to listOf("随机掉落", "成就系统", "每日任务", "双刃平衡", "肉鸽循环"),
        "音乐" to listOf("节奏", "和声进行", "采样", "复调", "即兴"),
        "天文" to listOf("红移", "潮汐锁定", "轨道共振", "暗物质"),
        "昆虫" to listOf("信息素", "变态发育", "拟态", "社会性分工"),
        "麻将" to listOf("听牌", "杠上开花", "流局", "牌河记忆"),
        "折纸" to listOf("折叠约束", "一纸成型", "皱褶展开", "模数化"),
        "烹饪" to listOf("美拉德反应", "发酵", "火候梯度", "分子重组"),
        "物流" to listOf("最后一公里", "枢纽辐射", "冷链", "逆向物流"),
        "金融" to listOf("复利", "对冲", "沉没成本", "期权"),
        "建筑" to listOf("模数", "被动式节能", "动线", "废墟美学"),
        "交通" to listOf("潮汐车道", "绿波带", "换乘枢纽", "最后一公里"),
        "医疗" to listOf("诊断树", "依从性", "安慰剂效应", "预防医学"),
        "天气" to listOf("蝴蝶效应", "微气候", "云分类", "降雨概率"),
        "社交" to listOf("弱连接", "六度分隔", "社会资本", "群体极化"),
        "手工艺" to listOf("误差即风格", "包浆", "慢工出细活", "材料的脾气"),
        "密码学" to listOf("随机数", "零知识证明", "同态加密", "密钥分发"),
        "语言学" to listOf("音位变体", "语法化", "语言濒危", "皮钦语"),
        "考古" to listOf("地层学", "类型学", "埋藏学", "器物组合"),
        "体育" to listOf("高原训练", "竞技状态周期", "主场优势", "肌肉记忆"),
        "硬件" to listOf("传感器融合", "功耗预算", "裸金属", "过时淘汰"),
        "隐私" to listOf("最小化", "差分隐私", "数据主权", "遗忘权"),
        "内容创作" to listOf("钩子", "节奏", "信息密度", "灵感碎片池"),
        "园艺" to listOf("物候", "根际", "修剪策略", "共生种植"),
        "零售" to listOf("动线", "坪效", "会员心智", "冲动购买")
    )

    private val TEMPLATES = listOf<Triple<String, String, String>>(
        Triple("{a}", "{cA}", "把「{a}」的「{cA}」装到「{b}」上，会得到什么？"),
        Triple("{b}", "{cB}", "用「{b}」的视角重新看一遍「{a}」：{cB}会不会是{a}的隐藏钥匙？"),
        Triple("{a}", "{cB}", "如果「{a}」是一台机器，{cB}就是它缺的那个零件。"),
        Triple("{b}", "{cA}", "给「{b}」引入「{a}」的{cA}规则，规则会杀死它还是救活它？"),
        Triple("{a}", "{cB}", "「{a}」×「{b}」：{cA} × {cB}，碰撞产物长什么样？")
    )

    private fun pick(list: List<String>): String = list[Random.nextInt(list.size)]

    private fun pickDomain(exclude: Set<String>): String {
        val candidates = DOMAINS.filter { it !in exclude }
        return pick(if (candidates.isEmpty()) DOMAINS else candidates)
    }

    fun generate(): Pair<String, List<String>> {
        val a = pickDomain(emptySet())
        val b = pickDomain(setOf(a))
        val cA = pick(CONCEPTS[a] ?: emptyList())
        val cB = pick(CONCEPTS[b] ?: emptyList())
        val (slotA, slotB, raw) = TEMPLATES[Random.nextInt(TEMPLATES.size)]
        val text = raw
            .replace("{a}", a)
            .replace("{b}", b)
            .replace("{cA}", cA)
            .replace("{cB}", cB)
        return text to listOf(a, b)
    }
}
