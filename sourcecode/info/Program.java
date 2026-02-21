package info;

public class Program {
    private String programName;
    private String programCode;

    private void setPCode(String code){
        this.programCode = code;
    } 

    private String getPCode(){
        return programCode;
    }

    private void setPName(String name){
        this.programName = name;
    }

    private String getPName(){
        return programName;
    }

    private Program(String name, String code){
        this.programName = name;
        this.programCode = code;
    }

    
    
    
}
