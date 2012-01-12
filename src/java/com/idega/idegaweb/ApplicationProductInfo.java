/*
 * $Id: ApplicationProductInfo.java,v 1.14 2007/04/18 08:09:22 civilis Exp $
 * Created on 4.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.idega.util.IOUtil;
import com.idega.util.IWTimestamp;


/**
 *  This class holds information about the application product installed.<br>
 *
 *  Last modified: $Date: 2007/04/18 08:09:22 $ by $Author: civilis $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.14 $
 */
public class ApplicationProductInfo {

	//This will be swapped out by reading from /WEB-INF/idegaweb/properties/product.properties
	private String inceptionYear="2000";
	private String version="4.0-SNAPSHOT";
	private String platformVersion="4.0-SNAPSHOT";
	private String buildId="20060701.000000";
	private String vendor="idega software";
	private String vendorUrl="http://www.idega.com/";
	private String vendorLogoUrl="http://developer.idega.com/idega_onwhite_120.gif";
	private String name="ePlatform";
	private String family="";

	public ApplicationProductInfo(IWMainApplication iwma){
		String filePath = iwma.getApplicationRealPath() + "META-INF"+File.separator+"MANIFEST.MF";
		InputStream fileStream = iwma.getResourceAsStream(filePath);
		if (fileStream == null) {
			filePath = iwma.getPropertiesRealPath()+File.separator+"product.properties";
			fileStream = iwma.getResourceAsStream(filePath);
		}
		if (fileStream == null) {
			filePath = iwma.getApplicationRealPath()+"WEB-INF"+File.separator+"classes"+File.separator+"WEB-INF"+File.separator+"idegaweb"+
				File.separator+"properties"+File.separator+"product.properties";
			try {
				File props = new File(filePath);
				if (props.exists())
					fileStream = new FileInputStream(props);
				else
					Logger.getLogger(getClass().getName()).warning("Properties file does not exsits: " + filePath);
			} catch (Exception e) {}
		}
		loadFromManifest(fileStream);
	}

	public void loadFromManifest(InputStream fileStream){
		if (fileStream == null)
			return;

		Map<String, String> properties = new HashMap<String, String>();
		try {
			loadManifestIntoMap(fileStream,properties);

			String inceptionYear = properties.get("Implementation-InceptionYear");
			if(inceptionYear!=null){
				setInceptionYear(inceptionYear);
			}
			String productVersion = properties.get("Implementation-Version");
			if(productVersion!=null){
				setVersion(productVersion);
			}
			String buildId = properties.get("Implementation-Build");
			if(buildId!=null){
				setBuildId(buildId);
			}
			String vendorName = properties.get("Implementation-Vendor");
			if(vendorName!=null){
				setVendor(vendorName);
			}
			String productName = properties.get("Implementation-Title");
			if(productName!=null){
				setName(productName);
			}
			String productFamily = properties.get("Implementation-Family");
			if(productFamily!=null){
				setFamily(productFamily);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtil.close(fileStream);
		}
	}

	private void loadManifestIntoMap(InputStream fileStream, Map<String, String> properties) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
		String line = br.readLine();

		while (line != null){
			String lineStr = line.toString();
			int index = lineStr.indexOf(": ");
			if(index!=-1){
				String key = lineStr.substring(0, index);
				String value = lineStr.substring(index+2,lineStr.length());
				//System.out.println("DEBUG: " +key+". line = " + value);
				properties.put(key, value);
			}
			line = br.readLine();
		}


	}

	public void loadFromProductPropertiesFile(File file){
		if(file.exists()){
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(file));
				//iwma.sendStartupMessage("Loading product.properties from file: "+file.getPath());

				String inceptionYear = (String) properties.get("application.product.inceptionyear");
				if(inceptionYear!=null){
					setInceptionYear(inceptionYear);
				}
				String productVersion = (String) properties.get("application.product.version");
				if(productVersion!=null){
					setVersion(productVersion);
				}
				String buildId = (String) properties.get("application.product.build.id");
				if(buildId!=null){
					setBuildId(buildId);
				}
				String vendorName = (String) properties.get("application.product.vendor");
				if(vendorName!=null){
					setVendor(vendorName);
				}
				String productName = (String) properties.get("application.product.name");
				if(productName!=null){
					setName(productName);
				}
				String productFamily = (String) properties.get("application.product.family");
				if(productFamily!=null){
					setFamily(productFamily);
				}

			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		}

	}


	/**
	 * @return Returns the buildId.
	 */
	public String getBuildId() {
		return this.buildId;
	}
	/**
	 * @param buildId The buildId to set.
	 */
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}
	/**
	 * @return Returns the family.
	 */
	public String getFamily() {
		return this.family;
	}
	/**
	 * @param family The family to set.
	 */
	public void setFamily(String family) {
		this.family = family;
	}
	/**
	 * @return Returns the inceptionYear.
	 */
	public String getInceptionYear() {
		return this.inceptionYear;
	}
	/**
	 * @param inceptionYear The inceptionYear to set.
	 */
	public void setInceptionYear(String inceptionYear) {
		this.inceptionYear = inceptionYear;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the productName with the productFamily
	 * @return
	 */
	public String getFullProductName(){
		String family=getFamily();
		if(family==null||family.equals("")){
			return getName();
		}
		else{
			return family+" "+getName();
		}
	}
	/**
	 * @return Returns the vendor.
	 */
	public String getVendor() {
		return this.vendor;
	}
	/**
	 * @param vendor The vendor to set.
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	/**
	 * @return Returns the version.
	 */
	public String getVersion() {
		return this.version;
	}
	/**
	 * @param version The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	public String getCopyrightText(){
		return "Copyright (c) "+getInceptionYear()+"-"+IWTimestamp.RightNow().getYear()+" "+getVendor()+" All rights reserved";
	}

	/**
	 * Gets the major version (the first integer in the version number)
	 * @return
	 */
	public int getMajorVersion(){
		String version = getVersion();
		int dotIndex = version.indexOf(".");
		String sMVersion = version.substring(0,dotIndex);
		return Integer.parseInt(sMVersion);
	}

	public boolean isMajorVersionEqualOrHigherThan(int version){
		int majorVersion = getMajorVersion();
		return (version<=majorVersion);
	}


	/**
	 * @return Returns the platformVersion.
	 */
	public String getPlatformVersion() {
		return this.platformVersion;
	}


	/**
	 * @param platformVersion The platformVersion to set.
	 */
	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}

	/**
	 * Gets the major version (the first integer in the version number)
	 * @return
	 */
	public int getMajorPlatformVersion(){
		String version = getPlatformVersion();
		int dotIndex = version.indexOf(".");
		String sMVersion = version.substring(0,dotIndex);
		return Integer.parseInt(sMVersion);
	}

	public boolean isMajorPlatformVersionEqualOrHigherThan(int version){
		int majorVersion = getMajorPlatformVersion();
		return (version<=majorVersion);
	}


	public String getVendorUrl() {
		return vendorUrl;
	}


	public void setVendorUrl(String vendorUrl) {
		this.vendorUrl = vendorUrl;
	}



	public String getVendorLogoUrl() {
		return vendorLogoUrl;
	}


	public void setVendorLogoUrl(String vendorLogoUrl) {
		this.vendorLogoUrl = vendorLogoUrl;
	}

}
