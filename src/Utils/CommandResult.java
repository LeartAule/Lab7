package Utils;

import Dragon.Dragon;
import Exceptions.InvalidCountOfArgumentException;
import ServerClasses.CommandManager;
import ServerClasses.Commands.AbstractCommand;
import ServerClasses.Information;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.LinkedHashMap;


public class CommandResult {

    CommandManager commandManager;


    public CommandResult(CommandManager commandManager) throws TransformerException, ParserConfigurationException {
        this.commandManager = commandManager;
    }

    /**
     * Метод на входе получает Абстракт-команду, и на выходе даёт её результат.
     *
     * @param command
     * @return String: результат команды
     */
    public String sendResult(AbstractCommand command, DataBaseManager dataBaseManager) throws IOException, InvalidCountOfArgumentException {

        dataBaseManager.updateCollectionFromDataBase();


        System.out.println("CR_user = " + dataBaseManager.getUSER());

        if(command.getCommand().equals("exit")){
            commandManager.getCommand("save").execute();
        }

        //Команды, где не нужен объект и аргумент к нему.(Пример help, show)
        if (!command.getObjectExecute() && !command.isNeedAnStr()) {
            return command.execute(dataBaseManager, command.getString());
        }

        //Команды, где нужен только аргумент. (Пример: remove_key_lower {key})
        if(!command.getObjectExecute() && command.isNeedAnStr()){



            if(command.getString() != null){
                return command.execute(dataBaseManager, command.getString());
            }else{
                if (command.getString() == null) return "Нет строки";
            }

        }

        //Команды, где нужен объект и аргумент. (Пример: remove_lower {element}})
        if (command.getObjectExecute() && !command.isNeedAnStr()) {

            if(command.getDragon() != null){
                return command.execute(dataBaseManager, command.getString());
            }else{
                return "Нет дракона";
        }}

        //Команды, где нужен объект и аргумент. (Пример: insert {key} + {element}})
        if (command.getObjectExecute() && command.isNeedAnStr()) {


            if(command.getDragon() != null && command.getString() != null){
                return command.execute(dataBaseManager, command.getString());
            }else{
                if(command.getDragon() == null && command.getString() == null) return "Нет дракона и строки";
                if (command.getDragon() == null) return "Нет дракона";
                if (command.getString() == null) return "Нет строки";
            }
        }


        return "Что-то пошло не так.";
    }

}
