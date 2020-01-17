package com.example.rssreader.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.CorrectionInfo
import android.widget.Adapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.R
import com.example.rssreader.adapter.ArticleAdapter
import com.example.rssreader.models.Article
import com.example.rssreader.models.Feed
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {

    private lateinit var articlesRecycler: RecyclerView
    private lateinit var viewAdapter: ArticleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val dfDispatcher = newSingleThreadContext(name = "ServiceCall")
    private val dispatcher = newFixedThreadPoolContext(2, "IO")
    private val uiDispatcher = Dispatchers.Main
    private val factory = DocumentBuilderFactory.newInstance()
    private val feeds = listOf(
        Feed("npr", "https://www.npr.org/rss/rss.php?id=1001"),
        Feed("cnn", "http://rss.cnn.com/rss/cnn_topstories.rss"),
        Feed("fox", "http://feeds.foxnews.com/foxnews/politics?format=xml"),
        Feed("inv", "htt:myNewsFeed")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ArticleAdapter()
        articlesRecycler = findViewById<RecyclerView>(R.id.articles).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        asyncLoadNews()


    }


    private fun asyncLoadNews() = CoroutineScope(dispatcher).async {
        val requests = mutableListOf<Deferred<List<Article>>>()

        //  val newsCount = findViewById<TextView>(R.id.newsCount)
        val bar = findViewById<ProgressBar>(R.id.progressBar)
        //  val failsCount = findViewById<TextView>(R.id.failsCount)

        feeds.mapTo(requests) {
            asyncFetchArticles(it, dfDispatcher)
        }
        requests.forEach {
            it.join()
        }

//        val headlines = requests.filter { !it.isCancelled }.flatMap { it.getCompleted() }
//        val failed = requests.filter { it.isCancelled }.size
//        val obtained = requests.size - failed

        val articles = requests.filter { !it.isCancelled }.flatMap { it.getCompleted() }

        CoroutineScope(uiDispatcher).launch {
            //            newsCount.text = "Found ${headlines.size} News " + "in ${requests.size} feeds"
//            Log.i("NEWS", "Found ${headlines.size} News \" + \"in $obtained feeds")
//            if (failed > 0) {
//                failsCount.text = "Failed to fetch $failed feeds"
//
////            }
//            if (headlines.isNotEmpty()) {
//                bar.visibility = View.GONE
//            }

            bar.visibility = View.GONE
            viewAdapter.add(articles)

        }
    }

//    private fun asyncFetchHeadlines(feed: Feed,dispatcher: CoroutineDispatcher) = CoroutineScope(dispatcher).async {
//        val builder = factory.newDocumentBuilder()
//        val xml = builder.parse(feed.url)
//        val news = xml.getElementsByTagName("channel").item(0)
//
//        (0 until news.childNodes.length)
//            .map { news.childNodes.item(it) }
//            .filter { Node.ELEMENT_NODE == it.nodeType }
//            .map { it as Element }
//            .filter { "item" == it.tagName }
//            .map {
//                it.getElementsByTagName("title").item(0).textContent
//            }
//    }

    private fun asyncFetchArticles(feed: Feed, dispatcher: CoroutineDispatcher) =
        CoroutineScope(dispatcher).async {
            delay(2000)
            val builder = factory.newDocumentBuilder()
            val xml = builder.parse(feed.url)
            val news = xml.getElementsByTagName("channel").item(0)


            (0 until news.childNodes.length)
                .map { news.childNodes.item(it) }
                .filter { Node.ELEMENT_NODE == it.nodeType }
                .map { it as Element }
                .filter { "item" == it.tagName }
                .map {
                    val title = it.getElementsByTagName("title").item(0).textContent
                    var summary = it.getElementsByTagName("description").item(0).textContent
                    if(!summary.startsWith("<div>") && summary.contains("<div>")){
                        summary = summary.substring(0, summary.indexOf("<div>"))
                    }
                    Article(feed.name, title, summary)
                }
        }


}
