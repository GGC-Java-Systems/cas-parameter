package org.guanzon.cas.parameter.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;

public class Model_Inventory_Count_Type extends Model {

    private Model_Department poDepartment;
    private Model_Industry poIndustry;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            //assign default values
            poEntity.updateNull("sDeptIDxx");
            poEntity.updateInt("nQuantity", 0);
            poEntity.updateString("cAllowBFw", RecordStatus.ACTIVE);
            poEntity.updateString("cRecdStat", RecordStatus.INACTIVE);
            poEntity.updateObject("dModified", poGRider.getServerDate());
            //end - assign default values

            poDepartment = new ParamModels(poGRider).Department();
            poIndustry = new ParamModels(poGRider).Industry();
            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);
            ID2 = poEntity.getMetaData().getColumnLabel(4);
            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    public JSONObject setInventoryCountID(String inventoryCountID) {
        return setValue("sInvCtrID", inventoryCountID);
    }

    public String getInventoryCountID() {
        return (String) getValue("sInvCtrID");
    }

    public JSONObject setDescription(String description) {
        return setValue("sDescript", description);
    }

    public String getDescription() {
        return (String) getValue("sDescript");
    }

    public JSONObject setDepartmentID(String departmentID) {
        return setValue("sDeptIDxx", departmentID);
    }

    public String getDepartmentID() {
        return (String) getValue("sDeptIDxx");
    }

    public JSONObject setIndustryCode(String industryCode) {
        return setValue("sIndstCdx", industryCode);
    }

    public String getIndustryCode() {
        return (String) getValue("sIndstCdx");
    }

    public JSONObject setPeriod(String period) {
        return setValue("cPeriodxx", period);
    }

    public String getPeriod() {
        return (String) getValue("cPeriodxx");
    }

    public JSONObject setIncluded(String included) {
        return setValue("sIncluded", included);
    }

    public String getIncluded() {
        return (String) getValue("sIncluded");
    }

    public JSONObject setQuantity(int quantity) {
        return setValue("nQuantity", quantity);
    }

    public Integer getQuantity() {
        return Integer.valueOf(getValue("nQuantity").toString());
    }

    public JSONObject setAllowBalanceForward(String allowBalanceForward) {
        return setValue("cAllowBFw", allowBalanceForward);
    }

    public String getAllowBalanceForward() {
        return (String) getValue("cAllowBFw");
    }

    public boolean isAllowBalanceForward() {
        return "1".equals((String) getValue("cAllowBFw"));
    }

    public JSONObject setRecordStatus(String recordStatus) {
        return setValue("cRecdStat", recordStatus);
    }

    public String getRecordStatus() {
        return (String) getValue("cRecdStat");
    }

    public boolean isRecordActive() {
        return "1".equals((String) getValue("cRecdStat"));
    }

    public JSONObject setModifyingId(String modifyingId) {
        return setValue("sModified", modifyingId);
    }

    public String getModifyingId() {
        return (String) getValue("sModified");
    }

    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(getTable(), ID, false, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }

    public Model_Department Department() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sDeptIDxx"))) {
            if (poDepartment.getEditMode() == EditMode.READY
                    && poDepartment.getDepartmentId().equals((String) getValue("sDeptIDxx"))) {
                return poDepartment;
            } else {
                poJSON = poDepartment.openRecord((String) getValue("sDeptIDxx"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poDepartment;
                } else {
                    poDepartment.initialize();
                    return poDepartment;
                }
            }
        } else {
            poDepartment.initialize();
            return poDepartment;
        }
    }

    public Model_Industry Industry() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sIndstCdx"))) {
            if (poIndustry.getEditMode() == EditMode.READY
                    && poIndustry.getIndustryId().equals((String) getValue("sIndstCdx"))) {
                return poIndustry;
            } else {
                poJSON = poIndustry.openRecord((String) getValue("sIndstCdx"));

                if ("success".equals((String) poJSON.get("result"))) {
                    return poIndustry;
                } else {
                    poIndustry.initialize();
                    return poIndustry;
                }
            }
        } else {
            poIndustry.initialize();
            return poIndustry;
        }
    }
}
