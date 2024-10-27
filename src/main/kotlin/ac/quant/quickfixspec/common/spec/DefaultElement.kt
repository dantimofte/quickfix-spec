package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

class DefaultElement: IElement {
    override val name: String
    override val number:String
    override val type: ElementType
    override val elementTag: XmlTag
    override val fixDataDictionary: IFixDataDictionaryService

    constructor(name: String, number:String, elementType : ElementType, elementTag: XmlTag,  FixDataDictionaryService: IFixDataDictionaryService) {
        this.name = name
        this.number = number
        this.type = elementType
        this.elementTag = elementTag
        this.fixDataDictionary = FixDataDictionaryService
    }
}
