package com.github.bingoohuang.utils.xml;

import org.junit.Test;

import static com.github.bingoohuang.utils.xml.Xmls.MarshalOption.PrettyFormat;
import static com.github.bingoohuang.utils.xml.Xmls.MarshalOption.WithXmlDeclaration;
import static com.google.common.truth.Truth.assertThat;

public class XmlsTest {
    @Test
    public void getCarAsXml() {
        String registration = "abc123";
        String brand = "沃尔沃";
        String description = "Sedan<xx.yy@gmail.com>";

        Car car = new Car(registration, brand, description);
        String xml = Xmls.marshal(car);
        assertThat(xml).isEqualTo("<car registration=\"abc123\"><brand>沃尔沃</brand><description><![CDATA[Sedan<xx.yy@gmail.com>]]></description><tag></tag></car>");

        xml = Xmls.marshal(car, PrettyFormat);
        assertThat(xml).isEqualTo("<car registration=\"abc123\">\n" +
                "  <brand>沃尔沃</brand>\n" +
                "  <description><![CDATA[Sedan<xx.yy@gmail.com>]]></description>\n" +
                "  <tag></tag>\n" +
                "</car>");

        xml = Xmls.marshal(car, WithXmlDeclaration);
        assertThat(xml).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?><car registration=\"abc123\"><brand>沃尔沃</brand><description><![CDATA[Sedan<xx.yy@gmail.com>]]></description><tag></tag></car>");

        xml = Xmls.marshal(car, PrettyFormat, WithXmlDeclaration);
        assertThat(xml).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<car registration=\"abc123\">\n" +
                "  <brand>沃尔沃</brand>\n" +
                "  <description><![CDATA[Sedan<xx.yy@gmail.com>]]></description>\n" +
                "  <tag></tag>\n" +
                "</car>");
    }
}
