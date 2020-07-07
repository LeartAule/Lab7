import Dragon.Dragon;
import Exceptions.ScriptException;
import ServerClasses.CommandManager;
import ServerClasses.Commands.AbstractCommand;
import ServerClasses.Commands.Execute_script;
import ServerClasses.SpecialCommands.Authorization;
import ServerClasses.SpecialCommands.Login;
import ServerClasses.SpecialCommands.Register;
import Utils.OtherUtils.Serialization;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.UnresolvedAddressException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Приложение Клиента
 */

public class Client implements Runnable{

    private static boolean UserLog = false;

    private static String username;
    private static String password;

    private final DatagramChannel datagramChannel;
    private SocketAddress socketAddress;
    private final Selector selector;
    private CommandManager commandManager = new CommandManager();
    private Iterator<String> script_iterator = null;
    private static int port;

    public Client() throws IOException, TransformerException, ParserConfigurationException {
        selector = Selector.open();
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
    }

    public static void main(String args[]) throws IOException, ArrayIndexOutOfBoundsException {


        Scanner scanner = new Scanner(System.in);
        String string;
        try {
            string = args[0];
        }catch (java.lang.ArrayIndexOutOfBoundsException e){
            System.out.println("Вы ввели порт не верно." + "\nВведите порт в виде целого числа");
            string = scanner.nextLine();
        }



        while(!CheckINT(string)){
            System.out.println("Введите порт в виде целого числа");
            string = scanner.nextLine();
        }

        port = Integer.parseInt(string);


        try {
            Client client = new Client();
            client.connect("localhost", port);
            client.run();
        } catch (IOException | UnresolvedAddressException | TransformerException | ParserConfigurationException e) {}
    }


    int i = 1;
    @Override
    public void run() {


        try {
            Scanner scanner = new Scanner(System.in);

            datagramChannel.register(selector, SelectionKey.OP_WRITE);
            while (selector.select() > 0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    //Тут просиходит получение ответа от Сервера.
                    if (selectionKey.isReadable()){

                        String answer = receiveAnswer();
                        if(!answer.contains("Something goes wrong")){
                            if(!answer.contains("Access")){
                            System.out.println(answer);
                        }else{
                                UserLog = true;
                                System.out.println("Вы зашли под пользователем " + username + ".");
                            }

                            datagramChannel.register(selector, SelectionKey.OP_WRITE);
                        }

                        if(answer.equals("Вы не авторизованы. Пожалуйста зайдите под существующим аккаунтом " +
                                "или создайте новый") || answer.equals("Не верный логин или пароль.")) UserLog = false;


                    }
                    if (selectionKey.isWritable()){
                        datagramChannel.register(selector, SelectionKey.OP_READ);

                        if(UserLog) {

                            try {
                                String string;

                                if (script_iterator != null && script_iterator.hasNext()) {
                                    string = script_iterator.next();
                                } else {
                                    string = scanner.nextLine();
                                }

                                String[] strings = string.trim().split("\\s+");
                                AbstractCommand command = null;


                                //Выполнение скрипта
                                if (strings[0].equals("execute_script") && strings.length == 2) {
                                    Execute_script execute_script = new Execute_script();
                                    try {
                                        execute_script.GetExecute(strings[1]);
                                        script_iterator = execute_script.ScriptCommands().iterator();
                                    } catch (ParserConfigurationException | TransformerException e) {
                                        e.printStackTrace();
                                    }
                                    throw new ScriptException();
                                }


                                //Отсеивание неправильных команд
                                if (strings[0].equals("execute_script") && strings.length != 2) {
                                    throw new NumberFormatException();
                                }

                                //Отсеивание неправильных команд
                                if (strings.length > 2 || !commandManager.Check(strings[0])) {
                                    if (!string.contains("execute_script")) sendCommand(string);
                                    //throw new NoCommandException("Такой команды не существует");
                                } else {

                                    command = commandManager.getCommand(strings[0]);
                                    command.setUserName(username);
                                    //Отсеивание неправильных команд
                                    if (strings.length == 0) {
                                        throw new NumberFormatException();
                                    }

                                    //Отсеивание неправильных команд
                                    if (strings.length == 2) {
                                        if (!CheckINT(strings[1]) && !strings[0].equals("execute_script")) {
                                            throw new NumberFormatException();
                                        }
                                    }
                                    //Отсеивание неправильных команд
                                    if (strings.length == 2 && !command.isNeedAnStr()) {
                                        throw new NumberFormatException();
                                    }

                                    //Отсеивание неправильных команд
                                    if (strings.length == 1 && command.isNeedAnStr()) {
                                        throw new NumberFormatException();
                                    }

                                    //Команды, где не нужен объект и аргумент к нему.(Пример help, show)
                                    if (!command.getObjectExecute() && !command.isNeedAnStr()) {
                                        sendCommand(command);
                                        continue;
                                    }
                                    //Команды, где нужен только аргумент. (Пример: remove_key_lower {key})
                                    if (!command.getObjectExecute() && command.isNeedAnStr()) {
                                        sendCommand(command, strings[1]);
                                        continue;
                                    }

                                    //Команды, где нужен объект и аргумент. (Пример: remove_lower {element}})
                                    if (command.getObjectExecute() && !command.isNeedAnStr()) {
                                        sendDragonCommand(command, strings[1]);
                                        continue;
                                    }


                                    //Команды, где нужен объект и аргумент. (Пример: insert {key} + {element}})
                                    if (command.getObjectExecute() && command.isNeedAnStr()) {
                                        sendDragonCommand(command, strings[1]);
                                    }

                                }
                            } catch (NumberFormatException e) {

                                System.out.println("Неправильный ввод команды");
                                run();
                            } catch (ScriptException e) {
                                run();
                            }

                        }else{


                            if(i == 1) {
                                System.out.println("Вы не авторизованы.\nВведите Login, чтобы авторизоваться, или Register для создания " +
                                        "нового пользователя.");
                                i++;
                            }else {
                                System.out.println("Вы не авторизованы");
                            }

                            while(true) {
                                String str1 = scanner.nextLine();
                                if (str1.equals("Register") || str1.equals("register")) {
                                    register(scanner);
                                    break;
                                }


                                if (str1.equals("login") || str1.equals("Login")) {
                                    login(scanner);
                                    break;
                                }


                                System.out.println("Вы ввели команду не правильно");
                            }


                        }

                    }
                }
            }
        } catch (PortUnreachableException e) {
            System.err.println("Не удалось получить данные по указанному порту/сервер не доступен. \n" +
                    "Введите cont - чтобы продолжить. Change - чтобы поменять порт");
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();
            if(str.equals("Change")){
                System.out.print("Вы можете вводить порт: ");
                str = scanner.nextLine();
                while(!CheckINT(str)){
                    System.out.println("Введите порт в виде целого числа");
                    str = scanner.nextLine();
                }
                port = Integer.parseInt(str);
            }

            startProgram(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Подключение к серверу
    private void connect(String hostname, int port) throws IOException{
        socketAddress = new InetSocketAddress(hostname, port);
        datagramChannel.connect(socketAddress);
        System.out.println("Устанавливаем соединение с " + hostname + " по порту " + port);
    }


    //Получение ответа от сервера
    private String receiveAnswer() throws IOException {
        byte[] bytes = new byte[65536];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        socketAddress = datagramChannel.receive(buffer);
        String msg = new String(buffer.array());
        return msg;
    }


    //Отправка команды c параметром на сервер
    private void sendCommand(AbstractCommand command, String parameter) throws IOException{
        if (command != null){
            command.setString(parameter);
        }
        ByteBuffer buffer = ByteBuffer.wrap(new Serialization().SerializeObject(command));
        datagramChannel.send(buffer, socketAddress);
        sendCommand(parameter);
    }


    //Отправка строки на сервер
    private void sendCommand(String str) throws IOException{
        ByteBuffer buffer = ByteBuffer.wrap(new Serialization().SerializeObject(str));
        datagramChannel.send(buffer, socketAddress);

    }

    //Отправка команды на сервер
    private void sendCommand(AbstractCommand str) throws IOException{


        ByteBuffer buffer = ByteBuffer.wrap(new Serialization().SerializeObject(str));
        datagramChannel.send(buffer, socketAddress);

        if(str.getCommand().equals("exit")){
            System.out.println("Завершение программы");
            System.exit(0);
        }

    }


    //Отправляет команду вместе с объектом
    private void sendDragonCommand(AbstractCommand command, String string) throws IOException {

        Dragon dragon = new Dragon(username);
        dragon.update(Integer.parseInt(string));
        ByteBuffer buffer = ByteBuffer.wrap(new Serialization().SerializeObject(command));
        datagramChannel.send(buffer, socketAddress);
        sendDragon(dragon);
    }

    // Отправляет объект на сервер
    private void sendDragon(Dragon dragon) throws IOException{
        ByteBuffer buffer = ByteBuffer.wrap(new Serialization().SerializeObject(dragon));
        datagramChannel.send(buffer, socketAddress);
        System.out.println(dragon.getName() + " отправлен");
    }



    //проверка на число
    private static boolean CheckINT(String str){
        try{
            Integer abs = Integer.valueOf(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void startProgram(int port){
        try {
            Client client = new Client();
            client.connect("localhost", port);
            client.run();
        }catch (IOException | UnresolvedAddressException | TransformerException | ParserConfigurationException e) {}

    }


    private void sendCommand(Authorization authorization) throws IOException{
        ByteBuffer buffer = ByteBuffer.wrap(new Serialization().SerializeObject(authorization));
        datagramChannel.send(buffer, socketAddress);
    }


    //Регистрация пользователя
    private void register(Scanner scanner) throws IOException{
        username = "";
        boolean lessThen4 = true;
        boolean withSpaces = true;
        boolean invalidChars = true;
        do {
            System.out.println("Придумайте логин, содержащий не менее 4 символов (допускается использование только английских прописных букв и цифр)");
            username = scanner.nextLine();
            lessThen4 = username.trim().split("\\s+")[0].length() < 4;
            withSpaces = username.trim().split("\\s+").length != 1;
            invalidChars = !username.trim().split("\\s+")[0].matches("[a-z0-9]+");
        }while (lessThen4 || withSpaces || invalidChars);
        password = "";
        lessThen4 = true;
        withSpaces = true;
        invalidChars = true;
        do {
            System.out.println("Придумайте пароль, содержащий не менее 4 (допускается использование только английских прописных букв и цифр)");
            password = scanner.nextLine();
            lessThen4 = password.trim().split("\\s+")[0].length() < 4;
            withSpaces = password.trim().split("\\s+").length != 1;
            invalidChars = !password.trim().split("\\s+")[0].matches("[a-z0-9]+");
        } while (lessThen4 || withSpaces || invalidChars);
        System.out.println("Ваш логин: " + password.trim().split("\\s+")[0] + "\nВаш пароль: " + password.trim().split("\\s+")[0]);

        sendCommand(new Register(username, password));

        System.out.println("Получение ответа...");
    }


    //Авторизация пользователя
    private void login(Scanner scanner)throws IOException {
        System.out.println("Введите логин: ");
        username = scanner.nextLine();
        System.out.println("Введите пароль: ");
        password = scanner.nextLine();
        sendCommand(new Login(username, password));



        System.out.println("Получение ответа...");
    }









}

