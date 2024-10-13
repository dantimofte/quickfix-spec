package ac.quant.quickfixspec.common

import com.intellij.psi.xml.XmlTag
import lombok.Getter

@Getter
class FixField(tag: XmlTag) {
    val name: String = parseName(tag)
    val number: String = parseNumber(tag)
    val fixType: String = parseFixType(tag)
    val values: Map<String, String> = parseValues(tag)


    private fun parseName(tag: XmlTag): String {
        return getAttributeValue(tag, "name")
    }

    private fun parseNumber(tag: XmlTag): String {
        return getAttributeValue(tag, "number")
    }

    private fun parseFixType(tag: XmlTag): String {
        return getAttributeValue(tag, "type")
    }

    private fun parseValues(tag: XmlTag): Map<String, String> {
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
