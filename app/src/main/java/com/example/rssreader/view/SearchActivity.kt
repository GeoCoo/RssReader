package com.example.rssreader.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.R
import com.example.rssreader.adapter.ArticleAdapter
import com.example.rssreader.models.Article
import com.example.rssreader.search.Searcher
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {


    private lateinit var articlesRecycler: RecyclerView
    private lateinit var viewAdapter: ArticleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val searcher = Searcher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ArticleAdapter()
        articlesRecycler.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        findViewById<Button>(R.id.searchButton).setOnClickListener {
            viewAdapter.clear()
            CoroutineScope(Dispatchers.Main).launch {
                search()
            }
        }
    }


    private suspend fun search() {
        val query = searchText.text.toString()
        val channel = searcher.search(query)
        while (!channel.isClosedForReceive){
            val articles = channel.receive()
            viewAdapter.add(articles)
        }

    }


}
