package com.empuje.soap.service;

import com.empuje.soap.dto.OrganizationResponse;
import com.empuje.soap.dto.OrganizationsResponse;
import com.empuje.soap.dto.PresidentResponse;
import com.empuje.soap.dto.PresidentsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SoapClientService {

    private static final String WSDL_URL = "https://soap-app-latest.onrender.com/?wsdl";
    private static final String SOAP_ACTION_PRESIDENT = "list_presidents";
    private static final String SOAP_ACTION_ORGANIZATION = "list_associations";

    public PresidentsResponse getPresidentsByOrganizationIds(List<String> organizationIds) {
        log.info("Consultando presidentes para organizaciones: {}", organizationIds);

        try {
            String soapRequest = buildPresidentsSoapRequest(organizationIds);
            String soapResponse = sendSoapRequest(soapRequest, SOAP_ACTION_PRESIDENT);

            List<PresidentResponse> presidents = parsePresidentsResponse(soapResponse);

            return PresidentsResponse.builder()
                .presidents(presidents)
                .soapRequest(formatSoapRequest(soapRequest))
                .soapResponse(formatSoapResponse(soapResponse))
                .build();

        } catch (Exception e) {
            log.error("Error consultando presidentes: {}", e.getMessage());
            throw new RuntimeException("Error en consulta SOAP de presidentes: " + e.getMessage());
        }
    }

    public OrganizationsResponse getOrganizationsByIds(List<String> organizationIds) {
        log.info("Consultando organizaciones: {}", organizationIds);

        try {
            String soapRequest = buildOrganizationsSoapRequest(organizationIds);
            String soapResponse = sendSoapRequest(soapRequest, SOAP_ACTION_ORGANIZATION);

            List<OrganizationResponse> organizations = parseOrganizationsResponse(soapResponse);

            return OrganizationsResponse.builder()
                .organizations(organizations)
                .soapRequest(formatSoapRequest(soapRequest))
                .soapResponse(formatSoapResponse(soapResponse))
                .build();

        } catch (Exception e) {
            log.error("Error consultando organizaciones: {}", e.getMessage());
            throw new RuntimeException("Error en consulta SOAP de organizaciones: " + e.getMessage());
        }
    }

    private String buildPresidentsSoapRequest(List<String> organizationIds) {
        StringBuilder idsString = new StringBuilder();
        for (String id : organizationIds) {
            idsString.append("<tns:string>").append(id).append("</tns:string>\n        ");
        }

        return """
            <?xml version="1.0" encoding="utf-8"?>
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:auth="auth.headers" xmlns:tns="soap.backend">
              <soapenv:Header>
                <auth:Auth>
                  <auth:Grupo>GrupoA-TM</auth:Grupo>
                  <auth:Clave>clave-tm-a</auth:Clave>
                </auth:Auth>
              </soapenv:Header>
              <soapenv:Body>
                <tns:list_presidents>
                  <tns:org_ids>
                    %s
                  </tns:org_ids>
                </tns:list_presidents>
              </soapenv:Body>
            </soapenv:Envelope>
            """.formatted(idsString.toString().trim());
    }

    private String buildOrganizationsSoapRequest(List<String> organizationIds) {
        StringBuilder idsString = new StringBuilder();
        for (String id : organizationIds) {
            idsString.append("<tns:string>").append(id).append("</tns:string>\n        ");
        }

        return """
            <?xml version="1.0" encoding="utf-8"?>
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:auth="auth.headers" xmlns:tns="soap.backend">
              <soapenv:Header>
                <auth:Auth>
                  <auth:Grupo>GrupoA-TM</auth:Grupo>
                  <auth:Clave>clave-tm-a</auth:Clave>
                </auth:Auth>
              </soapenv:Header>
              <soapenv:Body>
                <tns:list_associations>
                  <tns:org_ids>
                    %s
                  </tns:org_ids>
                </tns:list_associations>
              </soapenv:Body>
            </soapenv:Envelope>
            """.formatted(idsString.toString().trim());
    }

    private String sendSoapRequest(String soapRequest, String soapAction) throws Exception {
        URL url = new URL(WSDL_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", soapAction);
        connection.setDoOutput(true);

        connection.getOutputStream().write(soapRequest.getBytes());

        StringBuilder response = new StringBuilder();
        java.util.Scanner scanner = new java.util.Scanner(connection.getInputStream());
        while (scanner.hasNextLine()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        return response.toString();
    }

    private List<PresidentResponse> parsePresidentsResponse(String soapResponse) throws Exception {
        List<PresidentResponse> presidents = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new java.io.ByteArrayInputStream(soapResponse.getBytes()));

        NodeList presidentNodes = document.getElementsByTagNameNS("*", "PresidentType");

        for (int i = 0; i < presidentNodes.getLength(); i++) {
            Node presidentNode = presidentNodes.item(i);

            if (presidentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element presidentElement = (Element) presidentNode;

                PresidentResponse president = PresidentResponse.builder()
                    .id(Long.valueOf(getElementValue(presidentElement, "id")))
                    .name(getElementValue(presidentElement, "name"))
                    .email("") // No viene en el response
                    .phone(getElementValue(presidentElement, "phone"))
                    .organizationId(getElementValue(presidentElement, "organization_id"))
                    .organizationName("") // No viene en el response de presidentes
                    .build();

                presidents.add(president);
            }
        }

        return presidents;
    }

    private List<OrganizationResponse> parseOrganizationsResponse(String soapResponse) throws Exception {
        List<OrganizationResponse> organizations = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new java.io.ByteArrayInputStream(soapResponse.getBytes()));

        NodeList organizationNodes = document.getElementsByTagNameNS("*", "OrganizationType");

        for (int i = 0; i < organizationNodes.getLength(); i++) {
            Node organizationNode = organizationNodes.item(i);

            if (organizationNode.getNodeType() == Node.ELEMENT_NODE) {
                Element organizationElement = (Element) organizationNode;

                OrganizationResponse organization = OrganizationResponse.builder()
                    .id(getElementValue(organizationElement, "id"))
                    .name(getElementValue(organizationElement, "name"))
                    .description("") // No viene en el response
                    .address(getElementValue(organizationElement, "address"))
                    .phone(getElementValue(organizationElement, "phone"))
                    .email("") // No viene en el response
                    .website("") // No viene en el response
                    .build();

                organizations.add(organization);
            }
        }

        return organizations;
    }

    private String getElementValue(Element parent, String tagName) {
        try {
            NodeList nodeList = parent.getElementsByTagName("s0:" + tagName);
            if (nodeList != null && nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node != null) {
                    return node.getTextContent();
                }
            }

            nodeList = parent.getElementsByTagNameNS("*", tagName);
            if (nodeList != null && nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node != null) {
                    return node.getTextContent();
                }
            }

            nodeList = parent.getElementsByTagName(tagName);
            if (nodeList != null && nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node != null) {
                    return node.getTextContent();
                }
            }
        } catch (Exception e) {
            log.warn("Error getting element value for tag: {}", tagName);
        }
        return "";
    }

    private String formatSoapRequest(String soapRequest) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(soapRequest.getBytes()));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));

            return writer.toString();
        } catch (Exception e) {
            return soapRequest;
        }
    }

    private String formatSoapResponse(String soapResponse) {
        return formatSoapRequest(soapResponse);
    }
}
