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
	* IP�������
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
	 * ����һ�����֣����ݲ��������Ƿ��졣����������κι��˴ʣ�����null��
	 * http://www.guzzservices.com/2010/man-content-filter/
	 *
	 * @param content �������
	 * @param groupIds ���õĹ��˴�����
	 * @param markContent �Ƿ�ͬʱ�����˵����ݡ�
	 * @return MatchResult
	 */
	function filterText($content, $groupIds, $markContent) {
		return $this->_do_word_filter("gs.fw.f.text", $content, $groupIds, $markContent) ;		
	}
 
	/**
	 * ����һ��html����Σ����ݲ��������Ƿ��졣����������κι��˴ʣ�����null��
	 * http://www.guzzservices.com/2010/man-content-filter/
	 *
	 * @param content �������
	 * @param groupIds ���õĹ��˴�����
	 * @param markContent �Ƿ�ͬʱ�����˵����ݡ�
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
	 * ��ȡ��ͨ�ı�����ͼƬ��Ϣ��ͼƬ��Ϣ����棬�����ϵ����ص���ͨ�ı��С�
	 * http://www.guzzservices.com/2010/man_html2text/
	 * 
	 * @param htmlText �����html
	 * @param resultLengthLimit ��ȡ����ͨ�ı��ֻ��Ҫ��ȡ���ĳ��ȡ�0��ʾ��ȡ���С�
	 * @param imageCountLimit ���ֻ��Ҫ��ȡ��ͼƬ����-1��ʾ��ȡ���У�0��ʾ����ȡ��������ʾ��ȡ��ͼƬ������
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