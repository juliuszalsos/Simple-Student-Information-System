package info;

public class Program {
    private String programName;
    private String programCode;

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

    public Program(String name, String code){
        this.programName = name;
        this.programCode = code;
    }

    
    
    
}
