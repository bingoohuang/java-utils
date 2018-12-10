package com.github.bingoohuang.utils.config.impl;

import java.util.ArrayList;
import java.util.List;

public class CSVLineReader {
    /**
     * The default separator to use if none is supplied to the constructor.
     */
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * The default quote character to use if none is supplied to the
     * constructor.
     */
    public static final char DEFAULT_QUOTE_CHARACTER = '"';

    private char separator = DEFAULT_SEPARATOR; // 分隔符
    private char quotechar = DEFAULT_QUOTE_CHARACTER; // 引号符

    public String[] parseLine(String line) {
        List<String> tokensOnThisLine = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        do {
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == quotechar) {
                    // this gets complex... the quote may end a quoted block, or
                    // escape another quote. do a 1-char lookahead:
                    if (inQuotes // we are in quotes, therefore there can be
                            // escaped quotes in here.
                            && line.length() > i + 1 // there is indeed
                            // another character to check.
                            && line.charAt(i + 1) == quotechar) { // ..and
                        // that char. is a quote also.
                        // we have two quote chars in a row == one quote char,
                        // so consume them both and
                        // put one on the token. we do *not* exit the quoted text.
                        sb.append(line.charAt(i + 1));
                        i++;
                    } else {
                        inQuotes = !inQuotes;
                        // the tricky case of an embedded quote in the middle: a,bc"d"ef,g
                        if (i > 2 // not on the begining of the line
                                && line.charAt(i - 1) != separator // not at the  begining of an escape sequence
                                && line.length() > i + 1
                                && line.charAt(i + 1) != separator // not at the end of an escape sequence
                        ) {
                            sb.append(c);
                        }
                    }
                } else if (c == separator && !inQuotes) {
                    tokensOnThisLine.add(sb.toString().trim());
                    sb = new StringBuilder(); // start work on next token
                } else {
                    sb.append(c);
                }
            }
        } while (inQuotes);

        tokensOnThisLine.add(sb.toString().trim());
        return tokensOnThisLine.toArray(new String[0]);

    }
}
