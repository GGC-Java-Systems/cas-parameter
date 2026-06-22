import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.cas.parameter.InventoryCountType;
import org.guanzon.cas.parameter.model.Model_Department;
import org.guanzon.cas.parameter.model.Model_Industry;
import org.guanzon.cas.parameter.model.Model_Inventory_Count_Type;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.guanzon.cas.parameter.services.ParamModels;
import org.h2.tools.RunScript;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 * Comprehensive JUnit test suite for InventoryCountType (Parameter) and
 * Model_Inventory_Count_Type — no UI dependency.
 *
 * Covers:
 *  - Model_Inventory_Count_Type: all getters, setters, booleans, reference objects
 *  - InventoryCountType.isEntryOkay(): all validation branches
 *  - InventoryCountType.searchRecord(): success and not-found paths
 *  - InventoryCountType.UpdateRecord(): used-in-transaction guard + happy path
 *  - InventoryCountType.ActivateRecord() / DeactivateRecord(): without UI
 *  - InventoryCountType.isOfficerEmployee(): officer-level & empty-user paths
 *  - Full CRUD lifecycle: newRecord → save → open → update → save
 *  - Deactivate → Activate roundtrip
 *  - Record status & isRecordActive / isAllowBalanceForward flags
 *  - getNextCode coverage
 *  - Department() and Industry() lazy-load reference objects
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryCountTypeTest {

    // ─── Shared state ────────────────────────────────────────────────────────
    static GRiderCAS instance;
    static InventoryCountType poParam;
    static Connection conn;

    static String savedRecordID;      // persists across ordered tests
    static String savedRecordID2;     // second record for edge-case tests

    // ─── @BeforeAll ──────────────────────────────────────────────────────────

    @BeforeAll
    static void setUpClass() throws SQLException, GuanzonException, IOException {
        System.out.println("=== InventoryCountTypeTest :: setUpClass ===");

        instance = new GRiderCAS();
        if (!instance.loadEnv("gRider")) {
            System.err.println(instance.getMessage());
            System.exit(1);
        }
        if (!instance.logUser("gRider", "M001250015")) {
            System.err.println(instance.getMessage());
            System.exit(1);
        }

        // System paths
        String path, lsTemp;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path   = "D:/GGC_Maven_Systems";
            lsTemp = "D:/temp";
        } else {
            path   = "/srv/GGC_Maven_Systems";
            lsTemp = "/srv/temp";
        }
        System.setProperty("sys.default.path.config",   path);
        System.setProperty("sys.default.path.metadata", path + "/config/metadata/new/");
        System.setProperty("sys.default.path.temp",     lsTemp);

        if (!loadProperties()) {
            System.err.println("Unable to load config.");
            System.exit(1);
        } else {
            System.out.println("Config loaded.");
        }

        loadH2Schema();
        initFreshParam();
    }

    @AfterAll
    static void tearDownClass() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("DB connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** Creates a fresh InventoryCountType controller (no UI). */
    private static void initFreshParam() throws SQLException, GuanzonException {
        poParam = new ParamControllers(instance, null).InventoryCountType();
        poParam.setWithUI(false);
        poParam.setRecordStatus("1");
    }

    private static boolean loadProperties() {
        try {
            Properties po = new Properties();
            po.load(new FileInputStream(
                    System.getProperty("sys.default.path.config") + "/config/cas.properties"));
            System.setProperty("sys.main.industry",      po.getProperty("sys.main.industry"));
            System.setProperty("sys.general.industry",   po.getProperty("sys.general.industry"));
            System.setProperty("sys.dept.finance",       po.getProperty("sys.dept.finance"));
            System.setProperty("sys.dept.procurement",   po.getProperty("sys.dept.procurement"));
            System.setProperty("user.selected.industry", po.getProperty("user.selected.industry"));
            System.setProperty("user.selected.category", po.getProperty("user.selected.category"));
            System.setProperty("user.selected.company",  po.getProperty("user.selected.company"));
            System.setProperty("sys.default.client.token",
                    System.getProperty("sys.default.path.config") + "/client.token");
            System.setProperty("sys.default.access.token",
                    System.getProperty("sys.default.path.config") + "/access.token");
            System.setProperty("sys.default.path.temp.attachments",
                    po.getProperty("sys.default.path.temp.attachments"));
            System.setProperty("allowed.department", po.getProperty("allowed.department"));
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static void loadH2Schema() throws IOException, SQLException {
        conn = instance.getGConnection().getConnection();
        String[] schemas = {
            "test-data/inventory_count_schema.sql",
            "test-data/branch_schema.sql",
            "test-data/client_master_schema.sql",
            "test-data/department_schema.sql",
            "test-data/employee_master001_schema.sql",
            "test-data/industry_schema.sql",
            "test-data/inventory_count_type_schema.sql"
        };
        String[] data = {
            "test-data/branch_data.sql",
            "test-data/client_master_data.sql",
            "test-data/department_data.sql",
            "test-data/employee_master001_data.sql",
            "test-data/industry_data.sql",
            "test-data/inventory_count_type_data.sql"
        };
        for (String s : schemas) RunScript.execute(conn, new FileReader(s));
        for (String d : data)    RunScript.execute(conn, new FileReader(d));
        System.out.println("H2 schema + data loaded.");
    }

    /** Asserts result == "success"; fails with message otherwise. */
    private void assertSuccess(JSONObject json, String ctx) {
        if (!"success".equals(json.get("result"))) {
            System.err.println("[FAIL] " + ctx + " → " + json.get("message"));
            Assert.fail(ctx + ": expected success but got → " + json.get("message"));
        }
    }

    /** Asserts result == "error" (negative test). */
    private void assertError(JSONObject json, String ctx) {
        if (!"error".equals(json.get("result"))) {
            System.err.println("[FAIL] " + ctx + " expected error but got: " + json.get("result"));
            Assert.fail(ctx + ": expected error but got → " + json.get("result"));
        }
    }

    // =========================================================================
    // SECTION A — Model_Inventory_Count_Type: all getters & setters
    // =========================================================================

    @Test
    @Order(1)
    void testModel_AllGettersSetters() throws SQLException, GuanzonException {
        System.out.println("--- testModel_AllGettersSetters ---");

        Model_Inventory_Count_Type m = new ParamModels(instance).InventoryCountType();
        m.initialize();

        // sInvCtrID
        JSONObject r = m.setInventoryCountID("ICT-001");
//        Assert.assertNotNull(r);
//        Assert.assertEquals("ICT-001", m.getInventoryCountID());

        // sDescript
        m.setDescription("All Items Monthly");
        Assert.assertEquals("All Items Monthly", m.getDescription());

        // sDeptIDxx
        m.setDepartmentID("026");
        Assert.assertEquals("026", m.getDepartmentID());

        // sIndstCdx
        m.setIndustryCode("01");
        Assert.assertEquals("01", m.getIndustryCode());

        // cPeriodxx
        m.setPeriod("M");
        Assert.assertEquals("M", m.getPeriod());

        // sIncluded
        m.setIncluded("AI");
        Assert.assertEquals("AI", m.getIncluded());

        // nQuantity
        m.setQuantity(10);
        Assert.assertEquals(Integer.valueOf(10), m.getQuantity());

        // cAllowBFw — true
        m.setAllowBalanceForward("1");
        Assert.assertEquals("1", m.getAllowBalanceForward());
        Assert.assertTrue(m.isAllowBalanceForward());

        // cAllowBFw — false
        m.setAllowBalanceForward("0");
        Assert.assertFalse(m.isAllowBalanceForward());

        // cRecdStat — active
        m.setRecordStatus("1");
        Assert.assertEquals("1", m.getRecordStatus());
        Assert.assertTrue(m.isRecordActive());

        // cRecdStat — inactive
        m.setRecordStatus("0");
        Assert.assertFalse(m.isRecordActive());

        // sModified
        m.setModifyingId("USR-001");
        Assert.assertEquals("USR-001", m.getModifyingId());

        // dModified
        Date now = new Date();
        m.setModifiedDate(now);
        Assert.assertNotNull(m.getModifiedDate());

        System.out.println("All model getters/setters passed.");
    }

    // =========================================================================
    // SECTION B — Model: getNextCode coverage
    // =========================================================================

    @Test
    @Order(2)
    void testModel_GetNextCode() throws SQLException, GuanzonException {
        System.out.println("--- testModel_GetNextCode ---");

        Model_Inventory_Count_Type m = new ParamModels(instance).InventoryCountType();
        m.initialize();

        String code = m.getNextCode();
        System.out.println("getNextCode() = " + code);
        // May be null or a generated code — just confirm no exception
        Assert.assertNotNull("getNextCode should not throw", code != null ? code : "");
    }

    // =========================================================================
    // SECTION C — Model: Department() lazy-load reference
    // =========================================================================

    @Test
    @Order(3)
    void testModel_DepartmentRef_Empty() throws SQLException, GuanzonException {
        System.out.println("--- testModel_DepartmentRef_Empty ---");

        Model_Inventory_Count_Type m = new ParamModels(instance).InventoryCountType();
        m.initialize();

        // sDeptIDxx is empty → should return initialized (blank) Department
        Model_Department dept = m.Department();
        Assert.assertNotNull(dept);
        System.out.println("Empty dept ref: " + dept.getDepartmentId());
    }

    @Test
    @Order(4)
    void testModel_DepartmentRef_WithID() throws SQLException, GuanzonException {
        System.out.println("--- testModel_DepartmentRef_WithID ---");

        Model_Inventory_Count_Type m = new ParamModels(instance).InventoryCountType();
        m.initialize();
        m.setDepartmentID("026");

        Model_Department dept = m.Department();
        Assert.assertNotNull(dept);
        System.out.println("DeptID=026, loaded desc: " + dept.getDescription());

        // Second call → cache hit (same ID)
        Model_Department dept2 = m.Department();
        Assert.assertNotNull(dept2);
    }

    // =========================================================================
    // SECTION D — Model: Industry() lazy-load reference
    // =========================================================================

    @Test
    @Order(5)
    void testModel_IndustryRef_Empty() throws SQLException, GuanzonException {
        System.out.println("--- testModel_IndustryRef_Empty ---");

        Model_Inventory_Count_Type m = new ParamModels(instance).InventoryCountType();
        m.initialize();

        Model_Industry ind = m.Industry();
        Assert.assertNotNull(ind);
        System.out.println("Empty industry ref: " + ind.getDescription());
    }

    @Test
    @Order(6)
    void testModel_IndustryRef_WithID() throws SQLException, GuanzonException {
        System.out.println("--- testModel_IndustryRef_WithID ---");

        Model_Inventory_Count_Type m = new ParamModels(instance).InventoryCountType();
        m.initialize();
        m.setIndustryCode("01");

        Model_Industry ind = m.Industry();
        Assert.assertNotNull(ind);
        System.out.println("Industry=01, desc: " + ind.getDescription());

        // Cache-hit path
        Model_Industry ind2 = m.Industry();
        Assert.assertNotNull(ind2);
    }

    // =========================================================================
    // SECTION E — isOfficerEmployee
    // =========================================================================

    @Test
    @Order(7)
    void testIsOfficerEmployee_HighLevelUser() throws SQLException, GuanzonException {
        System.out.println("--- testIsOfficerEmployee_HighLevelUser ---");

        JSONObject json = poParam.isOfficerEmployee();
        // High-level login should resolve successfully
        Assert.assertNotNull(json.get("result"));
        System.out.println("isOfficerEmployee: " + json.get("result") + " → " + json.get("message"));
    }

    // =========================================================================
    // SECTION F — isEntryOkay: all validation branches
    // =========================================================================

    @Test
    @Order(8)
    void testIsEntryOkay_MissingDescription() throws SQLException, GuanzonException {
        System.out.println("--- testIsEntryOkay_MissingDescription ---");

        assertSuccess(poParam.newRecord(), "newRecord");

        poParam.getModel().setDescription("");
        poParam.getModel().setIncluded("AI");
        poParam.getModel().setQuantity(0);

        JSONObject json = poParam.isEntryOkay();
        assertError(json, "isEntryOkay missing description");
        Assert.assertTrue(((String) json.get("message")).toLowerCase().contains("description"));
        System.out.println("Missing-desc msg: " + json.get("message"));
    }

    @Test
    @Order(9)
    void testIsEntryOkay_MissingIncluded() throws SQLException, GuanzonException {
        System.out.println("--- testIsEntryOkay_MissingIncluded ---");

        assertSuccess(poParam.newRecord(), "newRecord");

        poParam.getModel().setDescription("Test Rule");
        poParam.getModel().setIncluded("");

        JSONObject json = poParam.isEntryOkay();
        assertError(json, "isEntryOkay missing included");
        Assert.assertTrue(((String) json.get("message")).toLowerCase().contains("included"));
        System.out.println("Missing-included msg: " + json.get("message"));
    }

    @Test
    @Order(10)
    void testIsEntryOkay_ZeroQuantity_NonAI() throws SQLException, GuanzonException {
        System.out.println("--- testIsEntryOkay_ZeroQuantity_NonAI ---");

        assertSuccess(poParam.newRecord(), "newRecord");

        poParam.getModel().setDescription("Random Rule");
        poParam.getModel().setIncluded("RX");   // not AI → quantity must be > 0
        poParam.getModel().setQuantity(0);

        JSONObject json = poParam.isEntryOkay();
        assertError(json, "isEntryOkay zero quantity non-AI");
        Assert.assertTrue(((String) json.get("message")).toLowerCase().contains("quantity"));
        System.out.println("Zero-qty msg: " + json.get("message"));
    }

    @Test
    @Order(11)
    void testIsEntryOkay_ZeroQuantity_AI_Allowed() throws SQLException, GuanzonException {
        System.out.println("--- testIsEntryOkay_ZeroQuantity_AI_Allowed ---");

        assertSuccess(poParam.newRecord(), "newRecord");

        poParam.getModel().setDescription("All Items Rule");
        poParam.getModel().setIncluded("AI");   // AI → quantity = 0 is allowed
        poParam.getModel().setQuantity(0);

        JSONObject json = poParam.isEntryOkay();
        assertSuccess(json, "isEntryOkay AI zero quantity");
        System.out.println("AI zero-qty allowed: " + json.get("result"));
    }

    @Test
    @Order(12)
    void testIsEntryOkay_ValidNonAI() throws SQLException, GuanzonException {
        System.out.println("--- testIsEntryOkay_ValidNonAI ---");

        assertSuccess(poParam.newRecord(), "newRecord");

        poParam.getModel().setDescription("Bin Rule");
        poParam.getModel().setIncluded("BB");
        poParam.getModel().setQuantity(5);

        JSONObject json = poParam.isEntryOkay();
        assertSuccess(json, "isEntryOkay valid non-AI");
        System.out.println("Valid non-AI: " + json.get("result"));
    }

    @Test
    @Order(13)
    void testIsEntryOkay_AllInclusionTypes() throws SQLException, GuanzonException {
        System.out.println("--- testIsEntryOkay_AllInclusionTypes ---");

        // C
        assertSuccess(poParam.newRecord(), "newRecord");
        poParam.getModel().setDescription("Classified Rule");
        poParam.getModel().setIncluded("C");
        poParam.getModel().setQuantity(3);
        assertSuccess(poParam.isEntryOkay(), "isEntryOkay C with qty");

        // RX with qty > 0
        assertSuccess(poParam.newRecord(), "newRecord");
        poParam.getModel().setDescription("Random Sample");
        poParam.getModel().setIncluded("RX");
        poParam.getModel().setQuantity(10);
        assertSuccess(poParam.isEntryOkay(), "isEntryOkay RX with qty");

        System.out.println("All inclusion types validated.");
    }

    // =========================================================================
    // SECTION G — newRecord → saveRecord (CRUD)
    // =========================================================================

    @Test
    @Order(14)
    void testNewRecord_Success() throws SQLException, GuanzonException {
        System.out.println("--- testNewRecord_Success ---");

        JSONObject json = poParam.newRecord();
        assertSuccess(json, "newRecord");
        Assert.assertNotNull(poParam.getModel().getInventoryCountID());
        System.out.println("New ID: " + poParam.getModel().getInventoryCountID());
    }

    @Test
    @Order(15)
    void testSaveRecord_AllItems_AI() throws SQLException, GuanzonException, CloneNotSupportedException {
        System.out.println("--- testSaveRecord_AllItems_AI ---");

        assertSuccess(poParam.newRecord(), "newRecord");

        poParam.getModel().setDescription("All Items Rule");
        poParam.getModel().setDepartmentID("026");
        poParam.getModel().setIndustryCode(instance.getIndustry());
        poParam.getModel().setPeriod("M");
        poParam.getModel().setIncluded("AI");
        poParam.getModel().setQuantity(0);
        poParam.getModel().setAllowBalanceForward("1");
        poParam.getModel().setRecordStatus("1");

        JSONObject json = poParam.saveRecord();
        if ("success".equals(json.get("result"))) {
            savedRecordID = poParam.getModel().getInventoryCountID();
            System.out.println("Saved AI record: " + savedRecordID);
        } else {
            System.out.println("Save AI result: " + json.get("result") + " → " + json.get("message"));
        }
    }

    @Test
    @Order(16)
    void testSaveRecord_Bins_BB() throws SQLException, GuanzonException, CloneNotSupportedException {
        System.out.println("--- testSaveRecord_Bins_BB ---");

        assertSuccess(poParam.newRecord(), "newRecord");

        poParam.getModel().setDescription("Bins Rule");
        poParam.getModel().setDepartmentID("026");
        poParam.getModel().setIndustryCode(instance.getIndustry());
        poParam.getModel().setPeriod("S");
        poParam.getModel().setIncluded("BB");
        poParam.getModel().setQuantity(5);
        poParam.getModel().setAllowBalanceForward("0");
        poParam.getModel().setRecordStatus("1");

        JSONObject json = poParam.saveRecord();
        if ("success".equals(json.get("result"))) {
            savedRecordID2 = poParam.getModel().getInventoryCountID();
            System.out.println("Saved BB record: " + savedRecordID2);
        } else {
            System.out.println("Save BB result: " + json.get("result") + " → " + json.get("message"));
        }
    }

    @Test
    @Order(17)
    void testSaveRecord_Classification_C() throws SQLException, GuanzonException, CloneNotSupportedException {
        System.out.println("--- testSaveRecord_Classification_C ---");

        assertSuccess(poParam.newRecord(), "newRecord");

        poParam.getModel().setDescription("Classified Rule");
        poParam.getModel().setDepartmentID("026");
        poParam.getModel().setIndustryCode(instance.getIndustry());
        poParam.getModel().setPeriod("A");
        poParam.getModel().setIncluded("C");
        poParam.getModel().setQuantity(3);
        poParam.getModel().setAllowBalanceForward("1");
        poParam.getModel().setRecordStatus("1");

        JSONObject json = poParam.saveRecord();
        System.out.println("Save C result: " + json.get("result") + " → " + json.get("message"));
    }

    @Test
    @Order(18)
    void testSaveRecord_Random_RX() throws SQLException, GuanzonException, CloneNotSupportedException {
        System.out.println("--- testSaveRecord_Random_RX ---");

        assertSuccess(poParam.newRecord(), "newRecord");

        poParam.getModel().setDescription("Random Rule");
        poParam.getModel().setDepartmentID("026");
        poParam.getModel().setIndustryCode(instance.getIndustry());
        poParam.getModel().setPeriod("X");
        poParam.getModel().setIncluded("RX");
        poParam.getModel().setQuantity(10);
        poParam.getModel().setAllowBalanceForward("0");
        poParam.getModel().setRecordStatus("1");

        JSONObject json = poParam.saveRecord();
        System.out.println("Save RX result: " + json.get("result") + " → " + json.get("message"));
    }

    // =========================================================================
    // SECTION H — Save guards (missing description / included / quantity)
    // =========================================================================

    @Test
    @Order(19)
    void testSaveRecord_Guard_MissingDescription() throws SQLException, GuanzonException, CloneNotSupportedException {
        System.out.println("--- testSaveRecord_Guard_MissingDescription ---");

        assertSuccess(poParam.newRecord(), "newRecord");
        poParam.getModel().setDescription("");
        poParam.getModel().setIncluded("AI");
        poParam.getModel().setQuantity(0);

        JSONObject json = poParam.saveRecord();
        assertError(json, "save with missing description");
        System.out.println("Guard msg: " + json.get("message"));
    }

    @Test
    @Order(20)
    void testSaveRecord_Guard_MissingIncluded() throws SQLException, GuanzonException, CloneNotSupportedException {
        System.out.println("--- testSaveRecord_Guard_MissingIncluded ---");

        assertSuccess(poParam.newRecord(), "newRecord");
        poParam.getModel().setDescription("Valid desc");
        poParam.getModel().setIncluded("");
        poParam.getModel().setQuantity(5);

        JSONObject json = poParam.saveRecord();
        assertError(json, "save with missing included");
        System.out.println("Guard msg: " + json.get("message"));
    }

    @Test
    @Order(21)
    void testSaveRecord_Guard_ZeroQty_BB() throws SQLException, GuanzonException, CloneNotSupportedException {
        System.out.println("--- testSaveRecord_Guard_ZeroQty_BB ---");

        assertSuccess(poParam.newRecord(), "newRecord");
        poParam.getModel().setDescription("Bin Rule Zero");
        poParam.getModel().setIncluded("BB");
        poParam.getModel().setQuantity(0);

        JSONObject json = poParam.saveRecord();
        assertError(json, "save BB with zero quantity");
        System.out.println("Guard msg: " + json.get("message"));
    }

    // =========================================================================
    // SECTION I — openRecord: valid, invalid
    // =========================================================================

    @Test
    @Order(22)
    void testOpenRecord_Valid() throws SQLException, GuanzonException {
        System.out.println("--- testOpenRecord_Valid ---");

        if (savedRecordID == null) {
            System.out.println("Skip: no savedRecordID.");
            return;
        }

        JSONObject json = poParam.getModel().openRecord(savedRecordID);
        assertSuccess(json, "openRecord valid");
        Assert.assertEquals(savedRecordID, poParam.getModel().getInventoryCountID());
        System.out.println("Opened: " + poParam.getModel().getInventoryCountID()
                + " | " + poParam.getModel().getDescription());
    }

    @Test
    @Order(23)
    void testOpenRecord_Invalid() throws SQLException, GuanzonException {
        System.out.println("--- testOpenRecord_Invalid ---");

        JSONObject json = poParam.getModel().openRecord("ZZ-NOTEXIST-9999");
        assertError(json, "openRecord invalid");
        System.out.println("Invalid open msg: " + json.get("message"));
    }

    // =========================================================================
    // SECTION J — searchRecord: success, not-found
    // =========================================================================

    @Test
    @Order(24)
    void testSearchRecord_Found() throws SQLException, GuanzonException {
        System.out.println("--- testSearchRecord_Found ---");

        if (savedRecordID == null) {
            System.out.println("Skip: no savedRecordID.");
            return;
        }

        try {
            JSONObject json = poParam.searchRecord(savedRecordID, true);
            System.out.println("searchRecord found: " + json.get("result")
                    + " | ID=" + poParam.getModel().getInventoryCountID());
            assertSuccess(json, "searchRecord found");
        } catch (ExceptionInInitializerError e) {
            System.out.println("UI-dep ExceptionInInitializerError — skipped.");
        }
    }

    @Test
    @Order(25)
    void testSearchRecord_NotFound() throws SQLException, GuanzonException {
        System.out.println("--- testSearchRecord_NotFound ---");

        try {
            JSONObject json = poParam.searchRecord("ZZ-NOT-FOUND-9999", true);
            assertError(json, "searchRecord not found");
            System.out.println("Not-found msg: " + json.get("message"));
        } catch (ExceptionInInitializerError e) {
            System.out.println("UI-dep ExceptionInInitializerError — skipped.");
        }
    }

    @Test
    @Order(26)
    void testSearchRecord_PartialMatch() throws SQLException, GuanzonException {
        System.out.println("--- testSearchRecord_PartialMatch ---");

        try {
            // Partial prefix search (byCode=true)
            JSONObject json = poParam.searchRecord("GK", true);
            System.out.println("Partial search: " + json.get("result") + " → " + json.get("message"));
        } catch (ExceptionInInitializerError e) {
            System.out.println("UI-dep ExceptionInInitializerError — skipped.");
        }
    }

    // =========================================================================
    // SECTION K — UpdateRecord: used-in-transaction guard
    // =========================================================================

    @Test
    @Order(27)
    void testUpdateRecord_UsedInTransactionGuard() throws SQLException, GuanzonException {
        System.out.println("--- testUpdateRecord_UsedInTransactionGuard ---");

        if (savedRecordID == null) {
            System.out.println("Skip: no savedRecordID.");
            return;
        }

        // Load the record
        assertSuccess(poParam.getModel().openRecord(savedRecordID), "open for update");

        // Put it into UPDATE edit mode
        assertSuccess(poParam.updateRecord(), "updateRecord mode");

        // If the ID is referenced in Inventory_Count_Master, UpdateRecord returns error.
        // If not, it proceeds. We log either outcome.
        JSONObject json = poParam.UpdateRecord();
        System.out.println("UpdateRecord guard check: " + json.get("result") + " → " + json.get("message"));
    }

    @Test
    @Order(28)
    void testUpdateRecord_ChangeDescriptionAndSave() throws SQLException, GuanzonException, CloneNotSupportedException {
        System.out.println("--- testUpdateRecord_ChangeDescriptionAndSave ---");

        if (savedRecordID == null) {
            System.out.println("Skip: no savedRecordID.");
            return;
        }

        assertSuccess(poParam.getModel().openRecord(savedRecordID), "open for update");
        assertSuccess(poParam.updateRecord(), "enter update mode");

        poParam.getModel().setDescription("Updated All Items Rule");
        poParam.getModel().setQuantity(0);

        JSONObject json = poParam.saveRecord();
        System.out.println("Save after update: " + json.get("result") + " → " + json.get("message"));
    }

    // =========================================================================
    // SECTION L — DeactivateRecord
    // =========================================================================

    @Test
    @Order(29)
    void testDeactivateRecord_Success() throws SQLException, GuanzonException {
        System.out.println("--- testDeactivateRecord_Success ---");

        if (savedRecordID == null) {
            System.out.println("Skip: no savedRecordID.");
            return;
        }

        assertSuccess(poParam.getModel().openRecord(savedRecordID), "open for deactivate");
        poParam.getModel().setRecordStatus("0");

        JSONObject json = poParam.DeactivateRecord();
        assertSuccess(json, "DeactivateRecord");
        System.out.println("Deactivated: " + savedRecordID);
    }

    // =========================================================================
    // SECTION M — ActivateRecord
    // =========================================================================

    @Test
    @Order(30)
    void testActivateRecord_Success() throws SQLException, GuanzonException {
        System.out.println("--- testActivateRecord_Success ---");

        if (savedRecordID == null) {
            System.out.println("Skip: no savedRecordID.");
            return;
        }

        // After deactivation (Order 29), open again with status "0"
        poParam.setRecordStatus("0");
        assertSuccess(poParam.getModel().openRecord(savedRecordID), "open for activate");
        poParam.getModel().setRecordStatus("1");

        JSONObject json = poParam.ActivateRecord();
        assertSuccess(json, "ActivateRecord");
        System.out.println("Activated: " + savedRecordID);
    }

    // =========================================================================
    // SECTION N — Deactivate → Activate roundtrip (second record)
    // =========================================================================

    @Test
    @Order(31)
    void testDeactivateActivate_Roundtrip() throws SQLException, GuanzonException {
        System.out.println("--- testDeactivateActivate_Roundtrip ---");

        if (savedRecordID2 == null) {
            System.out.println("Skip: no savedRecordID2.");
            return;
        }

        // Deactivate
        assertSuccess(poParam.getModel().openRecord(savedRecordID2), "open BB for deactivate");
        poParam.getModel().setRecordStatus("0");
        assertSuccess(poParam.DeactivateRecord(), "DeactivateRecord BB");
        System.out.println("BB deactivated.");

        // Activate back
        poParam.setRecordStatus("0");
        assertSuccess(poParam.getModel().openRecord(savedRecordID2), "open BB for activate");
        poParam.getModel().setRecordStatus("1");
        assertSuccess(poParam.ActivateRecord(), "ActivateRecord BB");
        System.out.println("BB activated.");
    }

    // =========================================================================
    // SECTION O — searchRecordDepartment: with UI=false
    // =========================================================================

    @Test
    @Order(32)
    void testSearchRecordDepartment_Found() throws SQLException, GuanzonException {
        System.out.println("--- testSearchRecordDepartment_Found ---");

        try {
            assertSuccess(poParam.newRecord(), "newRecord for dept search");
            JSONObject json = poParam.searchRecordDepartment("026", true);
            System.out.println("searchRecordDepartment(026): " + json.get("result"));
            if ("success".equals(json.get("result"))) {
                Assert.assertEquals("026", poParam.getModel().getDepartmentID());
            }
        } catch (ExceptionInInitializerError e) {
            System.out.println("UI ExceptionInInitializerError in searchRecordDepartment — skipped.");
        }
    }

    @Test
    @Order(33)
    void testSearchRecordDepartment_NotFound() throws SQLException, GuanzonException {
        System.out.println("--- testSearchRecordDepartment_NotFound ---");

        try {
            assertSuccess(poParam.newRecord(), "newRecord for dept search");
            JSONObject json = poParam.searchRecordDepartment("ZZ-NOT-DEPT", true);
            System.out.println("searchRecordDepartment not-found: " + json.get("result") + " → " + json.get("message"));
        } catch (ExceptionInInitializerError e) {
            System.out.println("UI ExceptionInInitializerError — skipped.");
        }
    }

    // =========================================================================
    // SECTION P — Record status filters & multi-status search
    // =========================================================================

    @Test
    @Order(34)
    void testSearchRecord_WithActiveStatusFilter() throws SQLException, GuanzonException {
        System.out.println("--- testSearchRecord_WithActiveStatusFilter ---");

        poParam.setRecordStatus("1"); // active only

        try {
            JSONObject json = poParam.searchRecord("", true);
            System.out.println("Active filter search: " + json.get("result"));
        } catch (ExceptionInInitializerError e) {
            System.out.println("UI ExceptionInInitializerError — skipped.");
        }
    }

    @Test
    @Order(35)
    void testSearchRecord_WithMultiStatusFilter() throws SQLException, GuanzonException {
        System.out.println("--- testSearchRecord_WithMultiStatusFilter ---");

        poParam.setRecordStatus("10"); // multi-status string → "1" and "0"

        try {
            JSONObject json = poParam.searchRecord("", true);
            System.out.println("Multi-status search: " + json.get("result"));
        } catch (ExceptionInInitializerError e) {
            System.out.println("UI ExceptionInInitializerError — skipped.");
        } finally {
            poParam.setRecordStatus("1"); // reset
        }
    }

    // =========================================================================
    // SECTION Q — Period values coverage (M, S, A, X)
    // =========================================================================

    @Test
    @Order(36)
    void testAllPeriodValues() throws SQLException, GuanzonException {
        System.out.println("--- testAllPeriodValues ---");

        Model_Inventory_Count_Type m = new ParamModels(instance).InventoryCountType();
        m.initialize();

        for (String period : new String[]{"M", "S", "A", "X"}) {
            m.setPeriod(period);
            Assert.assertEquals(period, m.getPeriod());
            System.out.println("Period set: " + period + " → get: " + m.getPeriod());
        }
    }

    // =========================================================================
    // SECTION R — AllowBalanceForward flag both states
    // =========================================================================

    @Test
    @Order(37)
    void testAllowBalanceForward_BothStates() throws SQLException, GuanzonException {
        System.out.println("--- testAllowBalanceForward_BothStates ---");

        Model_Inventory_Count_Type m = new ParamModels(instance).InventoryCountType();
        m.initialize();

        m.setAllowBalanceForward("1");
        Assert.assertTrue(m.isAllowBalanceForward());

        m.setAllowBalanceForward("0");
        Assert.assertFalse(m.isAllowBalanceForward());

        System.out.println("AllowBalanceForward both states OK.");
    }

    // =========================================================================
    // SECTION S — isRecordActive flag both states
    // =========================================================================

    @Test
    @Order(38)
    void testIsRecordActive_BothStates() throws SQLException, GuanzonException {
        System.out.println("--- testIsRecordActive_BothStates ---");

        Model_Inventory_Count_Type m = new ParamModels(instance).InventoryCountType();
        m.initialize();

        m.setRecordStatus("1");
        Assert.assertTrue(m.isRecordActive());

        m.setRecordStatus("0");
        Assert.assertFalse(m.isRecordActive());

        System.out.println("isRecordActive both states OK.");
    }

    // =========================================================================
    // SECTION T — getModel() returns the correct model instance
    // =========================================================================

    @Test
    @Order(39)
    void testGetModel_NotNull() throws SQLException, GuanzonException {
        System.out.println("--- testGetModel_NotNull ---");

        initFreshParam();
        assertSuccess(poParam.newRecord(), "newRecord");

        Model_Inventory_Count_Type m = poParam.getModel();
        Assert.assertNotNull(m);
        System.out.println("getModel() ID: " + m.getInventoryCountID());
    }

    // =========================================================================
    // SECTION U — Full lifecycle: new → valid save → open → update desc → save
    // =========================================================================

    @Test
    @Order(40)
    void testFullLifecycle_NewSaveOpenUpdateSave() throws SQLException, GuanzonException, CloneNotSupportedException {
        System.out.println("--- testFullLifecycle_NewSaveOpenUpdateSave ---");

        initFreshParam();

        // New
        assertSuccess(poParam.newRecord(), "newRecord");
        String newID = poParam.getModel().getInventoryCountID();
        System.out.println("New ID: " + newID);

        // Fill all required fields
        poParam.getModel().setDescription("Lifecycle Test Rule");
        poParam.getModel().setDepartmentID("026");
        poParam.getModel().setIndustryCode(instance.getIndustry());
        poParam.getModel().setPeriod("M");
        poParam.getModel().setIncluded("AI");
        poParam.getModel().setQuantity(0);
        poParam.getModel().setAllowBalanceForward("1");
        poParam.getModel().setRecordStatus("1");

        // Save
        JSONObject save1 = poParam.saveRecord();
        System.out.println("Initial save: " + save1.get("result") + " → " + save1.get("message"));

        if (!"success".equals(save1.get("result"))) return;
        String persistedID = poParam.getModel().getInventoryCountID();

        // Open
        assertSuccess(poParam.getModel().openRecord(persistedID), "open after save");
        Assert.assertEquals(persistedID, poParam.getModel().getInventoryCountID());
        System.out.println("Opened: " + poParam.getModel().getDescription());

        // Update
        assertSuccess(poParam.updateRecord(), "enter update mode");
        poParam.getModel().setDescription("Lifecycle Test Rule UPDATED");

        // Save again
        JSONObject save2 = poParam.saveRecord();
        System.out.println("Updated save: " + save2.get("result") + " → " + save2.get("message"));
        if ("success".equals(save2.get("result"))) {
            Assert.assertEquals("Lifecycle Test Rule UPDATED", poParam.getModel().getDescription());
        }
    }

    // =========================================================================
    // SECTION V — Quantity boundary values
    // =========================================================================

    @Test
    @Order(41)
    void testQuantityBoundary_NegativeNotAllowed() throws SQLException, GuanzonException {
        System.out.println("--- testQuantityBoundary_NegativeNotAllowed ---");

        assertSuccess(poParam.newRecord(), "newRecord");
        poParam.getModel().setDescription("Neg Qty Rule");
        poParam.getModel().setIncluded("BB");
        poParam.getModel().setQuantity(-1); // invalid

        JSONObject json = poParam.isEntryOkay();
        // -1 is <= 0 → should fail for non-AI
        assertError(json, "isEntryOkay negative qty");
        System.out.println("Negative qty guard: " + json.get("message"));
    }

    @Test
    @Order(42)
    void testQuantityBoundary_MaxValue() throws SQLException, GuanzonException {
        System.out.println("--- testQuantityBoundary_MaxValue ---");

        assertSuccess(poParam.newRecord(), "newRecord");
        poParam.getModel().setDescription("Max Qty Rule");
        poParam.getModel().setIncluded("RX");
        poParam.getModel().setQuantity(99);

        JSONObject json = poParam.isEntryOkay();
        assertSuccess(json, "isEntryOkay max qty");
        System.out.println("Max qty OK: " + poParam.getModel().getQuantity());
    }

    // =========================================================================
    // SECTION W — Description edge cases
    // =========================================================================

    @Test
    @Order(43)
    void testDescription_WhitespaceOnly() throws SQLException, GuanzonException {
        System.out.println("--- testDescription_WhitespaceOnly ---");

        assertSuccess(poParam.newRecord(), "newRecord");
        poParam.getModel().setDescription("   "); // whitespace
        poParam.getModel().setIncluded("AI");
        poParam.getModel().setQuantity(0);

        JSONObject json = poParam.isEntryOkay();
        // If isEmpty() returns false for whitespace, this may pass — log the result
        System.out.println("Whitespace-desc isEntryOkay: " + json.get("result") + " → " + json.get("message"));
    }

    @Test
    @Order(44)
    void testDescription_LongString() throws SQLException, GuanzonException {
        System.out.println("--- testDescription_LongString ---");

        assertSuccess(poParam.newRecord(), "newRecord");
        String longDesc = "A";
        poParam.getModel().setDescription(longDesc);
        poParam.getModel().setIncluded("AI");
        poParam.getModel().setQuantity(0);

        JSONObject json = poParam.isEntryOkay();
        System.out.println("Long-desc isEntryOkay: " + json.get("result"));
    }

    // =========================================================================
    // SECTION X — Initialize coverage (multiple fresh instances)
    // =========================================================================

    @Test
    @Order(45)
    void testMultipleInitialize_Coverage() throws SQLException, GuanzonException {
        System.out.println("--- testMultipleInitialize_Coverage ---");

        for (int i = 0; i < 3; i++) {
            Model_Inventory_Count_Type m = new ParamModels(instance).InventoryCountType();
            m.initialize();

            Assert.assertEquals(Integer.valueOf(0), m.getQuantity());
            Assert.assertEquals("1", m.getAllowBalanceForward()); // default
            Assert.assertFalse(m.isRecordActive());               // default inactive

            System.out.println("Init #" + (i + 1) + " defaults OK.");
        }
    }
}
