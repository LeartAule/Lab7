package Utils;

import Dragon.*;
import Utils.OtherUtils.PortForwarder;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.LinkedHashMap;


public class DataBaseManager implements Serializable {

    protected static LinkedHashMap<Integer, Dragon> dragonLinkedHashMap = new LinkedHashMap<Integer, Dragon>();

    private String URL ;//= PortForwarder.forwardAnyPort();

    private String USER = "s284702";
    private String PASSWORD = "xxxxxx";

    //Нужны для авторизации пользователя

    private String userLogin = "КОСТЫЛЬ   -____-";
    private String userPassword;
    public DataBaseManager() {
        //System.out.println(USER);
        try {
            DriverManager.registerDriver(new org.postgresql.Driver ());
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

//Добавление юзера в БД

    public boolean CheckDB(){
        try (Connection connection = DriverManager.getConnection(PortForwarder.forwardAnyPort(), USER, PASSWORD)) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    public boolean CheckUser(String login){
        try (Connection connection = DriverManager.getConnection(PortForwarder.forwardAnyPort(), USER, PASSWORD)) {

            PreparedStatement ps = connection.prepareStatement("select * from users where login = ?");
            ps.setString(1, login);
            return true;

        } catch (SQLException e) {
            return false;
        }
    }


    public boolean AddUser(String login, String password){
        try (Connection connection = DriverManager.getConnection(PortForwarder.forwardAnyPort(), USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("INSERT INTO s284702.users(login, pass) VALUES (?, ?)");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, encryptThisString(password));
            preparedStatement.execute();
            userLogin = login;
            return true;
        }catch (SQLException e) {
        return false;
        }
    }
//метод для авторизации пользователя

    public boolean login(String login, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(PortForwarder.forwardAnyPort(), USER, PASSWORD)) {

            PreparedStatement ps = connection.prepareStatement("select * from users where login = ?");

            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return false;

            if(rs.getString("pass").equals(encryptThisString(password))) {
                userLogin = login;
                return true;
            }

            return false;
        }
    }

    public void updateCollectionFromDataBase(){
        LinkedHashMap<Integer, Dragon> linkedHashMap = new LinkedHashMap<>();
        try (Connection connection = DriverManager.getConnection(PortForwarder.forwardAnyPort(), USER, PASSWORD)){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM dragon");
            while (resultSet.next()){

                Dragon dragon = new Dragon();
                dragon.setId(resultSet.getInt("id"));
                dragon.setName(resultSet.getString("name"));
                dragon.setAge(resultSet.getInt("age"));
                dragon.setColor(Color.byOrdinal(resultSet.getString("color")));
                dragon.setType(DragonType.byOrdinal(resultSet.getString("type")));
                dragon.setCharacter(DragonCharacter.byOrdinal(resultSet.getString("character")));
                dragon.setCoordinates(new Coordinates(resultSet.getInt("x"), resultSet.getInt("y")));
                dragon.setCave(new DragonCave(resultSet.getFloat("cave_depth")));
                dragon.setUserName(resultSet.getString("user"));


                linkedHashMap.put(dragon.getId(), dragon);
            }
            dragonLinkedHashMap.clear();
            dragonLinkedHashMap = linkedHashMap;
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean addToDataBase(Dragon dragon) {
        try (Connection connection = DriverManager.getConnection(PortForwarder.forwardAnyPort(), USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO dragon (\"id\",\"name\", \"age\", \"color\", \"type\",  \"character\", \"cave_depth\", /* \"creation_time\", */ \"x\", \"y\", \"user\") " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setLong(1, dragon.getId());
            preparedStatement.setString(2, dragon.getName());
            preparedStatement.setLong(3, dragon.getAge());
            preparedStatement.setString(4, String.valueOf(dragon.getColor()));
            preparedStatement.setString(5, String.valueOf(dragon.getType()));
            preparedStatement.setString(6, String.valueOf(dragon.getCharacter()));
            preparedStatement.setDouble(7, dragon.getCave().getDepth());
            preparedStatement.setLong(8, dragon.getCoordinates().getX());
            preparedStatement.setLong(9, dragon.getCoordinates().getY());
            preparedStatement.setString(10, dragon.getUserName());
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addToDataBase1(Dragon dragon) {
        try (Connection connection = DriverManager.getConnection(PortForwarder.forwardAnyPort(), USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO person (\"name\", \"age\", \"color\", \"type\",  \"character\", \"cave_depth\", /* \"creation_time\", */ \"x\", \"y\", \"user\") " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setLong(1, dragon.getId());
            preparedStatement.setString(2, dragon.getName());
            preparedStatement.setLong(3, dragon.getAge());
            preparedStatement.setString(4, String.valueOf(dragon.getColor()));
            preparedStatement.setString(5, String.valueOf(dragon.getType()));
            preparedStatement.setString(6, String.valueOf(dragon.getCharacter()));
            preparedStatement.setLong(7, dragon.getCoordinates().getX());
            preparedStatement.setLong(8, dragon.getCoordinates().getY());
            preparedStatement.setString(9, userLogin);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateElementInDataBase(Dragon dragon) {
        try (Connection connection = DriverManager.getConnection(PortForwarder.forwardAnyPort(), USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM dragon");
            long maxId = 0;
            while (resultSet.next()){
                maxId = resultSet.getLong("max");
            }
            if (addToDataBase1(dragon)){
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE dragon SET id = " + dragon.getId() + " WHERE id = (SELECT MAX(id) FROM dragon);");
                preparedStatement.executeUpdate();
                return true;
            } else {
            }
        } catch (SQLException e) {
            System.out.println("");
            return true;
        }
        return false;
    }


    public boolean updateElementInDataBase1(Dragon dragon, String user) {
        try (Connection connection = DriverManager.getConnection(PortForwarder.forwardAnyPort(), USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM dragon WHERE id = " + dragon.getId());

            String userString = "";

            while (resultSet.next()){
                userString = resultSet.getString("user");
            }

            if(user.equals(userString)){
                removeFromDataBase(dragon.getId(), user);
                addToDataBase(dragon);
              return true;
            } else return false;

        } catch (SQLException e) {
            return false;
        }
    }




    public boolean removeFromDataBase(int id, String userLogin){
        try (Connection connection = DriverManager.getConnection(PortForwarder.forwardAnyPort(), USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM dragon WHERE id = " + id);
            while (resultSet.next()){
                if (!resultSet.getString("user").equals(userLogin)){
                    return false;
                }
            }
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM dragon WHERE id = " + id + " AND \"user\" = '" + userLogin + "'");
            preparedStatement.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }


    public void setUSER(String userLogin){
        this.userLogin = userLogin;
    }

    public void setPASSWORD(String PASSWORD){
        this.PASSWORD = PASSWORD;
    }

    public String getUSER(){
        return userLogin;
    }

    //Проверяет авторизован ли пользователь

    public boolean UserAuthorized() {

        try {


            return !userLogin.equals("КОСТЫЛЬ   -____-");
        }catch (NullPointerException e) {
            return false;
        }
    }

    public  LinkedHashMap<Integer, Dragon> getCollection() {
        return dragonLinkedHashMap;
    }

    public  void setCollection(LinkedHashMap<Integer, Dragon> dragonLinkedHashMap) {
        DataBaseManager.dragonLinkedHashMap = dragonLinkedHashMap;
    }




    public String encryptThisString(String input)

    {
        try {
            // метод getInstance () вызывается с алгоритмом MD2
            MessageDigest md = MessageDigest.getInstance("MD2");

            // вызывается метод digest ()
            // вычислить дайджест сообщения из входной строки
            // возвращается как массив байтов
            byte[] messageDigest = md.digest(input.getBytes());
            // Преобразование байтового массива в представление знака
            BigInteger no = new BigInteger(1, messageDigest);
            // Преобразуем дайджест сообщения в шестнадцатеричное значение
            String hashtext = no.toString(16);
            // Добавить предыдущие 0, чтобы сделать его 32-битным
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            // возвращаем HashText
            return hashtext;
        }

        // Для указания неправильных алгоритмов дайджеста сообщений
        catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(e);

        }

    }



}