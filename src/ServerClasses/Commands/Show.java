package ServerClasses.Commands;

import Dragon.*;
import Exceptions.InvalidCountOfArgumentException;
import ServerClasses.Information;
import Utils.DataBaseManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Show extends AbstractCommand {

    public Show(){
        command = "show";
        TextInfo = ": Показывает содержимое коллекции";
        NeedAnStr = false;
    }

    public String execute(DataBaseManager dataBaseManager, String arg) throws IOException, InvalidCountOfArgumentException {

        dataBaseManager.setCollection(SortCollection(dataBaseManager.getCollection()));


        String ShowCollection = "";
        String ShowCollection1 = "";
        if(!dataBaseManager.getCollection().isEmpty()){
            for(Map.Entry<Integer, Dragon> entry : dataBaseManager.getCollection().entrySet()) {

                String dragonUser = entry.getValue().getUserName().trim();


                //System.out.println("dragonUser = " +  entry.getValue().getUserName());
                //System.out.println("user = " + dataBaseManager.getUSER());

                System.out.println("if " + dragonUser.equals(user));

                if(dragonUser.equals(user)) {
                    ShowCollection = ShowCollection + (entry.getValue().toDragonString()) + "\n";
                }else{
                    ShowCollection1 = ShowCollection1 + entry.getValue().toString();
                }
                //System.out.println(entry.getKey());
            }
        }else{
            return ("Collection is empty");
        }
        return (ShowCollection + ShowCollection1);



    }
}
