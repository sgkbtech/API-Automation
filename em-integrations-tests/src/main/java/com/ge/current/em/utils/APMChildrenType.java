package com.ge.current.em.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class APMChildrenType {
	
	// children type of ENTERPRISE
	private static final String[] enterpriseChildrenType = {"ENTERPRISE" , "SITE" , "REGION"};
	public static final  Set<String> ENTERPRISE = new HashSet<String>(Arrays.asList(enterpriseChildrenType)) ;
	
	// children type of REGION
	private static final String[] regionChildrenType = { "SITE" , "REGION"};
	public static final  Set<String> REGION = new HashSet<String>(Arrays.asList(regionChildrenType)) ;
	
	// children type of SITE
	private static final String[] siteChildrenType = { "SITE","SEGMENT" };
	public static final  Set<String> SITE = new HashSet<String>(Arrays.asList(siteChildrenType)) ;
	
	// children type of SEGMENT
	private static final String[] segmentChildrenType = { "ASSET","SEGMENT" };
	public static final  Set<String> SEGMENT = new HashSet<String>(Arrays.asList(segmentChildrenType)) ;
		
	
}
