package ac.quant.quickfixspec.common.spec

import com.intellij.psi.xml.XmlTag

class DefaultElement(override val name: String,override val  number:String,override val  type : ElementType, override val  elementTag: XmlTag,  override val fixDataDictionary: IFixDataDictionaryService): IElement {
    override val fields: MutableList<FieldElement> = mutableListOf()
    override val components: MutableMap<String, ComponentElement> = mutableMapOf()
    override val groups: MutableMap<String, GroupElement> = mutableMapOf()
}
