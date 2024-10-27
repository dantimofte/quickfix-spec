package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

interface IFixDataDictionaryService {
    val rootTag :  XmlTag
    val fields: Elements
    val components: Elements
    val messages: Elements
    val header: MessageElement
    val trailer: MessageElement

    fun getFieldByNumber(number: String): FieldElement {
        val field = fields.valueByNumber[number] as FieldElement?
        return field ?: FieldElement(rootTag, this)
    }
}
