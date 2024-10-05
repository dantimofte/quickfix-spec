package ac.quant.quickfixspec.documentation

import ac.quant.quickfixspec.common.PsiUtils.NAME_ATTRIBUTE
import ac.quant.quickfixspec.common.PsiUtils.TAGS_WITH_DEFINITION
import ac.quant.quickfixspec.common.PsiUtils.DEFINITION_GROUP_NAME
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

        if (NAME_ATTRIBUTE != attribute.name) {
            return emptyList()
        }

        val tag = attribute.parent as? XmlTag ?: return emptyList()

        if (tag.name !in TAGS_WITH_DEFINITION) {
            return emptyList()
        }

        val tagParent = tag.parent as? XmlTag ?: return emptyList()


        //  skip definition when tagParent.name is "components" or "fields"

        if (tagParent.name in DEFINITION_GROUP_NAME.values) {
            return emptyList()
        }

        val attrNameValue = attribute.value ?: return emptyList()
        return listOf(QuickfixComponentDocumentationTarget(attrNameValue, tag.name, rootTag))

    }


}