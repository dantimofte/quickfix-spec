package ac.quant.quickfixspec.documentation

import ac.quant.quickfixspec.common.FixDataDictionaryService
import ac.quant.quickfixspec.common.PsiUtils.NAME_ATTRIBUTE
import ac.quant.quickfixspec.common.PsiUtils.TAGS_WITH_DEFINITION
import ac.quant.quickfixspec.common.PsiUtils.DEFINITION_GROUP_NAME
import ac.quant.quickfixspec.common.PsiUtils.getRootTag
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlAttributeValue


class QuickfixComponentDocumentationTargetProvider : DocumentationTargetProvider {


    var rootTag: XmlTag? = null
    var fixDataDictionaryService: FixDataDictionaryService? = null


    override fun documentationTargets(file: PsiFile, offset: Int): List<DocumentationTarget> {

        val element = file.findElementAt(offset) ?: return emptyList()

        return if (file.name.endsWith(".xml")) {
            getTagDefinition(element)
        } else {
            getFixMessageDetails(file, element)
        }

    }

    fun getTagDefinition(element: PsiElement):  List<DocumentationTarget> {
        val attributeValue = element.parent as? XmlAttributeValue ?: return emptyList()
        val attribute = attributeValue.parent as? XmlAttribute ?: return emptyList()

        if (rootTag == null) {
            rootTag = getRootTag(attribute)
        }

        if (NAME_ATTRIBUTE != attribute.name) {
            return emptyList()
        }

        val tag = attribute.parent  ?: return emptyList()

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

    // search for strings that are actually fix messages and return them with the actual name of the tag number based on the xml file
    fun getFixMessageDetails(file: PsiFile, element: PsiElement): List<DocumentationTarget> {
        fixDataDictionaryService = file.project.getService(FixDataDictionaryService::class.java)

        val fixMessages = mutableListOf<DocumentationTarget>()
        val fixMessageRegex = Regex("8=FIX.*?10=[0-9]{3}(\\\\u0001)?(.)?")
        val matches = fixMessageRegex.findAll(element.text)
        for (match in matches) {
            val fixMessage= match.value
            // if group 0 is empty then try group 1
            val fixDelimiter = if (match.groupValues[1].isNotEmpty()) match.groupValues[1] else match.groupValues[2]

            fixMessages.add(FixMessageDocumentationTarget(fixMessage, fixDelimiter, fixDataDictionaryService!!))
        }

        return fixMessages
    }

}