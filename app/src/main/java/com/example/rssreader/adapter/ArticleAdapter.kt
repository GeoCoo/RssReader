package com.example.rssreader.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.R
import com.example.rssreader.models.Article
import kotlinx.android.synthetic.main.activity_main.view.*
import org.w3c.dom.Text

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    val articles: MutableList<Article> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.articles,
            parent,
            false
        ) as ConstraintLayout
        val feed = layout.findViewById<TextView>(R.id.feed)
        val title = layout.findViewById<TextView>(R.id.title)
        val summary = layout.findViewById<TextView>(R.id.summary)

        return ViewHolder(layout, feed, title, summary)

    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val singleArticle = articles[position]
        holder.feed.text = singleArticle.feed
        holder.title.text = singleArticle.title
        holder.summary.text = singleArticle.summary
    }

    fun add(articles: List<Article>){
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }


    class ViewHolder(
        val layout: ConstraintLayout,
        val feed: TextView,
        val title: TextView,
        val summary: TextView
    ) : RecyclerView.ViewHolder(layout)
}