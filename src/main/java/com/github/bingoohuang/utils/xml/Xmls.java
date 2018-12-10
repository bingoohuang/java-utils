package com.github.bingoohuang.utils.xml;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.val;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.EnumSet;

public class Xmls {
    public enum MarshalOption {
        PrettyFormat, WithXmlDeclaration
    }

    public static String marshal(Object bean, MarshalOption... marshalOptions) {
        val enumSet = Sets.newEnumSet(Lists.newArrayList(marshalOptions), MarshalOption.class);
        return marshal(bean, enumSet, bean.getClass());
    }

    @SneakyThrows
    public static String marshal(Object bean, EnumSet<MarshalOption> marshalOptions, Class... types) {
        val sw = new StringWriter();

        val carContext = JAXBContext.newInstance(types);
        val marshaller = carContext.createMarshaller();
        val xof = XMLOutputFactory.newInstance();
        val streamWriter = xof.createXMLStreamWriter(sw);
        val handler = new PrettyPrintHandler(streamWriter, marshalOptions);
        val prettyPrintWriter = (XMLStreamWriter) Proxy.newProxyInstance(
                XMLStreamWriter.class.getClassLoader(), new Class[]{XMLStreamWriter.class}, handler);
        marshaller.marshal(bean, prettyPrintWriter);
        String xml = sw.toString();
        xml = xml.replaceAll(" xmlns:xsi=\"http://www\\.w3\\.org/2001/XMLSchema-instance\" xsi:nil=\"true\"", "");
        return xml;
    }

    public static <T> T unmarshal(String xml, Class<T> beanClass) {
        val reader = new StringReader(xml);
        return JAXB.unmarshal(reader, beanClass);
    }

    @SneakyThrows
    public static String prettyXml(String xml) {
        val omitXmlDeclaration = !xml.startsWith("<?xml");

        val transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 2);
        val transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclaration ? "yes" : "no");
        val result = new StreamResult(new StringWriter());
        val source = new DOMSource(parseXmlFile(xml));
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    @SneakyThrows
    public static Document parseXmlFile(String xml) {
        val dbf = DocumentBuilderFactory.newInstance();
        val db = dbf.newDocumentBuilder();
        val is = new InputSource(new StringReader(xml));
        return db.parse(is);
    }
}
