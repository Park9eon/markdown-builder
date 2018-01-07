package com.park9eon.blog

import com.park9eon.blog.dao.Markdown
import com.park9eon.blog.dao.authenticatedBlog
import com.park9eon.blog.dao.post

/**
 * Initial version by: park9eon
 * Initial version created on: 07/01/2018
 */
fun main(vararg args: String) {

    val path = args[0]
    val clientId = args[1]
    val clientSecret = args[2]
    val blogUrl = args[3]

    authenticatedBlog(clientId, clientSecret, blogUrl) {
        val post = post(Markdown.load(path)).insert()
        println(post.title)
    }
}