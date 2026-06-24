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

        poModel = new ParamModels(poGRider).InventoryCountType();

        super.initialize();
    }

    @Override
    public JSONObject isEntryOkay() throws SQLException {
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
            lsSQL = MiscUtil.addCondition(lsSQL, "sInvCtrID LIKE " + SQLUtil.toSQL(value + "%"));
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
            return poModel.openRecord((String) poJSON.get("sInvCtrID"));
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

    public JSONObject ActivateRecord() throws SQLException, GuanzonException {
        if (pbWithUI) {
            //validate before allowing to be activted 
            //UserRight.AUDIT is Audit Head or OIC
            if (poGRider.getUserLevel() < UserRight.AUDIT) {
                poJSON = ShowDialogFX.getUserApproval(poGRider);
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                } else {
                    if (Integer.parseInt(poJSON.get("nUserLevl").toString()) < UserRight.AUDIT) {
                        poJSON.put("result", "error");
                        poJSON.put("message", "User is not an authorized approving officer.");
                        return poJSON;
                    }
//                psApprovalUser = poJSON.get("sUserIDxx") != null
//                        ? poJSON.get("sUserIDxx").toString()
//                        : poGRider.getUserID();
                }
            } else {
//            psApprovalUser = poGRider.getUserID();
            }
        }
        return activateRecord();
    }

    public JSONObject DeactivateRecord() throws SQLException, GuanzonException {
        if (pbWithUI) {
            //validate before allowing to be deactivated  
            //UserRight.AUDIT is Audit Head or OIC
            if (poGRider.getUserLevel() < UserRight.AUDIT) {
                poJSON = ShowDialogFX.getUserApproval(poGRider);
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                } else {
                    if (Integer.parseInt(poJSON.get("nUserLevl").toString()) < UserRight.AUDIT) {
                        poJSON.put("result", "error");
                        poJSON.put("message", "User is not an authorized approving officer.");
                        return poJSON;
                    }
//                psApprovalUser = poJSON.get("sUserIDxx") != null
//                        ? poJSON.get("sUserIDxx").toString()
//                        : poGRider.getUserID();
                }
            } else {
//            psApprovalUser = poGRider.getUserID();
            }
        }
        return deactivateRecord();
    }

    public JSONObject UpdateRecord() throws SQLException, GuanzonException {
        if (pbWithUI) {
            //validate before allowing to be deactivated  
            //UserRight.AUDIT is Audit Head or OIC
            if (poGRider.getUserLevel() < UserRight.AUDIT) {
                poJSON = ShowDialogFX.getUserApproval(poGRider);
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                } else {
                    if (Integer.parseInt(poJSON.get("nUserLevl").toString()) < UserRight.AUDIT) {
                        poJSON.put("result", "error");
                        poJSON.put("message", "User is not an authorized approving officer.");
                        return poJSON;
                    }
//                psApprovalUser = poJSON.get("sUserIDxx") != null
//                        ? poJSON.get("sUserIDxx").toString()
//                        : poGRider.getUserID();
                }
            } else {
//            psApprovalUser = poGRider.getUserID();
            }
        }

        String lsSQL = "SELECT sInvCtrID"
                + " FROM Inventory_Count_Master a"
                + " WHERE a.sInvCtrID = " + SQLUtil.toSQL(getModel().getInventoryCountID()) + " LIMIT 1";

        System.out.println("Retrieve query: " + lsSQL);
        ResultSet loRSExist = poGRider.executeQuery(lsSQL);

        if (MiscUtil.RecordCount(loRSExist) > 0L) {
            poJSON.put("result", "error");
            poJSON.put("message", "User is not allowed to update Inventory Type Count. Rule's is already used in Transaction");
            return poJSON;
        }
        MiscUtil.close(loRSExist);

        return updateRecord();
    }

    public JSONObject isOfficerEmployee() throws SQLException {
        poJSON = new JSONObject();
        String userID = poGRider.getEmployeeNo();

        //check by user level
        if (poGRider.getUserLevel() >= UserRight.AUDIT) {
            poJSON.put("result", "success");
        }
        if (userID == null || userID.trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid User ID. Please inform MIS Dept. for Account configuration.");
            return poJSON;
        }

        String lsSQL = "SELECT"
                + " a.sClientID"
                + ", a.sCompnyNm"
                + ", b.sBranchCd"
                + ", b.cEmpRankx"
                + ", c.cMainOffc"
                + " FROM Client_Master a"
                + "    LEFT JOIN Employee_Master001 b ON a.sClientID = b.sEmployID"
                + "    LEFT JOIN Branch c ON b.sBranchCd = c.sBranchCd"
                + "           WHERE (b.sDeptIDxx IN ('034','026','A008')"
                + "             AND b.sEmployID = " + SQLUtil.toSQL(userID) + ")";

        System.out.println("Employee eligibility: " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) <= 0) {
            poJSON.put("result", "error");
            poJSON.put("message", "User is not authorized to use this system.");
            return poJSON;
        }

        poJSON.put("result", "success");
        return poJSON;
    }
}
