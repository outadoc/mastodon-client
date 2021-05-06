package fr.outadoc.compose.htmltext

import fr.outadoc.compose.htmltext.model.FlatLinkNode
import fr.outadoc.compose.htmltext.model.FlatNode
import fr.outadoc.compose.htmltext.model.FlatParagraph
import fr.outadoc.compose.htmltext.model.FlatTextNode
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

actual class HtmlParser {

    actual fun parse(html: String): List<FlatNode> {
        val document = Jsoup.parse(html)
        return flattenElementsToList(document.body())
    }

    private fun flattenElementsToList(element: Element): List<FlatNode> {
        return element.childNodes()
            .mapNotNull { node: Node ->
                when (node) {
                    is TextNode ->
                        listOf(FlatTextNode(node.text()))
                    is Element -> {
                        when (node.tagName()) {
                            "a" -> listOf(FlatLinkNode(href = node.attr("href"), text = node.text()))
                            "p" -> listOf(FlatParagraph(children = flattenElementsToList(node)))
                            "br" -> listOf(FlatTextNode(text = "\n"))
                            else -> flattenElementsToList(node)
                        }
                    }
                    else -> null
                }
            }.flatten()
    }
}