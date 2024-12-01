package ac.quant.quickfixspec.inspections

import ac.quant.quickfixspec.common.spec.XmlUtils.findDefinition
import ac.quant.quickfixspec.common.spec.XmlUtils.getRootTag
import ac.quant.quickfixspec.common.spec.XmlUtils.isComponentReference
import ac.quant.quickfixspec.common.spec.XmlUtils.getCurrentTag

import ac.quant.quickfixspec.inspections.ReplaceWithDefinitionInspection.ReplaceWithDefinitionQuickFix
import com.intellij.codeInsight.intention.FileModifier.SafeFieldForPreview
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.xml.XmlTag
import com.intellij.util.IncorrectOperationException


class ReplaceWithDefinitionInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                val rootTag = getRootTag(element) ?: return

                val currentTag: XmlTag = getCurrentTag(element)
                if (currentTag.name != "component") {
                    return
                }


                // return if the tag is not a component reference, and it's actually a definition from the "components" group
                if (!isComponentReference(currentTag)) {
                    return
                }

                val componentName = currentTag.getAttributeValue("name") ?: return
                val definition = findDefinition(componentName, "component",  rootTag) ?: return

                // check if we didn't already register a problem for this tag
                if (holder.isOnTheFly && holder.results.any { it.psiElement == currentTag }) {
                    return
                }

                holder.registerProblem(currentTag, "Replace with definition", ReplaceWithDefinitionQuickFix(definition))
            }
        }
    }

    private class ReplaceWithDefinitionQuickFix(definition: XmlTag) : LocalQuickFix {
        @SafeFieldForPreview
        private val definitionPointer: SmartPsiElementPointer<XmlTag?> = SmartPointerManager.createPointer<XmlTag?>(definition)

        override fun getName(): String {
            return "Replace component reference with definition"
        }

        override fun getFamilyName(): String {
            return name
        }

        @Throws(IncorrectOperationException::class)
        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            // Get the <component> tag that is to be replaced
            val tagToReplace = descriptor.psiElement as XmlTag

            // Retrieve the full definition of the <component> from the pointer
            val definition = definitionPointer.getElement()

            if (definition != null) {
                // Create a copy of the definition tag to be inserted
                val newTag = definition.copy() as XmlTag

                // Replace the original <component> tag with the full definition
                tagToReplace.replace(newTag)
            }
        }
    }
}