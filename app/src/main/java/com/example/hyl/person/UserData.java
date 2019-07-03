package com.example.hyl.person;

public class UserData {
    private String userName;                  //用户名
    private String userPwd;                   //用户密码
    private String userId;                    //用户ID号
    private String telephonenumber;           //电话
    private String age;                          //年龄
    private String sex;                       //性别
    private String Address;                   //地址
    private String email;                     //邮箱
    //获取用户名
    public String getUserName() {             //获取用户名
        return userName;
    }
    //设置用户名
    public void setUserName(String userName) {  //输入用户名
        this.userName = userName;
    }
    //获取用户密码
    public String getUserPwd() {                //获取用户密码
        return userPwd;
    }
    //设置用户密码
    public void setUserPwd(String userPwd) {     //输入用户密码
        this.userPwd = userPwd;
    }
    //获取用户id
    public String getUserId() {                   //获取用户ID号
        return userId;
    }
    //设置用户id
    public void setUserId(String userId) {       //设置用户ID号

        this.userId = userId;
    }

    public String getTelephonenumber(){
        return telephonenumber;
    }

    public void setTelephonenumber(String telephonenumber){
        this.telephonenumber = telephonenumber;
    }

    public String getAge(){
        return age;
    }
    public void setAge(String age){
        this.age = age;
    }

    public String getSex(){
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public UserData(String userId, String userPwd, String userName, String telephonenumber, String age, String Address, String sex, String email){
        super();
        this.userId = userId;
        this.userPwd = userPwd;
        this.userName = userName;
        this.telephonenumber = telephonenumber;
        this.age = age;
        this.Address = Address;
        this.sex = sex;
        this.email = email;
    }

}