<?php
require_once("GuzzServicesClient.class.php") ;

//����������IP���˿ڣ�����php����ı��룻���������UTF8�����봫��"UTF-8"
$gsc = new GuzzServicesClient("cloud.guzzservices.com", 80, "GBK") ;
$gsc->setAuthKey("your-key-for-auth") ;

//IP�������
//http://www.guzzservices.com/2010/man_ip_service/
$geo = $gsc->locateIP("58.23.67.2") ;

if(is_null($geo)){
	echo "��������" ;	
}else{	
	echo $geo->cityName."����" ;
}
echo "<p/>" ;


//�ı���ȡ
//http://www.guzzservices.com/2010/man_html2text/
echo $gsc->extractTextFromHtml("677falf����jl<br>safj")->plainText ;
echo "<p/>" ;

//���˴ʷ���
//http://www.guzzservices.com/2010/man_html2text/
$matchResult = $gsc->filterHtml("SB77falfjlsafj����", array("17h37jnkyn1fkdvz3qhncbp3wyoo0x"), true) ;
if(is_null($matchResult)){
	echo "filter passed." ;
}else{
	echo $matchResult->hittedContentList ;
	echo "<br>".$matchResult->markedContent ;
}
echo "<p/>" ;

?>