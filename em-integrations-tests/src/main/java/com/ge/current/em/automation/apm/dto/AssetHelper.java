package com.ge.current.em.automation.apm.dto;

import java.util.ArrayList;
import java.util.List;

import com.ge.current.em.automation.apm.APMConstants;
import com.ge.current.em.automation.apm.ValidatableBeanList;
import com.ge.current.em.persistenceapi.dto.AssetCatalogDTO;
import com.ge.current.em.persistenceapi.dto.AssetDTO;
import com.ge.current.em.persistenceapi.dto.AssetIdentifierDTO;
import com.ge.current.em.persistenceapi.dto.PropertiesDTO;
import com.ge.current.em.persistenceapi.dto.MetaEntityDTO;;

public class AssetHelper {
	private long unixTime = System.currentTimeMillis() % 1000000000L;

	public ValidatableBeanList<AssetDTO> getAssetUnderSite(String site_id,String gatewaysourcekey) {
		ValidatableBeanList<AssetDTO> vbl = new ValidatableBeanList<AssetDTO>();
		AssetDTO dto = new AssetDTO();
		dto.setParentSourceKey(site_id);
		dto.setParentClassificationCode(APMConstants.SITE);
		dto.setName("Test_AssetUnderSite_" + unixTime);
		dto.setGatewaySourceKey(gatewaysourcekey);
		AssetIdentifierDTO identifier = new AssetIdentifierDTO();
		long serialNumber = System.currentTimeMillis() % 1000000000L;
		identifier.setSerialNumber(""+serialNumber);
		identifier.setVirtualSerialNumber(""+serialNumber);
		identifier.setMacAddress("si20:6t04:dr34");
		identifier.setExternalRefId("EXT REF ID");
		dto.setIdentifier(identifier);
		AssetCatalogDTO catalog = new AssetCatalogDTO();
		catalog.setType("AHU");
		catalog.setModel("AHU_MODEL");
		dto.setCatalog(catalog);
		PropertiesDTO properties = new PropertiesDTO();
		Object[] value= {"[{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/dischargeAirTempSensor\",\"pointName\":\"dischargeAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/${csv.siteName}_AHU/points/dischargeAirFanCmd\",\"pointName\":\"dischargeAirFanCmd\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/returnAirTempSensor\",\"pointName\":\"returnAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/outsideAirTempSensor\",\"pointName\":\"outsideAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/zoneAirTempSensor\",\"pointName\":\"zoneAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/zoneAirHumiditySensor\",\"pointName\":\"zoneAirHumiditySensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/zoneAirCo2Sensor\",\"pointName\":\"zoneAirCo2Sensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/ahuModeSp\",\"pointName\":\"ahuModeSp\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/alarmPoint\",\"pointName\":\"alarmPoint\"}]"};
		properties.setId("haystackMapping");
		properties.setType("STRING");
		properties.setValue(value);
		List<PropertiesDTO> propList =  new ArrayList<PropertiesDTO>();
		propList.add(properties);
		dto.setProperties(propList);
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<AssetDTO> getAssetUnderSegment(String segment_id,String gatewaysourcekey) {
		ValidatableBeanList<AssetDTO> vbl = new ValidatableBeanList<AssetDTO>();
		AssetDTO dto = new AssetDTO();
		dto.setParentSourceKey(segment_id);
		dto.setParentClassificationCode(APMConstants.SEGMENT);
		dto.setName("Test_AssetUnderSegment_" + unixTime);
		dto.setGatewaySourceKey(gatewaysourcekey);
		AssetIdentifierDTO identifier = new AssetIdentifierDTO();
		long serialNumber = System.currentTimeMillis() % 1000000000L;
		identifier.setSerialNumber(""+serialNumber);
		identifier.setVirtualSerialNumber(""+serialNumber);
		identifier.setMacAddress("si20:6t04:dr34");
		identifier.setExternalRefId("EXT REF ID");
		dto.setIdentifier(identifier);
	    AssetCatalogDTO catalog = new AssetCatalogDTO();
		catalog.setType("AHU");
		catalog.setModel("AHU_MODEL");
		dto.setCatalog(catalog);
		PropertiesDTO properties = new PropertiesDTO();
		Object[] value= {"[{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/dischargeAirTempSensor\",\"pointName\":\"dischargeAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/dischargeAirFanCmd\",\"pointName\":\"dischargeAirFanCmd\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/returnAirTempSensor\",\"pointName\":\"returnAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/AHU/points/outsideAirTempSensor\",\"pointName\":\"outsideAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/zoneAirTempSensor\",\"pointName\":\"zoneAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/zoneAirHumiditySensor\",\"pointName\":\"zoneAirHumiditySensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/zoneAirCo2Sensor\",\"pointName\":\"zoneAirCo2Sensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/ahuModeSp\",\"pointName\":\"ahuModeSp\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/AHU/points/alarmPoint\",\"pointName\":\"alarmPoint\"}]"};
		properties.setId("haystackMapping");
		properties.setType("STRING");
		properties.setValue(value);
		List<PropertiesDTO> propList =  new ArrayList<PropertiesDTO>();
		propList.add(properties);
		dto.setProperties(propList);
		vbl.getList().add(dto);
		return vbl;
	}
	
	public ValidatableBeanList<MetaEntityDTO> getAssetForPatch(String asset_id) {
		ValidatableBeanList<MetaEntityDTO> vbl = new ValidatableBeanList<MetaEntityDTO>();
		MetaEntityDTO dto = new MetaEntityDTO();
		dto.setSourceKey(asset_id);
		dto.setClassificationCode("ASSET");
		vbl.getList().add(dto);
		return vbl;
		}
	
	public ValidatableBeanList<AssetDTO> getAssetWithoutGatewayInfo(String site_id) {
		ValidatableBeanList<AssetDTO> vbl = new ValidatableBeanList<AssetDTO>();
		AssetDTO dto = new AssetDTO();
		dto.setParentSourceKey(site_id);
		dto.setParentClassificationCode(APMConstants.SITE);
		dto.setName("Test_AssetUnderSite_" + unixTime);
		dto.setGatewaySourceKey("");
		AssetIdentifierDTO identifier = new AssetIdentifierDTO();
		long serialNumber = System.currentTimeMillis() % 1000000000L;
		identifier.setSerialNumber(""+serialNumber);
		identifier.setVirtualSerialNumber(""+serialNumber);
		identifier.setMacAddress("si20:6t04:dr34");
		identifier.setExternalRefId("EXT REF ID");
		dto.setIdentifier(identifier);
		AssetCatalogDTO catalog = new AssetCatalogDTO();
		catalog.setType("AHU");
		catalog.setModel("AHU_MODEL");
		dto.setCatalog(catalog);
		PropertiesDTO properties = new PropertiesDTO();
		Object[] value= {"[{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/dischargeAirTempSensor\",\"pointName\":\"dischargeAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/${csv.siteName}_AHU/points/dischargeAirFanCmd\",\"pointName\":\"dischargeAirFanCmd\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/returnAirTempSensor\",\"pointName\":\"returnAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/outsideAirTempSensor\",\"pointName\":\"outsideAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/zoneAirTempSensor\",\"pointName\":\"zoneAirTempSensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/zoneAirHumiditySensor\",\"pointName\":\"zoneAirHumiditySensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/zoneAirCo2Sensor\",\"pointName\":\"zoneAirCo2Sensor\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/ahuModeSp\",\"pointName\":\"ahuModeSp\"},{\"pointId\":\"/Drivers/ModbusAsyncNetwork/_AHU/points/alarmPoint\",\"pointName\":\"alarmPoint\"}]"};
		properties.setId("haystackMapping");
		properties.setType("STRING");
		properties.setValue(value);
		List<PropertiesDTO> propList =  new ArrayList<PropertiesDTO>();
		propList.add(properties);
		dto.setProperties(propList);
		vbl.getList().add(dto);
		return vbl;
	}

	
}
