package com.ge.current.em.provider;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import static com.ge.current.em.provider.APMDataProvider.getEnterprises;
import static com.ge.current.em.provider.APMDataProvider.getSegmentsUI;
import static com.ge.current.em.provider.APMDataProvider.getSites;

/**
 * Created by 502645575 on 11/12/16.
 */
public class UIWidgetProvider {


    //Combine Enterprise and Site details for the API's common for both

    @DataProvider(name = "UIWidget-provider")
    public static Object[][] getEnterpriseAndSite(ITestContext context) {

        //String uri=getApmUrl(context)+"enterprises";
        return combine(getEnterprises(context),  getSites(context));



    }

    //Combine Enterprise,Site,Segment details for the API's common for both


    @DataProvider(name = "TotalUIWidget-provider")
    public static Object[][] getEnterpriseSiteSegment(ITestContext context)
    {
        return combine(getEnterpriseAndSite(context),  getSegmentsUI(context));
    }


    public static Object[][] combine(Object[][] a1, Object[][] a2){
        Object[][] data =new Object[a1.length+a2.length][3];
        for(int i=0;i<a1.length;i++)
        {
            data[i][0]=a1[i][0];
            data[i][1]=a1[i][1];
            data[i][2]=a1[i][2];
        }
        for(int i=0;i<a2.length;i++)
        {
            data[a1.length+i][0]=a2[i][0];
            data[a1.length+i][1]=a2[i][1];
            data[a1.length+i][2]=a2[i][2];
        }
        return data;
    }
}
