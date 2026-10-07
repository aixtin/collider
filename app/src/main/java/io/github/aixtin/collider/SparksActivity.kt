package io.github.aixtin.collider

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SparksActivity : AppCompatActivity() {

    private val adapter = SparksAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sparks)

        val list = findViewById<RecyclerView>(R.id.sparksList)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        reload()
    }

    private fun reload() {
        val pool = SparksStore.tickLifecycle(this)
        adapter.submit(pool)
    }

    private fun showMarkDialog(spark: Spark) {
        val now = System.currentTimeMillis()
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_mark)
        dialog.findViewById<TextView>(R.id.dlgTitle).text = spark.domainPair
        dialog.findViewById<TextView>(R.id.dlgText).text = spark.text
        dialog.findViewById<TextView>(R.id.dlgStatus).text = getString(
            R.string.status_line,
            spark.markLabel,
            SparksStore.daysLeft(spark, now)
        )
        dialog.findViewById<View>(R.id.btnJunk).setOnClickListener {
            SparksStore.mark(this, spark.id, "junk")
            dialog.dismiss()
            reload()
        }
        dialog.findViewById<View>(R.id.btnMeh).setOnClickListener {
            SparksStore.mark(this, spark.id, "meh")
            dialog.dismiss()
            reload()
        }
        dialog.findViewById<View>(R.id.btnLove).setOnClickListener {
            SparksStore.mark(this, spark.id, "love")
            dialog.dismiss()
            reload()
        }
        dialog.findViewById<View>(R.id.btnCancel).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    inner class SparksAdapter : RecyclerView.Adapter<SparksAdapter.Holder>() {

        private val items = mutableListOf<Spark>()

        fun submit(list: List<Spark>) {
            items.clear()
            items.addAll(list)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_spark, parent, false)
            return Holder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val spark = items[position]
            val now = System.currentTimeMillis()
            holder.text.text = spark.text
            holder.meta.text = spark.domainPair
            holder.status.text = if (spark.died) {
                getString(R.string.status_died)
            } else {
                getString(R.string.status_line, spark.markLabel, SparksStore.daysLeft(spark, now))
            }
            holder.itemView.setOnClickListener { showMarkDialog(spark) }
        }

        inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
            val text: TextView = view.findViewById(R.id.sparkText)
            val meta: TextView = view.findViewById(R.id.sparkMeta)
            val status: TextView = view.findViewById(R.id.sparkStatus)
        }
    }
}
