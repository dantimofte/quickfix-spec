package ac.quant.quickfixspec.common;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Nullable;

public class PsiUtils {


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

    public static @Nullable XmlTag findComponent(final XmlTag rootTag, final String componentName) {

        XmlTag[] componentsTags = rootTag.findSubTags("components");

        for (XmlTag componentsTag : componentsTags) {
            for (XmlTag componentTag : componentsTag.findSubTags("component")) {
                String nameAttr = componentTag.getAttributeValue("name");
                if (componentName.equals(nameAttr)) {
                    return componentTag;
                }
            }
        }
        return null;
    }

    public static boolean isComponentsDeclaration(XmlAttributeValue valueElement) {
        XmlAttribute parentAttribute = (XmlAttribute) valueElement.getParent();
        XmlTag xmlTag = parentAttribute.getParent();
        XmlTag parentTag = xmlTag.getParentTag();
        return parentTag != null && "components".equals(parentTag.getName());
    }
}
