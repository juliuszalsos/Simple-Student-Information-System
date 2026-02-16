public class Student{
    private String id;
    private String firstName;
    private String lastName;
    private String programCode;
    private String yearLevel;
    private String gender;

    private void setStudentID(String ID){
        this.id = ID;
    } 

    private String getStudentID(){
        return id;
    }

    private void setStudentfirstName(String firstname){
        this.firstName = firstname;
    }

    private String getStudentfirstName(){
        return firstName;
    }

    private void setStudentlastName(String lastname){
        this.lastName = lasstname;
    }

    private String getStudentlastName(){
        return lastName;
    }

    private void setStudentProgramCode(String PCode){
        this.programCode = PCode;
    }

    private String getStudentProgramCode(){
        return programCode;
    }

    private void setStudentYearLevel(String level){
        this.yearLevel = level;
    }

    private String getStudentYearLevel(){
        return yearLevel;
    }

    private void setStudentGender(String gender){
        this.gender = gender;
    }

    private String getStudentGender(){
        return gender;
    }

    private Student(String id, String firstName, String lastName,String code, String level, String gender){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.programCode = code;
        this.yearLevel = level;
        this.gender = gender;
    }



}