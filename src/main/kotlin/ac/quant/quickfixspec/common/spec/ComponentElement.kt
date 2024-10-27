package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

class ComponentElement(override val name: String,override val  type : ElementType,override val  elementTag: XmlTag,override val  fixDataDictionary: IFixDataDictionaryService): IElement {
    override val number: String = ""

    override val fields: MutableList<FieldElement> =  mutableListOf()
    override val components: MutableMap<String, ComponentElement> = mutableMapOf()
    override val groups: MutableMap<String, GroupElement> = mutableMapOf()

    private val componentsNames : MutableList<String> = mutableListOf()
    private val groupsTags: MutableMap<String, XmlTag> = mutableMapOf()

    init {
        parseSubTags(elementTag)
    }

    private fun parseSubTags(tag: XmlTag) {
        for (subTag in tag.subTags) {
            when (subTag.name) {
                "field" -> {
                    val fieldName = subTag.getAttribute("name")?.value ?: ""
                    val field = fixDataDictionary.fields.valuesByName[fieldName] as FieldElement
                    fields.add(field)
                }
                "component" -> {
                    val componentName = subTag.getAttribute("name")?.value ?: ""
                    componentsNames.add(componentName)
                }
                "group" -> {
                    val groupName = subTag.getAttribute("name")?.value ?: ""
                    groupsTags[groupName] = subTag
                }
                else -> {
                    println("Unprocessed component tag name: ${subTag.name}")
                }
            }
        }
    }

    fun setSubComponents() {
        for (componentName in componentsNames) {
            val component = fixDataDictionary.components.valuesByName[componentName] as ComponentElement
            components[componentName] = component
        }
    }

    override fun parseGroups() {
        for (groupName in groupsTags.keys) {
            val group = GroupElement(groupName, ElementType.GROUP, groupsTags[groupName]!!, fixDataDictionary)
            groups[groupName] = group
        }
    }

    override fun toString(): String {
        return "ComponentElement(name='$name', fields=${fields.size}, components=${components.size}, groups=${groups.size})"
    }
}
