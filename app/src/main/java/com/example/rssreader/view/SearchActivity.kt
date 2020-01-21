package com.example.rssreader.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.R
import com.example.rssreader.adapter.ArticleAdapter
import com.example.rssreader.models.Article
import com.example.rssreader.search.ResultsCounter
import com.example.rssreader.search.Searcher
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class SearchActivity : AppCompatActivity() {
    private val searcher = Searcher()

    private lateinit var articles: RecyclerView
    private lateinit var viewAdapter: ArticleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        viewManager = LinearLayoutManager(this)
        viewAdapter = ArticleAdapter()
        articles = findViewById<RecyclerView>(R.id.articles).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        findViewById<Button>(R.id.searchButton).setOnClickListener {
            viewAdapter.clear()
            CoroutineScope(Dispatchers.IO).launch {
                ResultsCounter.reset()
                search()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            updateCounter()
        }

    }

    private suspend fun search() {
        val query = findViewById<EditText>(R.id.searchText)
            .text.toString()

        val channel = searcher.search(query)

        while (!channel.isClosedForReceive) {
            val article = channel.receive()

            CoroutineScope(Dispatchers.Main).launch {
                viewAdapter.add(article)
            }
        }
    }


    private suspend fun updateCounter() {
        val notifications = ResultsCounter.getNotificationChannel()
        val results = findViewById<TextView>(R.id.results)
        while (!notifications.isClosedForReceive) {
            val newAmount = notifications.receive()

            CoroutineScope(Dispatchers.Main).launch {
                results.text = "Results: $newAmount"
            }
        }
    }
}