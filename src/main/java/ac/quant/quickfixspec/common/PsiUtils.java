package ac.quant.quickfixspec.common;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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

}
