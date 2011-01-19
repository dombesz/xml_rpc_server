package rpcserver;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class dbconn {

    public static String username,  password,  dbtype,  dbname,  port;
    public static Connection conn;

    public static Connection getConn(String dbtype, String dbname, String username, String password, String port) {
        Connection conn1 = null;
        //System.out.print(" "+username+" "+ password + dbtype + dbname + port);
        if (dbtype.equals("postgresql")) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException cnfe) {
                System.out.println("\nCouldn't find driver: org.postgresql.Driver");

            }

            try {
                conn1 = DriverManager.getConnection("jdbc:postgresql://localhost:" + port + "/" + dbname,
                        username, password);
            } catch (SQLException se) {
                System.out.println("\nCouldn't connect . Stack trace:\n");
                se.printStackTrace();

            }
        }
        if (dbtype.equals("mysql")) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException cnfe) {
                System.out.println("\nCouldn't find driver: com.mysql.jdbc.Driver");


            }

            try {
                conn1 = DriverManager.getConnection("jdbc:mysql://localhost/" + dbname,
                        username, password);
            } catch (SQLException se) {
                System.out.println("\nCouldn't connect . Stack trace:\n");
                se.printStackTrace();

            }
        }

        return conn1;
    }

    public static boolean connectionIsSucces() {
        Connection conn2 = null;
        try {

            conn2 = DriverManager.getConnection("jdbc:" + dbtype + "://localhost:" + port + "/" + dbname,
                    username, password);

        } catch (SQLException se) {
            return false;
        }

        conn = conn2;
        return true;


    }

    public static void loadSettings() {
        try {

            Properties properties = new Properties();
            FileInputStream fis = null;

            fis = new FileInputStream("database-configuration.xml");
            try {
                properties.loadFromXML(fis);

                username = properties.getProperty("username");
                password = properties.getProperty("password");
                dbtype = properties.getProperty("dbtype");
                port = properties.getProperty("port");
                dbname = properties.getProperty("dbname");

                fis.close();
            } catch (IOException e) {
                username = "";
                password = "";
                dbtype = "";
                port = "";
                dbname = "";
            }

        } catch (FileNotFoundException ex) {
            username = "";
            password = "";
            dbtype = "";
            port = "";
            dbname = "";
        }


    }

    public static void saveSettings() {
        try {
            Properties properties = new Properties();
            properties.setProperty("username", username);
            properties.setProperty("password", password);
            properties.setProperty("dbtype", dbtype);
            properties.setProperty("port", port);
            properties.setProperty("dbname", dbname);
            File fs = new File("database-configuration.xml");
            fs.createNewFile();
            FileOutputStream fos = new FileOutputStream(fs);
            properties.storeToXML(fos, "Database Configuration", "UTF-8");
        } catch (IOException ex) {
            System.out.print("\nFile save exception while saving database settings!");
        }

    }

    public static String getQueryasXml(String query) throws Exception {
        ResultSet rs = null;
        Statement stmt = null;
        Connection con = null;

        //Initiating a new Document Builder for the Xml answer
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();



        // connection

        con = conn;

        stmt = con.createStatement();
        rs = stmt.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        DatabaseMetaData dbmd = con.getMetaData();


        Element results = doc.createElement("Table");
        Integer colCount = rsmd.getColumnCount();
        results.setAttribute("colCount", colCount.toString());
        results.setAttribute("name", rsmd.getTableName(1));
        doc.appendChild(results);
        Element tbInfo = doc.createElement("tableInfo");
        Element clInfo = doc.createElement("columnInfo");
        Element pkInfo = doc.createElement("pkeyInfo");
        Element indInfo = doc.createElement("indexInfo");
        ResultSet rs2;
        
        try {
            rs2 = dbmd.getPrimaryKeys(null, null, rsmd.getTableName(1));
            while (rs2.next()) {
                if (!rs2.getString("COLUMN_NAME").equals("chunk_id") && !rs2.getString("COLUMN_NAME").equals("chunk_seq")) {
                    Element pk = doc.createElement("primarykey");
                    pk.setAttribute("count", String.valueOf(rs2.getRow()));
                    pk.appendChild(doc.createTextNode(rs2.getString("COLUMN_NAME")));
                    pkInfo.appendChild(pk);
                }
            }
        } catch (Exception e) {
        }
        try {
            rs2 = dbmd.getIndexInfo(null, null, rsmd.getTableName(1), false, false);
            while (rs2.next()) {
                Element ind = doc.createElement("index");
                ind.setAttribute("count", String.valueOf(rs2.getRow()));
                ind.setAttribute("name", rs2.getString("INDEX_NAME"));
                ind.setAttribute("sequence", rs2.getString("ASC_OR_DESC"));
                ind.appendChild(doc.createTextNode(rs2.getString("COLUMN_NAME")));
                indInfo.appendChild(ind);
            }
        } catch (Exception e) {
        }

        for (int i = 1; i <= colCount; i++) {
            Element column = doc.createElement("column");
            Element info = doc.createElement("name");
            String value = rsmd.getColumnLabel(i);
            info.appendChild(doc.createTextNode(value));
            column.appendChild(info);
            info = doc.createElement("type");
            value = rsmd.getColumnTypeName(i);
            info.appendChild(doc.createTextNode(value));
            column.appendChild(info);
            info = doc.createElement("length");
            value = String.valueOf(rsmd.getColumnDisplaySize(i));
            info.appendChild(doc.createTextNode(value));
            column.appendChild(info);
            info = doc.createElement("null");
            value = String.valueOf(rsmd.isNullable(i));
            info.appendChild(doc.createTextNode(value));
            column.appendChild(info);
            info = doc.createElement("autoinc");
            value = String.valueOf(rsmd.isAutoIncrement(i));
            info.appendChild(doc.createTextNode(value));
            column.appendChild(info);
            column.setAttribute("count", String.valueOf(i));
            clInfo.appendChild(column);

        }
        tbInfo.appendChild(clInfo);
        tbInfo.appendChild(pkInfo);
        tbInfo.appendChild(indInfo);
        results.appendChild(tbInfo);



        Element rows = doc.createElement("rowInfo");


        Integer i = 0;

        while (rs.next()) {
            i++;
            Element row = doc.createElement("row");
            row.setAttribute("count", i.toString());
            rows.appendChild(row);
            for (int ii = 1; ii <= colCount; ii++) {
                String columnName = rsmd.getColumnLabel(ii);

                String value = null;
                if (rs.getObject(ii) != null) {
                    value = rs.getObject(ii).toString();
                    value = value.trim();

                }
                Element node = doc.createElement(columnName);
                if (value != null) {
                    node.appendChild(doc.createTextNode(value));
                }
                row.appendChild(node);
            }
        }
        rows.setAttribute("rowCount", i.toString());
        results.appendChild(rows);
        return (getDocumentAsXml(doc));


    }

    private static String getDocumentAsXml(Document doc)
            throws TransformerConfigurationException, TransformerException {
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        java.io.StringWriter sw = new java.io.StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        return sw.toString();
    }

    public static String getTableNames() throws Exception {
        String result = null;

        try {
            if (dbtype.equals("postgresql")) {
                result = getQueryasXml("select tablename" +
                        " from pg_tables where tableowner='" + dbname + "' AND schemaname='public'");
            }
            if (dbtype.equals("mysql")) {
                result = getQueryasXml("SELECT table_name as tablename " +
                        "FROM information_schema.tables WHERE TABLE_SCHEMA = '" + dbname + "'");
            }
        } catch (Exception ex) {
            throw ex;
        }



        return result;
    }

    public static int setUpdate(String query) throws Exception {


        Statement stmt = null;
        //Connection con = getConn(dbtype, dbname, username, password, port);
        Connection con = conn;
        try {

            // connection
            stmt = con.createStatement();
            //System.out.print("\n"+query);
            int i = stmt.executeUpdate(query);

            return i;
        } catch (Exception ex) {
            throw ex;
        }

    }

    public static int getTableRowCount(String table) throws SQLException {
        ResultSet rs = null;
        Statement stmt = null;
        Connection con = conn;
        stmt = con.createStatement();
        rs = stmt.executeQuery("Select count(*) As szam From " + table);
        rs.next();
        return rs.getInt("szam");
    }
}
