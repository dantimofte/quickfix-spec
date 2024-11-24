package ac.quant.quickfixspec.documentation


import ac.quant.quickfixspec.common.spec.XmlUtils.findDefinition
import com.intellij.model.Pointer
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.xml.XmlTag

@Suppress("UnstableApiUsage")
class QuickfixComponentDocumentationTarget(
    private val tagName: String,
    private val parentTagName: String,
    private val rootTag: XmlTag
) : DocumentationTarget {


    override fun createPointer(): Pointer<out DocumentationTarget> {
        return Pointer.hardPointer(this)
    }

    override fun computePresentation(): TargetPresentation {
        return TargetPresentation.builder(tagName).presentation()
    }

    override fun computeDocumentationHint(): String {
        val docHint = computeDocumentation().toString()
        return docHint
    }

    override fun computeDocumentation(): DocumentationResult {
        val componentDetails = getTagDetails(tagName)
        return DocumentationResult.documentation(componentDetails)
    }

    private fun getTagDetails(tagName: String): String {
        val tag: XmlTag? = findDefinition(tagName, parentTagName, rootTag)
        tag ?: return ""
        return getDisplayText(tag)
    }

    private fun getDisplayText(tag: XmlTag): String {
        val text = tag.children.joinToString("") { it.text }
        val xmlEscapedText = xmlEscaped(text)
        val indentationRemoved = indentationRemoved(xmlEscapedText)
        val displayText = "<html><body><h1>${tag.name}</h1><pre>$indentationRemoved</pre></body></html>"
        return displayText
    }

    private fun xmlEscaped(xmlString: String): String {
        return xmlString.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    }

    private fun indentationRemoved(xmlString: String): String {
        return xmlString.replace("\n        ", "\n")
    }
}