package ServerClasses.Commands;

import Dragon.Dragon;
import Exceptions.InvalidCountOfArgumentException;
import Utils.DataBaseManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Remove_lower_key extends AbstractCommand{
    public Remove_lower_key() {
        command = "remove_lower_key";
        TextInfo = " {key}: заменить значение по ключу, если новое значение больше старого.\n (Удалаются только те экземпляры, владельцем которых являетесь Вы)";
        NeedAnStr = true;
    }


    public String execute(DataBaseManager dataBaseManager, String arg) {
        String msg = " ";



        if(!dataBaseManager.getCollection().isEmpty()) {

            int rlk = Integer.parseInt(arg);
            msg = ("Были удалены: \n");
            for (Map.Entry<Integer, Dragon> dragonEntry : dataBaseManager.getCollection().entrySet()) {
                String userName = dragonEntry.getValue().getUserName().trim();

                if ((dragonEntry.getKey() < rlk) && userName.equals(user)) {
                    msg = msg + (dragonEntry.getValue().getId() + " -> " + dragonEntry.getValue().getName() +  " \n");
                    dataBaseManager.getCollection().remove(dragonEntry.getValue().getId());
                    dataBaseManager.removeFromDataBase(dragonEntry.getValue().getId(), user);
                }
            }
        }else{
            msg = ("Коллекция пуста.");
        }
        return msg;
    }

    /*public String execute(String[] arg){
        String msg = "";
        if(!getCollection().isEmpty()) {
            int rlk = Integer.parseInt(arg[1]);
            msg = ("Были удалены: ");
            for (Map.Entry<Integer, Dragon> dragonEntry : getCollection().entrySet()) {
                if (dragonEntry.getKey() > rlk && dragonEntry.getValue().getUserName().equals(getUSER())) {
                    getCollection().remove(rlk);

                    msg = msg + (rlk + " ");
                }
            }
        }else{
            return ("Коллекция пуста.");
        }
        if (msg.equals("")) return "Никто не был удалён";

        return msg;
    }*/

}

