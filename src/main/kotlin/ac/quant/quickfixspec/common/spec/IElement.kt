package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

interface IElement {
    val name: String
    val number: String
    val type: ElementType
    val elementTag: XmlTag
    val fixDataDictionary: IFixDataDictionaryService

    val fields: MutableList<FieldElement>
    val components: MutableMap<String, ComponentElement>
    val groups: MutableMap<String, GroupElement>


    fun getAttributeValue(tag: XmlTag, attributeName: String): String {
        return tag.getAttribute(attributeName)?.value ?: ""
    }

    fun parseGroups() {
        println("Should be parsing groups for element $name , but not doing anything")
    }
}
