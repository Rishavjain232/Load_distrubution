package org.app.iiitb.peorig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLReaderDom {
	
	List<DataCenterDetails> getDataCenterDetailsXML(String filePath) {
//		String filePath = "datacenters.xml";
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        List<DataCenterDetails> dcdList=null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("DataCenterDetails");
            //now XML is loaded as Document in memory, lets convert it to Object List
            dcdList = new ArrayList<DataCenterDetails>();
            for (int i = 0; i < nodeList.getLength(); i++) {
            	dcdList.add(getDataCenterDetails(nodeList.item(i)));
            }
            //lets print Employee list information
//            for (DataCenterDetails dcd : dcdList) {
//                System.out.println(dcd.toString());
//            }
        } catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
        return dcdList;
	}
	
	private static DataCenterDetails getDataCenterDetails(Node node) {
	        //XMLReaderDOM domReader = new XMLReaderDOM();
	        DataCenterDetails dcd = new DataCenterDetails();
	        System.out.println(1);
	        if (node.getNodeType() == Node.ELEMENT_NODE) {
	            Element element = (Element) node;
	            dcd.setId(getTagValue("id", element));
	            dcd.setBroker(getTagValue("broker", element));
	            dcd.setCapacity(getItemDetails((Element)element.getElementsByTagName("load").item(0)));
//	            dcd.setCapacity(Double.parseDouble(getTagValue("capacity", element)));
	            
//	            dcd.setCapacity(new double[] {0.41161876,  0.1480243 ,  0.25835126,  0.00251222,  0.02036907,
//	                    0.52981629,  0.87480306,  0.37670089,  0.60606876,  0.05755281,
//	                    0.0300598 ,  0.67381285,  0.26877157,  0.19041148,  0.12909236,
//	                    0.07508628,  0.41475751,  0.0227075 ,  0.42370103,  0.93380347,
//	                    0.91016571,  0.70483874,  0.45260352,  0.93360724,  0.7146286 ,
//	                    0.75730088,  0.13702511,  0.96952864,  0.63670819,  0.53910302,
//	                    0.28046749,  0.63196298,  0.29929601,  0.4292205 ,  0.21917406,
//	                    0.98393384,  0.69529707,  0.07141942,  0.67022452,  0.38750184,
//	                    0.4404591 ,  0.81865247,  0.12293219,  0.51094363,  0.55142591,
//	                    0.77328433,  0.80575662,  0.04589499,  0.13328334,  0.69050627,
//	                    0.21191362,  0.20135541,  0.94698278,  0.18493704,  0.95890287,
//	                    0.40424762,  0.91417793,  0.18788607,  0.74059745,  0.32043543});
	            dcd.setGreenEnergy(Double.parseDouble(getTagValue("greenEnergy", element)));
	            
	        }

	        return dcd;
	    }
	 
	private static double[] getItemDetails(Element ele) {
		double[] cap=new double[60];
		NodeList nodelist = ele.getElementsByTagName("item");
//		Node node = nodelist.item(0);
//		System.out.println("------------"+nodelist.getLength()+" "+node.getTextContent());
//		System.out.println("------------"+nodelist.item(1).getNodeName());
		for(int i=0;i<nodelist.getLength();i++) {
			cap[i] = Double.parseDouble(nodelist.item(i).getTextContent());
		}
		
		return cap;
	}
	
	 private static String getTagValue(String tag, Element element) {
	        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
	        Node node = (Node) nodeList.item(0);
	        return node.getNodeValue();
	    }

}
