package org.guanzon.cas.parameter.model;

import java.sql.SQLException;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;

/**
 *
 * @author maynevval 07-26-2025
 */
public class Model_Branch_Cluster_Delivery extends Model {

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            poEntity.updateString("cTrckSize", "0");
            poEntity.updateInt("nAllocatn", 0);
            poEntity.updateInt("nDelivery", 0);

            ID = poEntity.getMetaData().getColumnLabel(1);
            ID2 = poEntity.getMetaData().getColumnLabel(2);

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }
    //Getter & Setter 
    //sClustrID
    //cTrckSize*
    //nAllocatn*
    //nDelivery*

    //sClustrID
    public JSONObject setClusterID(String clusterID) {
        return setValue("sClustrID", clusterID);
    }

    public String getClusterID() {
        return (String) getValue("sClustrID");
    }

    //cTrckSize
    public JSONObject setTruckSize(String truckSize) {
        return setValue("cTrckSize", truckSize);
    }

    public String getTruckSize() {
        return (String) getValue("cTrckSize");
    }

    //nAllocatn
    public JSONObject setAllocation(Number allocation) {
        return setValue("nAllocatn", allocation);
    }

    public Number getAllocation() {
        return (Number) getValue("nAllocatn");
    }

    //nDelivery
    public JSONObject setDelivery(Number allocation) {
        return setValue("nDelivery", allocation);
    }

    public Number getDelivery() {
        return (Number) getValue("nDelivery");
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

}
