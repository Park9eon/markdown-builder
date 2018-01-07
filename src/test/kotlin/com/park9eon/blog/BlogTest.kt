package com.park9eon.blog

import com.park9eon.blog.dao.*
import org.junit.Test

/**
 * Initial version by: park9eon
 * Initial version created on: 05/01/2018
 */
class BlogTest {

    private val blogUrl = "https://dev9eon.blogspot.kr/"
    private val apiKey
        get() = getConfig().getOne("api_key")!!
    private val clientId
        get() = getConfig().getOne("client_id")!!
    private val clientSecret
        get() = getConfig().getOne("client_secret")!!

    private fun getConfig(): YamlData = Markdown.loadFromResource("config.md").yamlData

    @Test
    fun `get blog posts`() {
        blog(apiKey, blogUrl) {
            println(blog.id)
            val posts = findAllPosts()
            posts.items.forEach {
                println("Title : ${it.title}, status : ${it.status}, kind : ${it.kind}, preview : ${it.content.slice(0..50)}")
            }
        }
    }

    @Test
    fun `get authentication blog posts`() {
        authenticatedBlog(clientId, clientSecret, blogUrl) {
            println(blog.id)
            val posts = findAllPosts()
            posts.items.forEach {
                println("Title : ${it.title}, status : ${it.status}, kind : ${it.kind}, preview : ${it.content.slice(0..50)}")
            }
        }
    }

    @Test
    fun `write blog draft post`() {
        authenticatedBlog(clientId, clientSecret, blogUrl) {
            val post = post {
                title = "Hello, World!"
                content = "Hello, World!"
                status = "draft"
            }.insert()
            println(post.title)
        }
    }

    @Test
    fun `write blog draft post by markdown`() {
        authenticatedBlog(clientId, clientSecret, blogUrl) {
            val markdown = Markdown.loadFromResource("posts/2018-01-01-test.md")
            val post = post(markdown)
                    .insert()
            println(post.title)
            post.images
        }

    }

}