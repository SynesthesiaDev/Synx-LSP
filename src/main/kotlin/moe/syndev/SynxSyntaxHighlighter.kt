package moe.syndev

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.IElementType

class SynxSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = SynxLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = when (tokenType) {
        SynxTypes.COMMENT -> arrayOf(DefaultLanguageHighlighterColors.LINE_COMMENT)

        SynxTypes.QUOTED_STRING, SynxTypes.LITERAL_STRING, SynxTypes.LONG_STRING ->
            arrayOf(DefaultLanguageHighlighterColors.STRING)

        SynxTypes.HEX, SynxTypes.INT, SynxTypes.LONG, SynxTypes.DOUBLE, SynxTypes.BINARY, SynxTypes.FLOAT ->
            arrayOf(DefaultLanguageHighlighterColors.NUMBER)

        SynxTypes.TRUE, SynxTypes.FALSE, SynxTypes.NULL ->
            arrayOf(DefaultLanguageHighlighterColors.CONSTANT)

        SynxTypes.LEFT_BRACE, SynxTypes.RIGHT_BRACE, SynxTypes.LEFT_BRACKET, SynxTypes.RIGHT_BRACKET ->
            arrayOf(DefaultLanguageHighlighterColors.BRACES)

        SynxTypes.IDENTIFIER -> arrayOf(DefaultLanguageHighlighterColors.KEYWORD)
        SynxTypes.BAD_CHARACTER -> arrayOf(DefaultLanguageHighlighterColors.IDENTIFIER)
        SynxTypes.COMMA -> arrayOf(DefaultLanguageHighlighterColors.COMMA)
        SynxTypes.EQUALS -> arrayOf(DefaultLanguageHighlighterColors.IDENTIFIER)
        
        else -> emptyArray()
    }.map { it }.toTypedArray()
}

class SynxSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(
        p0: Project?,
        p1: VirtualFile?
    ): SyntaxHighlighter = SynxSyntaxHighlighter()

}