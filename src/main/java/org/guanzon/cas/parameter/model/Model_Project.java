package org.guanzon.cas.parameter.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.json.simple.JSONObject;

/**
 * Model class for Project entity.
 * <p>
 * Handles project-related data such as project code, description,
 * record status, and audit fields. This class extends the base
 * {@link Model} class and provides getter and setter methods
 * for each column in the project table.
 * </p>
 * 
 * <p>
 * This class also initializes default values and generates
 * the next available project code.
 * </p>
 * 
 * @author Teejei
 * @since March 26, 2026
 */
public class Model_Project extends Model{
    
    /**
     * Initializes the project model by loading metadata,
     * preparing a new row, and setting default values.
     */
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());
            
            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            
            // Set default record status to ACTIVE
            poEntity.updateString("cUsedxxxx", "0");
            poEntity.updateString("cRecdStat", RecordStatus.INACTIVE);
            poEntity.updateString("sModified", poGRider.getUserID());

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = ("sProjCode");

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
       
    /**
     * Sets the project ID/code.
     *
     * @param projectID the project code
     * @return JSONObject result of the operation
     */
    public JSONObject setProjectID(String projectID){
        return setValue("sProjCode", projectID);
    }
    
    /**
     * Retrieves the project ID/code.
     *
     * @return project code
     */
    public String getProjectID(){
        return (String) getValue("sProjCode");
    }
    
    /**
     * Sets the project description.
     *
     * @param description project description
     * @return JSONObject result of the operation
     */
    public JSONObject setProjectDescription(String description){
        return setValue("sProjDesc", description);
    }
    
    /**
     * Retrieves the project description.
     *
     * @return project description
     */
    public String getProjectDescription(){
        return (String) getValue("sProjDesc");
    }
    
    /**
     * Sets the record status.
     *
     * @param recordStatus status value (e.g., ACTIVE/INACTIVE)
     * @return JSONObject result of the operation
     */
    public JSONObject setRecordStatus(String recordStatus){
        return setValue("cRecdStat", recordStatus);
    }
    
    /**
     * Retrieves the record status.
     *
     * @return record status
     */
    public String getRecordStatus(){
        return (String) getValue("cRecdStat");
    }
    
    /**
     * Sets the "used" status flag of the record.
     *
     * <p>
     * This method updates the underlying field <code>cUsedxxxx</code> by
     * converting the given boolean value into its corresponding string
     * representation:
     * <ul>
     * <li><code>"1"</code> if <code>true</code></li>
     * <li><code>"0"</code> if <code>false</code></li>
     * </ul>
     *
     * <p>
     * This is typically used to mark whether the record has already been
     * utilized in a transaction or process.
     *
     * @param isUsed {@code true} to mark the record as used; {@code false}
     * otherwise
     * @return a {@link JSONObject} containing the result of the update
     * operation,
     */
    public JSONObject isUsed(boolean isUsed) {
        return setValue("cUsedxxxx", isUsed ? "1" : "0");
    }
    
    /**
     * Retrieves the "used" status flag of the record.
     *
     * <p>
     * This method reads the value of the underlying field
     * <code>cUsedxxxx</code> and evaluates it as a boolean:
     * <ul>
     * <li>Returns <code>true</code> if the value is equal to
     * <code>"1"</code></li>
     * <li>Returns <code>false</code> for any other value, including
     * {@code null}</li>
     * </ul>
     *
     * <p>
     * The comparison is null-safe by converting the retrieved value to a string
     * before evaluation.
     *
     * @return {@code true} if the record is marked as used; {@code false}
     * otherwise
     */
    public boolean isUsed() {
        Object value = getValue("cUsedxxxx");
        return "1".equals(String.valueOf(value));
    }

    
    /**
     * Sets the ID of the user who modified the record.
     *
     * @param modifyingId user ID
     * @return JSONObject result of the operation
     */
    public JSONObject setModifyingId(String modifyingId){
        return setValue("sModified", modifyingId);
    }
    
    /**
     * Retrieves the ID of the user who last modified the record.
     *
     * @return modifying user ID
     */
    public String getModifyingId(){
        return (String) getValue("sModified");
    }
    
    /**
     * Sets the modified date of the record.
     *
     * @param modifiedDate date when the record was modified
     * @return JSONObject result of the operation
     */
    public JSONObject setModifiedDate(Date modifiedDate){
        return setValue("dModified", modifiedDate);
    }
    
    /**
     * Retrieves the modified date of the record.
     *
     * @return modified date
     */
    public Date getModifiedDate(){
        return (Date) getValue("dModified");
    }
    
    /**
     * Generates the next available project code.
     *
     * @return next project code
     */
    @Override
    public String getNextCode() {
        return "";
    }
}