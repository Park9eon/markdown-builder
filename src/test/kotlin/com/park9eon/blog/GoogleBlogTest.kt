package com.park9eon.blog

import com.google.api.client.util.DateTime
import com.google.api.services.blogger.model.Post
import com.park9eon.blog.service.GoogleBlogService
import org.junit.Test
import java.util.*


/**
 * Initial version by: park9eon
 * Initial version created on: 08/01/2018
 */
class GoogleBlogTest {

    private val googleBlogService by lazy { GoogleBlogService() }

    @Test
    fun `Get blog by url`() {
        val blog = googleBlogService.findByUrl("https://dev9eon.blogspot.kr")
        println("Blog name is ${blog.name}")
        println("Blog selfLinke is ${blog.selfLink}")
        println("Blog totalItems is ${blog.posts.totalItems}")
    }

    @Test
    fun `Get all blog posts`() {
        val blog = googleBlogService.findByUrl("https://dev9eon.blogspot.kr")
        val posts = googleBlogService.findAllPosts(blog)
        posts.forEach { post ->
            println("${blog.name} - ${post.author.displayName} : ${post.title} / [${post.published.toStringRfc3339()}]")
        }
    }

    @Test
    fun `Get one blog post by id`() {
        val blog = googleBlogService.findByUrl("https://dev9eon.blogspot.kr")
        val posts = googleBlogService.findAllPosts(blog)
        val post = googleBlogService.findPostById(blog, posts.first().id)
        println("${blog.name} - ${post.author.displayName} : ${post.title} / [${post.published.toStringRfc3339()}]")
    }

    @Test
    fun `insert blog and update and delete`() {

        var post = Post().apply {
            title = "Hello, World"
            content = "<p>Hello, World!</p>"
            labels = listOf("test", "java")
            published = DateTime(Date())
        }
        val blog = googleBlogService.findByUrl("https://dev9eon.blogspot.kr")
        println("Blog name : ${blog.name}")
        println("Post title : ${post.title}")
        post = googleBlogService.insert(blog, post)
        println("Post title : ${post.title} & id : ${post.id} / updated : ${post.updated.toStringRfc3339()}")
        println("Post title change")
        post.title = "New title"
        post.updated = DateTime(Date())
        post = googleBlogService.update(blog, post)
        println("Post title : ${post.title} & id : ${post.id} / updated : ${post.updated.toStringRfc3339()}")
        googleBlogService.delete(blog, post.id)
        println("Post deleted!")
    }
}