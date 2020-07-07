package ServerClasses.SpecialCommands;



public class Register extends Authorization {




    public Register(String userName, String password){
        super(userName, password);
        command = "register";
        TextInfo = " : Регистрация пользователя";


    }



}
