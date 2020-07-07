package ServerClasses.Commands;

import Dragon.Dragon;
import Exceptions.InvalidCountOfArgumentException;
import Utils.DataBaseManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Clear extends AbstractCommand {
    public Clear() {
        command = "clear";
        TextInfo = ": Удаляет содержимое коллекции";

        NeedAnStr = false;
    }


    public String execute(DataBaseManager dataBaseManager, String arg) throws IOException, InvalidCountOfArgumentException {

        for (Map.Entry<Integer, Dragon> dragonEntry : dataBaseManager.getCollection().entrySet()) {
            String UserName = dragonEntry.getValue().getUserName().trim();
            if(user.equals(UserName)){
                dataBaseManager.removeFromDataBase(dragonEntry.getValue().getId(), user);
            }

        }


        return "Коллекция " + dataBaseManager.getCollection() + " была очищена.";
    }
}
