package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

class MessageElement: IElement {
    override val name: String
    override val number: String
    override val type: ElementType
    override val elementTag: XmlTag
    override val fixDataDictionary: IFixDataDictionaryService

    val msgType : String
    val msgCat : String
    val fields: MutableList<FieldElement> = mutableListOf<FieldElement>()
    val components: MutableMap<String, ComponentElement> = mutableMapOf()
    val groups: MutableMap<String, GroupElement> = mutableMapOf()

    constructor(name: String, elementType : ElementType, elementTag: XmlTag, fixDataDictionary: IFixDataDictionaryService) {
        this.name = name
        this.type = elementType
        this.elementTag = elementTag
        this.fixDataDictionary = fixDataDictionary

        this.number = fixDataDictionary.fields.valuesByName[name]?.number ?: ""
        this.msgType = elementTag.getAttribute("msgtype")?.value ?: ""
        this.msgCat = elementTag.getAttribute("msgcat")?.value ?: ""

        parseSubTags()
    }

    fun containsField(fieldName: String): Boolean {
        return fields.any { it.name == fieldName }
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
                "group" -> {
                    val groupName = fieldTag.getAttribute("name")?.value ?: ""
                    groups[groupName] = GroupElement(groupName, ElementType.GROUP, fieldTag, fixDataDictionary)
                }
                else -> {
                    val elementNameAttr = elementTag.getAttribute("name")!!.value
                    val fieldNameAttr = fieldTag.getAttribute("name")!!.value
                    println("MessageElement with name $elementNameAttr has unprocessed group tag name: ${fieldTag.name} with name $fieldNameAttr")
                }
            }
        }
    }

    override fun parseGroups() {}

    override fun toString(): String {
        return "MessageElement(name='$name', msgType='$msgType', msgcat='$msgCat', fields=${fields.size}, components=${components.size}, groups=${groups.size})"
    }
}
