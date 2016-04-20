/**
 * LocationResult.java created at 2009-10-21 上午09:47:06 by liukaixuan@gmail.com
 */
package com.guzzservices.dir.ip;

import java.io.Serializable;

/**
 * 
 * IP反查结果
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class LocateResult implements Serializable {
	
	/**城市的附加标记。如“清华大学”，则为”北京“；如为“北京海淀”，则为null；对于外国，值为“海外”；  */
	private String cityMarker ;

	/**查询地市级名称，如：贵州省六盘水市*/
	private String cityName ;
	
	/**详细地址，如：六枝特区腾龙网吧*/
	private String detailLocation ;
	
	/**
	 * 完整地址
	 */
	public String fullLocation(){
		StringBuilder sb = new StringBuilder() ;
		
		if(this.cityMarker != null){
			sb.append(this.cityMarker) ;
		}
		
		if(this.cityName != null){
			sb.append(this.cityName) ;
		}
		
		if(this.detailLocation != null){
			sb.append(this.detailLocation) ;
		}
		
		return sb.toString()  ;
	}

	public String getCityName() {
		return cityName;
	}

	public String getDetailLocation() {
		return detailLocation;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder() ;
		sb.append("cityMarker:").append(cityMarker)
		  .append(",cityName:").append(cityName)
		  .append(",detailLocation:").append(detailLocation) ;
		
		return sb.toString() ;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public void setDetailLocation(String detailLocation) {
		this.detailLocation = detailLocation;
	}

	public String getCityMarker() {
		return cityMarker;
	}

	public void setCityMarker(String cityMarker) {
		this.cityMarker = cityMarker;
	}
	
}
