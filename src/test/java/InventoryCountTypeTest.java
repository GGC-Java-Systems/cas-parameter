
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.cas.parameter.InventoryCountType;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.h2.tools.RunScript;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author mnv
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryCountTypeTest {

    static GRiderCAS instance;
    static InventoryCountType poTrans;
    static Connection conn = null;
    static String currentTransaction;
    static String currentDescription;
    static String currentDepartmentID;
    static String currentPeriod;
    static String currentIncluded;
    static String currentnQuantity;
    static String currentAllowBalanceForward;
    static String currentRecordStat;

    @BeforeAll
    public static void setUpClass() throws SQLException, GuanzonException, IOException {
        System.out.println("setUpClass()");
        instance = new GRiderCAS();
        if (!instance.loadEnv("gRider")) {
            System.err.println(instance.getMessage());
            System.exit(1);
        }

        if (!instance.logUser("gRider", "M001250015")) {
            System.err.println(instance.getMessage());
            System.exit(1);
        }

        loadCorePrimary();
        String path;
        String lsTemp;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = "D:/GGC_Maven_Systems";
            lsTemp = "D:/temp";
        } else {
            path = "/srv/GGC_Maven_Systems";
            lsTemp = "/srv/temp";
        }
        System.setProperty("sys.default.path.config", path);
        System.setProperty("sys.default.path.metadata", System.getProperty("sys.default.path.config") + "/config/metadata/new/");
        System.setProperty("sys.default.path.temp", lsTemp);

        if (!loadProperties()) {
            System.err.println("Unable to load config.");
            System.exit(1);
        } else {
            System.out.println("Config file loaded successfully.");
        }

        poTrans = new ParamControllers(instance, null).InventoryCountType();
        poTrans.setRecordStatus("10");
        poTrans.setWithUI(false);
    }

    private static boolean loadProperties() {
        try {
            Properties po_props = new Properties();
            po_props.load(new FileInputStream(System.getProperty("sys.default.path.config") + "/config/cas.properties"));

            //industry ids
            System.setProperty("sys.main.industry", po_props.getProperty("sys.main.industry"));
            System.setProperty("sys.general.industry", po_props.getProperty("sys.general.industry"));

            //department ids
            System.setProperty("sys.dept.finance", po_props.getProperty("sys.dept.finance"));
            System.setProperty("sys.dept.procurement", po_props.getProperty("sys.dept.procurement"));

            //property for selected industry/company/category
            System.setProperty("user.selected.industry", po_props.getProperty("user.selected.industry"));
            System.setProperty("user.selected.category", po_props.getProperty("user.selected.category"));
            System.setProperty("user.selected.company", po_props.getProperty("user.selected.company"));

            //properties for client token and attachments
            System.setProperty("sys.default.client.token", System.getProperty("sys.default.path.config") + "/client.token");
            System.setProperty("sys.default.access.token", System.getProperty("sys.default.path.config") + "/access.token");

            System.setProperty("sys.default.path.temp.attachments", po_props.getProperty("sys.default.path.temp.attachments"));

            System.setProperty("allowed.department", po_props.getProperty("allowed.department"));

            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @AfterAll
    public static void tearDownClass() {
        killdbcon();

        // Clear system properties
        System.clearProperty("sys.default.path.config");
        System.clearProperty("sys.default.path.metadata");
        System.clearProperty("sys.default.path.temp");

        System.clearProperty("sys.main.industry");
        System.clearProperty("sys.general.industry");

        System.clearProperty("sys.dept.finance");
        System.clearProperty("sys.dept.procurement");

        System.clearProperty("user.selected.industry");
        System.clearProperty("user.selected.category");
        System.clearProperty("user.selected.company");

        System.clearProperty("sys.default.client.token");
        System.clearProperty("sys.default.access.token");

        System.clearProperty("sys.default.path.temp.attachments");

        System.clearProperty("allowed.department");

        System.out.println("System properties cleared.");
    }

    private static void killdbcon() {

        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    @Order(1)
    public void testNewRecord() throws SQLException, GuanzonException, CloneNotSupportedException {
        JSONObject loJSON;
        loJSON = poTrans.newRecord();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        poTrans.getModel().setInventoryCountID(poTrans.getModel().getNextCode());
        currentTransaction = poTrans.getModel().getInventoryCountID();
        loJSON = poTrans.getModel().setDescription("All Items");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentDescription = poTrans.getModel().getDescription();
        try {
            poTrans.searchRecordDepartment("026", true);
            if (!poTrans.getModel().getDepartmentID().equals("026")) {
                System.err.println("field is not equal to setted value");
                Assert.fail();
            }
        } catch (ExceptionInInitializerError ex) {
            poTrans.getModel().setDepartmentID("026");
        }
        currentDepartmentID = poTrans.getModel().getDepartmentID();

        poTrans.getModel().setIndustryCode(instance.getIndustry());
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }

        if (!poTrans.getModel().getIndustryCode().equals(instance.getIndustry())) {
            System.err.println("field is not equal to setted value");
            Assert.fail();
        }

        loJSON = poTrans.getModel().setPeriod("1");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentPeriod = poTrans.getModel().getPeriod();

        loJSON = poTrans.getModel().setIncluded("AI");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentIncluded = poTrans.getModel().getIncluded();

        loJSON = poTrans.getModel().setQuantity(0);
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentnQuantity = String.valueOf(poTrans.getModel().getQuantity());

        loJSON = poTrans.getModel().setAllowBalanceForward("1");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentAllowBalanceForward = poTrans.getModel().getAllowBalanceForward();

        //make sure saving not active
        if (poTrans.getModel().isRecordActive()) {
            System.err.println("field is not equal to setted value");
            Assert.fail();
        }
        currentRecordStat = poTrans.getModel().getRecordStatus();

        loJSON = poTrans.saveRecord();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
    }

    @Test
    @Order(2)
    public void testOpenRecord() throws SQLException, GuanzonException, CloneNotSupportedException {
        JSONObject loJSON;

        loJSON = poTrans.openRecord(currentTransaction);
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }

        System.out.println(" RECORD------------------- ");
        System.out.println("Record ID : " + poTrans.getModel().getInventoryCountID());
        System.out.println("Description : " + poTrans.getModel().getDescription());
        System.out.println("Department : " + poTrans.getModel().Department().getDescription());
        System.out.println("Industry : " + poTrans.getModel().Industry().getDescription());
        System.out.println("Period : " + poTrans.getModel().getPeriod());
        System.out.println("Included : " + poTrans.getModel().getIncluded());
        System.out.println("Quantity : " + poTrans.getModel().getQuantity());
        System.out.println("Allow Balance Forward : " + poTrans.getModel().isAllowBalanceForward());
        System.out.println("Record Stat : " + poTrans.getModel().isRecordActive());
        System.out.println("Modified Id : " + poTrans.getModel().getModifyingId());
        System.out.println("Modified Date : " + poTrans.getModel().getModifiedDate());
        System.out.println("");

    }

    @Test
    @Order(3)
    public void testUpdateRecord() throws SQLException, GuanzonException, CloneNotSupportedException {
        JSONObject loJSON;

        loJSON = poTrans.updateRecord();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }

        loJSON = poTrans.getModel().setDescription("All Items Updated");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        if (currentDescription.equals(poTrans.getModel().getDescription())) {
            System.err.println("field is not equal to setted value");
            Assert.fail();
        }

        loJSON = poTrans.getModel().setPeriod("2");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        if (currentPeriod.equals(poTrans.getModel().getPeriod())) {
            System.err.println("field is not equal to setted value");
            Assert.fail();
        }

    }

    @Test
    @Order(4)
    public void testDeactivateRecord() throws SQLException, GuanzonException, CloneNotSupportedException, ParseException {
        JSONObject loJSON;

        loJSON = poTrans.openRecord(currentTransaction);
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        loJSON = poTrans.getModel().setRecordStatus("0");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        loJSON = poTrans.deactivateRecord();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
    }

    @Test
    @Order(5)
    public void testActivateRecord() throws SQLException, GuanzonException, CloneNotSupportedException, ParseException {
        JSONObject loJSON;

        loJSON = poTrans.openRecord(currentTransaction);
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        loJSON = poTrans.getModel().setRecordStatus("1");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        loJSON = poTrans.activateRecord();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
    }

    @Test
    @Order(6)
    public void testNewRecordForgotDescription() throws SQLException, GuanzonException, CloneNotSupportedException {
        JSONObject loJSON;
        loJSON = poTrans.newRecord();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }

        System.out.println("Department open: " + poTrans.getModel().Department().getDescription());
        System.out.println("Industry open: " + poTrans.getModel().Industry().getDescription());
        loJSON = poTrans.getModel().setDescription("");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentDescription = poTrans.getModel().getDescription();
        try {
            poTrans.searchRecordDepartment("026", true);
            if (!poTrans.getModel().getDepartmentID().equals("026")) {
                System.err.println("field is not equal to setted value");
                Assert.fail();
            }
        } catch (ExceptionInInitializerError | NoClassDefFoundError ex) {
            poTrans.getModel().setDepartmentID("026");
        }
        currentDepartmentID = poTrans.getModel().getDepartmentID();

        poTrans.getModel().setIndustryCode(instance.getIndustry());
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }

        if (!poTrans.getModel().getIndustryCode().equals(instance.getIndustry())) {
            System.err.println("field is not equal to setted value");
            Assert.fail();
        }

        loJSON = poTrans.getModel().setPeriod("1");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentPeriod = poTrans.getModel().getPeriod();

        loJSON = poTrans.getModel().setIncluded("AI");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentIncluded = poTrans.getModel().getIncluded();

        loJSON = poTrans.getModel().setAllowBalanceForward("1");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentAllowBalanceForward = poTrans.getModel().getAllowBalanceForward();

        loJSON = poTrans.saveRecord();
        if ("success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
    }

    @Test
    @Order(7)
    public void testsearchRecord() throws SQLException, GuanzonException, CloneNotSupportedException {
        JSONObject loJSON;
        try {

            loJSON = poTrans.searchRecord(currentTransaction, true);
            if ("!success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            poTrans.setRecordStatus("1");
            loJSON = poTrans.searchRecord(currentTransaction, true);
            if ("!success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            loJSON = poTrans.searchRecord("test to fail", true);
            if ("success".equals((String) loJSON.get("result"))) {
                System.err.println((String) loJSON.get("message"));
                Assert.fail();
            }
            System.out.println(" RECORD------------------- ");
            System.out.println("Record ID : " + poTrans.getModel().getInventoryCountID());
            System.out.println("Description : " + poTrans.getModel().getDescription());
            System.out.println("Department : " + poTrans.getModel().Department().getDescription());
            System.out.println("Industry : " + poTrans.getModel().Industry().getDescription());

            System.out.println("Department open: " + poTrans.getModel().Department().getDescription());
            System.out.println("Industry open: " + poTrans.getModel().Industry().getDescription());
            System.out.println("Period : " + poTrans.getModel().getPeriod());
            System.out.println("Included : " + poTrans.getModel().getIncluded());
            System.out.println("Quantity : " + poTrans.getModel().getQuantity());
            System.out.println("Allow Balance Forward : " + poTrans.getModel().isAllowBalanceForward());
            System.out.println("Record Stat : " + poTrans.getModel().isRecordActive());
            System.out.println("");

        } catch (ExceptionInInitializerError ex) {

        }

    }

    @Test
    @Order(8)
    public void testNewRecordForgotIncluded() throws SQLException, GuanzonException, CloneNotSupportedException {
        JSONObject loJSON;
        loJSON = poTrans.newRecord();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }

        loJSON = poTrans.getModel().setDescription("to fail test");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentDescription = poTrans.getModel().getDescription();
        try {
            poTrans.searchRecordDepartment("026", true);
            if (!poTrans.getModel().getDepartmentID().equals("026")) {
                System.err.println("field is not equal to setted value");
                Assert.fail();
            }
        } catch (ExceptionInInitializerError | NoClassDefFoundError ex) {
            poTrans.getModel().setDepartmentID("026");
        }
        currentDepartmentID = poTrans.getModel().getDepartmentID();

        poTrans.getModel().setIndustryCode(instance.getIndustry());
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }

        if (!poTrans.getModel().getIndustryCode().equals(instance.getIndustry())) {
            System.err.println("field is not equal to setted value");
            Assert.fail();
        }

        loJSON = poTrans.getModel().setPeriod("1");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentPeriod = poTrans.getModel().getPeriod();

        loJSON = poTrans.getModel().setIncluded("");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentIncluded = poTrans.getModel().getIncluded();

        loJSON = poTrans.getModel().setAllowBalanceForward("1");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentAllowBalanceForward = poTrans.getModel().getAllowBalanceForward();

        
        loJSON = poTrans.saveRecord();
        if ("success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
    }

    @Test
    @Order(8)
    public void testNewRecordForgotQuantity() throws SQLException, GuanzonException, CloneNotSupportedException {
        JSONObject loJSON;
        loJSON = poTrans.newRecord();
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }

        loJSON = poTrans.getModel().setDescription("to fail test");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentDescription = poTrans.getModel().getDescription();
        try {
            poTrans.searchRecordDepartment("026", true);
            if (!poTrans.getModel().getDepartmentID().equals("026")) {
                System.err.println("field is not equal to setted value");
                Assert.fail();
            }
        } catch (ExceptionInInitializerError | NoClassDefFoundError ex) {
            poTrans.getModel().setDepartmentID("026");
        }
        currentDepartmentID = poTrans.getModel().getDepartmentID();

        poTrans.getModel().setIndustryCode(instance.getIndustry());
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }

        if (!poTrans.getModel().getIndustryCode().equals(instance.getIndustry())) {
            System.err.println("field is not equal to setted value");
            Assert.fail();
        }

        loJSON = poTrans.getModel().setPeriod("1");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentPeriod = poTrans.getModel().getPeriod();

        loJSON = poTrans.getModel().setIncluded("RX");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentIncluded = poTrans.getModel().getIncluded();

        loJSON = poTrans.getModel().setQuantity(0);
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentnQuantity = String.valueOf(poTrans.getModel().getQuantity());

        loJSON = poTrans.getModel().setAllowBalanceForward("1");
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
        currentAllowBalanceForward = poTrans.getModel().getAllowBalanceForward();

        loJSON = poTrans.saveRecord();
        if ("success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
            Assert.fail();
        }
    }

    private static void loadCorePrimary() throws IOException, SQLException {
        // 1. Get the raw connection from your poCon object																									
        conn = instance.getGConnection().getConnection();

        FileReader schemaReader1 = new FileReader("test-data/inventory_count_type_schema.sql");
        FileReader schemaReader2 = new FileReader("test-data/industry_schema.sql");
        FileReader schemaReader3 = new FileReader("test-data/department_schema.sql");
        FileReader dataReader1 = new FileReader("test-data/industry_data.sql");
        FileReader dataReader2 = new FileReader("test-data/department_data.sql");

        //conn.setAutoCommit(false); // Start a single giant transaction																									
        // 2. Use RunScript to stream the files																									
        RunScript.execute(conn, schemaReader1);
        RunScript.execute(conn, schemaReader2);
        RunScript.execute(conn, schemaReader3);
        RunScript.execute(conn, dataReader1);
        RunScript.execute(conn, dataReader2);
        //conn.commit();             // Save everything at once																									
        //conn.setAutoCommit(true);  // Turn it back on																									
    }
}
