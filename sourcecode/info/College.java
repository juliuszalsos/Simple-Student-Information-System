package info;

public class College {

    private String collegeCode;
    private String collegeName;

    private void setCCode(String code){
        this.collegeCode = code;
    } 

    private String getCCode(){
        return collegeCode;
    }

    private void setCName(String name){
        this.collegeName = name;
    }

    private String getCName(){
        return collegeName;
    }

    private College(String name, String code){
        this.collegeName = name;
        this.collegeCode = code;
    }

}
