<?
class MSSQL_API { 

	public function MSSQL_API(){
        $this->Host = "218.151.248.22";
        $this->User = "ecms";
        $this->Pwd = "@ecms!!";
		$this->Db	= "hssys";
    }

	public function DBConnect(){
        $this->Con = mssql_connect($this->Host, $this->User, $this->Pwd) or die("���� ����!");
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
    * ī����� �ϰ���� ��ȿ�� äũ���ν��� ȣ��
    * @param array $obj : ��ȿ�� �������� �迭
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
    * ī���ϰ� ���ε�� �������ν��� ȣ��
    * @param array $obj : ���� �������� �迭
    * @return ����.
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
    * �ݼ۵����� ������ POS ������Ʈ ���ν��� ȣ��
    * @param array $obj : ���� �������� �迭
    * @return ����.
    * @access public
    */
	function CardUpdate($obj){
		$stmt	= mssql_init("usp_CardAccept_update_ForProton", $this->Con);
		mssql_bind($stmt, '@stor_cd',			$obj[0], SQLVARCHAR, false, false, 10); //�����ڵ�
		mssql_bind($stmt, '@maecDate',			$obj[1], SQLVARCHAR, false, false, 8);	//��������
		mssql_bind($stmt, '@maecTime',			$obj[2], SQLVARCHAR, false, false, 6);	//���νð�
		mssql_bind($stmt, '@banpumGb',			$obj[3], SQLVARCHAR, false, false, 1);	//��ǰ����
		mssql_bind($stmt, '@acceptGb',			$obj[4], SQLVARCHAR, false, false, 1);	//���α���
		mssql_bind($stmt, '@cardAmt',			$obj[5], SQLINT4);						//���αݾ�
		mssql_bind($stmt, '@card_co',			$obj[6], SQLVARCHAR, false, false, 4);	//���Ի��ڵ�
		mssql_bind($stmt, '@card_nm',			$obj[7], SQLVARCHAR, false, false, 20);	//���Ի��
		mssql_bind($stmt, '@cardNo',			$obj[8], SQLVARCHAR, false, false, 16);	//ī���ȣ
		mssql_bind($stmt, '@acceptNo',			$obj[9], SQLVARCHAR, false, false, 8);	//���ι�ȣ
		mssql_bind($stmt, '@halbuMonth',		$obj[10], SQLINT2);						//�Һΰ�����
		mssql_bind($stmt, '@maecDate_org',		$obj[11], SQLVARCHAR, false, false, 8);	//����������
		mssql_bind($stmt, '@maecTime_org',		$obj[12], SQLVARCHAR, false, false, 6);	//�����νð�
		mssql_bind($stmt, '@contractNo',		$obj[13], SQLVARCHAR, false, false, 10);//����ȣ
		mssql_bind($stmt, '@emplCode',			$obj[14], SQLVARCHAR, false, false, 10);//�������
		mssql_bind($stmt, '@mchNo',				$obj[15], SQLVARCHAR, false, false, 15);//�������ڵ�
		mssql_bind($stmt, '@terminalId',		$obj[16], SQLVARCHAR, false, false, 20);//�ܸ�����̵�
		mssql_bind($stmt, '@saveUser',			$obj[17], SQLVARCHAR, false, false, 10);//�����ھ��̵�
		mssql_bind($stmt, '@cardid',			$obj[18], SQLVARCHAR, false, false, 10);//ī����̵�

		$result = mssql_execute($stmt) or die(mssql_get_last_message()); 
		$num = mssql_num_rows($result);
		$rst = mssql_fetch_assoc($result);
		return $rst;
	}

	/**
	* ���Կ�û ���� SPȣ��
	**/
	function DepoReqInsert($obj){
		$stmt	= mssql_init("usp_MRequest_insert_ForProton", $this->Con);
		mssql_bind($stmt, '@maecDate',			$obj[0], SQLVARCHAR, false, false, 8);	//������(��ҽ� ��������)
		mssql_bind($stmt, '@car_no',			$obj[1], SQLVARCHAR, false, false, 16); //ī���ȣ
		mssql_bind($stmt, '@acceptNo',			$obj[2], SQLVARCHAR, false, false, 8);	//���ι�ȣ
		mssql_bind($stmt, '@banpum_gb',			$obj[3], SQLVARCHAR, false, false, 2);	//������ҿ���
		mssql_bind($stmt, '@cardAmt',			$obj[4], SQLINT4);						//�ŷ��ݾ�
		mssql_bind($stmt, '@requestDate',		$obj[5], SQLVARCHAR, false, false, 8);	//��û��
		mssql_bind($stmt, '@saveUser',			$obj[6], SQLVARCHAR, false, false, 10);	//�۾���
	 
		print_r($obj);
		$result = mssql_execute($stmt) or die(mssql_get_last_message()); 
		$num = mssql_num_rows($result);
		$rst = mssql_fetch_assoc($result);
		return $rst;
	}

	/**
	* ���԰�� ������ ���� SPȣ��
	**/
	function DepoRcvData($obj){
		$stmt	= mssql_init("usp_MResult_insert_ForProton", $this->Con);
		mssql_bind($stmt, '@maec_date',			$obj[0], SQLVARCHAR, false, false, 8);		//������(��ҽ� ��������)
		mssql_bind($stmt, '@receipt_date',		$obj[1], SQLVARCHAR, false, false, 8);		//��û��
		mssql_bind($stmt, '@return_date',		$obj[2], SQLVARCHAR, false, false, 8);		//������
		mssql_bind($stmt, '@pay_date',			$obj[3], SQLVARCHAR, false, false, 8);		//�Ա���
		mssql_bind($stmt, '@org_date',			$obj[4], SQLVARCHAR, false, false, 8);		//���ŷ���
		mssql_bind($stmt, '@mch_no',			$obj[5], SQLVARCHAR, false, false, 15);		//��������ȣ
		mssql_bind($stmt, '@koces_cd',			$obj[6], SQLVARCHAR, false, false, 4);		//ī����ڵ�
		mssql_bind($stmt, '@car_no',			$obj[7], SQLVARCHAR, false, false, 16);		//ī���ȣ
		mssql_bind($stmt, '@accept_no',			$obj[8], SQLVARCHAR, false, false, 8);		//���ι�ȣ
		mssql_bind($stmt, '@banpum_gb',			$obj[9], SQLVARCHAR, false, false, 2);		//������ҿ���(��Ҵ� 12)
		mssql_bind($stmt, '@halbu_month',		$obj[10], SQLINT2);							//�Һΰ���(�Ͻú� 0)
		mssql_bind($stmt, '@net_maec_amt',		$obj[11], SQLINT4);							//���αݾ�
		mssql_bind($stmt, '@comm_amt',			$obj[12], SQLINT4);							//������
		mssql_bind($stmt, '@return_code',		$obj[13], SQLVARCHAR, false, false, 2);		//�����ڵ�
		mssql_bind($stmt, '@return_text',		$obj[14], SQLVARCHAR, false, false, 200);	//���䳻��
		mssql_bind($stmt, '@saveUser',			$obj[15], SQLVARCHAR, false, false, 8);		//�۾���

		$result = mssql_execute($stmt) or die(mssql_get_last_message()); 
		$num = mssql_num_rows($result);
		$rst = mssql_fetch_assoc($result);
		return $rst;
	}

	/**
	* ���԰�� ��� ����
	**/
	function DepoRcvHead($obj){
		$stmt	= mssql_init("usp_MTotalResult_insert_ForProton", $this->Con);
		mssql_bind($stmt, '@RETURN_DATE',		$obj[0], SQLVARCHAR, false, false, 8);		//������
		mssql_bind($stmt, '@RECEIPT_DATE',		$obj[1], SQLVARCHAR, false, false, 8);		//��û��
		mssql_bind($stmt, '@NET_MAEC_GUN',		$obj[2], SQLINT4);							//��û�Ǽ�(��ü)
		mssql_bind($stmt, '@NET_MAEC_AMT',		$obj[3], SQLINT4);							//��û�ݾ�
		mssql_bind($stmt, '@RETURN_GUN',		$obj[4], SQLINT4);							//�ݼ۰Ǽ�
		mssql_bind($stmt, '@RETURN_AMT',		$obj[5], SQLINT4);							//�ݼ۱ݾ�
		mssql_bind($stmt, '@BO_GUN',			$obj[6], SQLINT4);							//�����Ǽ�
		mssql_bind($stmt, '@BO_AMT',			$obj[7], SQLINT4);							//�����ݾ�
		mssql_bind($stmt, '@BO_HAE_GUN',		$obj[8], SQLINT4);							//���������Ǽ�
		mssql_bind($stmt, '@BO_HAE_AMT',		$obj[9], SQLINT4);							//���������ݾ�
		mssql_bind($stmt, '@REAL_GUN',			$obj[10], SQLINT4);							//ó���Ǽ�(����)
		mssql_bind($stmt, '@REAL_AMT',			$obj[11], SQLINT4);							//ó���ݾ�
		mssql_bind($stmt, '@COMM_AMT',			$obj[12], SQLINT4);							//������
		mssql_bind($stmt, '@office_gb',			$obj[13], SQLVARCHAR, false, false, 3);		//��������(INT:001, �ξ�:002, AS:003)
		mssql_bind($stmt, '@saveUser',			$obj[14], SQLVARCHAR, false, false, 8);		//�۾���

		$result = mssql_execute($stmt) or die(mssql_get_last_message()); 
		$num = mssql_num_rows($result);
		$rst = mssql_fetch_assoc($result);
		return $rst;
	}
	
}
?>