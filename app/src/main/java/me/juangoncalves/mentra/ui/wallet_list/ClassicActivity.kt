package me.juangoncalves.mentra.ui.wallet_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.juangoncalves.mentra.R

class ClassicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val rv: RecyclerView = findViewById(R.id.recyclerView)
        val viewManager = LinearLayoutManager(this)

        rv.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = MyAdapter((0..1000).map { it.toString() }.toTypedArray())
        }
    }
}

class MyAdapter(
    private val data: Array<String>
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_text_view, parent, false) as TextView

        return MyViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = data[position]
    }

    override fun getItemCount() = data.size
}