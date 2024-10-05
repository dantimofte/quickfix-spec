package ac.quant.quickfixspec.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import static ac.quant.quickfixspec.common.PsiUtils.*;

public class ReplaceWithDefinitionInspection extends LocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (isNotComponentTagReference(element)) {
                    return;
                }

                final XmlTag rootTag = getRootTag(element);
                final XmlTag tag = (XmlTag) element;
                if (rootTag == null) {
                    return;
                }

                final String componentName = tag.getAttributeValue("name");


                XmlTag definition = findDefinition(componentName, tag.getName(), rootTag);
                if (definition != null) {
                    holder.registerProblem(tag, "Replace with definition", new ReplaceWithDefinitionQuickFix(definition));
                }
            }
        };
    }

    private boolean isNotComponentTagReference(@NotNull PsiElement element) {
        if (!(element instanceof XmlTag tag)) {
            return true;
        }

        final XmlTag parentTag = tag.getParentTag();
        if (parentTag == null) {
            return true;
        }

        if (parentTag.getName().equals("components")) {
            return true;
        }

        // if the tag is not self-closing that means it is a definition
        if (tag.getSubTags().length > 0) {
            return true;
        }

        return !tag.getName().equals("component");
    }

    private static class ReplaceWithDefinitionQuickFix implements LocalQuickFix {
        @SafeFieldForPreview
        private final SmartPsiElementPointer<XmlTag> definitionPointer;

        ReplaceWithDefinitionQuickFix(XmlTag definition) {
            this.definitionPointer = SmartPointerManager.createPointer(definition);
        }

        @Override
        public @NotNull String getName() {
            return "Replace with definition";
        }

        @Override
        public @NotNull String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) throws IncorrectOperationException {
            // Get the <component> tag that is to be replaced
            XmlTag tagToReplace = (XmlTag) descriptor.getPsiElement();

            // Retrieve the full definition of the <component> from the pointer
            XmlTag definition = definitionPointer.getElement();

            if (definition != null) {
                // Create a copy of the definition tag to be inserted
                XmlTag newTag = (XmlTag) definition.copy();

                // Replace the original <component> tag with the full definition
                tagToReplace.replace(newTag);
            }
        }
    }
}