package moe.syndev

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class SynxBraceMatcher : PairedBraceMatcher {
    private val pairs = arrayOf(
        BracePair(SynxTypes.LEFT_BRACE, SynxTypes.RIGHT_BRACE, true),
        BracePair(SynxTypes.LEFT_BRACKET, SynxTypes.RIGHT_BRACKET, true),
    )

    override fun getPairs(): Array<BracePair> = pairs

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean =
        contextType == null ||
                contextType == SynxTypes.RIGHT_BRACE ||
                contextType == SynxTypes.RIGHT_BRACKET ||
                contextType == SynxTypes.COMMA ||
                contextType == com.intellij.psi.TokenType.WHITE_SPACE

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int = openingBraceOffset
}