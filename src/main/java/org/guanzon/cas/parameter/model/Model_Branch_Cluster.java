package org.guanzon.cas.parameter.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;

/**
 *
 * @author maynevval 07-26-2025
 */
public class Model_Branch_Cluster extends Model {

    private List<Model> paBranchOthers;
    private List<Model> paBranchClusterDelivery;

    public Model_Branch_Others BranchOthers(int fnRow) {
        return (Model_Branch_Others) paBranchOthers.get(fnRow);
    }

    public int getBranchOthersCount() {
        return paBranchOthers.size();
    }

    public Model_Branch_Others BranchOther(int fnRow) {
        return (Model_Branch_Others) paBranchOthers.get(fnRow);
    }

    @SuppressWarnings("unchecked")
    public List<Model_Branch_Others> getBranchOthersList() {
        return (List<Model_Branch_Others>) (List<?>) paBranchOthers;
    }

    public Model_Branch_Cluster_Delivery BranchClusterDelivery(int fnRow) {
        if (fnRow > paBranchClusterDelivery.size() - 1) {
            return null;
        }
        return (Model_Branch_Cluster_Delivery) paBranchClusterDelivery.get(fnRow);
    }

    public Model_Branch_Cluster_Delivery BranchClusterDeliveryTruck(int fnRow) {

        for (int lnCtr = 0; lnCtr <= paBranchClusterDelivery.size() - 1; lnCtr++) {
            if (Integer.parseInt(paBranchClusterDelivery.get(lnCtr)
                    .getValue("cTrckSize").toString()) == fnRow) {
                return (Model_Branch_Cluster_Delivery) paBranchClusterDelivery.get(lnCtr);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<Model_Branch_Cluster_Delivery> getBranchClusterDeliveryList() {
        return (List<Model_Branch_Cluster_Delivery>) (List<?>) paBranchClusterDelivery;
    }

    public int getBranchClusterDeliverysCount() {
        return paBranchClusterDelivery.size();
    }

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = poEntity.getMetaData().getColumnLabel(1);
            ID2 = poEntity.getMetaData().getColumnLabel(3);
            paBranchClusterDelivery = new ArrayList<Model>();
            paBranchOthers = new ArrayList<Model>();

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    //Getter & Setter 
    //sClustrID
    //sClustrDs*
    //sIndstCdx*
    //sHeadOffc*
    //cRecdStat

    //sClustrID
    public JSONObject setClusterID(String clusterID) {
        return setValue("sClustrID", clusterID);
    }

    public String getClusterID() {
        return (String) getValue("sClustrID");
    }

    //sClustrDs
    public JSONObject setClusterDescription(String clusterDescription) {
        return setValue("sClustrDs", clusterDescription);
    }

    public String getClusterDescription() {
        return (String) getValue("sClustrDs");
    }

    //sIndstCdx
    public JSONObject setIndustryCode(String industryCode) {
        return setValue("sIndstCdx", industryCode);
    }

    public String getIndustryCode() {
        return (String) getValue("sIndstCdx");
    }

    //sHeadOffc
    public JSONObject setHeadOffice(String headOffice) {
        return setValue("sHeadOffc", headOffice);
    }

    public String getHeadOffice() {
        return (String) getValue("sHeadOffc");
    }

    //cRecdStat
    public JSONObject setRecordStatus(String recordStatus) {
        return setValue("cRecdStat", recordStatus);
    }

    public String getRecordStatus() {
        return (String) getValue("cRecdStat");
    }

    //cRecdStat
    public JSONObject isRecordActive(boolean isRecordActive) {
        return setValue("cRecdStat", (isRecordActive == true) ? "1" : "0");
    }

    public boolean isRecordActive() {
        return RecordStatus.ACTIVE.equals(getValue("cRecdStat"));
    }

    //sModified
    public JSONObject setModifyingId(String modifyingId) {
        return setValue("sModified", modifyingId);
    }

    public String getModifyingId() {
        return (String) getValue("sModified");
    }

    //dModified
    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(this.getTable(), ID, true, poGRider.getGConnection().getConnection(), poGRider.getBranchCode());
    }

    public JSONObject loadBranchList()
            throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();

        if (getClusterID() == null || getClusterID().isEmpty()) {
            poJSON.put("result", "error");
//            poJSON.put("message", "Cluster is not set.");
            return poJSON;
        }

        paBranchOthers.clear();
        String lsSQL = "SELECT"
                + "  a.sBranchCD"
                + " , sClustrID"
                + " FROM Branch_Others a,"
                + " Branch b"
                + " WHERE a.sBranchCD = b.sBranchCd "
                + " AND sClustrID = " + SQLUtil.toSQL(getClusterID())
                + " AND b.cRecdStat = " + SQLUtil.toSQL(RecordStatus.ACTIVE)
                + " ORDER BY sBranchCD ASC,sClustrID";

        ResultSet loRS = poGRider.executeQuery(lsSQL);

        if (MiscUtil.RecordCount(loRS) <= 0) {
            poJSON.put("result", "error");
            poJSON.put("message", "No Branch registered on this Cluster.");
            return poJSON;
        }

        while (loRS.next()) {
            Model_Branch_Others loBranchOthers = new ParamModels(poGRider).BranchOthers();

            poJSON = loBranchOthers.openRecord(loRS.getString("sBranchCD"));

            if ("success".equals((String) poJSON.get("result"))) {
                paBranchOthers.add((Model) loBranchOthers);
            } else {
                return poJSON;
            }
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    public JSONObject loadBranchClusterDeliveryList()
            throws SQLException, GuanzonException, CloneNotSupportedException {
        poJSON = new JSONObject();

        if (getClusterID() == null || getClusterID().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Cluster is not set.");
            return poJSON;
        }

        paBranchOthers.clear();
        paBranchClusterDelivery.clear();
        String lsSQL = "SELECT"
                + "  sClustrID, cTrckSize"
                + " FROM Branch_Cluster_Delivery"
                + " WHERE sClustrID = " + SQLUtil.toSQL(getClusterID())
                + " ORDER BY sClustrID";

        ResultSet loRS = poGRider.executeQuery(lsSQL);

        if (MiscUtil.RecordCount(loRS) <= 0) {
            poJSON.put("result", "error");
            poJSON.put("message", "No Cluster Delivery registered on this Cluster.");
            return poJSON;
        }
        
        while (loRS.next()) {
            Model_Branch_Cluster_Delivery loBranchClusterDelivery = new ParamModels(poGRider).BranchClusterDelivery();

            poJSON = loBranchClusterDelivery.openRecord(loRS.getString("sClustrID"),loRS.getString("cTrckSize"));

            if ("success".equals((String) poJSON.get("result"))) {
                paBranchClusterDelivery.add((Model) loBranchClusterDelivery);
            } else {
                return poJSON;
            }
        }

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
}
