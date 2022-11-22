package YuriPackage;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class HandlerJSON {

  //Управляет считыванием и обновлением JSON-базы

  public void readBase(Map<String, Client> base) {
    try (FileReader fileReader = new FileReader(
        "D:\\JAVA\\UNIVERSITY\\Bot_consol\\ConsolniyBot\\text.json")) {
      Path file = Paths.get("D:\\JAVA\\UNIVERSITY\\Bot_consol\\ConsolniyBot\\text.json");
      String input = Files.readString(file);
      System.out.println(input);
      Client tempClient = new Client();//новый клиент в словарь
      JSONObject jsonObject = (JSONObject) JSONValue.parse(input);
      JSONArray jsonArray = (JSONArray) jsonObject.get("Map");

      for (Object item : jsonArray) {
        tempClient = new Client();
        JSONObject map = (JSONObject) item;
        String name = (String) map.get("Name");
        long lim = (Long) map.get("Limit");
        String date = (String) map.get("Date");
        if (castDateToInt(new Date().toString()) - castDateToInt(date) != 0) {
          tempClient.setLimitFromJSON(Integer.MAX_VALUE);
          tempClient.setDate(date);//уже можно добавлять
//          JSONArray jsonArray1 = (JSONArray) map.get("Products");
//          for (Object item1 : jsonArray1) {
//            JSONObject map1 = (JSONObject) item1;
//            String title = (String) map1.get("title");
//            long price = (Long) map1.get("price");
//            //tempClient.addExpensesFromJSON((int) price, title);
//          }
        } else {
          tempClient.setLimitFromJSON((int) lim);
          tempClient.setDate(date);
          JSONArray jsonArray1 = (JSONArray) map.get("Products");
          for (Object item1 : jsonArray1) {
            JSONObject map1 = (JSONObject) item1;
            String title = (String) map1.get("title");
            long price = (Long) map1.get("price");
            tempClient.addExpensesFromJSON((int) price, title);
          }
        }
        if (!base.containsKey(name)) {//если первый раз считываем или записали нового
          base.put(name, tempClient);
        }
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  ;

  public void updateBase(Map<String, Client> base) {
    try (FileWriter file = new FileWriter(
        "D:\\JAVA\\UNIVERSITY\\Bot_consol\\ConsolniyBot\\text.json")) {

      JSONObject mainObj = new JSONObject();
      JSONArray mp = new JSONArray();
      Client tempClient = new Client();
      HashMap<String, Integer> tempClientBase;
      for (Map.Entry<String, Client> entry : base.entrySet()) {
        JSONObject obj = new JSONObject();
        String uniqNAME = entry.getKey();
        tempClient = entry.getValue();
        tempClientBase = tempClient.mapForJSON();
        obj.put("Name", uniqNAME);
        obj.put("Limit", tempClient.LimitForJSON());
        obj.put("Date", new Date().toString());
        JSONArray products = new JSONArray();
        for (Map.Entry<String, Integer> entry1 : tempClientBase.entrySet()) {
          JSONObject obj1 = new JSONObject();
          obj1.put("title", entry1.getKey());
          obj1.put("price", entry1.getValue());
          products.add(obj1);
        }
        obj.put("Products", products);
        mp.add(obj);
      }
      mainObj.put("Map", mp);
      file.write(mainObj.toJSONString());
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Not today");
    }

  }

  private static int castDateToInt(String data) {
    char[] char_data = data.toCharArray();
    String str_day = String.valueOf(char_data[8]) + char_data[9];
    return Integer.parseInt(str_day);
  }
}