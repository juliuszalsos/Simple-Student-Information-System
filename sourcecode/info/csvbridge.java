package info;

import java.sql.*;
import java.util.Random;

public class csvbridge {
    private static final String URL = "jdbc:sqlite:sourcecode/database/sis.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Enable Foreign Keys
            stmt.execute("PRAGMA foreign_keys = ON;");

            // 1. Create Tables
            stmt.execute("CREATE TABLE IF NOT EXISTS College (code TEXT PRIMARY KEY, name TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Program (code TEXT PRIMARY KEY, name TEXT, college_code TEXT, " +
                         "FOREIGN KEY(college_code) REFERENCES College(code) ON UPDATE CASCADE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Student (id TEXT PRIMARY KEY, first_name TEXT, last_name TEXT, " +
                         "program_code TEXT, year TEXT, gender TEXT, " +
                         "FOREIGN KEY(program_code) REFERENCES Program(code) ON UPDATE CASCADE)");

            // 2. Check if we need to pre-populate
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM Program");
            if (rs.next() && rs.getInt(1) == 0) {
                prepopulate(conn);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void prepopulate(Connection conn) throws SQLException {
        String[] programs = {
            "BSCS", "BSIT", "BSCA", "BSIS", "BSN", "BSEcon", "BSCE", "BSCpE", "BSEcE", "BSME",
            "BSMetE", "BSBio (Microbiology)", "BSBio (Animal Biology)", "BSBio (Plant Biology)", 
            "BSBio (Marine Biology)", "BSBio (Biodiversity)", "BSChem", "BSPhy", "BSHM", 
            "BSPsych", "BAPsych", "BAEng", "BAFil", "BSEd Math", "BSEd English", "BSEd", 
            "BSEE", "BSStat", "BSAccountancy", "BSEdSH"
        };

        // Create a default College so programs are visible
        PreparedStatement pCol = conn.prepareStatement("INSERT OR IGNORE INTO College VALUES (?, ?)");
        pCol.setString(1, "MSUIIT");
        pCol.setString(2, "MSU-Iligan Institute of Technology");
        pCol.executeUpdate();

        // Insert 30 Programs
        PreparedStatement pProg = conn.prepareStatement("INSERT INTO Program VALUES (?, ?, 'MSUIIT')");
        for (String p : programs) {
            pProg.setString(1, p);
            pProg.setString(2, "Department of " + p);
            pProg.executeUpdate();
        }

        // Insert 6,767 Students (Batch Mode for speed)
        conn.setAutoCommit(false);
        PreparedStatement pStu = conn.prepareStatement("INSERT INTO Student VALUES (?, ?, ?, ?, ?, ?)");
        Random rand = new Random();
        String[] genders = {"Male", "Female", "Other"};

        for (int i = 1; i <= 6767; i++) {
            pStu.setString(1, String.format("2024-%04d", i));
            pStu.setString(2, "StudentFN" + i);
            pStu.setString(3, "StudentLN" + i);
            pStu.setString(4, programs[rand.nextInt(programs.length)]);
            pStu.setString(5, (rand.nextInt(4) + 1) + "");
            pStu.setString(6, genders[rand.nextInt(3)]);
            pStu.addBatch();
        }
        pStu.executeBatch();
        conn.commit();
        conn.setAutoCommit(true);
    }
}