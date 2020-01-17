package com.example.rssreader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.R
import com.example.rssreader.loader.ArtilceLoader
import com.example.rssreader.models.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    private val articles: MutableList<Article> = mutableListOf()
    private lateinit var loader: ArtilceLoader
    private var loading = false


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
        if(!loading && position >= articles.size -2 ){
            CoroutineScope(Dispatchers.Main).launch{
                loader.loadMore()
                loading = false
            }
        }
    }

    fun add(articles: List<Article>){
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }

    fun add(article: Article){
        this.articles.add(article)
        notifyDataSetChanged()
    }

    fun clear(){
        this.articles.clear()
        notifyDataSetChanged()
    }


    class ViewHolder(
        val layout: ConstraintLayout,
        val feed: TextView,
        val title: TextView,
        val summary: TextView
    ) : RecyclerView.ViewHolder(layout)
}