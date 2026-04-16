import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.cas.parameter.Project;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class testProject {
    static GRiderCAS instance;
    static Project record;

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/new/");

        instance = MiscUtil.Connect();
        
        try {
            record = new ParamControllers(instance, null).Project();
        } catch (SQLException | GuanzonException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testNewRecord() {
        try {
            JSONObject loJSON;

            loJSON = record.newRecord();
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }  
 
            loJSON = record.getModel().setProjectID("");
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }
            loJSON = record.getModel().setProjectDescription("");
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }  
            loJSON = record.saveRecord();
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }  
        } catch (SQLException | GuanzonException | CloneNotSupportedException e) {
            Assert.fail(e.getMessage());
        } 
    }
    
    @Test
    public void testConfirmRecord() {
        try {
            JSONObject loJSON;
            
            loJSON = record.openRecord("GCO126000001");
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }
            

            loJSON = record.ConfirmRecord("");
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }  

        } catch (SQLException | GuanzonException |ParseException |CloneNotSupportedException  e) {
            Assert.fail(e.getMessage());
            System.out.println("ERROR :" + e.getMessage());
        } 
    }
    
    @Test
    public void testVoidRecord() {
        try {
            JSONObject loJSON;
            
            loJSON = record.openRecord("GCO126000001");
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }
            

            loJSON = record.VoidRecord("");
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }  

        } catch (SQLException | GuanzonException |ParseException |CloneNotSupportedException  e) {
            Assert.fail(e.getMessage());
            System.out.println("ERROR :" + e.getMessage());
        } 
    }
    
    @Test
    public void testCancelRecord() {
        try {
            JSONObject loJSON;
            
            loJSON = record.openRecord("GCO126000001");
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }
            

            loJSON = record.CancelRecord("");
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }  

        } catch (SQLException | GuanzonException |ParseException |CloneNotSupportedException  e) {
            Assert.fail(e.getMessage());
            System.out.println("ERROR :" + e.getMessage());
        } 
    }
   
    @Test
    public void testUpdateRecord() {
        try {
            JSONObject loJSON;
            
            loJSON = record.openRecord("GCO1260000000001");
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }
            
            loJSON = record.updateRecord();
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }
            
            loJSON = record.getModel().setProjectDescription("The Monarch Canteena");
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }
            
            loJSON = record.getModel().setModifyingId(instance.getUserID());
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }
            
            loJSON = record.getModel().setModifiedDate(instance.getServerDate());
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message"));
            }
            
            loJSON = record.saveRecord();
            if ("error".equals((String) loJSON.get("result"))) {
                Assert.fail((String) loJSON.get("message")); 
            }
        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(testProject.class.getName()).log(Level.SEVERE, null, ex);
            System.out.print(ex.getMessage());
        }
    }
    
    @Test
    public void testSearch(){
        try {
            JSONObject loJSON = record.searchRecord("GCO1260000000001", true);
            if ("success".equals((String) loJSON.get("result"))){
                System.out.println(record.getModel().getProjectID());
                System.out.println(record.getModel().getProjectDescription());
                System.out.println(record.getModel().getRecordStatus());
            } else System.out.println("No record was selected.");
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(testProject.class.getName()).log(Level.SEVERE, null, ex);
             System.out.print(ex.getMessage());
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        record = null;
        instance = null;
    }
}
