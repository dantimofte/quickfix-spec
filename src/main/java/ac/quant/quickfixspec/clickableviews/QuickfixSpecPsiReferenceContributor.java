package ac.quant.quickfixspec.clickableviews;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import lombok.extern.slf4j.Slf4j;
import static ac.quant.quickfixspec.common.spec.XmlUtils.NAME_ATTRIBUTE;
import static ac.quant.quickfixspec.common.spec.XmlUtils.TAGS_WITH_DEFINITION;

@Slf4j
public class QuickfixSpecPsiReferenceContributor extends PsiReferenceContributor {


    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {


        PsiElementPattern.Capture<XmlAttributeValue> psiElementCapture =  PlatformPatterns.psiElement(XmlAttributeValue.class)
                .withParent(XmlPatterns.xmlAttribute().withName(NAME_ATTRIBUTE))
                .withSuperParent(2, XmlPatterns.xmlTag().withName(TAGS_WITH_DEFINITION));

        registrar.registerReferenceProvider(
                psiElementCapture,
                new QuickFixPsiReferenceProvider(),
                PsiReferenceRegistrar.DEFAULT_PRIORITY
        );
    }

}
