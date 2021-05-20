package ru.bia.voip.phone.util;

import org.apache.logging.log4j.Logger;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.xml.transform.StringResult;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public final class XMLUtil {
    public static void printXml(Logger log, JAXBElement element, Jaxb2Marshaller marshaller) {
        StringWriter sw = new StringWriter();
        Result result = new StreamResult(sw);
        marshaller.marshal(element, result);
        String xmlString = sw.toString();
        log.info(xmlString);
    }

    public static void printSoapExceptionXml(Logger log, SoapFaultClientException e) {
        Result result = new StringResult();
        try {
            TransformerFactory.newInstance().newTransformer().transform(e.getSoapFault().getSource(), result);
        } catch (TransformerException ex) {
        }
        log.info(result.toString());
    }
}
