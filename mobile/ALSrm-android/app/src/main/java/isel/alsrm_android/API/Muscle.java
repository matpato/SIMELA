package isel.alsrm_android.API;

public class Muscle {

    int Id;
    String Name;
    String Abbreviation;

    public Muscle(int id, String name, String abbreviation) {
        Id = id;
        Name = name;
        Abbreviation = abbreviation;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAbbreviation() {
        return Abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        Abbreviation = abbreviation;
    }
}
