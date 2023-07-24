package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        if (args.length < 2) {
            throw new RuntimeException("Параметров слишком мало");
        }

        String[] a = Arrays.copyOfRange(args, 0, args.length - 1);
        String dateArg = args[args.length - 1];

        LocalDate date = LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(dateArg));
        Set<String> objectIds = Arrays.stream(a).collect(Collectors.toSet());

        File fileAddrObj = new File("addr_obj.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        NodeList addressObjectsChilds = parseNoodes(fileAddrObj, dbf);
        printAddresses(addressObjectsChilds, objectIds, date);
    }

    private static NodeList parseNoodes(File file, DocumentBuilderFactory dbf) {
        Document docObj = null;
        try {
            docObj = dbf.newDocumentBuilder().parse(file);
        } catch (Exception e) {
            System.out.println("Open parsing error");
        }
        Node node = docObj.getFirstChild();
        NodeList childNodes = node.getChildNodes();
        return childNodes;
    }
    private static void printAddresses(NodeList addressObjectsChilds, Set<String> objectIds, LocalDate date) {
        for (int i = 0; i < addressObjectsChilds.getLength(); i++) {
            if (addressObjectsChilds.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String objectID = addressObjectsChilds.item(i).getAttributes().getNamedItem("OBJECTID").getTextContent();
            String startDateAttr = addressObjectsChilds.item(i).getAttributes().getNamedItem("STARTDATE").getTextContent();
            String endDateAttr = addressObjectsChilds.item(i).getAttributes().getNamedItem("ENDDATE").getTextContent();

            LocalDate startDate = LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(startDateAttr));
            LocalDate endDate = LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(endDateAttr));
            if (date.isAfter(startDate) && date.isBefore(endDate) && objectIds.contains(objectID)) {
                String objetTypeName = addressObjectsChilds.item(i).getAttributes().getNamedItem("TYPENAME").getTextContent();
                String objectName = addressObjectsChilds.item(i).getAttributes().getNamedItem("NAME").getTextContent();
                System.out.println(objectID + ": " + objetTypeName + " " + objectName);

            }
        }
    }
}
