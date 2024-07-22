package mjw.woodstox;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamException;
import java.io.File;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 7/22/2024, 8:02 PM
 */
public class Demo {
    public static void main(String[] args) throws XMLStreamException {
        XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory2.newFactory();
        XMLStreamReader2 reader = factory.createXMLStreamReader(new File(""));



        long start = System.currentTimeMillis();
        WSXmlParser parser = new WSXmlParser();
        String fileName = "/Users/kris/Downloads/dbdump_artistalbumtrack.0.290905586176.xml";
        int result = parser.parse(fileName, WSXmlInputFactory.getInputFactoryConfiguredForXmlConformance());
        long end = System.currentTimeMillis();
        System.out.println("Finished within: " + (end - start) + " processing: " + result + " results.");
    }
}
