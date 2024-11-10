package ac.quant.quickfixspec.documentation

import ac.quant.quickfixspec.common.spec.FixDataDictionaryService
import ac.quant.quickfixspec.common.PsiUtils.NAME_ATTRIBUTE
import ac.quant.quickfixspec.common.PsiUtils.TAGS_WITH_DEFINITION
import ac.quant.quickfixspec.common.PsiUtils.DEFINITION_GROUP_NAME
import ac.quant.quickfixspec.common.PsiUtils.getRootTag
import ac.quant.quickfixspec.common.parsed.ParsedFixMessage
import com.intellij.platform.backend.documentation.DocumentationTarget
import com.intellij.platform.backend.documentation.DocumentationTargetProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlAttributeValue
import com.google.common.annotations.VisibleForTesting

class QuickfixComponentDocumentationTargetProvider : DocumentationTargetProvider {


    private var rootTag: XmlTag? = null
    private var fixDataDictionaryService: FixDataDictionaryService? = null


    override fun documentationTargets(file: PsiFile, offset: Int): List<DocumentationTarget> {

        val element = file.findElementAt(offset) ?: return emptyList()

        return if (file.name.endsWith(".xml")) {
            getTagDefinition(element)
        } else {
            getFixMessageDetails(file, offset)
        }
    }

    private fun getTagDefinition(element: PsiElement):  List<DocumentationTarget> {
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

        if (tagParent.name in DEFINITION_GROUP_NAME.values) {
            return emptyList()
        }

        val attrNameValue = attribute.value ?: return emptyList()
        return listOf(QuickfixComponentDocumentationTarget(attrNameValue, tag.name, rootTag))
    }

    @VisibleForTesting
    internal fun getFixMessageDetails(file: PsiFile, offset: Int): List<DocumentationTarget> {
        fixDataDictionaryService = file.project.getService(FixDataDictionaryService::class.java)

        val fixMessages = mutableListOf<DocumentationTarget>()

        val regexOptionsSet = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.COMMENTS, RegexOption.IGNORE_CASE)
        val multiLineFixMessageRegex = Regex("8=FIX.*?10=[0-9]{3}(\\\\u0001)?(.)?", regexOptionsSet)

        val matches = multiLineFixMessageRegex.findAll(file.text)
        for (match in matches) {
            val fixMessage = match.value
            val fixDelimiter = match.groupValues[1].ifEmpty { match.groupValues[2] }
            if (match.range.contains(offset)) {
                val parsedFixMessage = ParsedFixMessage(fixMessage, fixDelimiter, fixDataDictionaryService!!)
                val fixMessageDocumentationTarget = FixMessageDocumentationTarget(parsedFixMessage)
                fixMessages.add(fixMessageDocumentationTarget)
            }
        }

        return fixMessages
    }
}
