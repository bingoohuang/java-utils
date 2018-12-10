package com.github.bingoohuang.utils.config.impl;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV读取器。
 *
 * @author BingooHuang
 */
public class CSVReader {
    /**
     * The default separator to use if none is supplied to the constructor.
     */
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * The default quote character to use if none is supplied to the
     * constructor.
     */
    public static final char DEFAULT_QUOTE_CHARACTER = '"';

    /**
     * The default line to start reading.
     */
    public static final int DEFAULT_SKIP_LINES = 0;
    private BufferedReader br;

    private boolean hasNext = true;
    private char separator; // 分隔符
    private char quotechar; // 引号符
    private int skipLines; //
    private boolean linesSkiped; //转移线

    /**
     * Constructs CSVReader using a comma for the separator.
     *
     * @param reader the reader to an underlying CSV source.
     */
    public CSVReader(Reader reader) {
        this(reader, DEFAULT_SEPARATOR);
    }

    /**
     * Constructs CSVReader with supplied separator.
     *
     * @param reader    the reader to an underlying CSV source.
     * @param separator the delimiter to use for separating entries.
     */
    public CSVReader(Reader reader, char separator) {
        this(reader, separator, DEFAULT_QUOTE_CHARACTER);
    }

    /**
     * Constructs CSVReader with supplied separator and quote char.
     *
     * @param reader    the reader to an underlying CSV source.
     * @param separator the delimiter to use for separating entries
     * @param quotechar the character to use for quoted elements
     */
    public CSVReader(Reader reader, char separator, char quotechar) {
        this(reader, separator, quotechar, DEFAULT_SKIP_LINES);
    }

    /**
     * Constructs CSVReader with supplied separator and quote char.
     *
     * @param reader    the reader to an underlying CSV source.
     * @param separator the delimiter to use for separating entries
     * @param quotechar the character to use for quoted elements
     * @param line      the line number to skip for start reading
     */
    public CSVReader(Reader reader, char separator, char quotechar, int line) {
        br = new BufferedReader(reader);
        this.separator = separator;
        this.quotechar = quotechar;
        skipLines = line;
    }

    /**
     * Reads the entire file into a List with each element being a String[] of
     * tokens.
     *
     * @return a List of String[], with each String[] representing a line of the
     * file.
     * @throws java.io.IOException if bad things happen during the read
     */
    public List<String[]> readAll() throws IOException {

        List<String[]> allElements = new ArrayList<String[]>();
        while (hasNext) {
            String[] nextLineAsTokens = readNext();
            if (nextLineAsTokens != null) {
                allElements.add(nextLineAsTokens);
            }
        }
        return allElements;

    }

    /**
     * Reads the next line from the buffer and converts to a string array.
     *
     * @return a string array with each comma-separated element as a separate
     * entry.
     * @throws java.io.IOException if bad things happen during the read
     */
    public String[] readNext() throws IOException {
        String nextLine = getNextLine();
        if (nextLine == null) {
            return null;
        }

        if (!StringUtils.startsWith(StringUtils.trim(nextLine), "#")) {
            return hasNext ? parseLine(nextLine) : null;
        }

        return readNext();

    }

    /**
     * Reads the next line from the file.
     *
     * @return the next line from the file without trailing newline
     * @throws java.io.IOException if bad things happen during the read
     */
    private String getNextLine() throws IOException {
        if (!linesSkiped) {
            for (int i = 0; i < skipLines; i++) {
                br.readLine();
            }
            linesSkiped = true;
        }
        String nextLine = br.readLine();
        if (nextLine == null) {
            hasNext = false;
        }
        return hasNext ? nextLine : null;
    }

    /**
     * Parses an incoming String and returns an array of elements.
     *
     * @param nextLine the string to parse
     * @return the comma-tokenized list of elements, or null if nextLine is null
     * @throws java.io.IOException if bad things happen during the read
     */
    private String[] parseLine(String nextLine) throws IOException {
        List<String> tokensOnThisLine = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        do {
            if (inQuotes) {
                // continuing a quoted section, reappend newline
                sb.append("\n");
                nextLine = getNextLine();
                if (nextLine == null) {
                    break;
                }
            }
            for (int i = 0; i < nextLine.length(); i++) {
                char c = nextLine.charAt(i);
                if (c == quotechar) {
                    // this gets complex... the quote may end a quoted block, or
                    // escape another quote.
                    // do a 1-char lookahead:
                    if (inQuotes // we are in quotes, therefore there can be
                            // escaped quotes in here.
                            && nextLine.length() > i + 1 // there is indeed
                            // another character
                            // to check.
                            && nextLine.charAt(i + 1) == quotechar) { // ..and
                        // that
                        // char.
                        // is a
                        // quote
                        // also.
                        // we have two quote chars in a row == one quote char,
                        // so consume them both and
                        // put one on the token. we do *not* exit the quoted
                        // text.
                        sb.append(nextLine.charAt(i + 1));
                        i++;
                    } else {
                        inQuotes = !inQuotes;
                        // the tricky case of an embedded quote in the middle:
                        // a,bc"d"ef,g
                        if (i > 2 // not on the begining of the line
                                && nextLine.charAt(i - 1) != separator // not
                                // at
                                // the
                                // begining
                                // of
                                // an
                                // escape
                                // sequence
                                && nextLine.length() > i + 1
                                && nextLine.charAt(i + 1) != separator // not
                            // at
                            // the
                            // end
                            // of
                            // an
                            // escape
                            // sequence
                        ) {
                            sb.append(c);
                        }
                    }
                } else if (c == separator && !inQuotes) {
                    tokensOnThisLine.add(sb.toString());
                    sb = new StringBuilder(); // start work on next token
                } else {
                    sb.append(c);
                }
            }
        }
        while (inQuotes);
        tokensOnThisLine.add(sb.toString());
        return tokensOnThisLine.toArray(new String[0]);

    }

    /**
     * Closes the underlying reader.
     *
     * @throws java.io.IOException if the close fails
     */
    public void close() throws IOException {
        br.close();
    }
}
