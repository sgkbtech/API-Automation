package com.ge.current.em.automation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by 212554696 on 4/20/17.
 */
public class AlertLogSolrIndex implements Serializable {

    public static final int TIME_BUCKET_INDEX = 0;
    public static final int ENTERPRISE_UID_INDEX = 1;
    public static final int RESRC_UID_INDEX = 2;
    public static final int ALERT_TS_INDEX = 3;
    public static final int LOG_UUID_INDEX = 4;

    @JsonProperty("_uniqueKey")
    private String uniqueKey;

    @JsonProperty("resrc_uid")
    private String resrcUid;

    @JsonProperty("zone_name")
    private String zoneName;

    @JsonProperty("alert_ts")
    private String alertTs;

    @JsonProperty("alert_type")
    private String alertType;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("log_uuid")
    private String logUuid;

    @JsonProperty("alert_ts_tz")
    private String alertTsTz;

    @JsonProperty("time_bucket")
    private String timeBucket;

    @JsonProperty("severity")
    private String severity;

    @JsonProperty("resrc_type")
    private String resrcType;

    @JsonProperty("alert_ts_loc")
    private String alertTsLoc;

    @JsonProperty("asset_uid")
    private String assetUid;

    @JsonProperty("enterprise_uid")
    private String enterpriseUid;

    @JsonProperty("site_name")
    private String siteName;

    @JsonProperty("category")
    private String category;

    @JsonProperty("alert_name")
    private String alertName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("ext_propsLT")
    private String extPropsLT;

    @JsonProperty("ext_propsLT2")
    private String extPropsLT2;

    @JsonProperty("ext_propsassetName")
    private String extPropsAssetName;

    @JsonProperty("ext_propsasset_type")
    private String extPropsAssetType;

    @JsonProperty("ext_propsfaultCategory")
    private String extPropsFaultCategory;

    @JsonProperty("ext_propsfrequency")
    private String extPropsFrequency;

    @JsonProperty("ext_propsprofileName")
    private String extPropsProfileName;

    @JsonProperty("ext_propssiteLtRuleGroup")
    private String extPropsSiteLtRuleGroup;

    @JsonProperty("ext_propssite_uid")
    private String extPropsSiteUid;

    @JsonProperty("ext_propstablename")
    private String extPropsTableName;

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getResrcUid() {
        return resrcUid;
    }

    public void setResrcUid(String resrcUid) {
        this.resrcUid = resrcUid;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getAlertTs() {
        return alertTs;
    }

    public void setAlertTs(String alertTs) {
        this.alertTs = alertTs;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getLogUuid() {
        return logUuid;
    }

    public void setLogUuid(String logUuid) {
        this.logUuid = logUuid;
    }

    public String getAlertTsTz() {
        return alertTsTz;
    }

    public void setAlertTsTz(String alertTsTz) {
        this.alertTsTz = alertTsTz;
    }

    public String getTimeBucket() {
        return timeBucket;
    }

    public void setTimeBucket(String timeBucket) {
        this.timeBucket = timeBucket;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getResrcType() {
        return resrcType;
    }

    public void setResrcType(String resrcType) {
        this.resrcType = resrcType;
    }

    public String getAlertTsLoc() {
        return alertTsLoc;
    }

    public void setAlertTsLoc(String alertTsLoc) {
        this.alertTsLoc = alertTsLoc;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getEnterpriseUid() {
        return enterpriseUid;
    }

    public void setEnterpriseUid(String enterpriseUid) {
        this.enterpriseUid = enterpriseUid;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExtPropsLT() {
        return extPropsLT;
    }

    public void setExtPropsLT(String extPropsLT) {
        this.extPropsLT = extPropsLT;
    }

    public String getExtPropsLT2() {
        return extPropsLT2;
    }

    public void setExtPropsLT2(String extPropsLT2) {
        this.extPropsLT2 = extPropsLT2;
    }

    public String getExtPropsAssetName() {
        return extPropsAssetName;
    }

    public void setExtPropsAssetName(String extPropsAssetName) {
        this.extPropsAssetName = extPropsAssetName;
    }

    public String getExtPropsAssetType() {
        return extPropsAssetType;
    }

    public void setExtPropsAssetType(String extPropsAssetType) {
        this.extPropsAssetType = extPropsAssetType;
    }

    public String getExtPropsFaultCategory() {
        return extPropsFaultCategory;
    }

    public void setExtPropsFaultCategory(String extPropsFaultCategory) {
        this.extPropsFaultCategory = extPropsFaultCategory;
    }

    public String getExtPropsFrequency() {
        return extPropsFrequency;
    }

    public void setExtPropsFrequency(String extPropsFrequency) {
        this.extPropsFrequency = extPropsFrequency;
    }

    public String getExtPropsProfileName() {
        return extPropsProfileName;
    }

    public void setExtPropsProfileName(String extPropsProfileName) {
        this.extPropsProfileName = extPropsProfileName;
    }

    public String getExtPropsSiteLtRuleGroup() {
        return extPropsSiteLtRuleGroup;
    }

    public void setExtPropsSiteLtRuleGroup(String extPropsSiteLtRuleGroup) {
        this.extPropsSiteLtRuleGroup = extPropsSiteLtRuleGroup;
    }

    public String getExtPropsSiteUid() {
        return extPropsSiteUid;
    }

    public void setExtPropsSiteUid(String extPropsSiteUid) {
        this.extPropsSiteUid = extPropsSiteUid;
    }

    public String getExtPropsTableName() {
        return extPropsTableName;
    }

    public void setExtPropsTableName(String extPropsTableName) {
        this.extPropsTableName = extPropsTableName;
    }

    @Override public String toString() {
        return "AlertLogSolrIndex{" +
                "uniqueKey='" + uniqueKey + '\'' +
                ", resrcUid='" + resrcUid + '\'' +
                ", zoneName='" + zoneName + '\'' +
                ", alertTs='" + alertTs + '\'' +
                ", alertType='" + alertType + '\'' +
                ", duration='" + duration + '\'' +
                ", logUuid='" + logUuid + '\'' +
                ", alertTsTz='" + alertTsTz + '\'' +
                ", timeBucket='" + timeBucket + '\'' +
                ", severity='" + severity + '\'' +
                ", resrcType='" + resrcType + '\'' +
                ", alertTsLoc='" + alertTsLoc + '\'' +
                ", assetUid='" + assetUid + '\'' +
                ", enterpriseUid='" + enterpriseUid + '\'' +
                ", siteName='" + siteName + '\'' +
                ", category='" + category + '\'' +
                ", alertName='" + alertName + '\'' +
                ", status='" + status + '\'' +
                ", extPropsLT='" + extPropsLT + '\'' +
                ", extPropsLT2='" + extPropsLT2 + '\'' +
                ", extPropsAssetName='" + extPropsAssetName + '\'' +
                ", extPropsAssetType='" + extPropsAssetType + '\'' +
                ", extPropsFaultCategory='" + extPropsFaultCategory + '\'' +
                ", extPropsFrequency='" + extPropsFrequency + '\'' +
                ", extPropsProfileName='" + extPropsProfileName + '\'' +
                ", extPropsSiteLtRuleGroup='" + extPropsSiteLtRuleGroup + '\'' +
                ", extPropsSiteUid='" + extPropsSiteUid + '\'' +
                ", extPropsTableName='" + extPropsTableName + '\'' +
                '}';
    }

    public static final class AlertLogSolrIndexBuilder {
        private String uniqueKey;
        private String resrcUid;
        private String zoneName;
        private String alertTs;
        private String alertType;
        private Integer duration;
        private String logUuid;
        private String alertTsTz;
        private String timeBucket;
        private String severity;
        private String resrcType;
        private String alertTsLoc;
        private String assetUid;
        private String enterpriseUid;
        private String siteName;
        private String category;
        private String alertName;
        private String status;
        private String extPropsLT;
        private String extPropsLT2;
        private String extPropsAssetName;
        private String extPropsAssetType;
        private String extPropsFaultCategory;
        private String extPropsFrequency;
        private String extPropsProfileName;
        private String extPropsSiteLtRuleGroup;
        private String extPropsSiteUid;
        private String extPropsTableName;

        private AlertLogSolrIndexBuilder() {

        }

        public static AlertLogSolrIndexBuilder anAlertLogSolrIndex() {
            return new AlertLogSolrIndexBuilder();
        }

        public AlertLogSolrIndexBuilder withUniqueKey(String uniqueKey) {
            this.uniqueKey = uniqueKey;
            return this;
        }

        public AlertLogSolrIndexBuilder withResrcUid(String resrcUid) {
            this.resrcUid = resrcUid;
            return this;
        }

        public AlertLogSolrIndexBuilder withZoneName(String zoneNameoneName) {
            this.zoneName = zoneName;
            return this;
        }

        public AlertLogSolrIndexBuilder withAlertTs(String alertTs) {
            this.alertTs = alertTs;
            return this;
        }

        public AlertLogSolrIndexBuilder withAlertType(String alertType) {
            this.alertType = alertType;
            return this;
        }

        public AlertLogSolrIndexBuilder withDuration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public AlertLogSolrIndexBuilder withLogUuid(String logUuid) {
            this.logUuid = logUuid;
            return this;
        }

        public AlertLogSolrIndexBuilder withAlertTsTz(String alertTsTz) {
            this.alertTsTz = alertTsTz;
            return this;
        }

        public AlertLogSolrIndexBuilder withTimeBucket(String timeBucket) {
            this.timeBucket = timeBucket;
            return this;
        }

        public AlertLogSolrIndexBuilder withSeverity(String severity) {
            this.severity = severity;
            return this;
        }

        public AlertLogSolrIndexBuilder withResrcType(String resrcType) {
            this.resrcType = resrcType;
            return this;
        }

        public AlertLogSolrIndexBuilder withAlertTsLoc(String alertTsLoc) {
            this.alertTsLoc = alertTsLoc;
            return this;
        }

        public AlertLogSolrIndexBuilder withAssetUid(String assetUid) {
            this.assetUid = assetUid;
            return this;
        }

        public AlertLogSolrIndexBuilder withEnterpriseUid(String enterpriseUid) {
            this.enterpriseUid = enterpriseUid;
            return this;
        }

        public AlertLogSolrIndexBuilder withSiteName(String siteName) {
            this.siteName = siteName;
            return this;
        }

        public AlertLogSolrIndexBuilder withCatergory(String category) {
            this.category = category;
            return this;
        }

        public AlertLogSolrIndexBuilder withAlertName(String alertName) {
            this.alertName = alertName;
            return this;
        }

        public AlertLogSolrIndexBuilder withStatus(String status) {
            this.status = status;
            return this;
        }

        public AlertLogSolrIndexBuilder withExtPropsLT(String extPropsLT) {
            this.extPropsLT = extPropsLT;
            return this;
        }

        public AlertLogSolrIndexBuilder withExtPropsLT2(String extPropsLT2) {
            this.extPropsLT2 = extPropsLT2;
            return this;
        }

        public AlertLogSolrIndexBuilder withExtPropsAssetName(String extPropsAssetName) {
            this.extPropsAssetName = extPropsAssetName;
            return this;
        }

        public AlertLogSolrIndexBuilder withExtPropsAssetType(String extPropsAssetType) {
            this.extPropsAssetType = extPropsAssetType;
            return this;
        }

        public AlertLogSolrIndexBuilder withExtPropsFaultCatergory(String extPropsFaultCategory) {
            this.extPropsFaultCategory = extPropsFaultCategory;
            return this;
        }

        public AlertLogSolrIndexBuilder withExtPropsFrequency(String extPropsFrequency) {
            this.extPropsFrequency = extPropsFrequency;
            return this;
        }

        public AlertLogSolrIndexBuilder withExtPropsProfileName(String extPropsProfileName) {
            this.extPropsProfileName = extPropsProfileName;
            return this;
        }

        public AlertLogSolrIndexBuilder withExtPropsSiteLtRuleGroup(String extPropsSiteLtRuleGroup) {
            this.extPropsSiteLtRuleGroup = extPropsSiteLtRuleGroup;
            return this;
        }

        public AlertLogSolrIndexBuilder withExtPropsSiteUid(String extPropsSiteUid) {
            this.extPropsSiteUid = extPropsSiteUid;
            return this;
        }

        public AlertLogSolrIndexBuilder withExtPropsTableName(String extPropsTableName) {
            this.extPropsTableName = extPropsTableName;
            return this;
        }

        public AlertLogSolrIndex build() {
            AlertLogSolrIndex alertLogSolrIndex = new AlertLogSolrIndex();
            alertLogSolrIndex.setUniqueKey(uniqueKey);
            alertLogSolrIndex.setResrcUid(resrcUid);
            alertLogSolrIndex.setZoneName(zoneName);
            alertLogSolrIndex.setAlertTs(alertTs);
            alertLogSolrIndex.setAlertType(alertType);
            alertLogSolrIndex.setDuration(duration);
            alertLogSolrIndex.setLogUuid(logUuid);
            alertLogSolrIndex.setAlertTsTz(alertTsTz);
            alertLogSolrIndex.setTimeBucket(timeBucket);
            alertLogSolrIndex.setSeverity(severity);
            alertLogSolrIndex.setResrcType(resrcType);
            alertLogSolrIndex.setAlertTsLoc(alertTsLoc);
            alertLogSolrIndex.setAssetUid(assetUid);
            alertLogSolrIndex.setEnterpriseUid(enterpriseUid);
            alertLogSolrIndex.setSiteName(siteName);
            alertLogSolrIndex.setCategory(category);
            alertLogSolrIndex.setAlertName(alertName);
            alertLogSolrIndex.setStatus(status);
            alertLogSolrIndex.setExtPropsLT(extPropsLT);
            alertLogSolrIndex.setExtPropsLT2(extPropsLT2);
            alertLogSolrIndex.setExtPropsAssetName(extPropsAssetName);
            alertLogSolrIndex.setExtPropsAssetType(extPropsAssetType);
            alertLogSolrIndex.setExtPropsFaultCategory(extPropsFaultCategory);
            alertLogSolrIndex.setExtPropsFrequency(extPropsFrequency);
            alertLogSolrIndex.setExtPropsProfileName(extPropsProfileName);
            alertLogSolrIndex.setExtPropsSiteLtRuleGroup(extPropsSiteLtRuleGroup);
            alertLogSolrIndex.setExtPropsSiteUid(extPropsSiteUid);
            alertLogSolrIndex.setExtPropsTableName(extPropsTableName);
            return alertLogSolrIndex;
        }
     }

    /**
     *  <p>
     *     Split the uniqueKeys into an array. Assumes that Solr indexing will be consistent since we're hard-coding each key position.
     *     <br>e.g. uniqueKey = '["201704191400","enterprise_GE","ASSET_1234_ComponentTest_DischargeAirFanFailure_Positive.Discharge Air Fan Failure","1492632000000","2acb3ba0-256b-11e7-8ebb-3f3723877c9a"]' </br>
     * </p>
     * @return array : 201704191400 , enterprise_GE , ASSET_1234_ComponentTest_DischargeAirFanFailure_Positive.Discharge Air Fan Failure, 1492632000000, 2acb3ba0-256b-11e7-8ebb-3f3723877c9a
     * @throws IOException
     */
     public String[] retrieveUniqueKeys() throws IOException {
        String[] primaryKeys = uniqueKey.split("\",\"");
        for(int i = 0; i < primaryKeys.length; i++) {
            primaryKeys[i] = primaryKeys[i].replaceAll("\\[\"|\"\\]", "");
        }
        return primaryKeys;
    }

    public static int compareByEventBucket(AlertLogSolrIndex leftHandSide, AlertLogSolrIndex rightHandSide) {
         return (int) (Double.valueOf(leftHandSide.getTimeBucket()) - Double.valueOf(rightHandSide.getTimeBucket()));
    }
}
