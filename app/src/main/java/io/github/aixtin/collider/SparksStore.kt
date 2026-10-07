package io.github.aixtin.collider

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 点子池存取：与云端 sparks.json 同构，存在 App 私有 files 目录，无任何网络上报 */
object SparksStore {

    private const val DAY = 24 * 3600 * 1000L
    private const val UNMARKED_LIFE = 7 * DAY
    private const val JUNK_LIFE = 30 * DAY
    private const val OTHER_LIFE = 90 * DAY

    private fun file(ctx: Context): File = File(ctx.filesDir, "sparks.json")

    fun load(ctx: Context): MutableList<Spark> {
        val f = file(ctx)
        if (!f.exists()) return mutableListOf()
        return runCatching {
            val arr = JSONArray(f.readText())
            val list = mutableListOf<Spark>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val domains = mutableListOf<String>()
                val da = o.optJSONArray("domains") ?: JSONArray()
                for (j in 0 until da.length()) domains.add(da.getString(j))
                list.add(
                    Spark(
                        id = o.getLong("id"),
                        text = o.getString("text"),
                        domains = domains,
                        createdAt = o.getLong("createdAt"),
                        mark = o.optString("mark", ""),
                        died = o.optBoolean("died", false),
                        daily = if (o.has("daily") && !o.isNull("daily")) o.getString("daily") else null
                    )
                )
            }
            list
        }.getOrElse { mutableListOf() }
    }

    fun save(ctx: Context, pool: List<Spark>) {
        val arr = JSONArray()
        for (s in pool) {
            val o = JSONObject()
            o.put("id", s.id)
            o.put("text", s.text)
            val da = JSONArray()
            s.domains.forEach { da.put(it) }
            o.put("domains", da)
            o.put("createdAt", s.createdAt)
            o.put("mark", s.mark)
            o.put("died", s.died)
            if (s.daily != null) o.put("daily", s.daily)
            arr.put(o)
        }
        file(ctx).writeText(arr.toString(2))
    }

    fun collide(ctx: Context): Spark {
        val (text, domains) = ColliderEngine.generate()
        val now = System.currentTimeMillis()
        val spark = Spark(id = now, text = text, domains = domains, createdAt = now)
        val pool = load(ctx)
        pool.add(0, spark)
        save(ctx, pool)
        return spark
    }

    /** 每日一撞：当天已撞过则只返回已有火花 */
    fun daily(ctx: Context): Pair<Spark?, Spark> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val pool = load(ctx)
        pool.firstOrNull { it.daily == today }?.let { return null to it }
        val spark = collide(ctx)
        val updated = load(ctx)
        updated[0] = spark.copy(daily = today)
        save(ctx, updated)
        return spark to spark
    }

    fun mark(ctx: Context, id: Long, kind: String): Spark? {
        val pool = load(ctx)
        val target = pool.firstOrNull { it.id == id } ?: return null
        target.mark = kind
        save(ctx, pool)
        return target
    }

    /** 生命周期：7 天未标记自动变垃圾；标记后超期转死亡。返回调整后的池子 */
    fun tickLifecycle(ctx: Context): MutableList<Spark> {
        val now = System.currentTimeMillis()
        val pool = load(ctx)
        var changed = false
        for (it in pool) {
            if (it.died) continue
            if (it.mark.isEmpty() && now - it.createdAt > UNMARKED_LIFE) {
                it.mark = "junk"
                changed = true
            }
            val life = if (it.mark == "junk") JUNK_LIFE else OTHER_LIFE
            if (it.mark.isNotEmpty() && now - it.createdAt > life) {
                it.died = true
                changed = true
            }
        }
        if (changed) save(ctx, pool)
        return pool
    }

    /** 未死亡火花的剩余天数；未标记按 7 天寿命计 */
    fun daysLeft(spark: Spark, now: Long): Int {
        if (spark.died) return 0
        val life = if (spark.mark.isEmpty()) UNMARKED_LIFE
        else if (spark.mark == "junk") JUNK_LIFE else OTHER_LIFE
        val left = life - (now - spark.createdAt)
        return if (left <= 0) 0 else ((left + DAY - 1) / DAY).toInt()
    }
}
