package ac.quant.quickfixspec.common.utils

import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.impl.BaseFixture

object Utils {

    @JvmStatic
    public fun getXmlFile(fixture: CodeInsightTestFixture,  fileName: String): PsiElement {
        val xmlFile = fixture.configureByFile(fileName)
        return xmlFile
    }

}