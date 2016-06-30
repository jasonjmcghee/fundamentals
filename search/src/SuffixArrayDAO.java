/**
 * Created by jmcghee on 6/28/16.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SuffixArrayDAO {

    public static SuffixArray read(String name) throws IOException, SQLException, ClassNotFoundException {
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Connection c = null;
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();

        ResultSet rs = statement.executeQuery( "SELECT SUFFIX, LCP FROM " + name + ";" );

        ArrayList<Integer> indices = new ArrayList<>();
        ArrayList<Integer> lcps = new ArrayList<>();
        while ( rs.next() ) {
            indices.add(rs.getInt(1));
            lcps.add(rs.getInt(2));
        }

        Integer[] _lcps = new Integer[lcps.size()];
        lcps.toArray(_lcps);

        Integer[] _indices = new Integer[indices.size()];
        lcps.toArray(_indices);

        ResultSet rsText = statement.executeQuery( "SELECT \"FULL\" FROM " + name + "_TEXT;" );

        String original = "";
        while (rsText.next()) original = rsText.getString(1);
        SuffixArray s = new SuffixArray(original, _indices, _lcps);

        statement.close();
        c.close();
        return s;
    }

    public static void store(String name, SuffixArray sarray) throws IOException, SQLException, ClassNotFoundException {
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Connection c = null;
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();

        String sql = "CREATE TABLE " + name +
                " (ID INT PRIMARY KEY     NOT NULL," +
                " SUFFIX          INT     NOT NULL," +
                " LCP             INT     NOT NULL)";

        String sqlText = "CREATE TABLE " + name + "_TEXT" +
                " (ID INT PRIMARY KEY     NOT NULL," +
                " FULL            TEXT    NOT NULL)";
        try {
            statement.executeUpdate(sql);
            statement.executeUpdate(sqlText);
        } catch (SQLException e) {
            System.out.println(e);
        }

        for (int i = 0; i < sarray.length(); i++) {
            sql = "INSERT INTO " + name + " (ID, SUFFIX, LCP) " +
                    "VALUES (" + i + ", " + sarray.getIndex(i) + ", " + sarray.longestCommonPrefix(i) + ");";
            try {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        sql = "INSERT INTO " + name + "_TEXT (ID, FULL) " +
                "VALUES (1, '" + sarray.getText().replaceAll("'", "''") + "');";
        statement.executeUpdate(sql);

        statement.close();
        c.close();
    }

    public static void drop(String name) throws IOException, SQLException, ClassNotFoundException {
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Connection c = null;
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();

        String sql = "DROP TABLE " + name;
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {}
        statement.close();
        c.close();
    }

    public static ResultSet getResultSet(String sql, Connection c) throws IOException, SQLException, ClassNotFoundException {
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();

        ResultSet rs;
        try {
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            rs = null;
        }
        statement.close();
        return rs;
    }

    public static SuffixArray buildFromFile (String fileName, String table) {
        StringBuilder fullText = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            for (String line : stream.collect(Collectors.toList())) {
                fullText.append(line + " ");
            }
        } catch (IOException e) {}
        SuffixArray sarray = new SuffixArray(new String(fullText));

        try {
            SuffixArrayDAO.store(table, sarray);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.out.println(e);
            System.exit(1);
        }
        return sarray;
    }

    public static String longestRepeatedSubstring (String tableName, String text) throws ClassNotFoundException, SQLException, IOException {
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Connection c = null;
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();
        ResultSet rs = statement.executeQuery("SELECT SUFFIX, MAX(LCP) FROM " + tableName + ";");
        int maxIndex = 0;
        int lcp = 0;
        while ( rs.next() ) {
            maxIndex = rs.getInt("SUFFIX");
            lcp = rs.getInt("MAX(LCP)");
        }
        c.close();
        return text.substring(maxIndex, maxIndex + lcp);
    }

    public static ArrayList<String> longestRepeatedSubstrings (String tableName, int number, String text) throws ClassNotFoundException, SQLException, IOException {
        ArrayList<String> substrings = new ArrayList<>();
        String query = "SELECT SUFFIX, LCP FROM " + tableName + " ORDER BY LCP DESC LIMIT " + number + ";";
        String url = "jdbc:sqlite:identifier.db";
        Class.forName("org.sqlite.JDBC");
        Connection c = null;
        Statement statement = null;
        c = DriverManager.getConnection(url);
        statement = c.createStatement();

        ResultSet rs = statement.executeQuery(query);
        int maxIndex;
        int lcp;
        while ( rs.next() ) {
            maxIndex = rs.getInt("SUFFIX");
            lcp = rs.getInt("LCP");
            substrings.add(text.substring(maxIndex, maxIndex + lcp));
        }
        statement.close();
        c.close();
        return substrings;
    }
}
