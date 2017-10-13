package com.ge.current.em.automation.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * THIS FILE WAS COPIED DIRECTLY FROM IE-ANALYTICS-UTILITIES
 *  Until the models are refactored, this file will remain here
 * Created by 212582112 on 12/12/16.
 */
public class EventsByMinuteStaging implements Serializable {
    private String event_bucket;
    private String yyyymmdd;
    private String hhmm;
    private String resrc_type;
    private String resrc_uid;
    private int increment_mins;
    private UUID log_uuid;
    private Date event_ts;
    private String enterprise_uid;
    private String region_name;
    private String site_city;
    private String site_state;
    private String site_country;
    private String site_name;
    private List<String> zones;
    private List<String> segments;
    private List<String> labels;
    private Map<String, Double> measures_aggr;
    private Map<String, Double> measures_cnt;
    private Map<String, Double> measures_min;
    private Map<String, Double> measures_max;
    private Map<String, Double> measures_avg;
    private Map<String, String> tags;
    private Map<String, String> ext_props;

    private String solr_query;

    public EventsByMinuteStaging() {
    }

    public EventsByMinuteStaging(String event_bucket, String yyyymmdd, String hhmm, String resrc_type, String resrc_uid, int increment_mins, UUID log_uuid, Date event_ts, String enterprise_uid, String region_name, String site_city, String site_state, String site_country, String site_name, List<String> zones, List<String> segments, List<String> labels, Map<String, Double> measures_aggr, Map<String, Double> measures_cnt, Map<String, Double> measures_min, Map<String, Double> measures_max, Map<String, Double> measures_avg, Map<String, String> tags, Map<String, String> ext_props) {
        this.event_bucket = event_bucket;
        this.yyyymmdd = yyyymmdd;
        this.hhmm = hhmm;
        this.resrc_type = resrc_type;
        this.resrc_uid = resrc_uid;
        this.increment_mins = increment_mins;
        this.log_uuid = log_uuid;
        this.event_ts = event_ts;
        this.enterprise_uid = enterprise_uid;
        this.region_name = region_name;
        this.site_city = site_city;
        this.site_state = site_state;
        this.site_country = site_country;
        this.site_name = site_name;
        this.zones = zones;
        this.segments = segments;
        this.labels = labels;
        this.measures_aggr = measures_aggr;
        this.measures_cnt = measures_cnt;
        this.measures_min = measures_min;
        this.measures_max = measures_max;
        this.measures_avg = measures_avg;
        this.tags = tags;
        this.ext_props = ext_props;
    }

    public String getEvent_bucket() { return event_bucket; }

    public void setEvent_bucket(String event_bucket) { this.event_bucket = event_bucket; }

    public String getYyyymmdd() {
        return yyyymmdd;
    }

    public void setYyyymmdd(String yyyymmdd) {
        this.yyyymmdd = yyyymmdd;
    }

    public String getHhmm() {
        return hhmm;
    }

    public void setHhmm(String hhmm) {
        this.hhmm = hhmm;
    }

    public String getResrc_type() {
        return resrc_type;
    }

    public void setResrc_type(String resrc_type) {
        this.resrc_type = resrc_type;
    }

    public String getResrc_uid() {
        return resrc_uid;
    }

    public void setResrc_uid(String resrc_uid) {
        this.resrc_uid = resrc_uid;
    }

    public int getIncrement_mins() {
        return increment_mins;
    }

    public void setIncrement_mins(int increment_mins) {
        this.increment_mins = increment_mins;
    }

    public UUID getLog_uuid() {
        return log_uuid;
    }

    public void setLog_uuid(UUID log_uuid) {
        this.log_uuid = log_uuid;
    }

    public Date getEvent_ts() {
		return event_ts;
	}

	public void setEvent_ts(Date event_ts) {
		this.event_ts = event_ts;
	}

	public String getEnterprise_uid() {
        return enterprise_uid;
    }

    public void setEnterprise_uid(String enterprise_uid) {
        this.enterprise_uid = enterprise_uid;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public String getSite_city() {
        return site_city;
    }

    public void setSite_city(String site_city) {
        this.site_city = site_city;
    }

    public String getSite_state() {
        return site_state;
    }

    public void setSite_state(String site_state) {
        this.site_state = site_state;
    }

    public String getSite_country() {
        return site_country;
    }

    public void setSite_country(String site_country) {
        this.site_country = site_country;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public List<String> getZones() {
        return zones;
    }

    public void setZones(List<String> zones) {
        this.zones = zones;
    }

    public List<String> getSegments() {
        return segments;
    }

    public void setSegments(List<String> segments) {
        this.segments = segments;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Map<String, Double> getMeasures_aggr() {
        return measures_aggr;
    }

    public void setMeasures_aggr(Map<String, Double> measures_aggr) {
        this.measures_aggr = measures_aggr;
    }

    public Map<String, Double> getMeasures_cnt() {
        return measures_cnt;
    }

    public void setMeasures_cnt(Map<String, Double> measures_cnt) {
        this.measures_cnt = measures_cnt;
    }

    public Map<String, Double> getMeasures_min() {
        return measures_min;
    }

    public void setMeasures_min(Map<String, Double> measures_min) {
        this.measures_min = measures_min;
    }

    public Map<String, Double> getMeasures_max() {
        return measures_max;
    }

    public void setMeasures_max(Map<String, Double> measures_max) {
        this.measures_max = measures_max;
    }

    public Map<String, Double> getMeasures_avg() {
        return measures_avg;
    }

    public void setMeasures_avg(Map<String, Double> measures_avg) {
        this.measures_avg = measures_avg;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public Map<String, String> getExt_props() {
        return ext_props;
    }

    public void setExt_props(Map<String, String> ext_props) {
        this.ext_props = ext_props;
    }

    public String getSolr_query() { return solr_query; }

    public void setSolr_query(String solr_query) { this.solr_query = solr_query; }

    @Override
    public String toString() {
        return "EventsByMinuteStaging{" +
                "yyyymmdd='" + yyyymmdd + '\'' +
                ", hhmm='" + hhmm + '\'' +
                ", resrc_type='" + resrc_type + '\'' +
                ", resrc_uid='" + resrc_uid + '\'' +
                ", increment_mins=" + increment_mins +
                ", log_uuid=" + log_uuid +
                ", event_ts=" + event_ts +
                ", enterprise_uid='" + enterprise_uid + '\'' +
                ", region_name='" + region_name + '\'' +
                ", site_city='" + site_city + '\'' +
                ", site_state='" + site_state + '\'' +
                ", site_country='" + site_country + '\'' +
                ", site_name='" + site_name + '\'' +
                ", zones=" + zones +
                ", segments=" + segments +
                ", labels=" + labels +
                ", measures_aggr=" + measures_aggr +
                ", measures_cnt=" + measures_cnt +
                ", measures_min=" + measures_min +
                ", measures_max=" + measures_max +
                ", measures_avg=" + measures_avg +
                ", tags=" + tags +
                ", ext_props=" + ext_props +
                '}';
    }
}
