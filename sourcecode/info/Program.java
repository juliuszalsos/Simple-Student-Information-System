package info;

public class Program {
    private String programName;
    private String programCode;
    private String collegeCode;

    public void setPCode(String code){
        this.programCode = code;
    } 

    public String getPCode(){
        return programCode;
    }

    public void setPName(String name){
        this.programName = name;
    }

    public String getPName(){
        return programName;
    }

    public String getCCode(){
        return collegeCode;
    }

    public void setCCode(String code){
        this.collegeCode = code;
    }

    public Program(String name, String code, String ccode){
        this.programName = name;
        this.programCode = code;
        this.collegeCode = ccode;
    }

    
    
    
}
