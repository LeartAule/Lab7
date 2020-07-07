package ServerClasses.Commands;

import Dragon.Dragon;
import Exceptions.InvalidCountOfArgumentException;
import Utils.DataBaseManager;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Update extends AbstractCommand {

    public Update() throws TransformerException, ParserConfigurationException {

        command = "update_id";
        TextInfo = "{id} : обновить значение элемента коллекции, id которого равен заданному";
        NeedAnStr = true;

        NeedAnObject = true;
    }


    public String execute(DataBaseManager dataBaseManager, String arg) throws IOException, InvalidCountOfArgumentException {

        if (dragon.getName().equals(null)) return "Не задан объект";

        if (dataBaseManager.updateElementInDataBase1(dragon, user)) {
            return "Дракон был заменён.";
        } else {
            return ("У вас нет дракона с таким номером.");

        }
    }
}