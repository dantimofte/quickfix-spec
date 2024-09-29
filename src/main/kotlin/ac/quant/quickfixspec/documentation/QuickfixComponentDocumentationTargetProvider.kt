package ac.quant.quickfixspec.documentation

import ac.quant.quickfixspec.common.PsiUtils.getRootTag
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlAttributeValue

class QuickfixComponentDocumentationTargetProvider : DocumentationTargetProvider {

    var rootTag: XmlTag? = null

    override fun documentationTargets(file: PsiFile, offset: Int): List<DocumentationTarget> {

        val element = file.findElementAt(offset) ?: return emptyList()
        val attributeValue = element.parent as? XmlAttributeValue ?: return emptyList()
        val attribute = attributeValue.parent as? XmlAttribute ?: return emptyList()

        if (rootTag == null) {
            rootTag = getRootTag(attribute)
        }

        if ("name" != attribute.name) {
            return emptyList()
        }

        val tag = attribute.parent as? XmlTag ?: return emptyList()

        if ("component" != tag.name) {
            return emptyList()
        }

        val tagParent = tag.parent as? XmlTag ?: return emptyList()

        if ("components" == tagParent.name) {
            return emptyList()
        }

        val componentName = attribute.value ?: return emptyList()
        return listOf(QuickfixComponentDocumentationTarget(componentName, rootTag))

    }


}