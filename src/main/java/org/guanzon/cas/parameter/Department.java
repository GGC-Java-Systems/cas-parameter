package org.guanzon.cas.parameter;

import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.constant.ClientType;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.cas.client.ClientGUI;
import org.guanzon.cas.client.model.Model_Client_Address;
import org.guanzon.cas.parameter.model.Model_Department;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;

public class Department extends Parameter {

    Model_Department poModel;

    @Override
    public void initialize() throws SQLException, GuanzonException {
        psRecdStat = Logical.YES;

        poModel = new ParamModels(poGRider).Department();

        super.initialize();
    }

    @Override
    public JSONObject isEntryOkay() throws SQLException {
        poJSON = new JSONObject();

        if (poGRider.getUserLevel() < UserRight.SYSADMIN) {
            poJSON.put("result", "error");
            poJSON.put("message", "User is not allowed to save record.");
            return poJSON;
        } else {
            poJSON = new JSONObject();

            if (poModel.getDescription() == null || poModel.getDescription().isEmpty()) {
                poJSON.put("result", "error");
                poJSON.put("message", "Description must not be empty.");
                return poJSON;
            }

            if (poModel.getDepartmentCode() == null || poModel.getDepartmentCode().isEmpty()) {
                poJSON.put("result", "error");
                poJSON.put("message", "Department code must not be empty.");
                return poJSON;
            }
        }

        poModel.setModifyingId(poGRider.Encrypt(poGRider.getUserID()));
        poModel.setModifiedDate(poGRider.getServerDate());

        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    public Model_Department getModel() {
        return poModel;
    }

    @Override
    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException {
        String lsSQL = getSQ_Browse();

        
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                value,
                "ID»Description»Code",
                "sDeptIDxx»sDeptName»sDeptCode",
                "sDeptIDxx»sDeptName»sDeptCode",
                byCode ? 0 : 1);

        if (poJSON != null) {
            return poModel.openRecord((String) poJSON.get("sDeptIDxx"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }

    public JSONObject searchDepartmentHead(String fsValue, boolean fbByCode) throws SQLException, GuanzonException, Exception {

        if (fbByCode) {
            JSONObject loJSON = new JSONObject();
            if (fsValue.equals(getModel().DepartmentHeadAssign().getClientId())) {
                loJSON.put("result", "success");
                return loJSON;
            } else {
                loJSON.put("result", "error");
                loJSON.put("message", "Client not found.");
                return loJSON;
            }
        } else {
            JSONObject loJSON = null;
            if (!fsValue.isEmpty()) {

                String lsSQL = "SELECT"
                        + " sClientID"
                        + ", sCompnyNm"
                        + " FROM Client_Master"
                        + " WHERE cRecdStat= '1'";

                loJSON = ShowDialogFX.Search(poGRider,
                        lsSQL,
                        fsValue,
                        "Client ID»Client Name",
                        "sClientID»sCompnyNm",
                        "sClientID»sCompnyNm",
                        fbByCode ? 0 : 1);

                if (loJSON == null) {
                    return loJSON;
                } else {
                    if ("error".equals(loJSON.get("result"))) {
                        return loJSON;
                    }
                }
            }

            //initialize Client GUI
            ClientGUI loClient = new ClientGUI();

            loClient.setGRider(poGRider);
            loClient.setLogWrapper(null);
            loClient.setCategoryCode(poGRider.getCategory());

            //filter client type 
            loClient.setClientType(ClientType.INDIVIDUAL);

            //searchRecord(fsValue,fbByCode) will run make sure to set client and bycode
            //bycode true client id
            //bycode false company
            //set search by code
            loClient.setByCode(fbByCode);

            if (loJSON != null) {
                getModel().setDeptHeadAssignedId(loJSON.get("sClientID").toString());

                //set client id
                loClient.setClientId(getModel().getDeptHeadAssignedId());

            } else {
                loClient.setClientId("");

                JSONObject loResult = new JSONObject();
                loResult.put("result", "error");
                return loResult;
            }

            //load record
            CommonUtils.showModal(loClient);

            //initialize new json for result
            JSONObject loResult = new JSONObject();

            //load if button 
            if (!loClient.isCancelled()) {

                getModel().setDeptHeadAssignedId(loClient.getClient().getModel().getClientId() != null ? loClient.getClient().getModel().getClientId() : "");

            }
            loResult.put("result", "success");
            return loResult;
        }
    }

    public JSONObject searchHead(String fsValue, boolean fbByCode) throws SQLException, GuanzonException, Exception {

        if (fbByCode) {
            JSONObject loJSON = new JSONObject();
            if (fsValue.equals(getModel().HeadAssign().getClientId())) {
                loJSON.put("result", "success");
                return loJSON;
            } else {
                loJSON.put("result", "error");
                loJSON.put("message", "Client not found.");
                return loJSON;
            }
        } else {
            JSONObject loJSON = null;
            if (!fsValue.isEmpty()) {

                String lsSQL = "SELECT"
                        + " sClientID"
                        + ", sCompnyNm"
                        + " FROM Client_Master"
                        + " WHERE cRecdStat= '1'";

                loJSON = ShowDialogFX.Search(poGRider,
                        lsSQL,
                        fsValue,
                        "Client ID»Client Name",
                        "sClientID»sCompnyNm",
                        "sClientID»sCompnyNm",
                        fbByCode ? 0 : 1);

                if (loJSON == null) {
                    return loJSON;
                } else {
                    if ("error".equals(loJSON.get("result"))) {
                        return loJSON;
                    }
                }
            }

            //initialize Client GUI
            ClientGUI loClient = new ClientGUI();

            loClient.setGRider(poGRider);
            loClient.setLogWrapper(null);
            loClient.setCategoryCode(poGRider.getCategory());

            //filter client type 
            loClient.setClientType(ClientType.INDIVIDUAL);

            //searchRecord(fsValue,fbByCode) will run make sure to set client and bycode
            //bycode true client id
            //bycode false company
            //set search by code
            loClient.setByCode(fbByCode);

            if (loJSON != null) {
                getModel().setDeptHeadId(loJSON.get("sClientID").toString());

                //set client id
                loClient.setClientId(getModel().getDeptHeadId());

            } else {
                loClient.setClientId("");
                JSONObject loResult = new JSONObject();
                loResult.put("result", "error");
                return loResult;
            }

            //load record
            CommonUtils.showModal(loClient);

            //initialize new json for result
            JSONObject loResult = new JSONObject();

            //load if button 
            if (!loClient.isCancelled()) {

                getModel().setDeptHeadId(loClient.getClient().getModel().getClientId() != null ? loClient.getClient().getModel().getClientId() : "");

            }
            loResult.put("result", "success");
            return loResult;
        }
    }

    public JSONObject searchSupervisor(String fsValue, boolean fbByCode) throws SQLException, GuanzonException, Exception {

        if (fbByCode) {
            JSONObject loJSON = new JSONObject();
            if (fsValue.equals(getModel().SupervisorAssign().getClientId())) {
                loJSON.put("result", "success");
                return loJSON;
            } else {
                loJSON.put("result", "error");
                loJSON.put("message", "Client not found.");
                return loJSON;
            }
        } else {
            JSONObject loJSON = null;
            if (!fsValue.isEmpty()) {

                String lsSQL = "SELECT"
                        + " sClientID"
                        + ", sCompnyNm"
                        + " FROM Client_Master"
                        + " WHERE cRecdStat= '1'";

                loJSON = ShowDialogFX.Search(poGRider,
                        lsSQL,
                        fsValue,
                        "Client ID»Client Name",
                        "sClientID»sCompnyNm",
                        "sClientID»sCompnyNm",
                        fbByCode ? 0 : 1);

                if (loJSON == null) {
                    return loJSON;
                } else {
                    if ("error".equals(loJSON.get("result"))) {
                        return loJSON;
                    }
                }
            }

            //initialize Client GUI
            ClientGUI loClient = new ClientGUI();

            loClient.setGRider(poGRider);
            loClient.setLogWrapper(null);
            loClient.setCategoryCode(poGRider.getCategory());

            //filter client type 
            loClient.setClientType(ClientType.INDIVIDUAL);

            //searchRecord(fsValue,fbByCode) will run make sure to set client and bycode
            //bycode true client id
            //bycode false company
            //set search by code
            loClient.setByCode(fbByCode);

            if (loJSON != null) {
                getModel().setDeptSupervisorAssignedId(loJSON.get("sClientID").toString());

                //set client id
                loClient.setClientId(getModel().getDeptSupervisorAssignedId());

            } else {
                loClient.setClientId("");
                JSONObject loResult = new JSONObject();
                loResult.put("result", "error");
                return loResult;
            }

            //load record
            CommonUtils.showModal(loClient);

            //initialize new json for result
            JSONObject loResult = new JSONObject();

            //load if button 
            if (!loClient.isCancelled()) {

                getModel().setDeptSupervisorAssignedId(loClient.getClient().getModel().getClientId() != null ? loClient.getClient().getModel().getClientId() : "");

            }
            loResult.put("result", "success");
            return loResult;
        }
    }

    public JSONObject searchGeneral(String fsValue, boolean fbByCode) throws SQLException, GuanzonException, Exception {

        if (fbByCode) {
            JSONObject loJSON = new JSONObject();
            if (fsValue.equals(getModel().GeneralAssign().getClientId())) {
                loJSON.put("result", "success");
                return loJSON;
            } else {
                loJSON.put("result", "error");
                loJSON.put("message", "Client not found.");
                return loJSON;
            }
        } else {
            JSONObject loJSON = null;
            if (!fsValue.isEmpty()) {

                String lsSQL = "SELECT"
                        + " sClientID"
                        + ", sCompnyNm"
                        + " FROM Client_Master"
                        + " WHERE cRecdStat= '1'";

                loJSON = ShowDialogFX.Search(poGRider,
                        lsSQL,
                        fsValue,
                        "Client ID»Client Name",
                        "sClientID»sCompnyNm",
                        "sClientID»sCompnyNm",
                        fbByCode ? 0 : 1);

                if (loJSON == null) {
                    return loJSON;
                } else {
                    if ("error".equals(loJSON.get("result"))) {
                        return loJSON;
                    }
                }
            }

            //initialize Client GUI
            ClientGUI loClient = new ClientGUI();

            loClient.setGRider(poGRider);
            loClient.setLogWrapper(null);
            loClient.setCategoryCode(poGRider.getCategory());

            //filter client type 
            loClient.setClientType(ClientType.INDIVIDUAL);

            //searchRecord(fsValue,fbByCode) will run make sure to set client and bycode
            //bycode true client id
            //bycode false company
            //set search by code
            loClient.setByCode(fbByCode);

            if (loJSON != null) {
                getModel().setGeneralAssignedId(loJSON.get("sClientID").toString());

                //set client id
                loClient.setClientId(getModel().getGeneralAssignedId());

            } else {
                loClient.setClientId("");
                JSONObject loResult = new JSONObject();
                loResult.put("result", "error");
                return loResult;
            }

            //load record
            CommonUtils.showModal(loClient);

            //initialize new json for result
            JSONObject loResult = new JSONObject();

            //load if button 
            if (!loClient.isCancelled()) {

                getModel().setGeneralAssignedId(loClient.getClient().getModel().getClientId() != null ? loClient.getClient().getModel().getClientId() : "");

            }
            loResult.put("result", "success");
            return loResult;
        }
    }
}
