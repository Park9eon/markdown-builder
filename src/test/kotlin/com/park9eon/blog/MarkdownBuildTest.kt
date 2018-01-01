package com.park9eon.blog

import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.junit.Test
import java.io.File


/**
 * Initial version by: park9eon
 * Initial version created on: 01/01/2018
 */
class MarkdownBuildTest {

    @Test
    fun `build test`() {
        val parser: Parser = Parser.builder()
                .build()
        val document: Node = parser.parse(javaClass.classLoader.getResource("posts/2018-01-01-test.md").readText())
        val renderer: HtmlRenderer = HtmlRenderer.builder()
                .build()
        println(renderer.render(document))
        // <p>This is <em>Sparta</em></p>
    }

    @Test
    fun `build test with extensions`() {
        val yamlVisitor = YamlFrontMatterVisitor()

        val extensions = listOf(
                AutolinkExtension.create(),
                YamlFrontMatterExtension.create()
        )
        val parser: Parser = Parser.builder()
                .extensions(extensions)
                .build()
        val document: Node = parser.parse(javaClass.classLoader.getResource("posts/2018-01-01-test.md")
                .readText())
        val renderer: HtmlRenderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build()

        document.accept(yamlVisitor)

        println(renderer.render(document))
        yamlVisitor.data.forEach { key, value ->
            println("$key - ${value} : ${value.size}")
        }
        // <p>This is <em>Sparta</em></p>
    }

    @Test
    fun `get single markdown from resource`() {
        println(javaClass.classLoader.getResource("posts/2018-01-01-test.md").readText()) // 없을 경우 에러
        /*
        ---
        title: test
        date: 2018-01-01
        tags: [test, java]
        ---

        Hello, World!
        */
    }

    @Test
    fun `get single markdown file from resource`() {
        val file = File(javaClass.classLoader.getResource("posts/2018-01-01-test.md").toURI())
        println(file.nameWithoutExtension)
        println(file.extension)
        println(file.path)
        println(file.readText()) // 없을 경우 에러!
        /*
        ---
        title: test
        date: 2018-01-01
        tags: [test, java]
        ---

        Hello, World!
        */
    }

    @Test
    fun `get all markdown files from resource`() {
        val posts = File(javaClass.classLoader.getResource("posts").toURI())
        posts.listFiles().forEach { file ->
            println(file.nameWithoutExtension)
            println(file.extension)
            println(file.readText()) // 없을 경우 에러!
        }
    }

    private fun String.toString(): String {
        return "`${this}`"
    }
}