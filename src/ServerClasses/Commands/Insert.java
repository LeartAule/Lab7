package ServerClasses.Commands;

import Dragon.*;
import Exceptions.InvalidCountOfArgumentException;
import Utils.DataBaseManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Insert extends AbstractCommand {

    public Insert(){
        command = "insert";
        TextInfo = "{key} : добавить новый элемент с заданным ключом";


        NeedAnStr = true;
        NeedAnObject = true;
    }




    public String execute(DataBaseManager dataBaseManager, String arg) throws IOException, InvalidCountOfArgumentException {

        Integer id = getDragon().getId();


        for (Map.Entry<Integer, Dragon> dragonEntry : dataBaseManager.getCollection().entrySet()) {
            if (dragonEntry.getKey().equals(id)) {
                String str = "Данный индефекационный номер уже занят другим объетом.";
                return str;

            }
        }

            dataBaseManager.getCollection().put(id, dragon);
            dataBaseManager.addToDataBase(dragon);



            return "Дракон " + dragon.getName() + " был добавлен.";

    }
}
