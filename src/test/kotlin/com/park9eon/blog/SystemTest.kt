package com.park9eon.blog

import com.park9eon.blog.dao.YamlData
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
class SystemTest {

    @Test
    fun `get config`() {
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


    }
}