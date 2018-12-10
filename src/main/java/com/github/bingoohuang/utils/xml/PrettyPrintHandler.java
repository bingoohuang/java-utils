package com.github.bingoohuang.utils.xml;

import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.github.bingoohuang.utils.xml.Xmls.MarshalOption.PrettyFormat;
import static com.github.bingoohuang.utils.xml.Xmls.MarshalOption.WithXmlDeclaration;
import static org.apache.commons.lang3.StringUtils.repeat;

public class PrettyPrintHandler implements InvocationHandler {
    private final XMLStreamWriter target;
    private final boolean prettyFormat;
    private final boolean withXmlDeclaration;
    private int depth = 0;
    private final Map<Integer, Boolean> hasChildElement = new HashMap<Integer, Boolean>();
    private static final String INDENT_CHAR = "  ";
    private static final String LINEFEED_CHAR = "\n";
    private static final Pattern XML_CHARS = Pattern.compile("[&<>]");

    public PrettyPrintHandler(XMLStreamWriter target, EnumSet<Xmls.MarshalOption> marshalOptions) {
        this.target = target;
        this.prettyFormat = marshalOptions.contains(PrettyFormat);
        this.withXmlDeclaration = marshalOptions.contains(WithXmlDeclaration);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if ("writeStartDocument".equals(methodName)) {
            if (withXmlDeclaration) target.writeStartDocument("UTF-8", "1.0");
            return null;
        }

        if ("writeCharacters".equals(methodName) && args[0] instanceof String) {
            String text = (String) args[0];
            boolean useCData = XML_CHARS.matcher(text).find();
            if (useCData) target.writeCData(text);
            else target.writeCharacters(text);
            return null;
        }

        // Needs to be BEFORE the actual event, so that for instance the
        // sequence writeStartElem, writeAttr, writeStartElem, writeEndElem, writeEndElem
        // is correctly handled
        if ("writeStartElement".equals(methodName) && prettyFormat) {
            // update state of parent node
            if (depth > 0) hasChildElement.put(depth - 1, true);
            // reset state of current node
            hasChildElement.put(depth, false);
            // indent for current depth
            if (depth > 0 || withXmlDeclaration) target.writeCharacters(LINEFEED_CHAR);
            target.writeCharacters(repeat(INDENT_CHAR, depth));
            depth++;
        } else if ("writeEndElement".equals(methodName) && prettyFormat) {
            depth--;
            if (hasChildElement.get(depth) == true) {
                target.writeCharacters(LINEFEED_CHAR);
                target.writeCharacters(repeat(INDENT_CHAR, depth));
            }
        }

        return method.invoke(target, args);
    }

}