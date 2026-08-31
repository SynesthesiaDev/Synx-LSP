package moe.syndev

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

object SynxTypes {
    val LEFT_BRACE = SynxTokenType("LEFT_BRACE")
    val RIGHT_BRACE = SynxTokenType("RIGHT_BRACE")
    val LEFT_BRACKET = SynxTokenType("LEFT_BRACKET")
    val RIGHT_BRACKET = SynxTokenType("RIGHT_BRACKET")
    val COMMA = SynxTokenType("COMMA")
    val EQUALS = SynxTokenType("EQUALS")

    val IDENTIFIER = SynxTokenType("IDENTIFIER")
    val QUOTED_STRING = SynxTokenType("QUOTED_STRING")
    val LITERAL_STRING = SynxTokenType("LITERAL_STRING")
    val LONG_STRING = SynxTokenType("LONG_STRING")

    val INT = SynxTokenType("INT")
    val LONG = SynxTokenType("LONG")
    val FLOAT = SynxTokenType("FLOAT")
    val DOUBLE = SynxTokenType("DOUBLE")
    val HEX = SynxTokenType("HEX")
    val BINARY = SynxTokenType("BINARY")

    val TRUE = SynxTokenType("TRUE")
    val FALSE = SynxTokenType("FALSE")
    val NULL = SynxTokenType("NULL")

    val COMMENT = SynxTokenType("COMMENT")
    
    val BAD_CHARACTER = SynxTokenType("BAD_CHARACTER")

    fun mapTokenType(type: TokenType): IElementType = when (type) {
        TokenType.LeftBrace -> LEFT_BRACE
        TokenType.RightBrace -> RIGHT_BRACE
        TokenType.LeftBracket -> LEFT_BRACKET
        TokenType.RightBracket -> RIGHT_BRACKET
        TokenType.Comma -> COMMA
        TokenType.Equals -> EQUALS
        TokenType.Identifier -> IDENTIFIER
        TokenType.QuotedString -> QUOTED_STRING
        TokenType.LiteralString -> LITERAL_STRING
        TokenType.LongString -> LONG_STRING
        TokenType.Int -> INT
        TokenType.Long -> LONG
        TokenType.Float -> FLOAT
        TokenType.Double -> DOUBLE
        TokenType.Hex -> HEX
        TokenType.Binary -> BINARY
        TokenType.True -> TRUE
        TokenType.False -> FALSE
        TokenType.Null -> NULL
        TokenType.Comment -> COMMENT
        TokenType.Whitespace -> com.intellij.psi.TokenType.WHITE_SPACE
        TokenType.EOF -> throw IllegalStateException("EOF should never be mapped to a highlighted token")
        TokenType.BadCharacter -> BAD_CHARACTER
    }
}

class SynxTokenType(debugName: String) : IElementType(debugName, SynxLanguage)

class SynxLexerAdapter : LexerBase() {
    private lateinit var bufferText: CharSequence
    private var bufferEnd = 0
    private var offsetShift = 0

    private lateinit var tokens: List<Token>
    private var index = 0

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        bufferText = buffer
        bufferEnd = endOffset
        offsetShift = startOffset

        val text = buffer.subSequence(startOffset, endOffset).toString()
        tokens = Lexer.from(text, file = "<editor>").filter { it.type != TokenType.EOF }
        index = 0
    }

    override fun getState(): Int = 0

    override fun getTokenType(): IElementType? =
        if (index < tokens.size) SynxTypes.mapTokenType(tokens[index].type) else null

    override fun getTokenStart(): Int =
        if (index < tokens.size) offsetShift + tokens[index].offset else bufferEnd
    
    override fun getTokenEnd(): Int =
        if (index < tokens.size) offsetShift + tokens[index].offset + tokens[index].length else bufferEnd

    override fun advance() {
        index++
    }

    override fun getBufferSequence(): CharSequence = bufferText

    override fun getBufferEnd(): Int = bufferEnd
}