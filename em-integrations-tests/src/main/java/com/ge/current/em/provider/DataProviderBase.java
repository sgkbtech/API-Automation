/*
 * Copyright (c) 2016 GE. All Rights Reserved.
 * GE Confidential: Restricted Internal Distribution
 */
package com.ge.current.em.provider;

import org.testng.ITestContext;

import com.ge.current.em.utils.Constants;

public abstract class DataProviderBase {
    protected static String getSiteSourceKey(ITestContext context) {
        return (String) context.getAttribute(Constants.SITE_SOURCE_KEY);
    }
    protected static String getEnterpriseSourceKey(ITestContext context) {
        return (String) context.getAttribute(Constants.ENTERPRISE_SOURCE_KEY);
    }
    protected static String getAssetSourceKey(ITestContext context) {
        return (String) context.getAttribute(Constants.ASSETID);
    }
    protected static String getSegmentSourceKey(ITestContext context) {
        return (String) context.getAttribute(Constants.SEGMENT_SOURCE_KEY);
    }

    protected static String getApmUrl(ITestContext context) {
        return (String) context.getAttribute(Constants.APM_URL);
    }
}
