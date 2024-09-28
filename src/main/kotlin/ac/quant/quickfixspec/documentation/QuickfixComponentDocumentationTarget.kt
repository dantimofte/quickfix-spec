package ac.quant.quickfixspec.documentation


import ac.quant.quickfixspec.common.PsiUtils.findComponent
import com.intellij.model.Pointer
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.xml.XmlTag
import com.intellij.openapi.diagnostic.Logger

@Suppress("UnstableApiUsage")
class QuickfixComponentDocumentationTarget(
    private val componentName: String,
    private val rootTag: XmlTag?
) : DocumentationTarget {


    override fun createPointer(): Pointer<out DocumentationTarget> {
        return Pointer.hardPointer(this)
    }

    override fun computePresentation(): TargetPresentation {
        return TargetPresentation.builder(componentName).presentation()
    }

    override fun computeDocumentationHint(): String {
        val docHint = computeDocumentation().toString()
        return docHint
    }

    override fun computeDocumentation(): DocumentationResult {
        val componentDetails = getComponentDetails(componentName)
        return DocumentationResult.documentation(componentDetails)
    }

    private fun getComponentDetails(componentName: String): String {
        val component: XmlTag? = findComponent(rootTag, componentName)
        if (component == null) {
            println("Component with name $componentName not found")
            return ""
        }

        return getDisplayText(component)
    }

    private fun getDisplayText(component: XmlTag): String {
        val text = component.children.joinToString("") { it.text }
        val xmlEscapedText = xmlEscaped(text)
        val indentationRemoved = indentationRemoved(xmlEscapedText)
        val displayText = "<html><body><h1>${component.name}</h1><pre>$indentationRemoved</pre></body></html>"
        return displayText
    }

    private fun xmlEscaped(xmlString: String): String {
        return xmlString.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    }

    private fun indentationRemoved(xmlString: String): String {
        return xmlString.replace("\n        ", "\n")
    }
}