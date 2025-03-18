import { useState } from 'react';
import styled from "styled-components";
import React from "react";
import TableComponent from "Frontend/components/TableComponent";
import PostUploadDetailPopup from "Frontend/components/popup/PostUploadDetailPopup";
import PostUploadConfirmPopup from "Frontend/components/popup/PostUploadConfirmPopup";
import {ExcelRow} from "Frontend/global/type/ExcelType";
import UploadPostTableDropButton from "Frontend/components/uploadpost/UploadPostTableDropButton";
import UploadPostImageDropButton from "Frontend/components/uploadpost/UploadPostImageDropButton";

export default function ExcelUploader() {
    const [tableData, setTableData] = useState<ExcelRow[]>([]); // ✅ 객체 배열로 변경
    const [uploadedImages, setUploadedImages] = useState<File[]>([]); // ✅ 업로드된 이미지 파일 리스트 저장
    const [headers, setHeaders] = useState<string[]>([]); // ✅ 헤더 따로 저장

    const [selectedRow, setSelectedRow] = useState<ExcelRow | null>(null); // ✅ 선택된 행도 객체 형태로 변경
    const [isActiveConfirmPopup, setIsActiveConfirmPopup] = useState<boolean>(false);

    const onReset = () =>{
        setUploadedImages([]);
        setHeaders([]);
        setTableData([]);
    }

    return (
        <div className="p-5">
            {/* ✅ 파일 업로드 UI */}
            {(tableData.length > 0 || uploadedImages.length > 0) && (
                <CancelWrap>
                    <CancelButton onClick={onReset}>
                        취소
                    </CancelButton>
                </CancelWrap>
            )}

            {tableData.length <= 0 && <UploadPostTableDropButton tableData={tableData} setTableData={setTableData} setHeaders={setHeaders} />}

            {(tableData.length > 0 && uploadedImages.length <= 0) && (
                <>
                <UploadPostImageDropButton tableData={tableData} setTableData={setTableData} setHeaders={setHeaders} setUploadedImages={setUploadedImages} />
                </>
            )}


            {tableData.length > 0 && uploadedImages.length > 0 && (
                <UploadButtonWrap>
                    <UploadButton onClick={() => setIsActiveConfirmPopup(true)}>
                        {tableData.filter((v)=> v.imagePath !== "").length.toLocaleString()}개의 포스트를 업로드 하시겠습니까?
                    </UploadButton>
                </UploadButtonWrap>
            )}

            {/* ✅ 엑셀 데이터 미리보기 테이블 */}
            {tableData.length > 0 && (
                <TableComponent
                    contentList={tableData} // ✅ 객체 배열 전달
                    headers={headers} // ✅ 헤더 전달
                    onRowClick={setSelectedRow} // ✅ 객체 데이터를 직접 전달
                />
            )}

            {/* ✅ 상세보기 팝업 */}
            {selectedRow && (
                <PostUploadDetailPopup
                    tableData={tableData}
                    selectedRow={selectedRow}
                    onClose={() => setSelectedRow(null)}
                />
            )}

            {/* ✅ 업로드 확인 팝업 */}
            {isActiveConfirmPopup && (
                <PostUploadConfirmPopup
                    onClose={() => setIsActiveConfirmPopup(false)}
                    onReset={onReset}
                    tableData={tableData}
                    uploadedImages={uploadedImages}
                />
            )}
        </div>
    );
}

// ✅ 스타일드 컴포넌트
const UploadButtonWrap = styled.div`
  display: flex;
`;

const UploadButton = styled.div`
  border-radius: 10px;
  text-align: center;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  margin: 0 auto;
  padding: 5px 10px;

  transition: background 0.2s ease-in-out;
  @media (hover: hover) {
    &:hover {
      color: white;
      background: rgba(0, 0, 0, 0.1);
    }
  }
`;

const CancelWrap = styled.div`
  display: flex;
  margin: 10px 0;
`;


const CancelButton = styled.div`
  border-radius: 20px;
  text-align: center;
  cursor: pointer;
  background-color: white;
  color: black;

  
  border: 1px solid #e8e8e8;
  display: flex;
  margin: 0 auto;
  padding: 5px 20px;

  transition: background 0.2s ease-in-out;
  @media (hover: hover) {
    &:hover {
      color: white;
      background: rgba(0, 0, 0, 0.1);
    }
  }
`;

