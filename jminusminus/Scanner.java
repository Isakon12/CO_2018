// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Hashtable;
import static jminusminus.TokenKind.*;

/**
 * A lexical analyzer for j--, that has no backtracking mechanism.
 * 
 * When you add a new token to the scanner, you must also add an entry in the
 * TokenKind enum in TokenInfo.java specifying the kind and image of the new
 * token.
 */

class Scanner {

    /** End of file character. */
    public final static char EOFCH = CharReader.EOFCH;

    /** Keywords in j--. */
    private Hashtable<String, TokenKind> reserved;

    /** Source characters. */
    private CharReader input;

    /** Next unscanned character. */
    private char ch;

    /** Whether a scanner error has been found. */
    private boolean isInError;

    /** Source file name. */
    private String fileName;

    /** Line number of current token. */
    private int line;

    /**
     * Construct a Scanner object.
     * 
     * @param fileName
     *            the name of the file containing the source.
     * @exception FileNotFoundException
     *                when the named file cannot be found.
     */

    public Scanner(String fileName) throws FileNotFoundException {
        this.input = new CharReader(fileName);
        this.fileName = fileName;
        isInError = false;

        // Keywords in j--
        reserved = new Hashtable<String, TokenKind>();
        reserved.put(ABSTRACT.image(), ABSTRACT);
        reserved.put(BOOLEAN.image(), BOOLEAN);
        reserved.put(CHAR.image(), CHAR);
        reserved.put(CLASS.image(), CLASS);
        reserved.put(ELSE.image(), ELSE);
        reserved.put(EXTENDS.image(), EXTENDS);
        reserved.put(FALSE.image(), FALSE);
        reserved.put(IF.image(), IF);
        reserved.put(IMPORT.image(), IMPORT);
        reserved.put(INSTANCEOF.image(), INSTANCEOF);
        reserved.put(INT.image(), INT);
        reserved.put(NEW.image(), NEW);
        reserved.put(NULL.image(), NULL);
        reserved.put(PACKAGE.image(), PACKAGE);
        reserved.put(PRIVATE.image(), PRIVATE);
        reserved.put(PROTECTED.image(), PROTECTED);
        reserved.put(PUBLIC.image(), PUBLIC);
        reserved.put(RETURN.image(), RETURN);
        reserved.put(STATIC.image(), STATIC);
        reserved.put(SUPER.image(), SUPER);
        reserved.put(THIS.image(), THIS);
        reserved.put(TRUE.image(), TRUE);
        reserved.put(VOID.image(), VOID);
        reserved.put(WHILE.image(), WHILE);
        reserved.put(BREAK.image(), BREAK);
        reserved.put(BYTE.image(), BYTE);
        reserved.put(CASE.image(), CASE);
        reserved.put(CATCH.image(), CATCH);
        reserved.put(CONST.image(), CONST);
        reserved.put(CONTINUE.image(), CONTINUE);
        reserved.put(DEFAULT.image(), DEFAULT);
        reserved.put(DO.image(), DO);
        reserved.put(DOUBLE.image(), DOUBLE);
        reserved.put(FINAL.image(), FINAL);
        reserved.put(FINALLY.image(), FINALLY);
        reserved.put(FLOAT.image(), FLOAT);
        reserved.put(FOR.image(), FOR);
        reserved.put(GOTO.image(), GOTO);
        reserved.put(IMPLEMENTS.image(), IMPLEMENTS);
        reserved.put(INTERFACE.image(), INTERFACE);
        reserved.put(LONG.image(), LONG);
        reserved.put(NATIVE.image(), NATIVE);
        reserved.put(SHORT.image(), SHORT);
        reserved.put(STRICTFP.image(), STRICTFP);
        reserved.put(SWITCH.image(), SWITCH);
        reserved.put(SYNCHRONIZED.image(), SYNCHRONIZED);
        reserved.put(THROW.image(), THROW);
        reserved.put(THROWS.image(), THROWS);
        reserved.put(TRANSIENT.image(), TRANSIENT);
        reserved.put(TRY.image(), TRY);
        reserved.put(VOLATILE.image(), VOLATILE);
        
        // Prime the pump.
        nextCh();
    }

    /**
     * Scan the next token from input.
     * 
     * @return the the next scanned token.
     */

    public TokenInfo getNextToken() {
        StringBuffer buffer;
        boolean moreWhiteSpace = true;
        while (moreWhiteSpace) {
            while (isWhitespace(ch)) {
                nextCh();
            }
            if (ch == '/') {
                nextCh();
                if (ch == '/') {
                    // CharReader maps all new lines to '\n'
                    while (ch != '\n' && ch != EOFCH) {
                        nextCh();
                    }
                } else if(ch == '*') {
                	nextCh();
                	while (ch != EOFCH) {
                        if(ch == '*') {
                        	nextCh();
                        	if(ch == '/') {
                        		nextCh();
                        		break;
                        	}
                        } else {
                            nextCh();
                        }
                    }
                } else if(ch == '=') {
                	nextCh();
                	return new TokenInfo(DIV_ASSIGN, line);
                } else {
                	return new TokenInfo(DIV, line);
                }
            } else {
                moreWhiteSpace = false;
            }
        }
        line = input.line();
        switch (ch) {
        case '(':
            nextCh();
            return new TokenInfo(LPAREN, line);
        case ')':
            nextCh();
            return new TokenInfo(RPAREN, line);
        case '{':
            nextCh();
            return new TokenInfo(LCURLY, line);
        case '}':
            nextCh();
            return new TokenInfo(RCURLY, line);
        case '[':
            nextCh();
            return new TokenInfo(LBRACK, line);
        case ']':
            nextCh();
            return new TokenInfo(RBRACK, line);
        case ';':
            nextCh();
            return new TokenInfo(SEMI, line);
        case ',':
            nextCh();
            return new TokenInfo(COMMA, line);
        case ':':
        	nextCh();
        	return new TokenInfo(COLON, line);
        case '=':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(EQUAL, line);
            } else {
                return new TokenInfo(ASSIGN, line);
            }
        case '!':
            nextCh();
            if(ch == '=') {
            	nextCh();
            	return new TokenInfo(NOT_EQUAL, line);
            } else {
            	return new TokenInfo(LNOT, line);
            }
        case '*':
        	nextCh();
        	if (ch == '=') {
                nextCh();
                return new TokenInfo(STAR_ASSIGN, line);
            } else {
            	return new TokenInfo(STAR, line);
            }
        case '%':
        	nextCh();
        	if (ch == '=') {
        		nextCh();
        		return new TokenInfo(REM_ASSIGN, line);
        	} else {
        		return new TokenInfo(REM, line);
        	}
        case '?':
        	nextCh();
        	return new TokenInfo(Q_MARK, line);
        case '~':
        	nextCh();
        	return new TokenInfo(UCOMP, line);
        case '+':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(PLUS_ASSIGN, line);
            } else if (ch == '+') {
                nextCh();
                return new TokenInfo(INC, line);
            } else {
                return new TokenInfo(PLUS, line);
            }
        case '-':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(MINUS_ASSIGN, line);
            } else if (ch == '-') {
                nextCh();
                return new TokenInfo(DEC, line);
            } else {
                return new TokenInfo(MINUS, line);
            }
        case '&':
            nextCh();
            if (ch == '&') {
                nextCh();
                return new TokenInfo(LAND, line);
            } else if(ch == '=') {
                nextCh();
                return new TokenInfo(AND_ASSIGN, line);
            } else {
            	return new TokenInfo(AND, line);
            }
        case '>':
            nextCh();
            if(ch == '=') {
            	nextCh();
            	return new TokenInfo(GE, line);
            } else if (ch == '>') {
                nextCh();
                if (ch == '=') {
                	nextCh();
                	return new TokenInfo(ARSHIFT_ASN, line);
                } else if (ch == '>') {
                	nextCh();
                	if(ch == '=') {
                		nextCh();
                		return new TokenInfo(LRSHIFT_ASN, line);
                	} else {
                		return new TokenInfo(LRSHIFT, line);
                	}
                } else {
                	return new TokenInfo(ARSHIFT, line);
                }
            } else {
                return new TokenInfo(GT, line);
            }
        case '^':
        	nextCh();
        	if(ch == '=') {
        		nextCh();
        		return new TokenInfo(EOR_ASSIGN, line);
        	} else {
        		return new TokenInfo(EOR, line);
        	}
        case '|':
        	nextCh();
        	if(ch == '|') {
        		nextCh();
        		return new TokenInfo(LOR, line);
        	} else if(ch == '=') {
        		nextCh();
        		return new TokenInfo(IOR_ASSIGN, line);
        	} else {
        		return new TokenInfo(IOR, line);
        	}
        case '<':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(LE, line);
            } else if (ch == '<') {
            	nextCh();
            	if(ch == '=') {
            		nextCh();
            		return new TokenInfo(ALSHIFT_ASN, line);
            	} else {
            		return new TokenInfo(ALSHIFT, line);
            	}
            } else {
            	return new TokenInfo(LT, line);
            }
        case '\'':
            buffer = new StringBuffer();
            buffer.append('\'');
            nextCh();
            if (ch == '\\') {
                nextCh();
                buffer.append(escape());
            } else {
                buffer.append(ch);
                nextCh();
            }
            if (ch == '\'') {
                buffer.append('\'');
                nextCh();
                return new TokenInfo(CHAR_LITERAL, buffer.toString(), line);
            } else {
                // Expected a ' ; report error and try to
                // recover.
                reportScannerError(ch
                        + " found by scanner where closing ' was expected.");
                while (ch != '\'' && ch != ';' && ch != '\n') {
                    nextCh();
                }
                return new TokenInfo(CHAR_LITERAL, buffer.toString(), line);
            }
        case '"':
            buffer = new StringBuffer();
            buffer.append("\"");
            nextCh();
            while (ch != '"' && ch != '\n' && ch != EOFCH) {
                if (ch == '\\') {
                    nextCh();
                    buffer.append(escape());
                } else {
                    buffer.append(ch);
                    nextCh();
                }
            }
            if (ch == '\n') {
                reportScannerError("Unexpected end of line found in String");
            } else if (ch == EOFCH) {
                reportScannerError("Unexpected end of file found in String");
            } else {
                // Scan the closing "
                nextCh();
                buffer.append("\"");
            }
            return new TokenInfo(STRING_LITERAL, buffer.toString(), line);
        case '.':
            nextCh();
            //check if token is a decimal number (double)
            if(isDigit(ch)) {
            	buffer = new StringBuffer();
            	buffer.append('.');
            	//collect multicharacter number
        		while (isDigit(ch)) {
        			buffer.append(ch);
        			nextCh();
        		}
        		//check if decimal has an exponential part
        		if(ch == 'e' || ch == 'E') {
        			buffer.append(ch);
        			nextCh();
        			//check decimal has exponential then it must be followed by number
        			if(isDigit(ch)) {
        				//collect multicharacter number
                		while (isDigit(ch)) {
                			buffer.append(ch);
                			nextCh();
                		}
                		//optional d or D || f|F for float || nothing then still double
                		if(ch== 'd' || ch == 'D') {
                			buffer.append('d');
                			nextCh();
                			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
                		} else if (ch == 'f' || ch == 'F'){
                			buffer.append('f');
                			nextCh();
                			return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line);
                		} else {
                			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
                		}
        			}
        		} else {
        			//optional d or D || f|F for float || nothing then still double
            		if(ch== 'd' || ch == 'D') {
            			buffer.append('d');
            			nextCh();
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		} else if (ch == 'f' || ch == 'F'){
            			buffer.append('f');
            			nextCh();
            			return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line);
            		} else {
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		}
        		}
            } else {
            return new TokenInfo(DOT, line);
            }
        case EOFCH:
            return new TokenInfo(EOF, line);
        case '0':
            nextCh();
        	//check for decimal numbers (double or float)
            if (ch == '.') {
        		buffer = new StringBuffer();
        		buffer.append('0');
        		buffer.append('.');
        		nextCh();
        		
        		//optional number
        		if(isDigit(ch)) {
        			while (isDigit(ch)) {
                        buffer.append(ch);
                        nextCh();
                    }
        		}
        		
        		//check if decimal has an exponential part
            	if(ch == 'e' || ch == 'E') {
            		buffer.append(ch);
            		nextCh();
            		//optional sign operator
            		if (ch == '+' || ch == '-') {
            			buffer.append(ch);
               			nextCh();
            		}
            		//non-optional number
            		if(isDigit(ch)) {
            			//collect multicharacter number
                   		while (isDigit(ch)) {
                   			buffer.append(ch);
                   			nextCh();
                   		}
                   		//optional d or D || f|F for float || nothing then still double
                		if(ch== 'd' || ch == 'D') {
                			buffer.append('d');
                			nextCh();
                			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
                		} else if (ch == 'f' || ch == 'F'){
                			buffer.append('f');
                			nextCh();
                			return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line);
                		} else {
                			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
                		}
                   	} 
            	} else {
            		//no exponential part
            		
            		//optional d or D || f|F for float || nothing then still double
            		if(ch== 'd' || ch == 'D') {
            			buffer.append('d');
            			nextCh();
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		} else if (ch == 'f' || ch == 'F'){
            			buffer.append('f');
            			nextCh();
            			return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line);
            		} else {
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		}
            	}
        	} else if(ch == 'l' || ch == 'L') {
            	nextCh();
            	return new TokenInfo(LONG_LITERAL, "0l", line);
            } else if (ch=='e' || ch=='E') {
            	buffer = new StringBuffer();
        		buffer.append('0');
            	//exponential part for double
            	buffer.append('e');
            	nextCh();
            	//optional sign
            	if(ch == '-' || ch == '+') {
            		buffer.append(ch);
                	nextCh();
            	}
            	//must be followed by number
            	if(isDigit(ch)) {
            		while (isDigit(ch)) {
            			buffer.append(ch);
            			nextCh();
            		}
            		//optional d or D || f|F for float || nothing then still double
            		if(ch== 'd' || ch == 'D') {
            			buffer.append('d');
            			nextCh();
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		} else if (ch == 'f' || ch == 'F'){
            			buffer.append('f');
            			nextCh();
            			return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line);
            		} else {
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		}
            	}
            } else if (ch == 'd' || ch == 'D') { 
            	nextCh();
        		return new TokenInfo(DOUBLE_LITERAL, "0d", line);
        	} else if (ch == 'f' || ch == 'F') { 
            	nextCh();
        		return new TokenInfo(FLOAT_LITERAL, "0f", line);
        	} else {
            	return new TokenInfo(INT_LITERAL, "0", line);
            }
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            buffer = new StringBuffer();
            while (isDigit(ch)) {
                buffer.append(ch);
                nextCh();
            }
            if(ch=='l' || ch=='L') {
            	buffer.append('l');
            	nextCh();
            	return new TokenInfo(LONG_LITERAL, buffer.toString(), line);
            } else if (ch=='d' || ch=='D') {
            	buffer.append('d');
            	nextCh();
            	return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            } else if (ch=='e' || ch=='E') {
            	//exponential part for double
            	buffer.append('e');
            	nextCh();
            	//optional sign
            	if(ch == '-' || ch == '+') {
            		buffer.append(ch);
                	nextCh();
            	}
            	//must be followed by number
            	if(isDigit(ch)) {
            		while (isDigit(ch)) {
            			buffer.append(ch);
            			nextCh();
            		}
            		//optional d or D || f|F for float || nothing then still double
            		if(ch== 'd' || ch == 'D') {
            			buffer.append('d');
            			nextCh();
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		} else if (ch == 'f' || ch == 'F'){
            			buffer.append('f');
            			nextCh();
            			return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line);
            		} else {
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		}
            	}
            } else if (ch=='.') {
            	buffer.append('.');
        		nextCh();
        		
        		//optional number
        		if(isDigit(ch)) {
        			while (isDigit(ch)) {
                        buffer.append(ch);
                        nextCh();
                    }
        		}
        		
        		//check if decimal has an exponential part
            	if(ch == 'e' || ch == 'E') {
            		buffer.append(ch);
            		nextCh();
            		//optional sign operator
            		if (ch == '+' || ch == '-') {
            			buffer.append(ch);
               			nextCh();
            		}
            		//non-optional number
            		if(isDigit(ch)) {
            			//collect multicharacter number
                   		while (isDigit(ch)) {
                   			buffer.append(ch);
                   			nextCh();
                   		}
                   	//optional d or D || f|F for float || nothing then still double
                		if(ch== 'd' || ch == 'D') {
                			buffer.append('d');
                			nextCh();
                			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
                		} else if (ch == 'f' || ch == 'F'){
                			buffer.append('f');
                			nextCh();
                			return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line);
                		} else {
                			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
                		}
                   	} 
            	} else {
            		//no exponential part
            		
            		//optional d or D || f|F for float || nothing then still double
            		if(ch== 'd' || ch == 'D') {
            			buffer.append('d');
            			nextCh();
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		} else if (ch == 'f' || ch == 'F'){
            			buffer.append('f');
            			nextCh();
            			return new TokenInfo(FLOAT_LITERAL, buffer.toString(), line);
            		} else {
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		}
            	}
            } else {
            	return new TokenInfo(INT_LITERAL, buffer.toString(), line);
            }
        default:
            if (isIdentifierStart(ch)) {
                buffer = new StringBuffer();
                while (isIdentifierPart(ch)) {
                    buffer.append(ch);
                    nextCh();
                }
                String identifier = buffer.toString();
                if (reserved.containsKey(identifier)) {
                    return new TokenInfo(reserved.get(identifier), line);
                } else {
                    return new TokenInfo(IDENTIFIER, identifier, line);
                }
            } else {
                reportScannerError("Unidentified input token: '%c'", ch);
                nextCh();
                return getNextToken();
            }
        }
    }

    /**
     * Scan and return an escaped character.
     * 
     * @return escaped character.
     */

    private String escape() {
        switch (ch) {
        case 'b':
            nextCh();
            return "\\b";
        case 't':
            nextCh();
            return "\\t";
        case 'n':
            nextCh();
            return "\\n";
        case 'f':
            nextCh();
            return "\\f";
        case 'r':
            nextCh();
            return "\\r";
        case '"':
            nextCh();
            return "\"";
        case '\'':
            nextCh();
            return "\\'";
        case '\\':
            nextCh();
            return "\\\\";
        default:
            reportScannerError("Badly formed escape: \\%c", ch);
            nextCh();
            return "";
        }
    }

    /**
     * Advance ch to the next character from input, and update the line number.
     */

    private void nextCh() {
        line = input.line();
        try {
            ch = input.nextChar();
        } catch (Exception e) {
            reportScannerError("Unable to read characters from input");
        }
    }

    /**
     * Report a lexcial error and record the fact that an error has occured.
     * This fact can be ascertained from the Scanner by sending it an
     * errorHasOccurred() message.
     * 
     * @param message
     *            message identifying the error.
     * @param args
     *            related values.
     */

    private void reportScannerError(String message, Object... args) {
        isInError = true;
        System.err.printf("%s:%d: ", fileName, line);
        System.err.printf(message, args);
        System.err.println();
    }

    /**
     * Return true if the specified character is a digit (0-9); false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    /**
     * Return true if the specified character is a whitespace; false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */

    private boolean isWhitespace(char c) {
        switch (c) {
        case ' ':
        case '\t':
        case '\n': // CharReader maps all new lines to '\n'
        case '\f':
            return true;
        }
        return false;
    }

    /**
     * Return true if the specified character can start an identifier name;
     * false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */

    private boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c == '$');
    }

    /**
     * Return true if the specified character can be part of an identifier name;
     * false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */

    private boolean isIdentifierPart(char c) {
        return (isIdentifierStart(c) || isDigit(c));
    }

    /**
     * Has an error occurred up to now in lexical analysis?
     * 
     * @return true or false.
     */

    public boolean errorHasOccurred() {
        return isInError;
    }

    /**
     * The name of the source file.
     * 
     * @return name of the source file.
     */

    public String fileName() {
        return fileName;
    }

}

/**
 * A buffered character reader. Abstracts out differences between platforms,
 * mapping all new lines to '\n'. Also, keeps track of line numbers where the
 * first line is numbered 1.
 */

class CharReader {

    /** A representation of the end of file as a character. */
    public final static char EOFCH = (char) -1;

    /** The underlying reader records line numbers. */
    private LineNumberReader lineNumberReader;

    /** Name of the file that is being read. */
    private String fileName;

    /**
     * Construct a CharReader from a file name.
     * 
     * @param fileName
     *            the name of the input file.
     * @exception FileNotFoundException
     *                if the file is not found.
     */

    public CharReader(String fileName) throws FileNotFoundException {
        lineNumberReader = new LineNumberReader(new FileReader(fileName));
        this.fileName = fileName;
    }

    /**
     * Scan the next character.
     * 
     * @return the character scanned.
     * @exception IOException
     *                if an I/O error occurs.
     */

    public char nextChar() throws IOException {
        return (char) lineNumberReader.read();
    }

    /**
     * The current line number in the source file, starting at 1.
     * 
     * @return the current line number.
     */

    public int line() {
        // LineNumberReader counts lines from 0.
        return lineNumberReader.getLineNumber() + 1;
    }

    /**
     * Return the file name.
     * 
     * @return the file name.
     */

    public String fileName() {
        return fileName;
    }

    /**
     * Close the file.
     * 
     * @exception IOException
     *                if an I/O error occurs.
     */

    public void close() throws IOException {
        lineNumberReader.close();
    }

}
