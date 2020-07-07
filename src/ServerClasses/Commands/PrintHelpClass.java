package ServerClasses.Commands;

import Dragon.*;
import Exceptions.InvalidCountOfArgumentException;
import ServerClasses.CommandManager;
import Utils.DataBaseManager;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.*;

import static Dragon.ColorText.Text;


/**
 *
 * Класс, который выводит список существующих команд
 */

public class PrintHelpClass extends AbstractCommand {


    public PrintHelpClass() throws TransformerException, ParserConfigurationException {
        command = "help";
        TextInfo = ": Вывод всех доступных команд";
        NeedAnStr = false;
    }


    public String execute(DataBaseManager dataBaseManager, String arg) {
        String str = "";


        for (Map.Entry<String, AbstractCommand> abs : CommandManager.getAvailableCommands().entrySet()) {
            str = str + abs.getValue().toPrint() + "\n";
        }
        //setMessage(str);
        return str;
    }

}
