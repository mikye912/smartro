<?
Class Common{
///////////////////////////////////////////////////////[에러메세지]
	Function ErrorMsg ($error_msg) { 
		echo "<script> alert(\" $error_msg \"); </script> "; 
		exit; 
	}

	Function ErrorMsgGourl ($error_msg, $url) { 
		echo "<script> alert(\" $error_msg \");</script> "; 
		$this->Refresh('0',$url);
	}


///////////////////////////////////////////////////////[페이지이동]
	Function Refresh($time, $url) { 
		echo "<html><head>
		<META HTTP-EQUIV=\"Refresh\" CONTENT=\"$time ;URL=$url\">
		</head><body></body></html>";
		exit; 
	}

///////////////////////////////////////////////////////[한글자르기]
	Function cutstr($String, $Num) { 
  		if(strlen($String) <= $Num) return $String;

		if(ord(substr($String, $Num-1, 1)) < 127) return substr($String, 0, $Num)."...";
		$check_i = 0;
		for($i=0;$i<$Num;$i++) { 

		if(ord(substr($String,$i,1))<127) $check_i++;
			else $i++;
		}

		if(($Num % 2 == 0) && ($check_i % 2 != 0) || ($Num % 2 != 0) && ($check_i % 2 == 0)) 
		return $String = substr($String,0, $Num-1)." ...";
		else return $String = substr($String,0, $Num)."...";
	}

	Function cutstr2($String, $Num) { 
  		if(strlen($String) <= $Num) return $String;

		if(ord(substr($String, $Num-1, 1)) < 127) return substr($String, 0, $Num);
		$check_i = 0;
		for($i=0;$i<$Num;$i++) { 

		if(ord(substr($String,$i,1))<127) $check_i++;
			else $i++;
		}

		if(($Num % 2 == 0) && ($check_i % 2 != 0) || ($Num % 2 != 0) && ($check_i % 2 == 0)) 
		return $String = substr($String,0, $Num-1) ;
		else return $String = substr($String,0, $Num) ;
	}

/////////////////////////////////////////////// 첫문자 이후 ***표시
	Function cutstr3($String, $Num) { 
		$Slen		=	strlen($String);
		$Slen1	=	number_format(($Slen-2)/2);
  		if($Slen <= $Num) return $String;

		for($i=0;$i<=($Slen1-1);$i++){
			$TmpName	.="*";
			$Temp2SetName	=	substr($String,0,$Num).$TmpName;
		}

		if($Slen < 127)
			return $Temp2SetName;
	}

	Function subdate($obj,$txt){
		if($obj!="" && $obj!="00000000"){
			$rtn	= substr($obj,0,4).$txt.substr($obj,4,2).$txt.substr($obj,6,2);
		}

		return $rtn;
	}
		
	Function subtime($obj,$txt){
		if($obj!=""){
			$rtn	= substr($obj,0,2).$txt.substr($obj,2,2).$txt.substr($obj,4,2);
		}

		return $rtn;
	}
///////////////////////////////////////////////////////[주민번호검사]
	Function CheckJumin($reginum) { 
		$weight = '234567892345'; // 자리수 weight 지정 
		$len = strlen($reginum); 
		$sum = 0; 

		if ($len <> 13) { return false; } 

		for ($i = 0; $i < 12; $i++) { 
			$sum = $sum + (substr($reginum,$i,1) * substr($weight,$i,1)); 
		} 

		$rst = $sum%11; 
		$result = 11 - $rst; 

		if ($result == 10) {$result = 0;} 
		else if ($result == 11) {$result = 1;} 

		$jumin = substr($reginum,12,1); 

		if ($result <> $jumin) {return false;} 
		return true; 
	}

///////////////////////////////////////////////////////[아이디검사]
	Function CheckIDPW($userid){
		if(!ereg("[[:alnum:]+]{4,12}",$userid)){
			return false;
		}else{
			return true;
		}
	}

///////////////////////////////////////////////////////[이메일검사]
	Function CheckEmail($usermail){
		if(!ereg("(^[_0-9a-zA-Z]+(\.[0-9a-zA-Z]+)*@[0-9a-zA-Z]+(\.[0-9a-zA-Z]+)*$)",$usermail)){
			return false;
		}else{
			return true;
		}
	}

///////////////////////////////////////////////////////[HTML 허용여부결정]	
	Function Remove_Tag($str) {
		$allowedTags = '<h1><b><i><a><ul><ol><li><hr><img><font><span><p>'; // 허용할 테그
		$stripAttrib = 'javascript:|onclick|ondblclick|onmousedown|onmouseup|onmouseover|'.
		'onmousemove|onmouseout|onkeypress|onkeydown|onkeyup|onchange|onblur|onfocus'; // 제거할 속성

		$str = preg_replace("/<(\/?)(?![\/a-z])([^>]*)>/i", "&lt;\\1\\2\\3&gt;", $str);
		$str = strip_tags($str,$allowedTags);

		return preg_replace("/<(.*)($stripAttrib)+([^>]*)>/i", "<\\1xx\\2xx\\3>", $str);
	}


///////////////////////////////////////////////////////[업로드 파일 확장자 허용여부]	
	function upload_file_name_ck($uploadfilename){
		if(eregi("\php|\php3|\html|\htm|\phtml|\com|\bat|\exe|\inc|\js|\ph|\asp|\jsp|\cgi|\pl",$uploadfilename))
		$this->ErrorMsg('업로드가 제한된 파일입니다.');
		return;
	}


///////////////////////////////////////////////////////[파일 삭제 및 디렉토리 삭제]	
	function deldir($dir)
	{
	$handle = opendir($dir);
		while (false!==($FolderOrFile = readdir($handle)))
		{
			if($FolderOrFile != "." && $FolderOrFile != "..") 
			{ 
				if(is_dir("$dir/$FolderOrFile")) 
				{ $this->deldir("$dir/$FolderOrFile"); } // recursive
				else
				{ unlink("$dir/$FolderOrFile"); }
			} 
		}
		closedir($handle);
		if(rmdir($dir))
		{ $success = true; }
		return $success; 
	}

	function downHeader($file, $REAL_FILE) {
		global $HTTP_USER_AGENT;
	    if( eregi("(MSIE 5.0|MSIE 5.1|MSIE 5.5|MSIE 6.0)", $HTTP_USER_AGENT) ) {
		    if(strstr($HTTP_USER_AGENT, "MSIE 5.5"))
	        {
	            header("Content-Type: doesn/matter");
	            if ( $file )  { header("Content-Length: ".(string)(filesize("$file"))); }
	            header("Content-disposition: filename=$REAL_FILE");
	            header("Content-Transfer-Encoding: binary");
	            header("Pragma: no-cache");
	            header("Expires: 0");
	        }

	        if(strstr($HTTP_USER_AGENT, "MSIE 5.0"))
	        {
	            header("Content-type: file/unknown");
	            if ( $file )  { header("Content-Length: ".(string)(filesize("$file"))); }
	            header("Content-Disposition: attachment; filename=$REAL_FILE");
	            header("Pragma: no-cache");
	            header("Expires: 0");
	        }

		    if(strstr($HTTP_USER_AGENT, "MSIE 5.1"))
	        {
	            header("Content-type: file/unknown");
	            if ( $file )  { header("Content-Length: ".(string)(filesize("$file"))); }
	            header("Content-Disposition: attachment; filename=$REAL_FILE");
	            header("Pragma: no-cache");
	            header("Expires: 0");
	        }

	        if(strstr($HTTP_USER_AGENT, "MSIE 6.0"))
	        {
	            header("Content-type: file/unknown");
	            if ( $file )  { header("Content-Length: ".(string)(filesize("$file"))); }
	            header("Content-Disposition: attachment; filename=$REAL_FILE");
	            header("Content-Transfer-Encoding: binary");
	            header("Pragma: no-cache");
	            header("Expires: 0");
	        }
	    } else {
	        header("Content-type: file/unknown");
	        if ( $file )  { header("Content-Length: ".(string)(filesize("$file"))); }
	        header("Content-Disposition: attachment; filename=$REAL_FILE");
	        header("Pragma: no-cache");
	        header("Expires: 0");
	    }
	}

	function file_up($t1, $t2, $t3){
		if($_FILES[$t1]['size'] > 0){
			$this->upload_file_name_ck($_FILES[$t1]['name']);
			$file_split		= explode('.', $_FILES[$t1]['name']);
			$file_split_cnt	= count($file_split);
			$file_rename	= $t3."/".$t2.".".$file_split[$file_split_cnt-1];
			$file_return_nm	= $t2.".".$file_split[$file_split_cnt-1];
			if(!move_uploaded_file($_FILES[$t1]['tmp_name'], $file_rename)){$this->ErrorMsg("파일 업로드에 실패했습니다.");}
			return $file_return_nm;
		}else{
			return;
		}
	}

	function get_url(){
		$arr = array();
		$uri = $_SERVER['REQUEST_URI'];

		// query
		$x = array_pad( explode( '?', $uri ), 2, false );
		$arr['query'] = ( $x[1] )? $x[1] : '' ;

		// resource
		$x         = array_pad( explode( '/', $x[0] ), 2, false );
		$x_last = array_pop( $x );
		if( strpos( $x_last, '.' ) === false )
		{
			$arr['resource'] = '';
			$x[] = $x_last;
		}
		else
		{
			$arr['resource'] = $x_last;
		}

		// path    
		$arr['path'] = implode( '/', $x );
		if( substr( $arr['path'], -1 ) !== '/' ) $arr['path'] .= '/';

		// domain
		$arr['domain']    = $_SERVER['SERVER_NAME'];

		// scheme
		$server_prt        = explode( '/', $_SERVER['SERVER_PROTOCOL'] );
		$arr['scheme']    = strtolower( $server_prt[0] );

		// url
		$arr['url'] = $arr['scheme'].'://'.$arr['domain'].$uri;

		return $arr;
	}

	function randdata(){
		$Randy[0]	= array('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z');
		$Randy[1]	= array('0','1','2','3','4','5','6','7','8','9');
		$rn	= "";
		for($i=0;$i<7;$i++){
			$rnt	= rand(0,1);
			if($rnt=="0"){
				$rn		.= $Randy[0][rand(0,25)];
			}else{
				$rn		.= $Randy[1][rand(0,9)];
			}
		}
		return $rn;
	}

	function upload($folder,$fn,$rn,$fcnt){ 

		for($i=0;$i<$fcnt;$i++){

			$settm		= mktime().$i;
			//$upfolder	= $folder."/".$settm;
			$upfolder	= $folder."/".$settm;

			if($_FILES[$fn]['size'][$i] > 0){
				
				$fname	= $_FILES[$fn]['name'][$i];
				$fsize	= $_FILES[$fn]['size'][$i];

				$file_split		= explode('.', $fname);
				$file_split_cnt	= count($file_split);

//				if(!is_dir("$upfolder")){
//					mkdir("$upfolder",0777);
//					chmod("$upfolder",0777);
//				}

				$file_rename	= $settm.".".$file_split[$file_split_cnt-1];
				$upfile			= $upfolder."/".$file_rename;
					
				if(eregi("\avi|\wmv|\asf|\mpg|\mpeg|\xls|\JPG|\jpeg|\JPEG|\bmp|\gif|\jpg|\png|\ppt|\pptx",$fname)){
					if(!move_uploaded_file($_FILES[$fn]['tmp_name'][$i], $upfile)){
						$this->ErrorMsg("파일 업로드에 실패했습니다.");
					}else{
						$rtn[r][$i]		= $fname;
						$rtn[s][$i]		= $file_rename;
						$rtn[f][$i]		= $settm;
						$rtn[size][$i]	= $fsize;
						$rtn[ext][$i]	= $file_split[$file_split_cnt-1];
					}
				}else{
					$this->ErrorMsg("허용되지 않는 파일이 있습니다.");
				}

			}else{
				$rtn[r][$i]		= "";
				$rtn[s][$i]		= "";
				$rtn[f][$i]		= "";
				$rtn[size][$i]	= "";
				$rtn[ext][$i]	= "";
			}
		}
	
		return $rtn;
	}

	function getClientIP(){
		$clientIP = "na";
		if($_SERVER["HTTP_X_FORWARED_FOR"]){
			$clientIP = $_SERVER["HTTP_X_FORWARED_FOR"];
		}else if($_SERVER["REMOTE_ADDR"]){
			$clientIP = $_SERVER["REMOTE_ADDR"];
		}

		return $clientIP;
	}

	function DeffChk($obj){
		
		if($obj=="P"){
			$rtn = "POS";
		}else if($obj=="V"){
			$rtn = "VAN";
		}else if($obj=="E"){
			$rtn = "정상";
		}else{
			$rtn = "";	
		}

		return $rtn;
	}

	function PayStep($obj){
		
		if($obj=="0"){
			$rtn = "매입대상";
		}else if($obj=="1"){
			$rtn = "매입진행";
		}else if($obj=="2"){
			$rtn = "매입완료";
		}else{
			$rtn = "";	
		}

		return $rtn;
	}

	function PayGb($obj){
		
		if($obj=="A"){
			$rtn = "승인";
		}else if($obj=="C"){
			$rtn = "취소";
		}else if($obj=="M"){
			$rtn = "망취소";
		}else if($obj==""){
			$rtn = "Null";	
		}else{
			$rtn = "Err:".$obj;
		}

		return $rtn;
	}

	function depPayGb($obj){
		
		if($obj=="02"){
			$rtn = "D1";
		}else if($obj=="12"){
			$rtn = "D2";
		}else{
			$rtn = "";	
		}

		return $rtn;
	}

	function rowgb($obj){
		if($obj=="C"){
			$rtn	 = "candata";
		}elseif($obj=="M"){
			$rtn	 = "candata2";
		}else{
			$rtn	 = "non";
		}

		return $rtn;
	}

	function TranStep($obj){
		if($obj=="0"){
			$rtn	 = "거래(매입대상)";
		}else if($obj=="1"){
			$rtn	 = "매입요청";
		}else if($obj=="2"){
			$rtn	 = "매입완료";
		}else if($obj=="3"){
			$rtn	 = "정산완료";
		}else if($obj=="4"){
			$rtn	 = "거래취소";
		}else if($obj=="5"){
			$rtn	 = "데이터삭제";
		}

		return $rtn;
	}

	function CardType($obj){
		if($obj=="01" || $obj=="N"){
			$rtn	= "신용";
		}else if($obj=="02" || $obj=="Y"){
			$rtn	= "체크";
		}else if($obj=="03"){
			$rtn	= "해외";
		}else if($obj==""){
			$rtn	= "Null";
		}else{
			$rtn	= "Err:".$obj;
		}

		return $rtn;
	}

	/**
	* 필터 적용을 위한 쿼리 생성
	* fid : 필드명
	* fil : 필터링된 value
	* opt : 1:'like'쿼리, 0:'='쿼리
	**/
	function makeFilterQuery($fid, $fil, $opt){
		$w = "";
		if($opt=="1"){
			$w = $fid." like '%".$fil."%'";
		}else{
			$w = $fid."='".$fil."'";
		}
		return $w;
	}

	function replaceCharter($str){
		return preg_replace("/[ #\&\+\-%@=\/\\\:;,\.'\"\^`~\_|\!\?\$#<>()\[\]\{\}]/i", "", $str);
	}

	function MakeNullToZero($obj){
		if($obj=="" || $obj == null){
			$rtn	= 0;
		}else{
			$rtn	= $obj;
		}

		return $obj;
	}
}
?>