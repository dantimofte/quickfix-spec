package ac.quant.quickfixspec.clickableviews;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuickfixSpecPsiReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {

        PsiElementPattern.Capture<XmlAttributeValue> psiElementCapture =  PlatformPatterns.psiElement(XmlAttributeValue.class)
                .withParent(XmlPatterns.xmlAttribute().withName("name"))
                .withSuperParent(2, XmlPatterns.xmlTag().withName("component"));

        registrar.registerReferenceProvider(
                psiElementCapture,
                new QuickFixPsiReferenceProvider(),
                PsiReferenceRegistrar.DEFAULT_PRIORITY
        );
    }

}
