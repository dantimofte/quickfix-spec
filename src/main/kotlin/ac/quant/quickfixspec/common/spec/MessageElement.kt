package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

class MessageElement(override val name: String, override val  type : ElementType, override val  elementTag: XmlTag, override val  fixDataDictionary: IFixDataDictionaryService): IElement {
    override val number: String = fixDataDictionary.fields.valuesByName[name]?.number ?: ""
    private val msgType : String = elementTag.getAttribute("msgtype")?.value ?: ""
    private val msgCat : String = elementTag.getAttribute("msgcat")?.value ?: ""
    override val fields: MutableList<FieldElement> = mutableListOf<FieldElement>()
    override val components: MutableMap<String, ComponentElement> = mutableMapOf()
    override val groups: MutableMap<String, GroupElement> = mutableMapOf()

    init {
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
