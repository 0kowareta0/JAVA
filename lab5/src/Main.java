import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
//import org.json.simple.*;
//import org.json.simple.parser.*;

interface WashingMachineDecorator extends WashingMachine {
}

class EnergyEfficiencyDecorator implements WashingMachineDecorator {
    private WashingMachine baseMachine;
    private String energyEfficiency;

    public EnergyEfficiencyDecorator(WashingMachine baseMachine, String energyEfficiency) {
        this.baseMachine = baseMachine;
        this.energyEfficiency = energyEfficiency;
    }

    @Override
    public int getId() {
        return baseMachine.getId();
    }

    @Override
    public String getType() {
        return baseMachine.getType();
    }

    @Override
    public String getModel() {
        return baseMachine.getModel();
    }

    public String getEnergyEfficiency() {
        return energyEfficiency;
    }

    @Override
    public String toString() {
        return baseMachine.toString() + ", Energy Efficiency: " + energyEfficiency;
    }
}

interface WashingMachineBuilder {
    WashingMachineBuilder setId(int id);

    WashingMachineBuilder setType(String type);

    WashingMachineBuilder setModel(String model);

    WashingMachine build();
}

class WashingMachineConcreteBuilder implements WashingMachineBuilder {
    private int id;
    private String type;
    private String model;

    @Override
    public WashingMachineBuilder setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public WashingMachineBuilder setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public WashingMachineBuilder setModel(String model) {
        this.model = model;
        return this;
    }

    @Override
    public WashingMachine build() {
        return new WashingMachineModel(id, type, model);
    }
}

class WashingMachineModel extends WashingMachine {
    public WashingMachineModel(int id, String type, String model) {
        super(id, type, model);
    }
}

class WashingMachineStorage extends AbstractStorage {
}

class FileHandler {
    public static void sortData(List<WashingMachine> machines, String sortBy) {
        Collections.sort(machines, new WashingMachineComparator(sortBy));
    }

    public static void writeToXML(String fileName, List<WashingMachine> machines) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element rootElement = document.createElement("WashingMachines");
            document.appendChild(rootElement);

            for (WashingMachine machine : machines) {
                Element machineElement = document.createElement("Machine");
                rootElement.appendChild(machineElement);

                Element idElement = document.createElement("Id");
                idElement.appendChild(document.createTextNode(String.valueOf(machine.getId())));
                machineElement.appendChild(idElement);

                Element typeElement = document.createElement("Type");
                typeElement.appendChild(document.createTextNode(machine.getType()));
                machineElement.appendChild(typeElement);

                Element modelElement = document.createElement("Model");
                modelElement.appendChild(document.createTextNode(machine.getModel()));
                machineElement.appendChild(modelElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);

            System.out.println("Данные успешно записаны в XML файл: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<WashingMachine> readFromXML(String fileName) {
        List<WashingMachine> machines = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));

            NodeList machineNodes = document.getElementsByTagName("Machine");
            for (int i = 0; i < machineNodes.getLength(); i++) {
                Node machineNode = machineNodes.item(i);

                if (machineNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element machineElement = (Element) machineNode;

                    int id = Integer.parseInt(machineElement.getElementsByTagName("Id").item(0).getTextContent());
                    String type = machineElement.getElementsByTagName("Type").item(0).getTextContent();
                    String model = machineElement.getElementsByTagName("Model").item(0).getTextContent();

                    machines.add(new WashingMachineModel(id, type, model));
                }
            }

            System.out.println("Данные успешно прочитаны из XML файла: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return machines;
    }

    public static void writeToJson(String fileName, List<WashingMachine> machines) {
        JSONArray machinesArray = new JSONArray();

        for (WashingMachine machine : machines) {
            JSONObject machineObject = new JSONObject();
            machineObject.put("Id", machine.getId());
            machineObject.put("Type", machine.getType());
            machineObject.put("Model", machine.getModel());

            machinesArray.add(machineObject);
        }

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(machinesArray.toJSONString());
            System.out.println("Данные успешно записаны в JSON файл: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<WashingMachine> readFromJson(String fileName) {
        List<WashingMachine> machines = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try (FileReader fileReader = new FileReader(fileName)) {
            Object obj = parser.parse(fileReader);
            JSONArray machinesArray = (JSONArray) obj;

            for (Object machineObj : machinesArray) {
                JSONObject machineJson = (JSONObject) machineObj;
                int id = Integer.parseInt(machineJson.get("Id").toString());
                String type = machineJson.get("Type").toString();
                String model = machineJson.get("Model").toString();

                machines.add(new WashingMachineModel(id, type, model));
            }

            System.out.println("Данные успешно прочитаны из JSON файла: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return machines;
    }

    public static byte[] encryptData(List<WashingMachine> machines, String key) {
        // Ваша логика шифрования здесь (например, использование AES)
        return new byte[0];
    }

    public static List<WashingMachine> decryptData(byte[] encryptedData, String key) {
        // Ваша логика дешифрования здесь
        return new ArrayList<>();
    }

    public static void zipData(String zipFileName, List<WashingMachine> machines) {
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (WashingMachine machine : machines) {
                ZipEntry entry = new ZipEntry(machine.getModel() + ".txt");
                zos.putNextEntry(entry);

                // Добавление данных в архив (в данном случае, просто модель стиральной машины)
                zos.write(machine.getModel().getBytes());

                zos.closeEntry();
            }

            System.out.println("Данные успешно архивированы в ZIP файл: " + zipFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> unzipData(String zipFileName) {
        List<String> models = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(zipFileName);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];

                int bytesRead;
                while ((bytesRead = zis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }

                models.add(bos.toString());
                zis.closeEntry();
            }

            System.out.println("Данные успешно извлечены из ZIP файла: " + zipFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return models;
    }
}

class DatabaseManager {
    private static DatabaseManager instance;

    private DatabaseManager() {
        // Приватный конструктор, чтобы предотвратить создание экземпляров извне
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Методы для работы с базой данных
}

class DataValidator {
    private static final String MODEL_REGEX = "^[A-Za-z0-9]+$";

    public static boolean isValidModel(String model) {
        return Pattern.matches(MODEL_REGEX, model);
    }
}

class WashingMachineComparator implements Comparator<WashingMachine> {
    private String sortBy;

    public WashingMachineComparator(String sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public int compare(WashingMachine machine1, WashingMachine machine2) {
        switch (sortBy) {
            case "model":
                return machine1.getModel().compareTo(machine2.getModel());
            case "type":
                return machine1.getType().compareTo(machine2.getType());
            default:
                return Integer.compare(machine1.getId(), machine2.getId());
        }
    }
}

class AbstractStorage {
    // Абстрактные методы для хранения и манипуляции данными
}

public class Main {
    public static void main(String[] args) {
        // Пример использования

        WashingMachineStorage storage = new WashingMachineStorage();

        WashingMachineModel machine1 = new WashingMachineModel(1, "Front Load", "ABC123");
        WashingMachineModel machine2 = new WashingMachineModel(2, "Top Load", "XYZ789");

        storage.addWashingMachine(machine1);
        storage.addWashingMachine(machine2);

        // Сортировка по модели
        FileHandler.sortData(storage.getWashingMachinesList(), "model");

        // Запись в XML файл
        FileHandler.writeToXML("machines.xml", storage.getWashingMachinesList());

        // Чтение из XML файла
        List<WashingMachine> machinesFromXML = FileHandler.readFromXML("machines.xml");

        // Запись в JSON файл
        FileHandler.writeToJson("machines.json", storage.getWashingMachinesList());

        // Чтение из JSON файла
        List<WashingMachine> machinesFromJson = FileHandler.readFromJson("machines.json");

        // Шифрование данных
        byte[] encryptedData = FileHandler.encryptData(storage.getWashingMachinesList(), "encryptionKey");

        // Дешифрование данных
        List<WashingMachine> decryptedData = FileHandler.decryptData(encryptedData, "encryptionKey");

        // Архивация в ZIP файл
        FileHandler.zipData("machines.zip", storage.getWashingMachinesList());

        // Извлечение из ZIP файла
        List<String> modelsFromZip = FileHandler.unzipData("machines.zip");
    }
}
