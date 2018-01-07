package com.park9eon.blog

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.ClientParametersAuthentication
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonObjectParser
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerRequestInitializer
import com.google.api.services.blogger.model.Post
import com.park9eon.blog.dao.*
import org.junit.Test
import java.io.File
import java.util.*


/**
 * Initial version by: park9eon
 * Initial version created on: 05/01/2018
 */
class BlogTest {

    private val blogUrl = "https://dev9eon.blogspot.kr/"
    private fun getConfig(): YamlData = Markdown.load("config.md").yamlData

    @Test
    fun `get blog posts`() {
        val apiKey = getConfig().getOne("api_key")!!
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
        val clientId = getConfig().getOne("client_id")!!
        val clientSecret = getConfig().getOne("client_secret")!!
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
        val clientId = getConfig().getOne("api_key")!!
        val clientSecret = getConfig().getOne("api_key")!!
        authenticatedBlog(clientId, clientSecret, blogUrl) {
            val post = Post()
            post.blog = Post.Blog().apply { id = blog.id }
            post.title = "Hello, World!"
            post.content = "Hello, World!"

            val posts = blogger.posts()
                    .insert(blog.id, post)
                    .setIsDraft(true)
                    .execute()
            println(posts.title)
        }
    }

}