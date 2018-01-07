package com.park9eon.blog.dao

import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.File


/**
 * Initial version by: park9eon
 * Initial version created on: 06/01/2018
 */
class Markdown private constructor(val html: String,
                                   val yamlData: YamlData,
                                   val path: String,
                                   val filename: String) {
    companion object {
        fun load(path: String, context: Any = Markdown::class): Markdown {
            val file = File(context.javaClass.classLoader.getResource(path).toURI())
            val yamlVisitor = YamlFrontMatterVisitor()
            val extensions = listOf(
                    AutolinkExtension.create(),
                    YamlFrontMatterExtension.create()
            )
            val parser: Parser = Parser.builder()
                    .let {
                        it.extensions(extensions)
                        it.build()
                    }

            val document: Node = parser.parse(file.readText())
                    .apply {
                        this.accept(yamlVisitor)
                    }

            val renderer: HtmlRenderer = HtmlRenderer.builder()
                    .let {
                        it.extensions(extensions)
                        it.build()
                    }

            val html = renderer.render(document)
            return Markdown(html, yamlVisitor.data, file.path, file.name)
        }
    }
}
