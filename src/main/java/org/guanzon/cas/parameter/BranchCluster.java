package org.guanzon.cas.parameter;

import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.services.Parameter;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.json.simple.JSONObject;
import org.guanzon.cas.parameter.model.Model_Branch_Cluster;
import org.guanzon.cas.parameter.services.ParamModels;

/**
 *
 * @author 12mnv
 */
public class BranchCluster extends Parameter {

    Model_Branch_Cluster poModel;

    public void initialize() throws SQLException, GuanzonException {
        this.poModel = (new ParamModels(this.poGRider)).BranchCluster();
        super.initialize();
    }

    public JSONObject isEntryOkay() throws SQLException {
        this.poJSON = new JSONObject();
        if (this.poGRider.getUserLevel() < 16) {
            this.poJSON.put("result", "error");
            this.poJSON.put("message", "User is not allowed to save record.");
            return this.poJSON;
        }
        this.poJSON = new JSONObject();
        if (this.poModel.getClusterID().isEmpty()) {
            this.poJSON.put("result", "error");
            this.poJSON.put("message", "Cluster ID must not be empty.");
            return this.poJSON;
        }
        if (this.poModel.getClusterDescription().isEmpty()) {
            this.poJSON.put("result", "error");
            this.poJSON.put("message", "Cluster Description must not be empty.");
            return this.poJSON;
        }
        this.poModel.setModifyingId(this.poGRider.Encrypt(this.poGRider.getUserID()));
        this.poModel.setModifiedDate(this.poGRider.getServerDate());
        this.poJSON.put("result", "success");
        return this.poJSON;
    }

    public Model_Branch_Cluster getModel() {
        return this.poModel;
    }

    public JSONObject searchRecord(String value, boolean byCode) throws SQLException, GuanzonException {
        String lsCondition = "";
        if (this.psRecdStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= this.psRecdStat.length() - 1; lnCtr++) {
                lsCondition = lsCondition + ", " + SQLUtil.toSQL(Character.toString(this.psRecdStat.charAt(lnCtr)));
            }
            lsCondition = "cRecdStat IN (" + lsCondition.substring(2) + ")";
        } else {
            lsCondition = "cRecdStat = " + SQLUtil.toSQL(this.psRecdStat);
        }
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);
        this.poJSON = ShowDialogFX.Search(this.poGRider,
                lsSQL,
                value,
                "ID»Cluster Name",
                "sClustrID»sClustrDs",
                "sBranchCd»sClustrDs",
                byCode ? 0 : 1);
        if (this.poJSON != null) {
            return this.poModel.openRecord((String) this.poJSON.get("sClustrID"));
        }
        this.poJSON = new JSONObject();
        this.poJSON.put("result", "error");
        this.poJSON.put("message", "No record loaded.");
        return this.poJSON;
    }

    public JSONObject searchRecordbyIndustry(String value, boolean byCode) throws SQLException, GuanzonException {

        if (poModel.getIndustryCode() == null || "".equals(poModel.getIndustryCode())) {
            poJSON.put("result", "error");
            poJSON.put("message", "Industry is not set.");
            return poJSON;
        }
        String lsCondition = "sIndstCdx = " + SQLUtil.toSQL(poModel.getIndustryCode());
        String lsSQL = MiscUtil.addCondition(getSQ_Browse(), lsCondition);
        this.poJSON = ShowDialogFX.Search(this.poGRider,
                lsSQL,
                value,
                "ID»Cluster Name",
                "sClustrID»sClustrDs",
                "sClustrID»sClustrDs",
                byCode ? 0 : 1);
        if (this.poJSON != null) {
            return this.poModel.openRecord((String) this.poJSON.get("sClustrID"),poModel.getIndustryCode());
        }
        this.poJSON = new JSONObject();
        this.poJSON.put("result", "error");
        this.poJSON.put("message", "No record loaded.");
        return this.poJSON;
    }
}
