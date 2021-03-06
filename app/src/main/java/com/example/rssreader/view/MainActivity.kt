package com.example.rssreader.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.R
import com.example.rssreader.adapter.ArticleAdapter
import com.example.rssreader.loader.ArtilceLoader
import com.example.rssreader.producer.ArticleProducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

class MainActivity : AppCompatActivity(), ArtilceLoader {

    private lateinit var articlesRecycler: RecyclerView
    private lateinit var viewAdapter: ArticleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val dfDispatcher = newSingleThreadContext(name = "ServiceCall")
    private val uiDispatcher = Dispatchers.Main


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ArticleAdapter()
        articlesRecycler = findViewById<RecyclerView>(R.id.articles).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        
        CoroutineScope(uiDispatcher).launch {
            loadMore()
        }
    }

    override suspend fun loadMore() {
        val producer = ArticleProducer.producer
        if (!producer.isClosedForReceive) {
            val articles = producer.receive()
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
            viewAdapter.add(articles)
        }
    }
}
