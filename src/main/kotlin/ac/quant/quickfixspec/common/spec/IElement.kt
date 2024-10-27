package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

interface IElement {
    val name: String
    val number: String
    val type: ElementType
    val elementTag: XmlTag
    val fixDataDictionary: IFixDataDictionaryService

    fun getAttributeValue(tag: XmlTag, attributeName: String): String {
        return tag.getAttribute(attributeName)?.value ?: ""
    }

    fun parseGroups() {
        println("Should be parsing groups for element $name , but not doing anything")
    }
}
