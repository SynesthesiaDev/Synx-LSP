package moe.syndev

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

object SynxFileType : LanguageFileType(SynxLanguage) {
    override fun getName(): @NonNls String = "Synx File"

    override fun getDescription(): @NlsContexts.Label String = "Synx configuration file"

    override fun getDefaultExtension(): @NlsSafe String = "synx"

    override fun getIcon(): Icon = AllIcons.FileTypes.Config;

}