/**
 * ChinaMainlandRuleMatcher.java created by liu kaixuan(liukaixuan@gmail.com) at 8:41:23 AM on Jul 9, 2008 
 */
package com.guzzservices.dir.ip.china;

import com.guzzservices.dir.ip.CityRuleMatcher;
import com.guzzservices.dir.ip.LocateResult;


/**
 * 
 * 中国大陆地理位置判断。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 * @date Jul 9, 2008 8:41:23 AM
 */
public class ChinaMainlandRuleMatcher implements CityRuleMatcher {

	private static String[] mainlandMarkers = {
		"北京","上海", "天津", "重庆", "四川", "贵州", "广东", "浙江", 
		
		"福建", "湖南", "湖北", "山东", "山西", "河南", 

		"河北", "吉林", "辽宁", "黑龙江", "安徽", "江苏", "江西", "海南", 

		"陕西", "云南", "青海", "宁夏", "甘肃", "新疆", "西藏", "广西",  "内蒙古", "深圳", 
		
		"哈尔滨", "长春", "沈阳", "呼和浩特", "石家庄", "乌鲁木齐", "兰州", "西宁", "西安", 
		
		"银川", "郑州", "济南", "太原", "合肥", "武汉", "南京", "成都", "贵阳", 
		
		"昆明", "南宁", "拉萨", "杭州", "南昌", "广州", "福州", "海口", "泉州", 
		
		"中经网", "全国", "华东", "华北", "长城",
		
		"中国", "大学", "学院", "网吧", "新华", "日报", "教育", "学校", "网通", "联通", "铁通"
		
		, "本机", "局域网"
		
		} ;  
	
	
/*	
	安徽：、芜湖
	北京
	重庆
	福建：、
	甘肃：、天水
	广东：、深圳
	广西：南宁、柳州
	贵州：贵阳、遵义
	海南：海口、三亚
	河北：石家庄、
	河南：、洛阳
	黑龙江：哈尔滨、齐齐哈尔
	湖北：、襄樊
	湖南：、株州
	吉林：、吉林
	江苏：、
	江西：南昌、九江
	辽宁：、大连
	内蒙古：呼和浩特、包头
	宁夏：银川、石嘴山
	青海：、格尔木
	山东：、青岛
	山西：太原、大同
	陕西：、宝鸡
	上海
	四川：、绵阳
	天津
	西藏：拉萨、日喀则
	新疆：、库尔勒
	云南：昆明、曲靖
	浙江：、宁波
	台湾：台北、高雄

*/
	
	public boolean matchRule(LocateResult location) {
		if(location == null) return true ;
		String locationName = location.fullLocation() ;
		
		for(int i = 0 ; i < mainlandMarkers.length ; i++){
			if(locationName.contains(mainlandMarkers[i])){
				return true ;
			}
		}
		
		return false ;
	}

}
