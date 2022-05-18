import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

        //Task1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        //Task2
        List<Employee> list1 = parseXML("data.xml");
        String json1 = listToJson(list1);
        writeString(json1, "data2.json");

        //Task3
        File file = new File("new_data.json");
        List<Employee> list2 = jsonToList(file);
        for (Employee employee: list2) {
            System.out.println(employee);
        }
    }

    static List<Employee> jsonToList(File jsonFile) {

        JSONParser parser = new JSONParser();
        List<Employee> list = new ArrayList<>();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(jsonFile));
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object object : jsonArray) {
                list.add(gson.fromJson(String.valueOf(object), Employee.class));
            }
            return list;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void writeString(String json, String jsonFileName) {
        try (FileWriter file = new FileWriter(jsonFileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(list, listType);
    }

    static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        long id = 0;
        String firstName = null;
        String lastName = null;
        String country = null;
        int age = 0;
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory. newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse( new File(fileName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node1 = nodeList.item(i);
            if (Node.ELEMENT_NODE == node1.getNodeType()) {
                NodeList nodeList1 = node1.getChildNodes();
                for (int j = 0; j < nodeList1.getLength(); j++) {
                    Node node2 = nodeList1.item(j);
                    if (Node.ELEMENT_NODE == node2.getNodeType()) {
                        Element element = (Element) node2;
                        switch (element.getNodeName()) {
                            case "id":
                                id = Long.parseLong(element.getTextContent());
                                break;
                            case "firstName":
                                firstName = element.getTextContent();
                                break;
                            case "lastName":
                                lastName= element.getTextContent();
                                break;
                            case "country":
                                country= element.getTextContent();
                                break;
                            case "age":
                                age= Integer.parseInt(element.getTextContent());
                                break;
                        }
                    }
                }
                list.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return list;
    }
}