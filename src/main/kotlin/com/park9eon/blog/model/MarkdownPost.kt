package com.park9eon.blog.model


/**
 * Initial version by: park9eon
 * Initial version created on: 01/01/2018
 */
data class MarkdownPost(
        val filename: String,
        val path: String,
        val origin: String,
        val title: String,
        val content: String,
        val date: String,
        val tags: List<String>
)