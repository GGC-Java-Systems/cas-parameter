package org.guanzon.cas.parameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.cas.parameter.model.Model_Department;
import org.guanzon.cas.parameter.model.Model_Inventory_Count_Type;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;

public class InventoryCountType extends Parameter {
    
    Model_Inventory_Count_Type poModel;
    
    @Override
    public void initialize() throws SQLException, GuanzonException {
        psRecdStat = Logical.YES;
        
        poModel = new ParamModels(poGRider).InventoryCountType();
        
        super.initialize();
    }
    
    @Override
    public JSONObject isEntryOkay() throws SQLException {
        poJSON = new JSONObject();
        
        if (poGRider.getUserLevel() < UserRight.AUDIT) {
            poJSON.put("result", "error");
            poJSON.put("message", "User is not allowed to save record.");
            return poJSON;
        } else {
            poJSON = new JSONObject();
            
            if (poModel.getDescription().isEmpty()) {
                poJSON.put("result", "error");
                poJSON.put("message", "Description must not be empty.");
                return poJSON;
            }
            
            if (poModel.getIncluded().isEmpty()) {
                poJSON.put("result", "error");
                poJSON.put("message", "Included filter must not be empty.");
                return poJSON;
            }
            if (!poModel.getIncluded().equals("AI")) {
                if (poModel.getQuantity() <= 0) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Quantity must not be 0.");
                    return poJSON;
                }
            }
        }
        
        poModel.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        poModel.setModifiedDate(poGRider.getServerDate());
        
        poJSON.put("result", "success");
        return poJSON;
    }
    
    @Override
    public Model_Inventory_Count_Type getModel() {
        return poModel;
    }
    
    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException {
        String lsCondition = "";
        
        if (psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psRecdStat.length() - 1; lnCtr++) {
                lsCondition += ", " + SQLUtil.toSQL(Character.toString(psRecdStat.charAt(lnCtr)));
            }
            
            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "cRecdStat = " + SQLUtil.toSQL(psRecdStat);
        }
        
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);
        
        if (!pbWithUI) {
            lsSQL = MiscUtil.addCondition(lsSQL, "sInvCtrID LIKE " + SQLUtil.toSQL(value+ "%") );
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            if (MiscUtil.RecordCount(loRS) <= 0) {
                poJSON.put("result", "error");
                poJSON.put("message", "No record found.");
                return poJSON;
            }
            loRS.absolute(1);
            return poModel.openRecord(loRS.getString("sInvCtrID"));
        }
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "ID»Description",
                "sInvCtrID»sDescript",
                "sInvCtrID»sDescript",
                byCode ? 0 : 1);
        
        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sBinIDxxx"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
    }
    
    public JSONObject searchRecordDepartment(String value, boolean byCode) throws SQLException, GuanzonException {
        
        Department loObject;
        loObject = new ParamControllers(poGRider, null).Department();
        loObject.setRecordStatus("1");
        loObject.setWithParentClass(true);
        
        poJSON = loObject.searchRecord(value, byCode);
        
        if (poJSON != null) {
            poModel.setDepartmentID((String) loObject.getModel().getDepartmentId());
            return poJSON;
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
}
