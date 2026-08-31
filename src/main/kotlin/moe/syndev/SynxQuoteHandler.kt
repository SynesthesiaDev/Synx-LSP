package moe.syndev

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler

class SynxQuoteHandler : SimpleTokenSetQuoteHandler(SynxTypes.QUOTED_STRING, SynxTypes.LITERAL_STRING) {
}