<?
class MSSQL_API { 

	public function MSSQL_API(){
        $this->Host = "218.151.248.22";
        $this->User = "ecms";
        $this->Pwd = "@ecms!!";
		$this->Db	= "hssys";
    }

	public function DBConnect(){
        $this->Con = mssql_connect($this->Host, $this->User, $this->Pwd) or die("접속 실패!");
		mssql_select_db($this->Db);
    }

    public function NewDBConnect($host, $user, $pwd, $db){
        if ($this->Con) $this->DBClose();

        $this->Host = $host;
        $this->User = $user;
        $this->Pwd  = $pwd;
        $this->DB   = $db;

        $this->DBConnect();
    }

	public function DBClose(){
        mssql_close($this->Con);
    }


	/**
    * 카드승인 일괄등록 유효성 채크프로시져 호출
    * @param array $obj : 유효성 데이터의 배열
    * @return array
    * @access public
    */
	function CardValidate($obj){
		$stmt	= mssql_init("usp_CardAccept_Validate_ForProton", $this->Con);
		mssql_bind($stmt, '@stor_cd',			$obj[0], SQLVARCHAR, false, false, 10);
		mssql_bind($stmt, '@terminalId',		$obj[1], SQLVARCHAR, false, false, 20);
		mssql_bind($stmt, '@memb_no',			$obj[2], SQLVARCHAR, false, false, 20);
		mssql_bind($stmt, '@card_co',			$obj[14], SQLVARCHAR, false, false, 4);
		mssql_bind($stmt, '@card_no',			$obj[3], SQLVARCHAR, false, false, 16);
		mssql_bind($stmt, '@card_amt',			$obj[4], SQLINT4);
		mssql_bind($stmt, '@halbu_month',		$obj[5], SQLVARCHAR, false, false, 2);
		mssql_bind($stmt, '@maec_date',			$obj[6], SQLVARCHAR, false, false, 8);
		mssql_bind($stmt, '@maec_time',			$obj[7], SQLVARCHAR, false, false, 6);
		mssql_bind($stmt, '@accept_no',			$obj[8], SQLVARCHAR, false, false, 8);
		mssql_bind($stmt, '@banpum_gb',			$obj[9], SQLVARCHAR, false, false, 1);
		mssql_bind($stmt, '@maec_date_origin',	$obj[10], SQLVARCHAR, false, false, 14);
		mssql_bind($stmt, '@contract_no',		$obj[11], SQLVARCHAR, false, false, 10);
		mssql_bind($stmt, '@empl_code',			$obj[12], SQLVARCHAR, false, false, 10);
		mssql_bind($stmt, '@pos_gb',			$obj[13], SQLVARCHAR, false, false, 1);

		$result = mssql_execute($stmt) or die(mssql_get_last_message()); 
		$num = mssql_num_rows($result);
		$rst = mssql_fetch_assoc($result);	   
		return $rst;
	}


	/**
    * 카드일괄 업로드시 저장프로시져 호출
    * @param array $obj : 저장 데이터의 배열
    * @return 없음.
    * @access public
    */
	function CardInsert($obj){
		(int)$amount	= (int)$obj[5];
		(int)$halbu	= (int)$obj[10];	
	
		$stmt	= mssql_init("usp_CardAccept_insert_ForProton", $this->Con);
		mssql_bind($stmt, '@stor_cd',			$obj[0], SQLVARCHAR, false, false, 10); 
		mssql_bind($stmt, '@maecDate',			$obj[1], SQLVARCHAR, false, false, 8);
		mssql_bind($stmt, '@maecTime',			$obj[2], SQLVARCHAR, false, false, 6);
		mssql_bind($stmt, '@banpumGb',			$obj[3], SQLVARCHAR, false, false, 1);
		mssql_bind($stmt, '@acceptGb',			$obj[4], SQLVARCHAR, false, false, 1);
		mssql_bind($stmt, '@cardAmt',			$amount, SQLINT4);
		mssql_bind($stmt, '@card_co',			$obj[6], SQLVARCHAR, false, false, 4);
		mssql_bind($stmt, '@card_nm',			$obj[7], SQLVARCHAR, false, false, 20);
		mssql_bind($stmt, '@cardNo',			$obj[8], SQLVARCHAR, false, false, 16);
		mssql_bind($stmt, '@acceptNo',			$obj[9], SQLVARCHAR, false, false, 8);
		mssql_bind($stmt, '@halbuMonth',		$halbu, SQLINT2);
		mssql_bind($stmt, '@maecDate_org',		$obj[11], SQLVARCHAR, false, false, 8);
		mssql_bind($stmt, '@maecTime_org',		$obj[12], SQLVARCHAR, false, false, 6);
		mssql_bind($stmt, '@contractNo',		$obj[13], SQLVARCHAR, false, false, 10);
		mssql_bind($stmt, '@emplCode',			$obj[14], SQLVARCHAR, false, false, 10);
		mssql_bind($stmt, '@mchNo',			$obj[15], SQLVARCHAR, false, false, 15);
		mssql_bind($stmt, '@terminalId',		$obj[16], SQLVARCHAR, false, false, 20);
		mssql_bind($stmt, '@saveUser',			$obj[17], SQLVARCHAR, false, false, 10);

		$result = mssql_execute($stmt) or die(mssql_get_last_message()); 
		$num = mssql_num_rows($result);
		$rst = mssql_fetch_assoc($result);

		return $rst;
	}

	/**
    * 반송데이터 수정시 POS 업데이트 프로시져 호출
    * @param array $obj : 저장 데이터의 배열
    * @return 없음.
    * @access public
    */
	function CardUpdate($obj){
		$stmt	= mssql_init("usp_CardAccept_update_ForProton", $this->Con);
		mssql_bind($stmt, '@stor_cd',			$obj[0], SQLVARCHAR, false, false, 10); //매장코드
		mssql_bind($stmt, '@maecDate',			$obj[1], SQLVARCHAR, false, false, 8);	//승인일자
		mssql_bind($stmt, '@maecTime',			$obj[2], SQLVARCHAR, false, false, 6);	//승인시간
		mssql_bind($stmt, '@banpumGb',			$obj[3], SQLVARCHAR, false, false, 1);	//반품구분
		mssql_bind($stmt, '@acceptGb',			$obj[4], SQLVARCHAR, false, false, 1);	//승인구분
		mssql_bind($stmt, '@cardAmt',			$obj[5], SQLINT4);						//승인금액
		mssql_bind($stmt, '@card_co',			$obj[6], SQLVARCHAR, false, false, 4);	//매입사코드
		mssql_bind($stmt, '@card_nm',			$obj[7], SQLVARCHAR, false, false, 20);	//매입사명
		mssql_bind($stmt, '@cardNo',			$obj[8], SQLVARCHAR, false, false, 16);	//카드번호
		mssql_bind($stmt, '@acceptNo',			$obj[9], SQLVARCHAR, false, false, 8);	//승인번호
		mssql_bind($stmt, '@halbuMonth',		$obj[10], SQLINT2);						//할부개월수
		mssql_bind($stmt, '@maecDate_org',		$obj[11], SQLVARCHAR, false, false, 8);	//원승인일자
		mssql_bind($stmt, '@maecTime_org',		$obj[12], SQLVARCHAR, false, false, 6);	//원승인시간
		mssql_bind($stmt, '@contractNo',		$obj[13], SQLVARCHAR, false, false, 10);//계약번호
		mssql_bind($stmt, '@emplCode',			$obj[14], SQLVARCHAR, false, false, 10);//영업사원
		mssql_bind($stmt, '@mchNo',				$obj[15], SQLVARCHAR, false, false, 15);//가맹점코드
		mssql_bind($stmt, '@terminalId',		$obj[16], SQLVARCHAR, false, false, 20);//단말기아이디
		mssql_bind($stmt, '@saveUser',			$obj[17], SQLVARCHAR, false, false, 10);//수정자아이디
		mssql_bind($stmt, '@cardid',			$obj[18], SQLVARCHAR, false, false, 10);//카드아이디

		$result = mssql_execute($stmt) or die(mssql_get_last_message()); 
		$num = mssql_num_rows($result);
		$rst = mssql_fetch_assoc($result);
		return $rst;
	}

	/**
	* 매입요청 저장 SP호출
	**/
	function DepoReqInsert($obj){
		$stmt	= mssql_init("usp_MRequest_insert_ForProton", $this->Con);
		mssql_bind($stmt, '@maecDate',			$obj[0], SQLVARCHAR, false, false, 8);	//승인일(취소시 원승인일)
		mssql_bind($stmt, '@car_no',			$obj[1], SQLVARCHAR, false, false, 16); //카드번호
		mssql_bind($stmt, '@acceptNo',			$obj[2], SQLVARCHAR, false, false, 8);	//승인번호
		mssql_bind($stmt, '@banpum_gb',			$obj[3], SQLVARCHAR, false, false, 2);	//승인취소여부
		mssql_bind($stmt, '@cardAmt',			$obj[4], SQLINT4);						//거래금액
		mssql_bind($stmt, '@requestDate',		$obj[5], SQLVARCHAR, false, false, 8);	//요청일
		mssql_bind($stmt, '@saveUser',			$obj[6], SQLVARCHAR, false, false, 10);	//작업자
	 
		print_r($obj);
		$result = mssql_execute($stmt) or die(mssql_get_last_message()); 
		$num = mssql_num_rows($result);
		$rst = mssql_fetch_assoc($result);
		return $rst;
	}

	/**
	* 매입결과 아이템 저장 SP호출
	**/
	function DepoRcvData($obj){
		$stmt	= mssql_init("usp_MResult_insert_ForProton", $this->Con);
		mssql_bind($stmt, '@maec_date',			$obj[0], SQLVARCHAR, false, false, 8);		//승인일(취소시 원승인일)
		mssql_bind($stmt, '@receipt_date',		$obj[1], SQLVARCHAR, false, false, 8);		//요청일
		mssql_bind($stmt, '@return_date',		$obj[2], SQLVARCHAR, false, false, 8);		//응답일
		mssql_bind($stmt, '@pay_date',			$obj[3], SQLVARCHAR, false, false, 8);		//입급일
		mssql_bind($stmt, '@org_date',			$obj[4], SQLVARCHAR, false, false, 8);		//원거래일
		mssql_bind($stmt, '@mch_no',			$obj[5], SQLVARCHAR, false, false, 15);		//가맹점번호
		mssql_bind($stmt, '@koces_cd',			$obj[6], SQLVARCHAR, false, false, 4);		//카드사코드
		mssql_bind($stmt, '@car_no',			$obj[7], SQLVARCHAR, false, false, 16);		//카드번호
		mssql_bind($stmt, '@accept_no',			$obj[8], SQLVARCHAR, false, false, 8);		//승인번호
		mssql_bind($stmt, '@banpum_gb',			$obj[9], SQLVARCHAR, false, false, 2);		//승인취소여부(취소는 12)
		mssql_bind($stmt, '@halbu_month',		$obj[10], SQLINT2);							//할부개월(일시불 0)
		mssql_bind($stmt, '@net_maec_amt',		$obj[11], SQLINT4);							//승인금액
		mssql_bind($stmt, '@comm_amt',			$obj[12], SQLINT4);							//수수료
		mssql_bind($stmt, '@return_code',		$obj[13], SQLVARCHAR, false, false, 2);		//응답코드
		mssql_bind($stmt, '@return_text',		$obj[14], SQLVARCHAR, false, false, 200);	//응답내용
		mssql_bind($stmt, '@saveUser',			$obj[15], SQLVARCHAR, false, false, 8);		//작업자

		$result = mssql_execute($stmt) or die(mssql_get_last_message()); 
		$num = mssql_num_rows($result);
		$rst = mssql_fetch_assoc($result);
		return $rst;
	}

	/**
	* 매입결과 헤더 저장
	**/
	function DepoRcvHead($obj){
		$stmt	= mssql_init("usp_MTotalResult_insert_ForProton", $this->Con);
		mssql_bind($stmt, '@RETURN_DATE',		$obj[0], SQLVARCHAR, false, false, 8);		//응답일
		mssql_bind($stmt, '@RECEIPT_DATE',		$obj[1], SQLVARCHAR, false, false, 8);		//요청일
		mssql_bind($stmt, '@NET_MAEC_GUN',		$obj[2], SQLINT4);							//요청건수(전체)
		mssql_bind($stmt, '@NET_MAEC_AMT',		$obj[3], SQLINT4);							//요청금액
		mssql_bind($stmt, '@RETURN_GUN',		$obj[4], SQLINT4);							//반송건수
		mssql_bind($stmt, '@RETURN_AMT',		$obj[5], SQLINT4);							//반송금액
		mssql_bind($stmt, '@BO_GUN',			$obj[6], SQLINT4);							//보류건수
		mssql_bind($stmt, '@BO_AMT',			$obj[7], SQLINT4);							//보류금액
		mssql_bind($stmt, '@BO_HAE_GUN',		$obj[8], SQLINT4);							//보류해제건수
		mssql_bind($stmt, '@BO_HAE_AMT',		$obj[9], SQLINT4);							//보류해제금액
		mssql_bind($stmt, '@REAL_GUN',			$obj[10], SQLINT4);							//처리건수(승인)
		mssql_bind($stmt, '@REAL_AMT',			$obj[11], SQLINT4);							//처리금액
		mssql_bind($stmt, '@COMM_AMT',			$obj[12], SQLINT4);							//수수료
		mssql_bind($stmt, '@office_gb',			$obj[13], SQLVARCHAR, false, false, 3);		//영업구분(INT:001, 부엌:002, AS:003)
		mssql_bind($stmt, '@saveUser',			$obj[14], SQLVARCHAR, false, false, 8);		//작업자

		$result = mssql_execute($stmt) or die(mssql_get_last_message()); 
		$num = mssql_num_rows($result);
		$rst = mssql_fetch_assoc($result);
		return $rst;
	}
	
}
?>