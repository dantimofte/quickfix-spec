package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

class GroupElement: IElement {
    override val name: String
    override val number: String
    override val type: ElementType
    override val elementTag: XmlTag
    override val fixDataDictionary: IFixDataDictionaryService
    val fields: MutableList<FieldElement> = mutableListOf<FieldElement>()
    val components: MutableMap<String, ComponentElement> = mutableMapOf()


    constructor(name: String, elementType : ElementType, elementTag: XmlTag, fixDataDictionary: IFixDataDictionaryService) {
        this.name = name
        this.type = elementType
        this.elementTag = elementTag
        this.fixDataDictionary = fixDataDictionary
        this.number = fixDataDictionary.fields.valuesByName[name]?.number ?: ""

        parseSubTags()
    }

    private fun parseSubTags() {
        for (fieldTag in elementTag.subTags) {
            when (fieldTag.name) {
                "field" -> {
                    val fieldName = fieldTag.getAttribute("name")?.value ?: ""
                    val field = fixDataDictionary.fields.valuesByName[fieldName] as FieldElement
                    fields.add(field)
                }
                "component" -> {
                    val componentName = fieldTag.getAttribute("name")?.value ?: ""
                    components[componentName] = fixDataDictionary.components.valuesByName[componentName] as ComponentElement
                }
                else -> {
                    println("Unprocessed group tag name: ${fieldTag.name}")
                }
            }
        }
    }
}
