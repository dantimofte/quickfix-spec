package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

class GroupElement(override val name: String,override val type : ElementType,override val elementTag: XmlTag,override val fixDataDictionary: IFixDataDictionaryService): IElement {
    override val number: String = fixDataDictionary.fields.valuesByName[name]?.number ?: ""
    override  val fields: MutableList<FieldElement> = mutableListOf()
    override val components: MutableMap<String, ComponentElement> = mutableMapOf()
    override val groups: MutableMap<String, GroupElement> = mutableMapOf()

    init {
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
