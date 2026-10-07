package io.github.aixtin.collider

data class Spark(
    val id: Long,
    val text: String,
    val domains: List<String>,
    val createdAt: Long,
    var mark: String = "",
    var died: Boolean = false,
    var daily: String? = null
) {
    val markLabel: String
        get() = when (mark) {
            "junk" -> "垃圾"
            "meh" -> "有点意思"
            "love" -> "心动"
            else -> "未标记"
        }

    val domainPair: String
        get() = if (domains.size >= 2) "${domains[0]} × ${domains[1]}" else domains.joinToString(" × ")
}
