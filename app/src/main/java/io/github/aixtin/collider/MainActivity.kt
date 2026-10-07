package io.github.aixtin.collider

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var resultText: TextView
    private lateinit var domainText: TextView
    private lateinit var poolCountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.resultText)
        domainText = findViewById(R.id.domainText)
        poolCountText = findViewById(R.id.poolCountText)

        findViewById<Button>(R.id.btnCollide).setOnClickListener { collideOnce() }
        findViewById<Button>(R.id.btnDaily).setOnClickListener { dailyOnce() }
        findViewById<Button>(R.id.btnPool).setOnClickListener {
            startActivity(Intent(this, SparksActivity::class.java))
        }

        refreshPoolCount()
    }

    override fun onResume() {
        super.onResume()
        refreshPoolCount()
    }

    private fun refreshPoolCount() {
        val count = SparksStore.tickLifecycle(this).count { !it.died }
        poolCountText.text = getString(R.string.pool_count, count)
    }

    private fun collideOnce() {
        val spark = SparksStore.collide(this)
        showSpark(spark)
        refreshPoolCount()
    }

    private fun dailyOnce() {
        val (existing, spark) = SparksStore.daily(this)
        showSpark(spark)
        if (existing != null) {
            resultText.text = getString(R.string.daily_already, spark.text)
        }
        refreshPoolCount()
    }

    private fun showSpark(spark: Spark) {
        resultText.text = spark.text
        domainText.text = spark.domainPair
    }
}
