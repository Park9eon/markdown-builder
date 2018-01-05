package com.park9eon.blog

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.blogger.Blogger
import com.google.api.services.blogger.BloggerRequestInitializer
import com.google.api.services.blogger.model.Blog
import com.google.api.services.blogger.model.Post
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.junit.Test


/**
 * Initial version by: park9eon
 * Initial version created on: 05/01/2018
 */
class BlogWriteTest {

    fun getApiKey(): String? {
        val yamlVisitor = YamlFrontMatterVisitor()

        val extensions = listOf(
                AutolinkExtension.create(),
                YamlFrontMatterExtension.create()
        )
        val parser: Parser = Parser.builder()
                .extensions(extensions)
                .build()
        val document: Node = parser.parse(javaClass.classLoader.getResource("config.md")
                .readText())
        val renderer: HtmlRenderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build()

        document.accept(yamlVisitor)

        println(renderer.render(document))

        val data: YamlData = yamlVisitor.data

        return data.getOne("api_key")
    }

    fun getBlogger(): Blogger {
        println(getApiKey())
        return Blogger.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory()) {
            println(it)
        }
                .setApplicationName("Test")
                .setGoogleClientRequestInitializer(BloggerRequestInitializer(getApiKey()))
                .build()
    }

    @Test
    fun `get blog posts`() {
        val blogger = getBlogger()
        val blog = blogger.blogs().getByUrl("https://dev9eon.blogspot.kr/").execute()
        val posts = blogger.posts().list(blog.id)
                .setMaxResults(blog.posts.totalItems.toLong()).execute()
        posts.items.forEach {
            println(it.title)
        }
    }

    @Test
    fun `write blog post`() {
        val blogger = getBlogger()
        val blog = blogger.blogs()
                .getByUrl("https://dev9eon.blogspot.kr/")
                .execute()

        val content = Post()
        content.blog = Post.Blog().apply { id = blog.id }
        content.title = "123"
        content.content = "Hello, World!"

        val posts = blogger.posts()
                .insert(blog.id, content)
                .execute()
    }
}