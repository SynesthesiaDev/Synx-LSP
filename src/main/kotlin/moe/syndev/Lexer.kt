package moe.syndev

enum class TokenType {
    LeftBrace, RightBrace, LeftBracket, RightBracket, Comma, Equals,
    Identifier, QuotedString, LiteralString, LongString,
    Int, Long, Float, Double, Hex, Binary,
    True, False, Null,
    Whitespace, Comment,
    EOF,
    BadCharacter
}

data class Token(
    val type: TokenType,
    val text: String,
    val line: Int,
    val column: Int,
    val offset: Int = 0,
    val length: Int = 0
)

class Lexer(private val source: String, private val file: String) {
    private var pos = 0
    private var line = 1
    private var col = 1

    companion object {
        fun from(source: String, file: String): List<Token> = Lexer(source, file).tokenize()
    }

    private val atEnd: Boolean
        get() = pos >= source.length

    private val current: Char
        get() = if (pos < source.length) source[pos] else '\u0000'

    private fun peek(offset: Int = 1): Char =
        if (pos + offset < source.length) source[pos + offset] else '\u0000'

    private fun advance(): Char {
        val c = current
        pos++
        if (c == '\n') {
            line++
            col = 1
        } else {
            col++
        }
        return c
    }

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()

        while (true) {
            if (atEnd) {
                tokens.add(Token(TokenType.EOF, "", line, col, pos))
                break
            }

            val startLine = line
            val startCol = col
            val startOffset = pos
            val c = current

            val token = when {
                c.isWhitespace() -> readWhitespace()
                c == '/' && peek() == '/' -> readComment()
                c == '{' -> singleCharToken(TokenType.LeftBrace)
                c == '}' -> singleCharToken(TokenType.RightBrace)
                c == '[' -> singleCharToken(TokenType.LeftBracket)
                c == ']' -> singleCharToken(TokenType.RightBracket)
                c == ',' -> singleCharToken(TokenType.Comma)
                c == '=' -> singleCharToken(TokenType.Equals)
                c == '\'' -> readLiteralString()
                c == '"' && peek() == '"' && peek(2) == '"' -> readLongString()
                c == '"' -> readBasicString()
                c.isLetter() || c == '_' -> readIdentifierOrKeyword()
                c.isDigit() -> readNumber()
                else -> {
                    advance()
                    Token(TokenType.BadCharacter, c.toString(), line, col)
                }
            }

            tokens.add(
                token.copy(
                    line = startLine,
                    column = startCol,
                    offset = startOffset,
                    length = pos - startOffset
                )
            )
        }

        return tokens
    }

    private fun readWhitespace(): Token {
        val start = pos
        while (!atEnd && current.isWhitespace()) advance()
        return Token(TokenType.Whitespace, source.substring(start, pos), line, col, start)
    }

    private fun readComment(): Token {
        val start = pos
        while (!atEnd && current != '\n') advance()
        return Token(TokenType.Comment, source.substring(start, pos), line, col, start)
    }

    private fun readLongString(): Token {
        advance(); advance(); advance()
        val sb = StringBuilder()

        while (true) {
            if (atEnd) throw Exception("Unterminated long string starting at line $line")
            if (current == '"' && peek() == '"' && peek(2) == '"') {
                advance(); advance(); advance()
                break
            }
            sb.append(advance())
        }

        return Token(TokenType.LongString, sb.toString(), line, col)
    }

    private fun isHexDigit(c: Char): Boolean =
        c.isDigit() || c in 'a'..'f' || c in 'A'..'F'

    private fun readNumber(): Token {
        val start = pos
        var hasDecimal = false

        if (current == '0' && (peek() == 'x' || peek() == 'X')) {
            advance(); advance()
            while (isHexDigit(current) || current == '_') advance()
            return Token(TokenType.Hex, source.substring(start, pos).replace("_", ""), line, col)
        }

        if (current == '0' && (peek() == 'b' || peek() == 'B')) {
            advance(); advance()
            while (current == '0' || current == '1' || current == '_') advance()
            return Token(TokenType.Binary, source.substring(start, pos).replace("_", ""), line, col)
        }

        advanceUntilNonDigitOrDelimiter()

        if (current == '.' && peek().isDigit()) {
            advance()
            hasDecimal = true
            advanceUntilNonDigitOrDelimiter()
        }

        val type: TokenType
        when {
            current == 'l' || current == 'L' -> {
                type = TokenType.Long; advance()
            }

            current == 'f' || current == 'F' -> {
                type = TokenType.Float; advance()
            }

            current == 'd' || current == 'D' -> {
                type = TokenType.Double; advance()
            }

            else -> type = if (hasDecimal) TokenType.Double else TokenType.Int
        }

        return Token(type, source.substring(start, pos).replace("_", ""), line, col)
    }

    private fun advanceUntilNonDigitOrDelimiter() {
        while (current.isDigit() || current == '_') advance()
    }

    private fun readLiteralString(): Token {
        advance()
        val sb = StringBuilder()

        while (true) {
            if (atEnd) break
            if (current == '\'') {
                advance()
                break
            }
            sb.append(advance())
        }

        return Token(TokenType.LiteralString, sb.toString(), line, col)
    }

    private fun readBasicString(): Token {
        advance()
        val sb = StringBuilder()
        while (true) {
            if (atEnd) break
            if (current == '"') {
                advance(); break
            }
            if (current == '\\') {
                advance()
                sb.append(readEscapeSequence())
            } else {
                sb.append(advance())
            }
        }
        return Token(TokenType.QuotedString, sb.toString(), line, col)
    }

    private fun readEscapeSequence(): Char {
        val c = advance()
        return when (c) {
            'n' -> '\n'
            't' -> '\t'
            '"' -> '"'
            '\\' -> '\\'
            else -> throw Exception("Unknown escape sequence '\\$c' at line $line")
        }
    }

    private fun readIdentifierOrKeyword(): Token {
        val start = pos
        while (!atEnd && (current.isLetterOrDigit() || current == '_')) advance()
        return when (val text = source.substring(start, pos)) {
            "true" -> Token(TokenType.True, text, line, col)
            "false" -> Token(TokenType.False, text, line, col)
            "null" -> Token(TokenType.Null, text, line, col)
            else -> Token(TokenType.Identifier, text, line, col)
        }
    }

    private fun singleCharToken(type: TokenType): Token {
        val text = advance().toString()
        return Token(type, text, line, col)
    }
}