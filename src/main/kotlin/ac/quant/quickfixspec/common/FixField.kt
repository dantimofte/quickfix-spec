package ac.quant.quickfixspec.common

import com.intellij.psi.xml.XmlTag

class FixField {
    val name: String
    val number: String
    val fixType: String
    val values: Map<String, String>

    constructor(tag: XmlTag) {
        this.name = getName(tag)
        this.number = getNumber(tag)
        this.fixType = getFixType(tag)
        this.values = getValues(tag)
    }

    private fun getName(tag: XmlTag): String {
        return getAttributeValue(tag, "name")
    }

    private fun getNumber(tag: XmlTag): String {
        return getAttributeValue(tag, "number")
    }

    private fun getFixType(tag: XmlTag): String {
        return getAttributeValue(tag, "type")
    }

    private fun getValues(tag: XmlTag): Map<String, String> {
        val valueTags = tag.findSubTags("value")
        val valueTagsDict = mutableMapOf<String, String>()
        for (valueTag in valueTags) {
            try {
                val enumValue = getAttributeValue(valueTag, "enum")
                val description = getAttributeValue(valueTag, "description")
                valueTagsDict[enumValue] = description
            } catch (e: Exception) {
                println("Error processing value tags. Error: ${e.message}")
            }
        }
        return valueTagsDict
    }

    private fun getAttributeValue(tag: XmlTag, attributeName: String): String {
        return tag.getAttribute(attributeName)?.value ?: ""
    }
}
