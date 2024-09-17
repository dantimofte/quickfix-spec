package ac.quant.quickfixspec.inlay;

import com.intellij.codeInsight.hints.*;
import com.intellij.codeInsight.hints.presentation.*;
import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.xml.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@SuppressWarnings("UnstableApiUsage")
@Slf4j
public class QuickfixInlayHintsProvider implements InlayHintsProvider<NoSettings> {

    private static final SettingsKey<NoSettings> KEY = new SettingsKey<>("fix.tag.inlay.hints");

    @Override
    public @NotNull SettingsKey<NoSettings> getKey() {
        return KEY;
    }

    @Override
    public @NotNull String getName() {
        return "Quickfix tag inlay hints";
    }

    @Override
    public @NotNull NoSettings createSettings() {
        return new NoSettings();
    }

    @Override
    public @NotNull InlayHintsCollector getCollectorFor(
            @NotNull PsiFile file,
            @NotNull Editor editor,
            @NotNull NoSettings settings,
            @NotNull InlayHintsSink sink) {
        log.info("getCollectorFor");
        return new MyCollector();
    }

    private static class MyCollector implements InlayHintsCollector {

        // Set the top and bottom insets using magic numbers for better alignment
        // TODO - Use a better way to calculate insets
        final int TOP_INSET = 6;
        final int BOTTOM_INSET = 2;

        @Override
        public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink sink){

            if (!(element instanceof XmlAttributeValue attributeValue)) {
                return true;
            }

            PsiElement parent = attributeValue.getParent();

            if (!(parent instanceof XmlAttribute attribute)) {
                return true;
            }

            // We're interested in attributes named "name" and "group"
            if (!"name".equals(attribute.getName()) && !"group".equals(attribute.getName())) {
                return true;
            }

            XmlTag tag = attribute.getParent();

            // We're interested in "field" and "group" tags
            if (!"field".equals(tag.getName()) && !"group".equals(tag.getName())) {
                return true;
            }

            // Get the field name
            String fieldName = attributeValue.getValue();

            // Find the tag number
            final XmlTag fieldTag = findField(getRootTag(tag), fieldName);

            String tagNumber = fieldTag != null ? fieldTag.getAttributeValue("number") : null;
            String fieldType = fieldTag != null ? fieldTag.getAttributeValue("type") : null;

            maybeAddTagNumber(editor, sink, attributeValue, tagNumber);
            maybeAddFieldType(editor, sink, attributeValue, fieldType);

            return true;
        }

        private void maybeAddTagNumber(Editor editor, InlayHintsSink sink, XmlAttributeValue attributeValue, String tagNumber) {
            if (tagNumber == null) {
                return;
            }

            // Create the inlay presentation
            PresentationFactory factory = new PresentationFactory(editor);
            InlayPresentation textPresentation = factory.text(" (" + tagNumber + ")");

            // Wrap with InsetPresentation
            InlayPresentation centeredPresentation = new InsetPresentation(
                    textPresentation,
                    0,              // Left inset
                    0,             // Right inset
                    TOP_INSET,     // Top inset
                    BOTTOM_INSET   // Bottom inset
            );

            int offset = attributeValue.getTextRange().getEndOffset();
            sink.addInlineElement(offset, false, centeredPresentation, false);
        }

        private void maybeAddFieldType(Editor editor, InlayHintsSink sink, XmlAttributeValue attributeValue, String fieldType) {
            if (fieldType == null) {
                return;
            }

            // Create the inlay presentation
            PresentationFactory factory = new PresentationFactory(editor);
            InlayPresentation textPresentation = factory.text(" (" + fieldType + ")");

            // Wrap with InsetPresentation
            InlayPresentation centeredPresentation = new InsetPresentation(
                    textPresentation,
                    0,              // Left inset
                    0,             // Right inset
                    TOP_INSET,     // Top inset
                    BOTTOM_INSET   // Bottom inset
            );

            int offset = attributeValue.getTextRange().getEndOffset();
            sink.addInlineElement(offset, false, centeredPresentation, false);
        }



        private @Nullable XmlTag findField(PsiElement root, String fieldName) {
            if (!(root instanceof XmlTag rootTag)) {
                return null;
            }

            // Assuming the "fields" tag is a direct child of the root
            XmlTag[] fieldsTags = rootTag.findSubTags("fields");

            for (XmlTag fieldsTag : fieldsTags) {
                for (XmlTag fieldTag : fieldsTag.findSubTags("field")) {
                    String nameAttr = fieldTag.getAttributeValue("name");
                    if (fieldName.equals(nameAttr)) {
                        return fieldTag;
                    }
                }
            }
            return null;
        }


        // Helper method to navigate to the root tag
        private @Nullable XmlTag getRootTag(PsiElement element) {
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

    @Override
    public boolean isLanguageSupported(@NotNull Language language) {
        return language.isKindOf(XMLLanguage.INSTANCE);
    }

    public @NotNull SettingsKey<NoSettings> getSettingsKey() {
        return KEY;
    }

    @Override
    public @NotNull String getPreviewText() {
        return "";
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull NoSettings settings) {
        return new ImmediateConfigurable() {
            @Override
            public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
                return new JPanel();
            }
        };
    }
}

