package sample;



public class User{
   public enum Priveledge{user,root,wrong_pass,wrong_login}

    Priveledge priveledge;
    String name;
    String password;

    public User(String Name, String Pass){
     this.name = Name;
     this.password = Pass;
    }

    public String getName(){return  name;}
    public String getPassword() { return password; }
    public Priveledge getPriveledge() { return priveledge;}

    public void setPriveledge(Priveledge pr) { this.priveledge = pr;}
    public void setPassword(String pass) { this.password = pass;}
    public void setName(String nam) { this.name = nam;}
}
