<?PHP
/*=========================================================================
    Page Name           : db_class.php
    Page Description    : 오라클 API 클래스
    Writer              : unistyle
    Write Date          : 2009-06-09
    Editor              : unistyle
    Edit Date           : 2009-08-12
    Edit Description    : class 소멸자 추가
                          setUpdate, setInsert 부분 "(쌍따옴표) 삭제 추가
    !Caution
        BeginTrans메서드로 트렌젝션을 사용 할 때는 insert, update, delete
        처리 이후(혹은 중간)에 select를 실행하게 될 경우 데이터 조작에 의한
        트렌젝션이 자동 Commit된다. 필히 트렌젝션을 사용 해야 한다면
        꼭 insert, update, delete 부분 처리가 있기 이전에 필요한 데이터를
        모두 갖춰 놓은 상태에서 처리 하여야 한다.
        또, BeginTrans 후 Commit 혹은 Rollback 처리를 해주지 않으면
        DeadLock에 걸릴 우려가 있으니 꼭 Transaction을 마무리 지어줘야 함
=========================================================================*/

class Oci_API
{
    private $Host;      //호스트 정보
    private $User;      //사용자 정보
    private $Pwd;       //비밀번호

    private $Parse;     //OciParse 결과 값

    private $Con;       //연결 정보
    public $query;      //쿼리 문자열

    public $Type;       //설정 될 경우 실행하지 않고 echo문으로 query를 뿌려줌

    private $Trans;     //트렉젠션 형식 (false일 경우 무조건 커밋)

    public $ErrNum;     //에러 번호
    public $ErrMsg;     //에러 메시지

    public $BindData;   //PL_SQL실행 후 리턴값을 받음

    //============================================================
    //  class 생성자
    //============================================================
    public function Oci_API()
    {
	    $this->Host		= "192.168.126.131:1521/orcl";
		$this->User		= "IFOU";
		$this->Pwd		= "1";
        $this->Type		= "REAL";
        $this->Trans	= false;
    }

    //============================================================
    //  DB연결 메서드
    //============================================================
    public function DBConnect()
    {
		putenv("NLS_LANG=American_America.UTF8");
        $this->Con = OciLogOn($this->User, $this->Pwd, $this->Host) or die("접속 실패!");
    }

    //============================================================
    //  새로운 DB연결 인스턴트를 생성한다
    //============================================================
    public function NewDBConnect($host, $user, $pwd, $db)
    {
        if ($this->Con) $this->DBClose();

        $this->Host = $host;
        $this->User = $user;
        $this->Pwd  = $pwd;
        $this->DB   = $db;

        $this->DBConnect();
    }

    //============================================================
    //  DB연결 해제 메서드
    //============================================================
    public function DBClose()
    {
        OciLogOff($this->Con);
    }

    //============================================================
    //  할당된 쿼리문의 유효성을 검수
    //============================================================
    private function SqlParse()
    {
        $this->Parse = OciParse($this->Con, $this->query);
    }

    //============================================================
    //  단순 쿼리 실행
    //============================================================
    public function SqlExec($query="")
    {
        if ($query)
            $this->query = $query;

        $this->SqlParse();

        if ($this->Trans)
        {
            @OciExecute($this->Parse, OCI_DEFAULT);
            $this->Error = OciError($this->Parse);
        }
        else
        {
            OciExecute($this->Parse);
            $this->Error = OciError($this->Parse);
            OciFreeStatement($this->Parse);
        }
    }

    //============================================================
    //  쿼리 실행 후 결과 값을 2차원 배열로 반환
    //  $query -> 실행할 쿼리문
    //  $type -> 배열반환 형식 (num : 숫자 그외 문자열)
    //============================================================
    public function FetchInto($query, $type="")
    {
        $this->query = $query;

        $this->SqlParse();

        OciExecute($this->Parse);

        $i = 0;
        if ($type == "num")
        {
            while (OciFetchInto($this->Parse, $result))
            {
                while (list($key, $val) = each($result))
                {
                    $rows[$i][$key] = stripslashes($val);
                }
                $i++;
            }
        }
        else
        {
            while (OciFetchInto($this->Parse, $result, OCI_ASSOC))
            {
                while (list($key, $val) = each($result))
                {
                    $rows[$i][$key] = stripslashes($val);
                }
                $i++;
            }
        }

        OciFreeStatement($this->Parse);

        return $rows;
    }

    //============================================================
    //  지정 필드에 대한 결과 값을 반환
    //============================================================
    public function OneResult($table, $field, $where="")
    {
        $this->query = "SELECT ".$field." FROM ".$table;

        if ($where) $this->query .= " WHERE ".$where;

        $this->SqlParse();

        OciExecute($this->Parse);
        OciFetchInto($this->Parse, $result);
        OciFreeStatement($this->Parse);

        return $result[0];
    }

    //============================================================
    //  Insert문 생성 및 실행
    //============================================================
    public function setInsert($table, $input, $seq_field="")
    {
        $this->query = "INSERT INTO ".$table." (";

        $i = 0;
        while (list($key, $val) = each($input))
        {
            if ($i == 0)
            {
                $fields = $key;

                if ($key == $seq_field)
                    $values = $val.".NEXTVAL";
                else
                {
                    if (strtoupper($val) == "_SYSDATE_")
                        $values = "SYSDATE";
                    else
                    {
                        if (strrpos($val, "TO_CHAR") !== false || strrpos($val, "TO_DATE") !== false)
                            $values = trim($val);
                        else
                        {
                            $val = str_replace("'", "''", trim($val));
                            $val = str_replace('"', '', $val);
                            $values = "'".$val."'";
                        }
                    }
                }
            }
            else
            {
                $fields .= ", ".$key;

                if ($key == $seq_field)
                    $values .= ", ".$val.".NEXTVAL";
                else
                {
                    if (strtoupper($val) == "_SYSDATE_")
                        $values .= ", SYSDATE";
                    else
                    {
                        if (strrpos($val, "TO_CHAR") !== false || strrpos($val, "TO_DATE") !== false)
                            $values .= ", ".trim($val);
                        else
                        {
                            $val = str_replace("'", "''", trim($val));
                            $val = str_replace('"', '', $val);
                            $values .= ", '".$val."'";
                        }
                    }
                }
            }

            $i++;
        }

        $this->query .= $fields.") VALUES (".$values.")";

        if (strtoupper($this->Type) == "REAL")
            $this->SqlExec();
        else
            echo $this->query."<br><hr><br>";
    }

    //============================================================
    //  Update문 생성 및 실행
    //============================================================
    public function setUpdate($table, $update, $where="", $type=1)
    {
        $this->query = "UPDATE ".$table." SET ";

        if (!$type)
        {
            $this->query .= $update;
        }
        else
        {
            $i = 0;
            while (list($key, $val) = each($update))
            {
                if ($i == 0)
                {
                    if (strtoupper($val) == "_SYSDATE_")
                    {
                        $this->query .= $key." = SYSDATE";
                    }
                    else if (strrpos(strtoupper($val), "FIELD:") !== false)
                    {
                        //필드의 값을 직접 입력할 경우
                        $val = str_replace("FIELD:", "", $val);
                        $this->query .= $val;
                    }
                    else
                    {
                        if (strpos($val, "TO_CHAR") !== false || strpos($val, "TO_DATE") !== false)
                            $this->query .= $key." = ".trim($val);
                        else
                        {
                            $val = str_replace("'", "''", trim($val));
                            $val = str_replace('"', '', $val);
                            $this->query .= $key." = '".$val."'";
                        }
                    }
                }
                else
                {
                    if (strtoupper($val) == "_SYSDATE_")
                    {
                        $this->query .= ", ".$key." = SYSDATE";
                    }
                    else if (strrpos(strtoupper($val), "FIELD:") !== false)
                    {
                        //필드의 값을 직접 입력할 경우
                        $val = str_replace("FIELD:", "", $val);
                        $this->query .= ", ".$val;
                    }
                    else
                    {
                        if (strpos($val, "TO_CHAR") !== false || strpos($val, "TO_DATE") !== false)
                            $this->query .= ", ".$key." = ".trim($val);
                        else
                        {
                            $val = str_replace("'", "''", trim($val));
                            $val = str_replace('"', '', $val);
                            $this->query .= ", ".$key." = '".$val."'";
                        }
                    }
                }

                $i++;
            }
        }

        if ($where)
            $this->query .= " WHERE ".$where;
        else
        {
            $this->DBClose();
            echo "조건 절이 없습니다!";
            exit;
        }

        if (strtoupper($this->Type) == "REAL")
            $this->SqlExec();
        else
            echo $this->query."<br><hr><br>";
    }

    //============================================================
    //  Delete문 생성 및 실행
    //============================================================
    public function setDelete($table, $where)
    {
        $this->query = "DELETE FROM ".$table;

        if ($where)
        {
            $this->query .= " WHERE ".$where;
        }
        else
        {
            $this->DBClose();
            echo "조건 절이 없습니다!";
            exit;
        }

        if (strtoupper($this->Type) == "REAL")
            $this->SqlExec();
        else
            echo $this->query."<br><hr><br>";
    }

    //============================================================
    //  트렌젝션을 시작
    //============================================================
    public function BeginTrans()
    {
        $this->Trans = true;
    }

    //============================================================
    //  트렌젝션 종료(트렌젝션을 사용하지 않을때 설정)
    //============================================================
    public function EndTrnas()
    {
        $this->Trans = false;
    }

    //============================================================
    //  트렌젝션을 커밋한다
    //============================================================
    public function Commit()
    {
        OciCommit($this->Con);
    }

    //============================================================
    //  트렌젝션을 롤백 한다
    //============================================================
    public function Rollback()
    {
        OciRollback($this->Con);
    }

    //============================================================
    //  에러정보
    //============================================================
    public function OnError()
    {
        if ($this->Error)
        {
            $this->ErrNum = $this->Error["code"];
            $this->ErrMsg = $this->Error["message"];

            return true;
        }
        else
        {
            $this->ErrNum = "";
            $this->ErrMsg = "";

            return false;
        }
    }

    //============================================================
    //  에러 출력
    //============================================================
    public function PrintErr()
    {
        echo "
            에러 번호 : <font color='red'>".$this->ErrNum."</font><br>
            에러 내용 : <font color='red'>".$this->ErrMsg."</font><br>
        ";
    }

    //============================================================
    //  PL/SQL Stored Proc 실행을 위한 멤버
    //============================================================
    public function ExecPLSQL($pl_sql, $bind="")
    {
        $this->query = $pl_sql;

        $this->SqlParse();

        if ($bind)
        {
            while (list($key, $val) = each($bind))
            {
                OciBindByName($this->Parse, ":".$key, $this->BindData[$key], $val);
            }
        }

        OciExecute($this->Parse);
    }

    //============================================================
    //  class 소멸자 (페이지가 종료되는 시점에 실행됨)
    //============================================================
    public function _Oci_API()
    {
        OciLogOff($this->Con);
    }

	/**
	* 시퀀스가 필요할경우 공통 시퀀서 함수
	* @param : obj1 -> 날짜
	* @param : obj2 -> 시퀀스종류
	**/
	public function SeqGet($obj1, $obj2){

		$SEQTB			= "TB_SALES_SEQ";
		$SEQFD			= "SEQ_DATE, SEQ_TYPE, SEQ_NO";
		$SEQWH			= "WHERE SEQ_DATE='".$obj1."' AND SEQ_TYPE='".$obj2."'";
		$SEQSEL			= "SELECT COUNT(1) PCNT FROM ".$SEQTB." ".$SEQWH." ";
		$this->SqlExec($SEQSEL);
		$PINGET	= $this->fetchInto($SEQSEL);

		if($PINGET[0][PCNT]<1){
			$iQry  = "BEGIN ";
			$iQry .= "INSERT INTO ".$SEQTB." (".$SEQFD.") VALUES ('".$obj1."','".$obj2."','1'); ";
			$iQry .= "COMMIT; ";
			$iQry .= "END; ";
			$this->SqlExec($iQry);
			$SEQNO	= 1;
		}else{
			$sSel	= "SELECT ".$SEQFD." FROM ".$SEQTB." ".$SEQWH;
			$this->SqlExec($sSel);
			$sGet	= $this->fetchInto($sSel);
			$SEQNO	= $sGet[0][SEQ_NO]+1;

			$uQry  = "BEGIN ";
			$uQry .= "UPDATE ".$SEQTB." SET SEQ_NO=SEQ_NO+1 ".$SEQWH.";";
			$uQry .= "COMMIT; ";
			$uQry .= "END; ";
			$this->SqlExec($uQry);
		}

		return $SEQNO;
	}
}
?>
