package com.example.rssreader.loader

interface ArtilceLoader {
    suspend fun loadMore()
}