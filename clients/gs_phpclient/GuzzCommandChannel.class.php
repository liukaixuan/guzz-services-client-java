<?php

require_once("HttpClient.class.php") ;

class GuzzCommandChannel extends HttpClient{
	
	var $url ;
	var $authKey ;
  
  function __construct($host, $port=80, $url = "/services/command/http.jsp"){
  	parent::__construct($host, $port);
  	
  	$this->url = $url ;
  }
  
  function setAuthKey($key){
  	$this->authKey = $key ;
  }
  
  function executeCommand($command, $isStringparam, $param){
  		$sucess = $this->post($this->url, array(
		    'command' => $command,
		    'isStringParam' => $isStringparam ? '1' : '0',
		    'param' => $param,
		    'authKey' => $this->authKey
			));
			  		
			if(!$sucess) return ;			
  		
  		$isStringResult = intval($this->getHeader("guzzCommandServiceString")) === 1 ;
  		$length = intval($this->getHeader("guzzCommandserviceLength")) ;
  		$resultVal = NULL ;
  		
  		if($length == 0){
  			$resultVal = "" ;
  		}else if($length > 0){
  			$resultVal = $this->getContent() ;
  		}
  		
  		if(intval($this->getHeader("guzzCommandServiceException")) === 1){
  			$this->debug("error:".$resultVal) ;
  			
  			die("error:".$resultVal) ;	
  		}else{
  			return ($resultVal) ;
  		}
  }
  
  function executeStringCommand($command, $param){
  		return ($this->executeCommand($command, true, $param)) ;
  }
	
}

?>