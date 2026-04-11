package org.guanzon.cas.parameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.cas.parameter.model.Model_Project;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * The {@code Project} class encapsulates the business logic for managing
 * Project parameters in the system.
 * <p>
 * Key responsibilities include:
 * <ul>
 * <li>Initializing project records and binding to the {@link Model_Project}
 * model.</li>
 * <li>Validating required fields and checking for duplicate entries before
 * saving.</li>
 * <li>Searching, loading, and updating existing project records.</li>
 * <li>Handling record status transitions: open, confirm, cancel, and void.</li>
 * <li>Managing approval workflows based on user access level.</li>
 * <li>Ensuring all modifications are tracked with user ID and timestamp
 * metadata.</li>
 * <li>Executing critical actions within controlled database transactions to
 * ensure data integrity.</li>
 * </ul>
 * <p>
 * Duplicate checking is performed by the
 * {@link #CheckDuplicate(String, String)} method, which ensures that no project
 * with the same code and description already exists in the database before
 * saving a new record.
 * </p>
 * <p>
 * This class extends {@link Parameter} and interacts closely with
 * {@code Model_Project} for persistence, validation, and transactional
 * operations. It provides a consistent framework for safely managing project
 * records and enforcing business rules.
 * </p>
 *
 * @author Teejei De Celis
 * @since 2026-03-26
 */
public class Project extends Parameter {

    /**
     * Model instance for Project
     */
    Model_Project poModel;

    /**
     * Initializes the Project parameter.
     *
     * Sets default record status and instantiates the Project model.
     *
     * @throws SQLException if a database access error occurs
     * @throws GuanzonException if initialization fails
     */
    @Override
    public void initialize() throws SQLException, GuanzonException {
        psRecdStat = Logical.YES;

        poModel = new ParamModels(poGRider).Project();

        super.initialize();
    }

    /**
     * Validates whether the current Project entry is ready to be saved.
     * <p>
     * This method performs the following checks:
     * <ul>
     * <li>Verifies that the current user belongs to the allowed department
     * ("Engineering Services").</li>
     * <li>Ensures that the Project ID is not empty.</li>
     * <li>Ensures that the Project Description is not empty.</li>
     * <li>Checks for duplicate entries using
     * {@link #CheckDuplicate(String, String)}.</li>
     * <li>Assigns modification metadata such as modifying user ID and
     * modification date.</li>
     * </ul>
     * <p>
     * If any validation fails, a JSONObject with {@code "result":"error"} and a
     * descriptive message is returned. If all validations pass, a JSONObject
     * with {@code "result":"success"} is returned.
     *
     * @return JSONObject containing the validation result:
     * <ul>
     * <li>{@code "result":"success"} if the entry passes all checks.</li>
     * <li>{@code "result":"error"} with a descriptive {@code "message"} if
     * validation fails.</li>
     * </ul>
     * @throws SQLException if a database access error occurs during duplicate
     * checking.
     * @throws GuanzonException if a business logic error occurs during
     * validation.
     */
    @Override
    public JSONObject isEntryOkay() throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        String allowedDepartment = System.getProperty("allowed.department");
        

        if (!poGRider.getDepartment().equals(allowedDepartment)) {
            poJSON.put("result", "error");
            poJSON.put("message", "You do not have permission to save this record."
                    + "\nOnly users from the Engineering Services department are allowed.");
            return poJSON;
        }

        if (poModel.getProjectID().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Project Code must not be empty.");
            return poJSON;
        }

        if (poModel.getProjectDescription().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Project Description must not be empty.");
            return poJSON;
        }

        poModel.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        poModel.setModifiedDate(poGRider.getServerDate());
        
        
//        poJSON = CheckDuplicate(poModel.getProjectID(), poModel.getProjectDescription());
//        if ("error".equals(poJSON.get("result"))) {
//            return poJSON;
//        }

        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Retrieves the current Project model instance.
     *
     * @return Model_Project instance
     */
    @Override
    public Model_Project getModel() {
        return poModel;
    }

    /**
     * Searches for a Project record and loads it into the model.
     *
     * @param value the search keyword (code or description)
     * @param byCode true if searching by project code, false for description
     * @return JSONObject containing: - loaded record if found - error message
     * if no record is selected
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if search operation fails
     */
    @Override
    public JSONObject searchRecord(String value, boolean byCode)
            throws SQLException, GuanzonException {

        poJSON = ShowDialogFX.Search(poGRider,
                getSQ_Browse(),
                value,
                "Project ID»Project Description»Status",
                "sProjCode»sProjDesc»cRecdStat",
                "sProjCode»sProjDesc»cRecdStat",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sProjCode"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }

    /**
     * Cancels the current Project record.
     *
     * Updates the record status to CANCEL, performs validation, requests
     * approval (if required), and saves the changes within a database
     * transaction.
     *
     * @param remarks remarks or reason for cancellation
     * @return JSONObject containing: - "success" if the cancellation is
     * completed - "error" with message if the process fails
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if business logic validation fails
     * @throws ParseException if date parsing fails
     * @throws CloneNotSupportedException if cloning is not supported
     */
    public JSONObject CancelRecord(String remarks)
            throws SQLException, GuanzonException, ParseException, CloneNotSupportedException {

        String lsStatus = ProjectConstant.CANCELLED;
        poJSON = new JSONObject();
        boolean lbConfirm = true;

        if (getModel().getEditMode() != EditMode.READY
                || getModel().getEditMode() != EditMode.UPDATE) {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
        }

        if (lsStatus.equals(poModel.getRecordStatus())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Record was already Cancelled.");
            return poJSON;
        }
        if (poModel.isUsed()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Record was already used.\n Cancellation of project is not allowed.");
            return poJSON;
        }
        

        if (!pbWthParent) {
            poJSON = seekApproval();
            if ("error".equals(poJSON.get("result"))) {
                return poJSON;
            }
        }
        
        poJSON = statusChange(poModel.getTable(),
                (String) poModel.getValue("sProjCode"),
                remarks, lsStatus, !lbConfirm, false);

        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }

        poJSON.put("result", "success");
        poJSON.put("message", "Record successfully cancelled.");
        return poJSON;
    }

    /**
     * Confirms the current Project record.
     *
     * Updates the record status to CONFIRM, performs validation, approval (if
     * required), and saves changes within a transaction.
     *
     * @param remarks remarks or reason for confirmation
     * @return JSONObject result of the confirmation process
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if business logic fails
     * @throws ParseException if date parsing fails
     * @throws CloneNotSupportedException if cloning fails
     */
    public JSONObject ConfirmRecord(String remarks)
            throws SQLException, GuanzonException, ParseException, CloneNotSupportedException {

        String lsStatus = ProjectConstant.CONFIRM;
        poJSON = new JSONObject();
        boolean lbConfirm = true;

        if (getModel().getEditMode() != EditMode.READY
                || getModel().getEditMode() != EditMode.UPDATE) {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
        }
        
        if (lsStatus.equals(poModel.getRecordStatus())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Record was already confirmed.");
            return poJSON;
        }
        if (poModel.isUsed()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Record was already used.\n Cancellation of project is not allowed.");
            return poJSON;
        }
        
        if (!pbWthParent) {
            poJSON = seekApproval();
            if ("error".equals(poJSON.get("result"))) {
                return poJSON;
            }
        }
        
        poJSON = statusChange(poModel.getTable(),
                (String) poModel.getValue("sProjCode"),
                remarks, lsStatus, !lbConfirm, false);

        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }

        poJSON.put("result", "success");
        poJSON.put("message", "Record successfully confirmed.");
        return poJSON;
    }

    /**
     * Voids the current Project record.
     *
     * Updates the record status to VOID, performs validation, approval (if
     * required), and saves changes within a transaction.
     *
     * @param remarks remarks or reason for voiding
     * @return JSONObject result of the voiding process
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if business logic fails
     * @throws ParseException if date parsing fails
     * @throws CloneNotSupportedException if cloning fails
     */
    public JSONObject VoidRecord(String remarks)
            throws SQLException, GuanzonException, ParseException, CloneNotSupportedException {

        String lsStatus = ProjectConstant.VOID;
        poJSON = new JSONObject();
        boolean lbConfirm = true;

        if (getModel().getEditMode() != EditMode.READY
                || getModel().getEditMode() != EditMode.UPDATE) {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
        }

        if (lsStatus.equals(poModel.getRecordStatus())) {
             poJSON.put("result", "error");
            poJSON.put("message", "Record was already voided.");
            return poJSON;
        }
        if (poModel.isUsed()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Record was already used.\n Cancellation of project is not allowed.");
            return poJSON;
        }

        if (!pbWthParent) {
            poJSON = seekApproval();
            if ("error".equals(poJSON.get("result"))) {
                return poJSON;
            }
        }

        poJSON = statusChange(poModel.getTable(),
                (String) poModel.getValue("sProjCode"),
                remarks, lsStatus, !lbConfirm, false);

        if ("error".equals(poJSON.get("result"))) {
            return poJSON;
        }

        poJSON.put("result", "success");
        poJSON.put("message", "Record successfully void.");
        return poJSON;
    }

    /**
     * Requests approval from an authorized user if required.
     *
     * If the current user has encoder-level access or lower, an approval dialog
     * is shown to validate higher authorization.
     *
     * @return JSONObject containing: - "success" if approval is granted -
     * "error" if approval fails or unauthorized
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if approval process fails
     */
    public JSONObject seekApproval() throws SQLException, GuanzonException {

        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
            poJSON = ShowDialogFX.getUserApproval(poGRider);

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            if (Integer.parseInt(poJSON.get("nUserLevl").toString())
                    <= UserRight.ENCODER) {
                poJSON.put("result", "error");
                poJSON.put("message",
                        "User is not an authorized approving officer..");
                return poJSON;
            }
        }
        
        
        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Checks for duplicate records in the Project table.
     *
     * <p>
     * This method performs a two-step duplicate check:
     * <ol>
     * <li><b>Step 1:</b> Checks if a project with the given
     * <code>fsProjectCode</code> already exists.
     * <ul>
     * <li>If it exists, the method immediately returns an error JSON with the
     * project code and description of the existing record.</li>
     * </ul>
     * </li>
     * <li><b>Step 2:</b> If the project code does not exist, it checks for a
     * full duplicate where <code>sProjCode</code>, <code>sProjDesc</code>, and
     * <code>cRecdStat = '1'</code> match.
     * <ul>
     * <li>If a matching record is found, the method returns an error JSON
     * indicating that the project has already been encoded.</li>
     * <li>If no match is found, the method returns a success JSON indicating
     * that the project can be safely added.</li>
     * </ul>
     * </li>
     * </ol>
     *
     * <p>
     * The JSON object returned contains the following keys:
     * <ul>
     * <li><code>result</code>: "success" if no duplicates were found, "error"
     * otherwise</li>
     * <li><code>message</code>: descriptive message about the duplicate
     * status</li>
     * </ul>
     *
     * <p>
     * <b>Notes:</b>
     * <ul>
     * <li>All database queries are executed via
     * <code>poGRider.executeQuery(String sql)</code>.</li>
     * <li>ResultSet is properly closed after each query to avoid resource
     * leaks.</li>
     * <li>This method throws <code>SQLException</code> for database errors and
     * <code>GuanzonException</code> for application-specific exceptions.</li>
     * </ul>
     *
     * <p>
     * Example usage:
     * <pre>{@code
     * JSONObject result = projectModel.CheckDuplicate("P001", "Project Alpha");
     * if ("success".equals(result.get("result"))) {
     *     // safe to add new project
     * } else {
     *     System.out.println(result.get("message"));
     * }
     * }</pre>
     *
     * @param fsProjectCode the project code to check for duplicates
     * @param fsProjectDesc the project description to check for duplicates
     * @return a {@link JSONObject} containing the result ("success" or "error")
     * and a descriptive message
     * @throws SQLException if a database access error occurs
     * @throws GuanzonException for any application-specific errors
     */
    
    public JSONObject CheckDuplicate(String fsProjectCode, String fsProjectDesc) throws SQLException, GuanzonException {
        poJSON = new JSONObject();

        String lsSQL = "SELECT sProjCode,"
                + " sProjDesc,"
                + " cRecdStat "
                + " FROM Project"
                + " WHERE sProjCode = " + SQLUtil.toSQL(fsProjectCode)
                + " AND sProjDesc = " + SQLUtil.toSQL(fsProjectDesc);
//                + " AND cRecdStat = '1'";
                 

        System.out.println("EXECUTING SQL: " + lsSQL);
        ResultSet rs = poGRider.executeQuery(lsSQL);

        if (rs.next()) {
            poJSON.put("result", "error");
            poJSON.put("message", "This project has already been encoded.\n "
                    + "Project Code: " + rs.getString("sProjCode")
                    + ",\nProject Description: " + rs.getString("sProjDesc"));
        } else {
            poJSON.put("result", "success");
            poJSON.put("message", "No duplicate found. You can proceed.");
        }

        rs.close();
        return poJSON;
    }
    
//    public JSONObject CheckDuplicate(String fsProjectCode, String fsProjectDesc) throws SQLException, GuanzonException {
//        poJSON = new JSONObject();
//
//        String lsSQL = "SELECT sProjCode, sProjDesc, cRecdStat "
//                + "FROM Project "
//                + "WHERE sProjCode = " + SQLUtil.toSQL(fsProjectCode);
//
//        System.out.println("EXECUTING SQL: " + lsSQL);
//        ResultSet rs = poGRider.executeQuery(lsSQL);
//
//        if (rs.next()) {
//            poJSON.put("result", "error");
//            poJSON.put("message", "Project Code already exists.\n"
//                    + "Project Code: " + rs.getString("sProjCode")
//                    + ",\nProject Description: " + rs.getString("sProjDesc"));
//            rs.close();
//            return poJSON;
//        }
//
//        rs.close();
//
//        lsSQL = "SELECT sProjCode, sProjDesc, cRecdStat "
//                + "FROM Project "
//                + "WHERE sProjCode = " + SQLUtil.toSQL(fsProjectCode)
//                + " AND sProjDesc = " + SQLUtil.toSQL(fsProjectDesc)
//                + " AND cRecdStat = '1'";
//
//        System.out.println("EXECUTING SQL: " + lsSQL);
//        rs = poGRider.executeQuery(lsSQL);
//
//        if (rs.next()) {
//            poJSON.put("result", "error");
//            poJSON.put("message", "This project has already been encoded.\n"
//                    + "Project Code: " + rs.getString("sProjCode")
//                    + ",\nProject Description: " + rs.getString("sProjDesc"));
//        } else {
//            poJSON.put("result", "success");
//            poJSON.put("message", "No duplicate found. You can proceed.");
//        }
//
//        rs.close();
//        return poJSON;
//    }

    /**
     * Constants representing Project record statuses.
     */
    public static class ProjectConstant {

        /**
         * Open/Active status
         */
        public static final String OPEN = "0";

        /**
         * Confirmed status
         */
        public static final String CONFIRM = "1";

        /**
         * Cancel status
         */
        public static final String CANCELLED = "3";

        /**
         * Void status
         */
        public static final String VOID = "4";

    }
    
    public void ShowStatusHistory() throws SQLException, GuanzonException, Exception{
        CachedRowSet crs = getStatusHistory();
        
        crs.beforeFirst();
        
        while(crs.next()){
            switch (crs.getString("cRefrStat")){
                case "":
                    crs.updateString("cRefrStat", "-");
                    break;
                case ProjectConstant.OPEN:
                    crs.updateString("cRefrStat", "OPEN");
                    break;
                case ProjectConstant.CONFIRM:
                    crs.updateString("cRefrStat", "CONFIRM");
                    break;
                case ProjectConstant.CANCELLED:
                    crs.updateString("cRefrStat", "CANCELLED");
                    break;
                case ProjectConstant.VOID:
                    crs.updateString("cRefrStat", "VOID");
                    break;
                default:
                    char ch = crs.getString("cRefrStat").charAt(0);
                    String stat = String.valueOf((int) ch - 64);
                    
                    switch (stat){
                        case ProjectConstant.OPEN:
                            crs.updateString("cRefrStat", "OPEN");
                            break;
                        case ProjectConstant.CONFIRM:
                            crs.updateString("cRefrStat", "CONFIRM");
                            break;
                        case ProjectConstant.CANCELLED:
                            crs.updateString("cRefrStat", "CANCELLED");
                            break;
                        case ProjectConstant.VOID:
                            crs.updateString("cRefrStat", "VOID");
                            break;
                        
                    }
            }
            crs.updateRow(); 
        }
        
        JSONObject loJSON  = getEntryBy();
        String entryBy = "";
        String entryDate = "";
        
        if ("success".equals((String) loJSON.get("result"))){
            entryBy = (String) loJSON.get("sCompnyNm");
            entryDate = (String) loJSON.get("sEntryDte");
        }
        
        showStatusHistoryUI("Project", (String) poModel.getValue("sProjCode"), entryBy, entryDate, crs);
    }
    
    public JSONObject getEntryBy() throws SQLException, GuanzonException {
        poJSON = new JSONObject();
        String lsEntry = "";
        String lsEntryDate = "";
        String lsSQL =  " SELECT b.sModified, b.dModified " 
                        + " FROM Project a "
                        + " LEFT JOIN xxxAuditLogMaster b ON b.sSourceNo = a.sProjCode AND b.sEventNme LIKE 'ADD%NEW' AND b.sRemarksx = " + SQLUtil.toSQL(poModel.getTable());
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sProjCode =  " + SQLUtil.toSQL(poModel.getProjectID())) ;
        System.out.println("Execute SQL : " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
          if (MiscUtil.RecordCount(loRS) > 0L) {
            if (loRS.next()) {
                if(loRS.getString("sModified") != null && !"".equals(loRS.getString("sModified"))){
                    if(loRS.getString("sModified").length() > 10){
                        lsEntry = getSysUser(poGRider.Decrypt(loRS.getString("sModified"))); 
                    } else {
                        lsEntry = getSysUser(loRS.getString("sModified")); 
                    }
                    // Get the LocalDateTime from your result set
                    LocalDateTime dModified = loRS.getObject("dModified", LocalDateTime.class);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
                    lsEntryDate =  dModified.format(formatter);
                }
            } 
          }
          MiscUtil.close(loRS);
        } catch (SQLException e) {
          poJSON.put("result", "error");
          poJSON.put("message", e.getMessage());
          return poJSON;
        } 
        
        poJSON.put("result", "success");
        poJSON.put("sCompnyNm", lsEntry);
        poJSON.put("sEntryDte", lsEntryDate);
        return poJSON;
    }
    public String getSysUser(String fsId) throws SQLException, GuanzonException {
        String lsEntry = "";
        String lsSQL =   " SELECT b.sCompnyNm from xxxSysUser a " 
                       + " LEFT JOIN Client_Master b ON b.sClientID = a.sEmployNo ";
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sUserIDxx =  " + SQLUtil.toSQL(fsId)) ;
        System.out.println("SQL " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
          if (MiscUtil.RecordCount(loRS) > 0L) {
            if (loRS.next()) {
                lsEntry = loRS.getString("sCompnyNm");
            } 
          }
          MiscUtil.close(loRS);
        } catch (SQLException e) {
          poJSON.put("result", "error");
          poJSON.put("message", e.getMessage());
        } 
        return lsEntry;
    }
}
