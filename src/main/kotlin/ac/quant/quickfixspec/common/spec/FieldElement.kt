package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag
import lombok.Getter

@Getter
class FieldElement: IElement {
    override val name: String
    override val type: ElementType
    override val number: String
    override val elementTag: XmlTag
    override val fixDataDictionary: IFixDataDictionaryService
    val fixType: String
    val values: Map<String, String>
    val value: String

    override val fields: MutableList<FieldElement> = mutableListOf()
    override val components: MutableMap<String, ComponentElement> = mutableMapOf()
    override val groups: MutableMap<String, GroupElement> = mutableMapOf()

    constructor(xmlTag: XmlTag, fixDataDictionary: IFixDataDictionaryService) {
        this.name = ""
        this.type = ElementType.FIELD
        this.number = ""
        this.elementTag = xmlTag
        this.fixDataDictionary = fixDataDictionary
        this.fixType = ""
        this.values = mutableMapOf()
        this.value = ""
    }

    constructor(name: String, elementType : ElementType, elementTag: XmlTag, fixDataDictionary: IFixDataDictionaryService) {
        this.name = name
        this.type = elementType
        this.elementTag = elementTag
        this.fixDataDictionary = fixDataDictionary
        this.number = parseNumber(elementTag)
        this.fixType = parseFixType(elementTag)
        this.values = parseValues(elementTag)
        this.value = ""
    }

    constructor(
        name: String,
        type: ElementType,
        number: String,
        elementTag: XmlTag,
        fixDataDictionary: IFixDataDictionaryService,
        fixType: String,
        values: Map<String, String>,
        value: String
    ) {
        this.name = name
        this.type = type
        this.number = number
        this.elementTag = elementTag
        this.fixDataDictionary = fixDataDictionary
        this.fixType = fixType
        this.values = values
        this.value = value
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

    fun isHeaderField(): Boolean {
        return fixDataDictionary.header.containsField(name)
    }

    fun isTrailerField(): Boolean {
        return fixDataDictionary.trailer.containsField(name)
    }

    override fun toString(): String {
        return "FieldElement(name='$name', number='$number', values=${values.size})"
    }

    fun withValue(value: String): FieldElement {
        return FieldElement(
            name = name,
            type = type,
            number = number,
            elementTag = elementTag,
            fixDataDictionary = fixDataDictionary,
            fixType = fixType,
            values = values,
            value = value
        )
    }
  }