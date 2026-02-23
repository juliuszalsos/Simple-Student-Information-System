package info;

public class College {

    private String collegeCode;
    private String collegeName;

    public void setCCode(String code){
        this.collegeCode = code;
    } 

    public String getCCode(){
        return collegeCode;
    }

    public void setCName(String name){
        this.collegeName = name;
    }

    public String getCName(){
        return collegeName;
    }

    public College(String name, String code){
        this.collegeName = name;
        this.collegeCode = code;
    }

}
