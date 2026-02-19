public class Student{
    private String id;
    private String firstName;
    private String lastName;
    private String programCode;
    private String yearLevel;
    private String gender;

    public void setStudentID(String ID){
        this.id = ID;
    } 

    public String getStudentID(){
        return id;
    }

    public void setStudentfirstName(String firstname){
        this.firstName = firstname;
    }

    public String getStudentfirstName(){
        return firstName;
    }

    public void setStudentlastName(String lastname){
        this.lastName = lastname;
    }

    public String getStudentlastName(){
        return lastName;
    }

    public void setStudentProgramCode(String PCode){
        this.programCode = PCode;
    }

    public String getStudentProgramCode(){
        return programCode;
    }

    public void setStudentYearLevel(String level){
        this.yearLevel = level;
    }

    public String getStudentYearLevel(){
        return yearLevel;
    }

    public void setStudentGender(String gender){
        this.gender = gender;
    }

    public String getStudentGender(){
        return gender;
    }

    public Student(String id, String firstName, String lastName,String code, String level, String gender){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.programCode = code;
        this.yearLevel = level;
        this.gender = gender;
    }
    
    public Student(){

    }


}