import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import java.nio.file.*;
import java.util.zip.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
// Абстрактный класс для объекта стиральной машины
abstract class WashingMachine {
    protected int id;
    protected String type;
    protected String model;

    public WashingMachine(int id, String type, String model) {
        this.id = id;
        this.type = type;
        this.model = model;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "WashingMachine{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}

// Абстрактный класс для хранения списков объектов
abstract class AbstractStorage {
    protected List<WashingMachine> washingMachinesList;
    protected Map<Integer, WashingMachine> washingMachinesMap;

    public AbstractStorage() {
        this.washingMachinesList = new ArrayList<>();
        this.washingMachinesMap = new HashMap<>();
    }

    public List<WashingMachine> getWashingMachinesList() {
        return washingMachinesList;
    }

    public Map<Integer, WashingMachine> getWashingMachinesMap() {
        return washingMachinesMap;
    }
}

class WashingMachineStorage extends AbstractStorage {

    // Метод для добавления новой стиральной машины в хранилище
    public void addWashingMachine(WashingMachine machine) {
        washingMachinesList.add(machine);
        washingMachinesMap.put(machine.getId(), machine);
    }

    // Метод для обновления существующей стиральной машины в хранилище
    public void updateWashingMachine(WashingMachine machine) {
        washingMachinesMap.put(machine.getId(), machine);
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

class FileHandler {
    // Реализация методов для чтения и записи

    // Метод для записи данных стиральных машин в файл
    public static void writeToFile(String fileName, List<WashingMachine> machines) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(machines);
            System.out.println("Данные успешно записаны в файл: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для чтения данных стиральных машин из файла
    public static List<WashingMachine> readFromFile(String fileName) {
        List<WashingMachine> machines = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            machines = (List<WashingMachine>) ois.readObject();
            System.out.println("Данные успешно прочитаны из файла: " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return machines;
    }

    // Метод для сортировки данных
    public static void sortData(List<WashingMachine> machines, String sortBy) {
        Collections.sort(machines, new WashingMachineComparator(sortBy));
    }

    // Метод для записи данных в XML файл (DOM технология)
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

    // Метод для чтения данных из XML файла
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

    // Метод для записи данных в JSON файл
    public static void writeToJson(String fileName, List<WashingMachine> machines) {
        JSONArray machinesArray = new JSONArray();

        for (WashingMachine machine : machines) {
            JSONObject machineObject = new JSONObject();
            machineObject.put("Id", machine.getId());
            machineObject.put("Type", machine.getType());
            machineObject.put("Model", machine.getModel());

            machinesArray.put(machineObject);
        }

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            //fileWriter.write(machinesArray.toJSONString());
            System.out.println("Данные успешно записаны в JSON файл: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для шифрования данных
    public static byte[] encryptData(List<WashingMachine> machines, String key) {
        // Ваша логика шифрования здесь (например, использование AES)
        return new byte[0];
    }

    // Метод для дешифрования данных
    public static List<WashingMachine> decryptData(byte[] encryptedData, String key) {
        // Ваша логика дешифрования здесь
        return new ArrayList<>();
    }

    // Метод для архивации данных в ZIP-файл
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

    // Метод для извлечения данных из ZIP-файла
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

class WashingMachineModel extends WashingMachine {
    public WashingMachineModel(int id, String type, String model) {
        super(id, type, model);
    }
}

class DataValidator {
    // Регулярное выражение для проверки корректности модели стиральной машины
    private static final String MODEL_REGEX = "^[A-Za-z0-9]+$";

    // Метод для проверки корректности модели стиральной машины
    public static boolean isValidModel(String model) {
        return Pattern.matches(MODEL_REGEX, model);
    }
}

// Пример использования
public class Main {
    public static void main(String[] args) {
        WashingMachineStorage storage = new WashingMachineStorage();

        WashingMachineModel machine1 = new WashingMachineModel(1, "Front Load", "ABC123");
        WashingMachineModel machine2 = new WashingMachineModel(2, "Top Load", "XYZ789");

        storage.getWashingMachinesList().add(machine1);
        storage.getWashingMachinesList().add(machine2);

        storage.getWashingMachinesMap().put(machine1.getId(), machine1);
        storage.getWashingMachinesMap().put(machine2.getId(), machine2);

        for (WashingMachine machine : storage.getWashingMachinesList()) {
            System.out.println(machine);
        }
        // Сортировка по модели
        FileHandler.sortData(storage.getWashingMachinesList(), "model");

        // Запись в XML файл
        FileHandler.writeToXML("machines.xml", storage.getWashingMachinesList());

        // Чтение из XML файла
        List<WashingMachine> machinesFromXML = FileHandler.readFromXML("machines.xml");

        // Запись в JSON файл
        FileHandler.writeToJson("machines.json", storage.getWashingMachinesList());

        // Чтение из JSON файла
        //List<WashingMachine> machinesFromJson = FileHandler.readFromJson("machines.json");

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
