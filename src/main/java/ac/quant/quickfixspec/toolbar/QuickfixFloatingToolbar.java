package ac.quant.quickfixspec.toolbar;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.toolbar.floating.*;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class QuickfixFloatingToolbar extends AbstractFloatingToolbarProvider implements Disposable {

    public QuickfixFloatingToolbar() {
        super("Fix.Floating");
    }


    @Override
    public void register(@NotNull DataContext dataContext, @NotNull FloatingToolbarComponent component, @NotNull Disposable parentDisposable) {
        super.register(dataContext, component, parentDisposable);
        FileEditor fileEditor = dataContext.getData(PlatformDataKeys.FILE_EDITOR);
        if (fileEditor == null || fileEditor.getFile() == null) {
            return;
        }
        Project project = dataContext.getData(PlatformDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        // Create a new toolbar panel
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Quickfix Toolbar"));

        // Set the toolbar component to the panel
        component.scheduleShow();
    }

    @Override
    public boolean getAutoHideable() {
        return false;
    }

    @Override
    public void dispose() {

    }
}

