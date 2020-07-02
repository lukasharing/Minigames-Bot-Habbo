package extensions.fastmap.FurniData;

import extensions.fastmap.FastMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;

public class FurniDataManager {

    HashMap<Integer, FurniData> furnisdata;

    public FurniDataManager(FastMap parent) {
        furnisdata = new HashMap<Integer, FurniData>();
        try{
            File xml = new File(FastMap.getFolderPath() + "/furnidata.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xml);

            NodeList roomitems = doc.getDocumentElement().getChildNodes().item(0).getChildNodes();
            for(int i = 0; i < roomitems.getLength(); ++i){
                Element furni = (Element)roomitems.item(i);
                FurniData new_furni = new FurniData(
                        furni.getElementsByTagName("name").item(0).getTextContent(),
                        furni.getElementsByTagName("category").item(0).getTextContent(),
                        furni.getElementsByTagName("description").item(0).getTextContent(),
                        Integer.parseInt(furni.getElementsByTagName("xdim").item(0).getTextContent()),
                        Integer.parseInt(furni.getElementsByTagName("ydim").item(0).getTextContent())
                );
                new_furni.setLay(furni.getElementsByTagName("canlayon").item(0).getTextContent().equals("1") ? true : false);
                new_furni.setSit(furni.getElementsByTagName("cansiton").item(0).getTextContent().equals("1") ? true : false);
                new_furni.setStand(furni.getElementsByTagName("canstandon").item(0).getTextContent().equals("1") ? true : false);


                furnisdata.put(Integer.parseInt(furni.getAttribute("id")), new_furni);
            }
            //NodeList wallitems = doc.getDocumentElement().getChildNodes().item(1).getChildNodes();

        }catch (Exception ex){}
    }

    public FurniData get(int idx){ return furnisdata.get(idx); };

}
