/*
 * Copyright (c) 2016 GE. All Rights Reserved.
 * GE Confidential: Restricted Internal Distribution
 */
package com.ge.current.em.provider;

import java.util.ArrayList;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

public class AssetDataProvider extends DataProviderBase {
    @DataProvider(name = "ASSETProvider")
    public static Object[][] getAssets(ITestContext context) throws NullPointerException {

            Object[][] data= new Object[1][1] ;
            data[0][0] =(Object) getAssetSourceKey(context);
            return data;
        }
}
