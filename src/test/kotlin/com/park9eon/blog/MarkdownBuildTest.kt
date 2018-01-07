package com.park9eon.blog

import com.park9eon.blog.dao.Markdown
import com.park9eon.blog.dao.YamlData
import com.park9eon.blog.dao.getAll
import com.park9eon.blog.dao.getOne
import org.junit.Test

/**
 * Initial version by: park9eon
 * Initial version created on: 01/01/2018
 */
class MarkdownBuildTest {

    @Test
    fun `build test`() {
        println(Markdown.load("posts/2018-01-01-test.md").html)
    }

    @Test
    fun `build test with extensions`() {

        val data: YamlData = Markdown.load("posts/2018-01-01-test.md").yamlData

        val title: String? = data.getOne("title")
        val date: String? = data.getOne("date")
        val tags: List<String?>? = data.getAll("tags")

        println(title)
        println(date)
        println(tags)
        // <p>This is <em>Sparta</em></p>
    }

    @Test
    fun `get single markdown from resource`() {
        println(Markdown.load("posts/2018-01-01-test.md").html)
    }
}