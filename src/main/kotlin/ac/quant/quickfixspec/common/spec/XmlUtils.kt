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

    private val DEFINITION_TAG_NAME: MutableMap<String?, String?> = Map.of<String?, String?>(
        "component", "component",
        "group", "field",
        "field", "field"
    )
    @JvmField
    val TAGS_WITH_DEFINITION = arrayOf("component", "group", "field")


    // Helper method to navigate to the root tag
    @JvmStatic
    fun getRootTag(element: PsiElement?): XmlTag? {
        var current = element
        while (current != null && current !is PsiFile) {
            if (current is XmlTag && "fix" == current.name) {
                return current
            }
            current = current.parent
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
        val parentAttribute = valueElement.parent as XmlAttribute
        val xmlTag = parentAttribute.parent
        val parentTag = xmlTag.parentTag
        val parentTagName = parentTag?.name

        return DEFINITION_GROUP_NAME.containsValue(parentTagName)
    }

    @JvmStatic
    fun getCurrentTag(element: PsiElement): XmlTag {
        if (element is XmlTag) {
            return element
        }

        // iterate up the tree until we find the tag
        var current = element
        while (current !is XmlTag) {
            current = current.parent
        }
        return current
    }

    //Check if the tag is a reference to a component tag
    @JvmStatic
     fun isComponentReference(tag: XmlTag): Boolean {
        val parentTag = tag.parentTag ?: return false
        if (parentTag.name == "components") {
            return false
        }

        // if the tag is not self-closing that means it is a definition
        if (tag.subTags.size > 0) {
            return false
        }

        return true
    }
}
