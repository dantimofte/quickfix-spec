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

import static ac.quant.quickfixspec.common.spec.XmlUtils.getRootTag;

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
        return "Fix tag inlay hints";
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
        return new MyCollector();
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

            final String tagName = tag.getName();

            if ("component".equals(tagName)) {
                handleComponentTag(editor, attributeValue, tag);
                return true;
            }

            // We're interested in "field" and "group" tags
            if (!"field".equals(tag.getName()) && !"group".equals(tag.getName())) {
                return true;
            }

            // Get the field name
            String fieldName = attributeValue.getValue();

            // Find the tag number
            final XmlTag field = findField(getRootTag(tag), fieldName);

            String tagNumber = field != null ? field.getAttributeValue("number") : "";
            String fieldType = field != null ? field.getAttributeValue("type") : "";

            maybePresent(editor, sink, attributeValue, tagNumber, fieldType);

            return true;
        }


        private void handleComponentTag(
                final Editor editor,
                final XmlAttributeValue attributeValue,
                final XmlTag tag
        ) {

            int offset = attributeValue.getTextRange().getEndOffset();

            // print something when the attributeValue is clicked
            InlayPresentation textPresentation = new PresentationFactory(editor).text("Click me!");
            InlayPresentation centeredPresentation = new InsetPresentation(
                    textPresentation,
                    0,              // Left inset
                    0,             // Right inset
                    TOP_INSET,     // Top inset
                    BOTTOM_INSET   // Bottom inset
            );

            // Add the inlay hint


        }

        private void maybePresent(
                final Editor editor,
                final InlayHintsSink sink,
                final XmlAttributeValue attributeValue,
                final String tagNumber,
                final String fieldType
        ) {
            if ( "".equals(tagNumber) && "".equals(fieldType)) {
                return;
            }

            // Create the inlay presentation
            PresentationFactory factory = new PresentationFactory(editor);
            InlayPresentation textPresentation = factory.text(" " + tagNumber + " " + fieldType + " ");

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

    }

}

