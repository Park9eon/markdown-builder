package com.park9eon.blog.service

import com.park9eon.blog.model.Markdown
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.Node
import org.commonmark.node.Visitor
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.File

/**
 * Initial version by: park9eon
 * Initial version created on: 06/01/2018
 */
class MarkdownService {
    private val yamlVisitor = YamlFrontMatterVisitor()
    private val visitors = mutableListOf<Visitor>(yamlVisitor)
    private val extensions = listOf<Extension>(
            AutolinkExtension.create(),
            YamlFrontMatterExtension.create()
    )
    private val parser: Parser = Parser.builder()
            .apply {
                this.extensions(extensions)
            }
            .build()
    private val renderer: HtmlRenderer = HtmlRenderer.builder()
            .apply {
                this.extensions(extensions)
            }
            .build()

    fun loadFromFile(file: File): Markdown {
        val document: Node = parser.parse(file.readText())
        visitors.forEach {
            document.accept(it)
        }
        val html = renderer.render(document)
        return Markdown(html, yamlVisitor.data)
    }

    fun loadFromResource(path: String, context: Any = Markdown::class): Markdown {
        val file = File(context.javaClass.classLoader.getResource(path).toURI())
        return loadFromFile(file)
    }

    fun load(path: String): Markdown {
        return loadFromFile(File(path))
    }

    fun addVisitor(visitor: Visitor) {
        this.visitors.add(visitor)
    }
}
