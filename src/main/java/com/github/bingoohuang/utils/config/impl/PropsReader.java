package com.github.bingoohuang.utils.config.impl;

import com.github.bingoohuang.utils.config.ex.ConfigException;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropsReader extends LineNumberReader {
    static final String COMMENT_CHARS = "#!";

    /**
     * The list of possible key/value separators
     */
    private static final char[] SEPARATORS = new char[]{'=', ':'};
    /**
     * The regular expression to parse the key and the value of a property.
     */
    private static final Pattern PROPERTY_PATTERN = Pattern
            .compile("(([\\S&&[^\\\\" + new String(SEPARATORS)
                    + "]]|\\\\.)*)(\\s*(\\s+|[" + new String(SEPARATORS)
                    + "])\\s*)(.*)");
    /**
     * Constant for the radix of hex numbers.
     */
    private static final int HEX_RADIX = 16;
    /**
     * Constant for the length of a unicode literal.
     */
    private static final int UNICODE_LEN = 4;
    /**
     * Constant for the index of the group for the key.
     */
    private static final int IDX_KEY = 1;

    /**
     * Constant for the index of the group for the value.
     */
    private static final int IDX_VALUE = 5;

    /**
     * Constant for the index of the group for the separator.
     */
    private static final int IDX_SEPARATOR = 3;

    /**
     * Stores the name of the last read property.
     */
    private String propertyName;

    /**
     * Stores the value of the last read property.
     */
    private String propertyValue;

    public PropsReader(Reader reader) {
        super(reader);
    }

    private static boolean checkCombineLines(String line) {
        return countTrailingBS(line) % 2 != 0;
    }

    private static String[] doParseProperty(String line) {
        Matcher matcher = PROPERTY_PATTERN.matcher(line);

        String[] result = {"", "", ""};

        if (matcher.matches()) {
            result[0] = matcher.group(IDX_KEY).trim();
            result[1] = matcher.group(IDX_VALUE).trim();
            result[2] = matcher.group(IDX_SEPARATOR);
        }

        return result;
    }

    static boolean isCommentLine(String line) {
        String s = line.trim();
        // blanc lines are also treated as comment lines
        return s.length() < 1 || COMMENT_CHARS.indexOf(s.charAt(0)) >= 0;
    }

    private static int countTrailingBS(String line) {
        int bsCount = 0;
        for (int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; idx--)
            bsCount++;

        return bsCount;
    }

    protected static String unescapeJava(String str, char delimiter) {
        if (str == null) return null;

        int sz = str.length();
        StringBuilder out = new StringBuilder(sz);
        StringBuilder unicode = new StringBuilder(UNICODE_LEN);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == UNICODE_LEN) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), HEX_RADIX);
                        out.append((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new ConfigException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }

            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;

                if (ch == '\\') out.append('\\');
                else if (ch == '\'') out.append('\'');
                else if (ch == '\"') out.append('"');
                else if (ch == 'r') out.append('\r');
                else if (ch == 'f') out.append('\f');
                else if (ch == 't') out.append('\t');
                else if (ch == 'n') out.append('\n');
                else if (ch == 'b') out.append('\b');
                else if (ch == delimiter) out.append('\\').append(delimiter);
                else if (ch == 'u') inUnicode = true; // uh-oh, we're in unicode country....
                else out.append(ch);

                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.append(ch);
        }

        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.append('\\');
        }

        return out.toString();
    }

    public String readProperty() throws IOException {
        StringBuilder buffer = new StringBuilder();

        while (true) {
            String line = readLine();
            if (line == null) return null; // EOF

            if (isCommentLine(line)) continue;

            line = line.trim();

            if (checkCombineLines(line)) {
                line = line.substring(0, line.length() - 1);
                buffer.append(line);
            } else {
                buffer.append(line);
                break;
            }
        }

        return buffer.toString();
    }

    public boolean nextProperty() throws IOException {
        String line = readProperty();

        if (line == null) return false; // EOF

        // parse the line
        parseProperty(line);
        return true;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    protected void parseProperty(String line) {
        String[] property = doParseProperty(line);
        initPropertyName(property[0]);
        initPropertyValue(property[1]);
    }

    protected void initPropertyName(String name) {
        propertyName = StringEscapeUtils.unescapeJava(name);
    }

    protected void initPropertyValue(String value) {
        propertyValue = unescapeJava(value, ',');
    }

}
