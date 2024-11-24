package ac.quant.quickfixspec.clickableviews;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ac.quant.quickfixspec.common.spec.XmlUtils.*;

@Slf4j
public class QuickfixPsiReference implements PsiReference {

    private final XmlAttributeValue attributeValue;
    private final XmlTag rootTag;
    private final String parentTagName;

    public QuickfixPsiReference(final XmlAttributeValue attributeValue) {
        this.attributeValue = attributeValue;
        this.parentTagName = ((XmlTag) attributeValue.getParent().getParent()).getName();
        this.rootTag = getRootTag(attributeValue);
    }

    @Override
    public @NotNull PsiElement getElement() {
        return this.attributeValue;
    }

    @Override
    public @NotNull TextRange getRangeInElement() {
        return new TextRange(1, attributeValue.getTextLength() - 1);
    }

    @Override
    public @Nullable PsiElement resolve() {
        String tagName = attributeValue.getValue();
        return findDefinition(tagName, parentTagName, rootTag);
    }

    @Override
    public @NotNull String getCanonicalText() {
        return attributeValue.getValue();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return element;
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        return element instanceof XmlTag && element.equals(resolve());
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
