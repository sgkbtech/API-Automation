package com.ge.current.em.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class APMParentType {
	
		// parent type of ENTERPRISE
		private static final String[] enterpriseParentType = {"ENTERPRISE" };
		public static final  Set<String> ENTERPRISE = new HashSet<String>(Arrays.asList(enterpriseParentType)) ;
		
		// parent type of REGION
		private static final String[] regionParentType = { "ENTERPRISE" , "REGION"};
		public static final  Set<String> REGION = new HashSet<String>(Arrays.asList(regionParentType)) ;
		
		// parent type of SITE
		private static final String[] siteParentType = { "ENTERPRISE" , "SITE" , "REGION" };
		public static final  Set<String> SITE = new HashSet<String>(Arrays.asList(siteParentType)) ;
		
		// parent type of SEGMENT
		private static final String[] segmentParentType = { "SITE", "ASSET","SEGMENT" };
		public static final  Set<String> SEGMENT = new HashSet<String>(Arrays.asList(segmentParentType)) ;
		
		// parent type of ASSET
		private static final String[] assetParentType = {"SITE", "ASSET","SEGMENT" };
		public static final  Set<String> ASSET = new HashSet<String>(Arrays.asList(assetParentType)) ;
		
		// parent type of TAG
		private static final String[] tagParentType = {"ENTERPRISE" , "SITE", "ASSET","SEGMENT" };
		public static final  Set<String> TAG = new HashSet<String>(Arrays.asList(tagParentType)) ;

}
