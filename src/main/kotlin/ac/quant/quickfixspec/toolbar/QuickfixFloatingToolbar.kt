package ac.quant.quickfixspec.toolbar

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.toolbar.floating.AbstractFloatingToolbarProvider
import com.intellij.openapi.editor.toolbar.floating.FloatingToolbarComponent
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel

class QuickfixFloatingToolbar : AbstractFloatingToolbarProvider("Fix.Floating"), Disposable {

    override fun register(dataContext: DataContext, component: FloatingToolbarComponent, parentDisposable: Disposable) {

        val fileEditor = dataContext.getData<FileEditor?>(PlatformDataKeys.FILE_EDITOR)
        if (fileEditor == null || fileEditor.getFile() == null) {
            return
        }
        val project = dataContext.getData<Project?>(PlatformDataKeys.PROJECT)
        if (project == null) {
            return
        }
        // Create a new toolbar panel
        val panel = JPanel(FlowLayout(FlowLayout.LEFT))
        panel.add(JLabel("Quickfix Toolbar"))

        // Set the toolbar component to the panel
        component.scheduleShow()
    }

    override val autoHideable: Boolean
        get() = false

    override fun dispose() {
    }
}

