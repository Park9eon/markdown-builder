package com.park9eon.blog

import com.park9eon.blog.model.YamlData
import com.park9eon.blog.model.getAll
import com.park9eon.blog.model.getOne
import com.park9eon.blog.service.MarkdownService
import org.junit.Test

/**
 * Initial version by: park9eon
 * Initial version created on: 01/01/2018
 */
class MarkdownBuildTest {

    private val markdownService by lazy { MarkdownService() }

    @Test
    fun `build test`() {
        println(markdownService.loadFromResource("posts/2018-01-01-test.md"))
    }

    @Test
    fun `build test with extensions`() {

        val data: YamlData = markdownService.loadFromResource("posts/2018-01-01-test.md").yamlData

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
        println(markdownService.loadFromResource("posts/2018-01-01-test.md").html)
    }
}