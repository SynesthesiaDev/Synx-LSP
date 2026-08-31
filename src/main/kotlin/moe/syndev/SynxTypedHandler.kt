package moe.syndev

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.openapi.util.TextRange

class SynxTypedHandler : TypedHandlerDelegate() {
    
    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        try {
            if (c == '{') {
                val doc = editor.document
                val offset = editor.caretModel.offset
                val posBrace = offset - 1

                val text = doc.charsSequence

                var i = posBrace - 1
                while (i >= 0 && (text[i] == ' ' || text[i] == '\t')) i--
                val prevIsListOpen = i >= 0 && text[i] == '['

                val lineNumber = doc.getLineNumber(posBrace)
                val lineStart = doc.getLineStartOffset(lineNumber)
                val lineText = doc.getText(TextRange(lineStart, posBrace))
                val leadingWhitespace = lineText.takeWhile { it == ' ' || it == '\t' }
                val indentUnit = if (leadingWhitespace.contains('\t')) "\t" else "    "

                val innerIndent = leadingWhitespace + indentUnit
                
                if (prevIsListOpen) {
                    WriteCommandAction.runWriteCommandAction(project) {
                        doc.deleteString(posBrace, posBrace + 1)


                        val innerIndent2 = innerIndent + indentUnit

                        val insertPrefix = "\n" + innerIndent + "{" + "\n" + innerIndent2
                        val insertSuffix = "\n$innerIndent}"

                        doc.insertString(posBrace, insertPrefix + insertSuffix)

                        editor.caretModel.moveToOffset(posBrace + insertPrefix.length)
                    }

                    return Result.STOP
                }

                val insertText = "\n$innerIndent\n$leadingWhitespace}"

                WriteCommandAction.runWriteCommandAction(project) {
                    doc.insertString(offset, insertText)
                }

                editor.caretModel.moveToOffset(offset + 1 + innerIndent.length)
                return Result.STOP
            }

            if (c == '[') {
                val doc = editor.document
                val offset = editor.caretModel.offset

                WriteCommandAction.runWriteCommandAction(project) {
                    doc.insertString(offset, "]")
                }

                editor.caretModel.moveToOffset(offset)
                return Result.STOP
            }

        } catch (e: Exception) {
        }

        return super.charTyped(c, project, editor, file)
    }
}
