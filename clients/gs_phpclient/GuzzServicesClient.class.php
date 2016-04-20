<?php

require_once("GuzzCommandChannel.class.php") ;

class GuzzServicesClient{
	var $host ;
	var $port ;
	var $encoding ;
	var $uri ;
	var $debug = false ;
	var $authKey ;
	
  function setEncoding($encoding){
  	$this->encoding = $encoding ;
  }
  
  function setAuthKey($key){
  	$this->authKey = $key ;
  }

	function __construct($host, $port=80, $guzz_local_encoding = "GBK", $uri="/services/command/http.jsp"){
  	$this->host = $host ;
  	$this->port = $port ;
  	$this->encoding = $guzz_local_encoding ;
  	$this->uri = $uri ;
  }

	function _execute_command($command, $param){
		$client = new GuzzCommandChannel($this->host, $this->port, $this->uri) ;
		$client->setDebug($this->debug) ;
		$client->setAuthKey($this->authKey) ;
		
		return ($client->executeStringCommand($command, $param)) ;	
	}
	
	/**
	* IP反查服务。
	* http://www.guzzservices.com/2010/man_ip_service/
	*/
	function locateIP($IP){		
		$result = $this->_execute_command('gs.ip.q.cn', $IP) ;
		
		if(is_null($result)) return NULL ;
		
		$obj = json_decode($result) ;
		
		if($this->encoding != "UTF-8"){
			$obj->cityName = iconv("UTF-8", $this->encoding, $obj->cityName) ;
			$obj->detailLocation = iconv("UTF-8", $this->encoding, $obj->detailLocation) ;
			$obj->cityMarker = iconv("UTF-8", $this->encoding, $obj->cityMarker) ;
		}
	
		return $obj ;
	}
	
	/**
	 * 过滤一段文字，根据参数决定是否标红。如果不含有任何过滤词，返回null。
	 * http://www.guzzservices.com/2010/man-content-filter/
	 *
	 * @param content 检测内容
	 * @param groupIds 配置的过滤词组编号
	 * @param markContent 是否同时标红过滤的内容。
	 * @return MatchResult
	 */
	function filterText($content, $groupIds, $markContent) {
		return $this->_do_word_filter("gs.fw.f.text", $content, $groupIds, $markContent) ;		
	}
 
	/**
	 * 过滤一段html代码段，根据参数决定是否标红。如果不含有任何过滤词，返回null。
	 * http://www.guzzservices.com/2010/man-content-filter/
	 *
	 * @param content 检测内容
	 * @param groupIds 配置的过滤词组编号
	 * @param markContent 是否同时标红过滤的内容。
	 * @return MatchResult
	 */
	function filterHtml($content, $groupIds, $markContent) {
		return $this->_do_word_filter("gs.fw.f.html", $content, $groupIds, $markContent) ;
	}
	
	function _do_word_filter($command, $content, $groupIds, $markContent){
		if(!$content) return NULL ;
		if(!$groupIds) return NULL ;	
		
		if($this->encoding != "UTF-8"){
			$content = iconv($this->encoding, "UTF-8", $content) ;
		}
		
		$json = json_encode(array("groupNames" => $groupIds, "markContent" => $markContent, "content" => $content));
		
		$result = $this->_execute_command($command, $json) ;		
				
		if(is_null($result)) return NULL ;
		$obj = json_decode($result) ;
		
		if($this->encoding != "UTF-8"){
			$obj->markedContent = iconv("UTF-8", $this->encoding, $obj->markedContent) ;
			//$obj->matchedFilterWords = iconv("UTF-8", $this->encoding, $obj->matchedFilterWords) ;
			$obj->hittedContentList = iconv("UTF-8", $this->encoding, $obj->hittedContentList) ;
		}
	
		return $obj ;	
	}
	
	
	/**
	 * 抽取普通文本，含图片信息。图片信息会另存，不会混合到返回的普通文本中。
	 * http://www.guzzservices.com/2010/man_html2text/
	 * 
	 * @param htmlText 传入的html
	 * @param resultLengthLimit 抽取的普通文本最长只需要截取到的长度。0表示抽取所有。
	 * @param imageCountLimit 最多只需要提取的图片数。-1表示抽取所有；0表示不抽取；其他表示抽取的图片张数。
	 */
	function extractTextFromHtml($htmlText, $resultLengthLimit = 0, $imageCountLimit = -1){
		
		if($this->encoding != "UTF-8"){
			$htmlText = iconv($this->encoding, "UTF-8", $htmlText) ;
		}
		
		$json = json_encode(array("htmlText" => $htmlText, "resultLengthLimit" => $resultLengthLimit, "imageCountLimit" => $imageCountLimit));
		
		$result = $this->_execute_command("gs.cnt.cvt.h2plain", $json) ;		
		if(is_null($result)) return NULL ;
		$obj = json_decode($result) ;
		
		if($this->encoding != "UTF-8"){
			$obj->plainText = iconv("UTF-8", $this->encoding, $obj->plainText) ;
		}
	
		return $obj ;	
		
	}
	
}

?>