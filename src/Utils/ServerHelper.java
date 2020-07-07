package Utils;

import Dragon.*;
import Exceptions.InvalidCountOfArgumentException;
import ServerClasses.CommandManager;
import ServerClasses.Commands.AbstractCommand;
import ServerClasses.Commands.SaveClass;
import ServerClasses.SpecialCommands.Login;
import ServerClasses.SpecialCommands.Register;
import Utils.OtherUtils.Serialization;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class ServerHelper extends Thread implements ColorText{

    private DatagramChannel datagramChannel;
    private SocketAddress socketAddress;
    private DataBaseManager dataBaseManager;
    private ByteBuffer byteBuffer;

    Object o = null;

    private boolean UserLog = false;

    public ServerHelper(DatagramChannel datagramChannel, SocketAddress socketAddress,
                        DataBaseManager dataBaseManager, ByteBuffer byteBuffer) throws TransformerException, ParserConfigurationException {
        this.datagramChannel = datagramChannel;
        this.socketAddress = socketAddress;
        this.dataBaseManager = dataBaseManager;
        this.byteBuffer = byteBuffer;
    }


    private CommandManager commandManager = new CommandManager(dataBaseManager);

    @Override
    public void run(){


        if(!dataBaseManager.CheckDB()){
            try {
                datagramChannel.send(ByteBuffer.wrap(("База данных временно не доступна. Просим прощения за неудобства.").getBytes()), socketAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        o = new Serialization().DeserializeObject(byteBuffer.array());
        String s = o.toString();

        commandManager.setServerDatagramChannel(datagramChannel);
        commandManager.setSocketAddress(socketAddress);



        System.out.println("Полученно [" + s + "] от " + socketAddress);



        if(o.getClass().getName().contains(".Login")){
            Login register = (Login) o;
            String login = register.getUserName();
            String pass = register.getPassword();

                try {
                    if(dataBaseManager.login(login, pass)) {
                        datagramChannel.send(ByteBuffer.wrap("Access".getBytes()), socketAddress);
                        boolean userLog = true;
                    }else{
                        datagramChannel.send(ByteBuffer.wrap("Не верный логин или пароль.".getBytes()), socketAddress);
                }}catch (IOException | SQLException e){ e.printStackTrace(); }
                return;
        }




        //Если пользователь хочет зарегистророваться
        if(o.getClass().getName().contains(".Register")){
            Register register = (Register) o;
            String login = register.getUserName();
            String pass = register.getPassword();

            if (dataBaseManager.AddUser(login, pass)){
                try {
                    datagramChannel.send(ByteBuffer.wrap(("Пользователь был добавлен в дазу данных. Войдите в систему. \n" +
                            "Ваш логин: " + login + ", и пароль: " + pass + ".").getBytes()), socketAddress);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    datagramChannel.send(ByteBuffer.wrap(("Не удалось добавить пользователя." +
                            " Логин занят, содержит недопустимые символы или их последовательность.").getBytes()), socketAddress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                return;
        }


        if(o.getClass().getName().contains(".AbstractCommand")){
            AbstractCommand ac = (AbstractCommand) o;
            if(dataBaseManager.CheckUser(ac.getUserName())){
                UserLog = true;
            }
        }

        //Если пользователь не авторизован
        if(UserLog){
            try {
                datagramChannel.send(ByteBuffer.wrap(("Вы не авторизованы. Пожалуйста зайдите под существующим аккаунтом " +
                        "или создайте новый").getBytes()), socketAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        //Если команда не сущестует
        if (!commandManager.Check(s)) {
            try {
                datagramChannel.send(ByteBuffer.wrap(("Команда [" + s + "] не найдена или имеент неверное количество аргументов." +
                        " Для просмотра доступных команд введите help").getBytes()), socketAddress);
                datagramChannel.send(ByteBuffer.wrap("Something goes wrong".getBytes()), socketAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        //Если команды нет
        if (o == null){
            try {
                datagramChannel.send(ByteBuffer.wrap(("Команда [" + s + "] не найдена или имеент неверное количество аргументов. " +
                        "Для просмотра доступных команд введите help").getBytes()), socketAddress);
                datagramChannel.send(ByteBuffer.wrap("Something goes wrong".getBytes()), socketAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }




        if (commandManager.Check(s) && dataBaseManager.UserAuthorized()){
            try {
                socketAddress = datagramChannel.receive(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            AbstractCommand command = null;
            if (socketAddress != null) {
                command = (AbstractCommand) o;
                dataBaseManager.setUSER(command.getUserName());
                //System.out.println("Получено [" + o + "] от " + ColorText.Text(dataBaseManager.getUSER(), Color.YELLOW) +" " + socketAddress);
            }

            command = (AbstractCommand) o;

            //Получение объекта  {Дракона}
            if(command.getObjectExecute() || o.getClass().getName().contains(".Dragon")){
                Dragon dragon = (Dragon) new Serialization().DeserializeObject(byteBuffer.array());
                command.setDragon(dragon);
            }else{
                if (command.isNeedAnStr()){

                    o =  new Serialization().DeserializeObject(byteBuffer.array());
                    String str = (String) o;
                    command.setString(str);
                }
            }


            //выполнение команды и отправка результата
            if(!command.getCommand().equals("exit")){
                try {
                    commandManager.printToClient(new CommandResult(commandManager).sendResult(command, dataBaseManager));
                } catch (IOException | ParserConfigurationException | TransformerException | InvalidCountOfArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
