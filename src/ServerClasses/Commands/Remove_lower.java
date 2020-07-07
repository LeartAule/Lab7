package ServerClasses.Commands;

import Dragon.Dragon;
import Exceptions.InvalidCountOfArgumentException;
import Utils.DataBaseManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Remove_lower extends AbstractCommand{


    public Remove_lower() {
        command = "remove_lower";
        TextInfo = "{element} : удалить из коллекции все элементы, меньшие, чем заданный.\n (Удалаются только те экземпляры, владельцем которых являетесь Вы)";


        NeedAnStr = false;
        NeedAnObject = true;
    }



    public String execute(DataBaseManager dataBaseManager, String arg) throws IOException {
        if(dataBaseManager.getCollection().isEmpty()) return "Коллекция пуста";
        //System.out.println("Введите данные Группы. Все группы, которые меньше вашей(исходя из логики сравнения), будут удалены.");


        for (Map.Entry<Integer, Dragon> dragonEntry : dataBaseManager.getCollection().entrySet()) {
            String UserName = dragonEntry.getValue().getUserName().trim();
            if(dragonEntry.getValue().compareTo(dragon) > 0 && user.equals(UserName)){
                dataBaseManager.removeFromDataBase(dragonEntry.getValue().getId(), user);
            }

        }


        return null;
    }


}
