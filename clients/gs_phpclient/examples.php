<?php
require_once("GuzzServicesClient.class.php") ;

//参数：域名IP，端口，调用php程序的编码；如果编码是UTF8，必须传入"UTF-8"
$gsc = new GuzzServicesClient("cloud.guzzservices.com", 80, "GBK") ;
$gsc->setAuthKey("your-key-for-auth") ;

//IP反查服务
//http://www.guzzservices.com/2010/man_ip_service/
$geo = $gsc->locateIP("58.23.67.2") ;

if(is_null($geo)){
	echo "火星网友" ;	
}else{	
	echo $geo->cityName."网友" ;
}
echo "<p/>" ;


//文本抽取
//http://www.guzzservices.com/2010/man_html2text/
echo $gsc->extractTextFromHtml("677falf哈哈jl<br>safj")->plainText ;
echo "<p/>" ;

//过滤词服务
//http://www.guzzservices.com/2010/man_html2text/
$matchResult = $gsc->filterHtml("SB77falfjlsafj中文", array("17h37jnkyn1fkdvz3qhncbp3wyoo0x"), true) ;
if(is_null($matchResult)){
	echo "filter passed." ;
}else{
	echo $matchResult->hittedContentList ;
	echo "<br>".$matchResult->markedContent ;
}
echo "<p/>" ;

?>