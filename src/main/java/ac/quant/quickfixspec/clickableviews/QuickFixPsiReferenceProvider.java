package ac.quant.quickfixspec.clickableviews;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class QuickFixPsiReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {

        final boolean isXmlAttributeValue = element instanceof XmlAttributeValue;
        if (!isXmlAttributeValue) {
            return PsiReference.EMPTY_ARRAY;
        }

        XmlAttributeValue valueElement = (XmlAttributeValue) element;
        return new PsiReference[]{new QuickfixPsiReference(valueElement)};

    }
}
