package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

class DefaultElement: IElement {
    override val name: String
    override val number:String
    override val type: ElementType
    override val elementTag: XmlTag
    override val fixDataDictionary: IFixDataDictionaryService

    override val fields: MutableList<FieldElement> = mutableListOf()
    override val components: MutableMap<String, ComponentElement> = mutableMapOf()
    override val groups: MutableMap<String, GroupElement> = mutableMapOf()


    constructor(name: String, number:String, elementType : ElementType, elementTag: XmlTag,  FixDataDictionaryService: IFixDataDictionaryService) {
        this.name = name
        this.number = number
        this.type = elementType
        this.elementTag = elementTag
        this.fixDataDictionary = FixDataDictionaryService
    }
}
