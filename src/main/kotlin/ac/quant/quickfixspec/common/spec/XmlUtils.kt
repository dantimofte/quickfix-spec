package ac.quant.quickfixspec.common.spec

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import java.util.Map

object XmlUtils {
    const val NAME_ATTRIBUTE: String = "name"

    val DEFINITION_GROUP_NAME: MutableMap<String?, String?> = Map.of<String?, String?>(
        "component", "components",
        "group", "fields",
        "field", "fields"
    )

    val DEFINITION_TAG_NAME: MutableMap<String?, String?> = Map.of<String?, String?>(
        "component", "component",
        "group", "field",
        "field", "field"
    )
    @JvmField
    val TAGS_WITH_DEFINITION: Array<String> = arrayOf<String>("component", "group", "field")


    // Helper method to navigate to the root tag
    @JvmStatic
    fun getRootTag(element: PsiElement?): XmlTag? {
        var current = element
        while (current != null && current !is PsiFile) {
            if (current is XmlTag && "fix" == current.getName()) {
                return current
            }
            current = current.getParent()
        }
        return null
    }

    @JvmStatic
    fun findDefinition(tagName: String, parentTagName: String, rootTag: XmlTag): XmlTag? {
        val tags = rootTag.findSubTags(DEFINITION_GROUP_NAME[parentTagName])

        for (tag in tags) {
            for (componentTag in tag.findSubTags(DEFINITION_TAG_NAME[parentTagName])) {
                val nameAttr = componentTag.getAttributeValue(NAME_ATTRIBUTE)
                if (tagName == nameAttr) {
                    return componentTag
                }
            }
        }
        return null
    }

    // answers the question :
    // is the attribute value inside one of the 2 tags that are used for declaration
    // <components> <component name=""/> </components>
    // <fields> <field name=""/> </fields>
    @JvmStatic
    fun isTagDeclaration(valueElement: XmlAttributeValue): Boolean {
        val parentAttribute = valueElement.getParent() as XmlAttribute
        val xmlTag = parentAttribute.getParent()
        val parentTag = xmlTag.getParentTag()
        if (parentTag == null) {
            return false
        }

        val parentTagName = parentTag.getName()

        return DEFINITION_GROUP_NAME.containsValue(parentTagName)
    }
}
