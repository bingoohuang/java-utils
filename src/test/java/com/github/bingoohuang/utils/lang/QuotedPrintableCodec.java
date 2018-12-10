package com.github.bingoohuang.utils.lang;


import com.github.bingoohuang.utils.codec.Bytes;
import com.google.common.base.Charsets;
import lombok.SneakyThrows;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.util.BitSet;

public class QuotedPrintableCodec {
    /**
     * BitSet of printable characters as defined in RFC 1521.
     */
    private static final BitSet PRINTABLE_CHARS = new BitSet(256);
    private static final byte TAB = 9;
    private static final byte SPACE = 32;


    // Static initializer for printable chars collection
    static {
        // alpha characters
        for (int i = 33; i <= 60; i++) {
            PRINTABLE_CHARS.set(i);
        }
        for (int i = 62; i <= 126; i++) {
            PRINTABLE_CHARS.set(i);
        }
        PRINTABLE_CHARS.set(TAB);
        PRINTABLE_CHARS.set(SPACE);
    }


    /**
     * Return the byte at position <code>index</code> of the byte array and
     * make sure it is unsigned.
     *
     * @param index position in the array
     * @param bytes the byte array
     * @return the unsigned octet at position <code>index</code> from the array
     */
    public static int getUnsignedOctet(final byte[] bytes, final int index) {
        return getUnsignedOctet(bytes[index]);
    }

    public static int getUnsignedOctet(final byte by) {
        int b = by;
        if (b < 0) {
            b = 256 + b;
        }
        return b;
    }


    /**
     * Encodes an array of bytes into an array of quoted-printable 7-bit characters. Unsafe characters are escaped.
     * <p>
     * Depending on the selection of the {@code strict} parameter, this function either implements the full ruleset
     * or only a subset of quoted-printable encoding specification (rule #1 and rule #2) as defined in
     * RFC 1521 and is suitable for encoding binary data and unformatted text.
     *
     * @param printable bitset of characters deemed quoted-printable
     * @param bytes     array of bytes to be encoded
     * @return array of bytes containing quoted-printable data
     * @since 1.10
     */
    @SneakyThrows
    public static final String encodeQuotedPrintable(BitSet printable, final byte[] bytes) {
        if (bytes == null) return null;
        if (printable == null) printable = PRINTABLE_CHARS;
        val buffer = new ByteArrayOutputStream();

        for (int width, i = 0; i < bytes.length; i += width) {
            final byte c = bytes[i];
            width = utf8Width(bytes, i);
            if (width > 1) {
                byte[] bytes1 = new byte[width];
                for (int j = 0; j < width; ++j) {
                    bytes1[j] = bytes[i + j];
                }
                buffer.write(bytes1);
                continue;
            }


            int b = getUnsignedOctet(c);
            switch (b) {
                case 0x07:
                    buffer.write("\\a".getBytes(Charsets.UTF_8));
                    continue;
                case '\b':
                    buffer.write("\\b".getBytes(Charsets.UTF_8));
                    continue;
                case '\f':
                    buffer.write("\\f".getBytes(Charsets.UTF_8));
                    continue;
                case '\n':
                    buffer.write("\\n".getBytes(Charsets.UTF_8));
                    continue;
                case '\r':
                    buffer.write("\\r".getBytes(Charsets.UTF_8));
                    continue;
                case '\t':
                    buffer.write("\\t".getBytes(Charsets.UTF_8));
                    continue;
                case 0x0b:
                    buffer.write("\\v".getBytes(Charsets.UTF_8));
                    continue;
            }

            if (printable.get(b)) {
                buffer.write(b);
            } else {
                // http://facweb.cs.depaul.edu/sjost/it212/documents/ascii-npr.htm
                buffer.write(String.format("%02x ", c).getBytes(Charsets.UTF_8));
            }
        }

        return Bytes.string(buffer.toByteArray());
    }

    /*
    https://en.wikipedia.org/wiki/UTF-8
    bytes	bits	First	Last		Byte 1		Byte 2		Byte 3		Byte 4
    1		7		U+0000	U+007F		0xxxxxxx
    2		11		U+0080	U+07FF		110xxxxx	10xxxxxx
    3		16		U+0800	U+FFFF		1110xxxx	10xxxxxx	10xxxxxx
    4		21		U+10000	U+10FFFF	11110xxx	10xxxxxx	10xxxxxx	10xxxxxx
    */
    public static final int utf8Width(final byte[] pText, int index) {
        for (int i = index; i < pText.length; i++) {
            int expectedLength;
            if ((pText[i] & 0b10000000) == 0b00000000) {
                expectedLength = 1;
            } else if ((pText[i] & 0b11100000) == 0b11000000) {
                expectedLength = 2;
            } else if ((pText[i] & 0b11110000) == 0b11100000) {
                expectedLength = 3;
            } else if ((pText[i] & 0b11111000) == 0b11110000) {
                expectedLength = 4;
            } else {
                return 1;
            }

            int tempLen = expectedLength;
            while (--tempLen > 0) {
                if (++i >= pText.length) {
                    return 1;
                }
                if ((pText[i] & 0b11000000) != 0b10000000) {
                    return 1;
                }
            }
            return expectedLength;
        }

        return 1;
    }

    public static final boolean isUTF8(final byte[] pText) {
        int expectedLength;

        for (int i = 0; i < pText.length; i++) {
            if ((pText[i] & 0b10000000) == 0b00000000) {
                expectedLength = 1;
            } else if ((pText[i] & 0b11100000) == 0b11000000) {
                expectedLength = 2;
            } else if ((pText[i] & 0b11110000) == 0b11100000) {
                expectedLength = 3;
            } else if ((pText[i] & 0b11111000) == 0b11110000) {
                expectedLength = 4;
            } else if ((pText[i] & 0b11111100) == 0b11111000) {
                expectedLength = 5;
            } else if ((pText[i] & 0b11111110) == 0b11111100) {
                expectedLength = 6;
            } else {
                return false;
            }

            while (--expectedLength > 0) {
                if (++i >= pText.length) {
                    return false;
                }
                if ((pText[i] & 0b11000000) != 0b10000000) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Encodes an array of bytes into an array of quoted-printable 7-bit characters. Unsafe characters are escaped.
     * <p>
     * Depending on the selection of the {@code strict} parameter, this function either implements the full ruleset
     * or only a subset of quoted-printable encoding specification (rule #1 and rule #2) as defined in
     * RFC 1521 and is suitable for encoding binary data and unformatted text.
     *
     * @param bytes array of bytes to be encoded
     * @return array of bytes containing quoted-printable data
     */

    public String encode(final byte[] bytes) {
        return encodeQuotedPrintable(PRINTABLE_CHARS, bytes);
    }
}