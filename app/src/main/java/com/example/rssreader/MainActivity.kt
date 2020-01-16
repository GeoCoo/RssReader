package com.example.rssreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.example.rssreader.models.Article
import com.example.rssreader.models.Feed
import kotlinx.coroutines.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {

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

        asyncLoadNews()


    }

    private fun asyncLoadNews() = CoroutineScope(dispatcher).async {
        val requests = mutableListOf<Deferred<List<Article>>>()

//        val newsCount = findViewById<TextView>(R.id.newsCount)
        val bar = findViewById<ProgressBar>(R.id.progressBar)
//        val failsCount = findViewById<TextView>(R.id.failsCount)

        feeds.mapTo(requests) {
            asyncFetchArticles(it, dfDispatcher)
        }
        requests.forEach {
            it.join()
        }

        val headlines = requests.filter { !it.isCancelled }.flatMap { it.getCompleted() }
        val failed = requests.filter { it.isCancelled }.size
        val obtained = requests.size - failed

        val articles = requests.filter { !it.isCancelled }.flatMap { it.getCompleted() }

        CoroutineScope(uiDispatcher).launch {
//            newsCount.text = "Found ${headlines.size} News " + "in ${requests.size} feeds"
//            Log.i("NEWS", "Found ${headlines.size} News \" + \"in $obtained feeds")
//            if (failed > 0) {
//                failsCount.text = "Failed to fetch $failed feeds"
//
//            }
//            if (headlines.isNotEmpty()) {
//                bar.visibility = View.GONE
//            }
            //TODO: SHOW ARTICLES
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
                    val summary = it.getElementsByTagName("description").item(0).textContent
                    Article(feed.name, title, summary)
                }
        }


}
