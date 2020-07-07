package ServerClasses.Commands;

import Dragon.Dragon;
import Exceptions.InvalidCountOfArgumentException;
import ServerClasses.Information;
import Utils.DataBaseManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Класс для вывода информации о коллекции
 */

public class PrintInfoClass extends AbstractCommand {

    public PrintInfoClass(){
        command = "info";
        TextInfo = ": вывести в стандартный поток вывода информацию о коллекции";
        NeedAnStr = false;
    }

    protected String printInfo(LinkedHashMap collection){
        String type = "Тип коллекции: " + collection.getClass().getSimpleName();
        String date = ("Дата инициализации: " + collection.values().toString());
        String size = ("Количество элементов: " + collection.size());

        return "Общие информация по базе данных" +
                type + "\n" + date + "\n" + size;
    }


    public String execute(DataBaseManager dataBaseManager, String arg) throws IOException, InvalidCountOfArgumentException {
        return printInfo(dataBaseManager.getCollection());
    }
}
