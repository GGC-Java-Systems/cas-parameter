package org.guanzon.cas.parameter.services;

import java.sql.SQLException;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.cas.parameter.AffiliatedCompany;
import org.guanzon.cas.parameter.Banks;
import org.guanzon.cas.parameter.BanksBranch;
import org.guanzon.cas.parameter.Barangay;
import org.guanzon.cas.parameter.Bin;
import org.guanzon.cas.parameter.Branch;
import org.guanzon.cas.parameter.BranchArea;
import org.guanzon.cas.parameter.BranchCluster;
import org.guanzon.cas.parameter.BranchClusterDelivery;
import org.guanzon.cas.parameter.BranchOthers;
import org.guanzon.cas.parameter.Brand;
import org.guanzon.cas.parameter.Category;
import org.guanzon.cas.parameter.CategoryLevel2;
import org.guanzon.cas.parameter.CategoryLevel3;
import org.guanzon.cas.parameter.CategoryLevel4;
import org.guanzon.cas.parameter.Color;
import org.guanzon.cas.parameter.ColorDetail;
import org.guanzon.cas.parameter.Company;
import org.guanzon.cas.parameter.Country;
import org.guanzon.cas.parameter.Department;
import org.guanzon.cas.parameter.Industry;
import org.guanzon.cas.parameter.InvLocation;
import org.guanzon.cas.parameter.InvType;
import org.guanzon.cas.parameter.InventoryCountType;
import org.guanzon.cas.parameter.Made;
import org.guanzon.cas.parameter.Measure;
import org.guanzon.cas.parameter.Model;
import org.guanzon.cas.parameter.ModelVariant;
import org.guanzon.cas.parameter.Project;
import org.guanzon.cas.parameter.Province;
import org.guanzon.cas.parameter.Region;
import org.guanzon.cas.parameter.Relationship;
import org.guanzon.cas.parameter.Section;
import org.guanzon.cas.parameter.Size;
import org.guanzon.cas.parameter.TaxCode;
import org.guanzon.cas.parameter.Term;
import org.guanzon.cas.parameter.TownCity;
import org.guanzon.cas.parameter.TransactionSource;
import org.guanzon.cas.parameter.TransactionSourceTable;
import org.guanzon.cas.parameter.Warehouse;

public class ParamControllers {

    public ParamControllers(GRiderCAS applicationDriver, LogWrapper logWrapper) {
        poGRider = applicationDriver;
        poLogWrapper = logWrapper;
    }

    public Barangay Barangay() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Barangay: Application driver is not set.");
            return null;
        }

        if (poBarangay != null) {
            return poBarangay;
        }

        poBarangay = new Barangay();
        poBarangay.setApplicationDriver(poGRider);
        poBarangay.setWithParentClass(false);
        poBarangay.setLogWrapper(poLogWrapper);
        poBarangay.initialize();
        poBarangay.newRecord();
        return poBarangay;
    }

    public Bin Bin() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Bin: Application driver is not set.");
            return null;
        }

        if (poBin != null) {
            return poBin;
        }

        poBin = new Bin();
        poBin.setApplicationDriver(poGRider);
        poBin.setWithParentClass(false);
        poBin.setLogWrapper(poLogWrapper);
        poBin.initialize();
        poBin.newRecord();
        return poBin;
    }

    public Branch Branch() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Branch: Application driver is not set.");
            return null;
        }

        if (poBranch != null) {
            return poBranch;
        }

        poBranch = new Branch();
        poBranch.setApplicationDriver(poGRider);
        poBranch.setWithParentClass(false);
        poBranch.setLogWrapper(poLogWrapper);
        poBranch.initialize();
        poBranch.newRecord();
        return poBranch;
    }

    public Brand Brand() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Brand: Application driver is not set.");
            return null;
        }

        if (poBrand != null) {
            return poBrand;
        }

        poBrand = new Brand();
        poBrand.setApplicationDriver(poGRider);
        poBrand.setWithParentClass(false);
        poBrand.setLogWrapper(poLogWrapper);
        poBrand.initialize();
        poBrand.newRecord();
        return poBrand;
    }

    public Category Category() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Category: Application driver is not set.");
            return null;
        }

        if (poCategory != null) {
            return poCategory;
        }

        poCategory = new Category();
        poCategory.setApplicationDriver(poGRider);
        poCategory.setWithParentClass(false);
        poCategory.setLogWrapper(poLogWrapper);
        poCategory.initialize();
        poCategory.newRecord();
        return poCategory;
    }

    public CategoryLevel2 CategoryLevel2() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.CategoryLevel2: Application driver is not set.");
            return null;
        }

        if (poCategory2 != null) {
            return poCategory2;
        }

        poCategory2 = new CategoryLevel2();
        poCategory2.setApplicationDriver(poGRider);
        poCategory2.setWithParentClass(false);
        poCategory2.setLogWrapper(poLogWrapper);
        poCategory2.initialize();
        poCategory2.newRecord();
        return poCategory2;
    }

    public CategoryLevel3 CategoryLevel3() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.CategoryLevel3: Application driver is not set.");
            return null;
        }

        if (poCategory3 != null) {
            return poCategory3;
        }

        poCategory3 = new CategoryLevel3();
        poCategory3.setApplicationDriver(poGRider);
        poCategory3.setWithParentClass(false);
        poCategory3.setLogWrapper(poLogWrapper);
        poCategory3.initialize();
        poCategory3.newRecord();
        return poCategory3;
    }

    public CategoryLevel4 CategoryLevel4() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.CategoryLevel4: Application driver is not set.");
            return null;
        }

        if (poCategory4 != null) {
            return poCategory4;
        }

        poCategory4 = new CategoryLevel4();
        poCategory4.setApplicationDriver(poGRider);
        poCategory4.setWithParentClass(false);
        poCategory4.setLogWrapper(poLogWrapper);
        poCategory4.initialize();
        poCategory4.newRecord();
        return poCategory4;
    }

    public Color Color() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Color: Application driver is not set.");
            return null;
        }

        if (poColor != null) {
            return poColor;
        }

        poColor = new Color();
        poColor.setApplicationDriver(poGRider);
        poColor.setWithParentClass(false);
        poColor.setLogWrapper(poLogWrapper);
        poColor.initialize();
        poColor.newRecord();
        return poColor;
    }

    public ColorDetail ColorDetail() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Color: Application driver is not set.");
            return null;
        }

        if (poColorDetail != null) {
            return poColorDetail;
        }

        poColorDetail = new ColorDetail();
        poColorDetail.setApplicationDriver(poGRider);
        poColorDetail.setWithParentClass(false);
        poColorDetail.setLogWrapper(poLogWrapper);
        poColorDetail.initialize();
        poColorDetail.newRecord();
        return poColorDetail;
    }

    public Country Country() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Country: Application driver is not set.");
            return null;
        }

        if (poCountry != null) {
            return poCountry;
        }

        poCountry = new Country();
        poCountry.setApplicationDriver(poGRider);
        poCountry.setWithParentClass(false);
        poCountry.setLogWrapper(poLogWrapper);
        poCountry.initialize();
        poCountry.newRecord();
        return poCountry;
    }

    public Department Department() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Department: Application driver is not set.");
            return null;
        }

        if (poDepartment != null) {
            return poDepartment;
        }

        poDepartment = new Department();
        poDepartment.setApplicationDriver(poGRider);
        poDepartment.setWithParentClass(false);
        poDepartment.setLogWrapper(poLogWrapper);
        poDepartment.initialize();
        poDepartment.newRecord();
        return poDepartment;
    }

    public Industry Industry() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.InventoryLocation: Application driver is not set.");
            return null;
        }

        if (poIndustry != null) {
            return poIndustry;
        }

        poIndustry = new Industry();
        poIndustry.setApplicationDriver(poGRider);
        poIndustry.setWithParentClass(false);
        poIndustry.setLogWrapper(poLogWrapper);
        poIndustry.initialize();
        poIndustry.newRecord();
        return poIndustry;
    }

    public InvLocation InventoryLocation() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.InventoryLocation: Application driver is not set.");
            return null;
        }

        if (poInvLocation != null) {
            return poInvLocation;
        }

        poInvLocation = new InvLocation();
        poInvLocation.setApplicationDriver(poGRider);
        poInvLocation.setWithParentClass(false);
        poInvLocation.setLogWrapper(poLogWrapper);
        poInvLocation.initialize();
        poInvLocation.newRecord();
        return poInvLocation;
    }
    public InventoryCountType InventoryCountType() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.InventoryCountType: Application driver is not set.");
            return null;
        }

        if (poInvType != null) {
            return poInventoryCountType;
        }

        poInventoryCountType = new InventoryCountType();
        poInventoryCountType.setApplicationDriver(poGRider);
        poInventoryCountType.setWithParentClass(false);
        poInventoryCountType.setLogWrapper(poLogWrapper);
        poInventoryCountType.initialize();
        poInventoryCountType.newRecord();
        return poInventoryCountType;
    }


    public InvType InventoryType() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.InventoryType: Application driver is not set.");
            return null;
        }

        if (poInvType != null) {
            return poInvType;
        }

        poInvType = new InvType();
        poInvType.setApplicationDriver(poGRider);
        poInvType.setWithParentClass(false);
        poInvType.setLogWrapper(poLogWrapper);
        poInvType.initialize();
        poInvType.newRecord();
        return poInvType;
    }

    public Measure Measurement() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Measurement: Application driver is not set.");
            return null;
        }

        if (poMeasure != null) {
            return poMeasure;
        }

        poMeasure = new Measure();
        poMeasure.setApplicationDriver(poGRider);
        poMeasure.setWithParentClass(false);
        poMeasure.setLogWrapper(poLogWrapper);
        poMeasure.initialize();
        poMeasure.newRecord();
        return poMeasure;
    }

    public Model Model() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Model: Application driver is not set.");
            return null;
        }

        if (poModel != null) {
            return poModel;
        }

        poModel = new Model();
        poModel.setApplicationDriver(poGRider);
        poModel.setWithParentClass(false);
        poModel.setLogWrapper(poLogWrapper);
        poModel.initialize();
        poModel.newRecord();
        return poModel;
    }

    public ModelVariant ModelVariant() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.ModelVariant: Application driver is not set.");
            return null;
        }

        if (poModelVariant != null) {
            return poModelVariant;
        }

        poModelVariant = new ModelVariant();
        poModelVariant.setApplicationDriver(poGRider);
        poModelVariant.setWithParentClass(false);
        poModelVariant.setLogWrapper(poLogWrapper);
        poModelVariant.initialize();
        poModelVariant.newRecord();
        return poModelVariant;
    }

    public Province Province() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Province: Application driver is not set.");
            return null;
        }

        if (poProvince != null) {
            return poProvince;
        }

        poProvince = new Province();
        poProvince.setApplicationDriver(poGRider);
        poProvince.setWithParentClass(false);
        poProvince.setLogWrapper(poLogWrapper);
        poProvince.initialize();
        poProvince.newRecord();
        return poProvince;
    }

    public Region Region() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Region: Application driver is not set.");
            return null;
        }

        if (poRegion != null) {
            return poRegion;
        }

        poRegion = new Region();
        poRegion.setApplicationDriver(poGRider);
        poRegion.setWithParentClass(false);
        poRegion.setLogWrapper(poLogWrapper);
        poRegion.initialize();
        poRegion.newRecord();
        return poRegion;
    }

    public Section Section() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Section: Application driver is not set.");
            return null;
        }

        if (poSection != null) {
            return poSection;
        }

        poSection = new Section();
        poSection.setApplicationDriver(poGRider);
        poSection.setWithParentClass(false);
        poSection.setLogWrapper(poLogWrapper);
        poSection.initialize();
        poSection.newRecord();
        return poSection;
    }

    public TownCity TownCity() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.TownCity: Application driver is not set.");
            return null;
        }

        if (poTownCity != null) {
            return poTownCity;
        }

        poTownCity = new TownCity();
        poTownCity.setApplicationDriver(poGRider);
        poTownCity.setWithParentClass(false);
        poTownCity.setLogWrapper(poLogWrapper);
        poTownCity.initialize();
        poTownCity.newRecord();
        return poTownCity;
    }

    public Term Term() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.TownCity: Application driver is not set.");
            return null;
        }

        if (poTerm != null) {
            return poTerm;
        }

        poTerm = new Term();
        poTerm.setApplicationDriver(poGRider);
        poTerm.setWithParentClass(false);
        poTerm.setLogWrapper(poLogWrapper);
        poTerm.initialize();
        poTerm.newRecord();
        return poTerm;
    }

    public Warehouse Warehouse() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Warehouse: Application driver is not set.");
            return null;
        }

        if (poWarehouse != null) {
            return poWarehouse;
        }

        poWarehouse = new Warehouse();
        poWarehouse.setApplicationDriver(poGRider);
        poWarehouse.setWithParentClass(false);
        poWarehouse.setLogWrapper(poLogWrapper);
        poWarehouse.initialize();
        poWarehouse.newRecord();
        return poWarehouse;
    }

    public Banks Banks() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Banks: Application driver is not set.");
            return null;
        }

        if (poBanks != null) {
            return poBanks;
        }

        poBanks = new Banks();
        poBanks.setApplicationDriver(poGRider);
        poBanks.setWithParentClass(false);
        poBanks.setLogWrapper(poLogWrapper);
        poBanks.initialize();
        poBanks.newRecord();
        return poBanks;
    }

    public BanksBranch BanksBranch() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Banks: Application driver is not set.");
            return null;
        }

        if (poBanksBranches != null) {
            return poBanksBranches;
        }

        poBanksBranches = new BanksBranch();
        poBanksBranches.setApplicationDriver(poGRider);
        poBanksBranches.setWithParentClass(false);
        poBanksBranches.setLogWrapper(poLogWrapper);
        poBanksBranches.initialize();
        poBanksBranches.newRecord();
        return poBanksBranches;
    }

    public Made Made() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Made: Application driver is not set.");
            return null;
        }

        if (poMade != null) {
            return poMade;
        }

        poMade = new Made();
        poMade.setApplicationDriver(poGRider);
        poMade.setWithParentClass(false);
        poMade.setLogWrapper(poLogWrapper);
        poMade.initialize();
        poMade.newRecord();
        return poMade;
    }

    public Relationship Relationship() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Made: Application driver is not set.");
            return null;
        }

        if (poRelationship != null) {
            return poRelationship;
        }

        poRelationship = new Relationship();
        poRelationship.setApplicationDriver(poGRider);
        poRelationship.setWithParentClass(false);
        poRelationship.setLogWrapper(poLogWrapper);
        poRelationship.initialize();
        poRelationship.newRecord();
        return poRelationship;
    }

    public Size Size() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Size: Application driver is not set.");
            return null;
        }

        if (poSize != null) {
            return poSize;
        }

        poSize = new Size();
        poSize.setApplicationDriver(poGRider);
        poSize.setWithParentClass(false);
        poSize.setLogWrapper(poLogWrapper);
        poSize.initialize();
        poSize.newRecord();
        return poSize;
    }

    public Company Company() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.Company: Application driver is not set.");
            return null;
        }

        if (poCompany != null) {
            return poCompany;
        }

        poCompany = new Company();
        poCompany.setApplicationDriver(poGRider);
        poCompany.setWithParentClass(false);
        poCompany.setLogWrapper(poLogWrapper);
        poCompany.initialize();
        poCompany.newRecord();
        return poCompany;
    }

    public AffiliatedCompany AffiliatedCompany() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.AffiliatedCompany: Application driver is not set.");
            return null;
        }

        if (poAffiliatedCompany != null) {
            return poAffiliatedCompany;
        }

        poAffiliatedCompany = new AffiliatedCompany();
        poAffiliatedCompany.setApplicationDriver(poGRider);
        poAffiliatedCompany.setWithParentClass(false);
        poAffiliatedCompany.setLogWrapper(poLogWrapper);
        poAffiliatedCompany.initialize();
        poAffiliatedCompany.newRecord();
        return poAffiliatedCompany;
    }

    public TaxCode TaxCode() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("ParamControllers.TaxCode: Application driver is not set.");
            return null;
        }

        if (poTaxCode != null) {
            return poTaxCode;
        }

        poTaxCode = new TaxCode();
        poTaxCode.setApplicationDriver(poGRider);
        poTaxCode.setWithParentClass(false);
        poTaxCode.setLogWrapper(poLogWrapper);
        poTaxCode.initialize();
        poTaxCode.newRecord();
        return poTaxCode;
    }

    public BranchOthers BranchOthers() throws SQLException, GuanzonException {
        if (this.poGRider == null) {
            this.poLogWrapper.severe("DeliveryParamController.BranchOthers: Application driver is not set.");
            return null;
        }
        if (this.poBranchOthers != null) {
            return this.poBranchOthers;
        }
        this.poBranchOthers = new BranchOthers();
        this.poBranchOthers.setApplicationDriver(this.poGRider);
        this.poBranchOthers.setWithParentClass(false);
        this.poBranchOthers.setLogWrapper(this.poLogWrapper);
        this.poBranchOthers.initialize();
        this.poBranchOthers.newRecord();
        return this.poBranchOthers;
    }

    public BranchArea BranchArea() throws SQLException, GuanzonException {
        if (this.poGRider == null) {
            this.poLogWrapper.severe("DeliveryParamController.BranchArea: Application driver is not set.");
            return null;
        }
        if (this.poBranchCluster != null) {
            return this.poBranchArea;
        }
        this.poBranchArea = new BranchArea();
        this.poBranchArea.setApplicationDriver(this.poGRider);
        this.poBranchArea.setWithParentClass(false);
        this.poBranchArea.setLogWrapper(this.poLogWrapper);
        this.poBranchArea.initialize();
        this.poBranchArea.newRecord();
        return this.poBranchArea;
    }

    public BranchCluster BranchCluster() throws SQLException, GuanzonException {
        if (this.poGRider == null) {
            this.poLogWrapper.severe("DeliveryParamController.BranchCluster: Application driver is not set.");
            return null;
        }
        if (this.poBranchCluster != null) {
            return this.poBranchCluster;
        }
        this.poBranchCluster = new BranchCluster();
        this.poBranchCluster.setApplicationDriver(this.poGRider);
        this.poBranchCluster.setWithParentClass(false);
        this.poBranchCluster.setLogWrapper(this.poLogWrapper);
        this.poBranchCluster.setRecordStatus(RecordStatus.ACTIVE);
        this.poBranchCluster.initialize();
        this.poBranchCluster.newRecord();

        return this.poBranchCluster;
    }

    public BranchClusterDelivery BranchClusterDelivery() throws SQLException, GuanzonException {
        if (this.poGRider == null) {
            this.poLogWrapper.severe("DeliveryParamController.BranchClusterDelivery: Application driver is not set.");
            return null;
        }
        if (this.poBranchCluster != null) {
            return this.poBranchClusterDelivery;
        }
        this.poBranchClusterDelivery = new BranchClusterDelivery();
        this.poBranchClusterDelivery.setApplicationDriver(this.poGRider);
        this.poBranchClusterDelivery.setWithParentClass(false);
        this.poBranchClusterDelivery.setLogWrapper(this.poLogWrapper);
        this.poBranchClusterDelivery.initialize();
        this.poBranchClusterDelivery.newRecord();
        return this.poBranchClusterDelivery;
    }

    public TransactionSource TransactionSource() throws SQLException, GuanzonException {
        if (this.poGRider == null) {
            this.poLogWrapper.severe("DeliveryParamController.TransactionSource: Application driver is not set.");
            return null;
        }
        if (this.poTransactionSource != null) {
            return this.poTransactionSource;
        }
        this.poTransactionSource = new TransactionSource();
        this.poTransactionSource.setApplicationDriver(this.poGRider);
        this.poTransactionSource.setWithParentClass(false);
        this.poTransactionSource.setLogWrapper(this.poLogWrapper);
        this.poTransactionSource.initialize();
        this.poTransactionSource.newRecord();
        return this.poTransactionSource;
    }
    
    public TransactionSourceTable TransactionSourceTable() throws SQLException, GuanzonException {
        if (this.poGRider == null) {
            this.poLogWrapper.severe("DeliveryParamController.TransactionSourceTable: Application driver is not set.");
            return null;
        }
        if (this.poTransactionSourceTable != null) {
            return this.poTransactionSourceTable;
        }
        this.poTransactionSourceTable = new TransactionSourceTable();
        this.poTransactionSourceTable.setApplicationDriver(this.poGRider);
        this.poTransactionSourceTable.setWithParentClass(false);
        this.poTransactionSourceTable.setLogWrapper(this.poLogWrapper);
        this.poTransactionSourceTable.initialize();
        this.poTransactionSourceTable.newRecord();
        return this.poTransactionSourceTable;
    }

    public Project Project() throws SQLException, GuanzonException {
        if (this.poGRider == null) {
            this.poLogWrapper.severe("DeliveryParamController.TransactionSourceTable: Application driver is not set.");
            return null;
        }
        if (this.poProject != null) {
            return this.poProject;
        }
        this.poProject = new Project();
        this.poProject.setApplicationDriver(this.poGRider);
        this.poProject.setWithParentClass(false);
        this.poProject.setLogWrapper(this.poLogWrapper);
        this.poProject.initialize();
        this.poProject.newRecord();
        return this.poProject;
    }        
            
            
    private GRiderCAS poGRider;
    private LogWrapper poLogWrapper;

    private Barangay poBarangay;
    private Bin poBin;
    private Branch poBranch;
    private BranchOthers poBranchOthers;
    private BranchArea poBranchArea;
    private BranchCluster poBranchCluster;
    private BranchClusterDelivery poBranchClusterDelivery;
    private Brand poBrand;
    private Category poCategory;
    private CategoryLevel2 poCategory2;
    private CategoryLevel3 poCategory3;
    private CategoryLevel4 poCategory4;
    private Color poColor;
    private ColorDetail poColorDetail;
    private Country poCountry;
    private Industry poIndustry;
    private InvLocation poInvLocation;
    private InventoryCountType poInventoryCountType;
    private InvType poInvType;
    private Measure poMeasure;
    private Model poModel;
    private ModelVariant poModelVariant;
    private Province poProvince;
    private Region poRegion;
    private Section poSection;
    private TownCity poTownCity;
    private Term poTerm;
    private Warehouse poWarehouse;
    private Banks poBanks;
    private BanksBranch poBanksBranches;
    private Made poMade;
    private Relationship poRelationship;
    private Size poSize;
    private Company poCompany;
    private Department poDepartment;
    private AffiliatedCompany poAffiliatedCompany;
    private TaxCode poTaxCode;
    private TransactionSource poTransactionSource;
    private TransactionSourceTable poTransactionSourceTable;
    private Project poProject;
}
