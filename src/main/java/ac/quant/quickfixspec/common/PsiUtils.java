package ac.quant.quickfixspec.common;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PsiUtils {

    public final static String NAME_ATTRIBUTE = "name";

    public static final Map<String, String> DEFINITION_GROUP_NAME = Map.of(
        "component", "components",
        "group", "fields",
        "field", "fields"
    );

    public static final Map<String, String> DEFINITION_TAG_NAME = Map.of(
        "component", "component",
        "group", "field",
        "field", "field"
    );
    public final static String[] TAGS_WITH_DEFINITION = new String[]{"component", "group", "field"};


    // Helper method to navigate to the root tag
    public static @Nullable XmlTag getRootTag(PsiElement element) {
        PsiElement current = element;
        while (current != null && !(current instanceof PsiFile)) {
            if (current instanceof XmlTag && "fix".equals(((XmlTag) current).getName())) {
                return (XmlTag) current;
            }
            current = current.getParent();
        }
        return null;
    }

    public static @Nullable XmlTag findDefinition(final String tagName , final String parentTagName, final XmlTag rootTag) {

        XmlTag[] tags = rootTag.findSubTags(DEFINITION_GROUP_NAME.get(parentTagName));

        for (XmlTag tag : tags) {
            for (XmlTag componentTag : tag.findSubTags(DEFINITION_TAG_NAME.get(parentTagName))) {
                String nameAttr = componentTag.getAttributeValue(NAME_ATTRIBUTE);
                if (tagName.equals(nameAttr)) {
                    return componentTag;
                }
            }
        }
        return null;
    }

    // answers the question :
    // is the attribute value is inside one of the 2 tags that are used for declaration
    // <components> <component name=""/> </components>
    // <fields> <field name=""/> </fields>
    public static boolean isTagDeclaration(XmlAttributeValue valueElement) {
        XmlAttribute parentAttribute = (XmlAttribute) valueElement.getParent();
        XmlTag xmlTag = parentAttribute.getParent();
        XmlTag parentTag = xmlTag.getParentTag();
        if (parentTag == null) {
            return false;
        }

        final String parentTagName = parentTag.getName();

        if (DEFINITION_GROUP_NAME.containsValue(parentTagName)) {
            return true;
        }

        return false;
    }
}
