import ImagePreviewComponent from "Frontend/components/ImagePreviewComponent";
import Modal from "Frontend/components/Modal";
import React from "react";
import styled from "styled-components";

interface ExcelRow {
    [key: string]: any;
}

interface PostUploadDetailPopupProps {
    onClose: () => void;
    selectedRow: ExcelRow; // ✅ 객체 배열 형태로 변경
    tableData: ExcelRow[]; // ✅ 전체 데이터도 객체 배열로 변경
}

const PostUploadDetailPopup: React.FC<PostUploadDetailPopupProps> = ({ onClose, tableData, selectedRow }) => {
    // ✅ 헤더를 자동 추출 (tableData가 없을 경우 대비)
    const headers = tableData.length > 0 ? Object.keys(tableData[0]) : Object.keys(selectedRow);

    return (
        <Modal onClose={onClose}>
            <h2 className="text-xl font-bold mb-4">상세 정보</h2>
            <SelectRowUi>
                {headers.map((header, index) => (
                    <TextContainerWrap key={index} className="mb-2">
                        <TextContainerString>{header}:</TextContainerString>
                        <TextContainerSubWrap>
                            <TextContainerStringWrap>
                                {typeof selectedRow[header] === "string" && selectedRow[header] !== "" &&
                                selectedRow[header] !== undefined && selectedRow[header].startsWith("http")
                                    ? <TextContainerHref target={"_blank"} href={selectedRow[header]}>{selectedRow[header]}</TextContainerHref>
                                    : <TextContainer>{selectedRow[header]}</TextContainer>}
                            </TextContainerStringWrap>
                            {typeof selectedRow[header] === "string" && selectedRow[header] !== "" &&
                                (selectedRow[header] !== undefined && selectedRow[header].startsWith("http") ||
                                selectedRow[header] !== undefined && selectedRow[header].startsWith("blob")
                                ) &&
                                <ImagePreviewComponent imgPath={selectedRow[header]} />}
                        </TextContainerSubWrap>
                    </TextContainerWrap>
                ))}
            </SelectRowUi>
        </Modal>
    );
};

// ✅ 스타일드 컴포넌트
const SelectRowUi = styled.div`
  gap: 10px;
  display: flex;
  flex-direction: column;
`;

const TextContainerWrap = styled.div`
  display: flex;
  gap:5px;
`;

const TextContainerString = styled.div`
  display: flex;
  white-space: nowrap;
  padding: 4px 0;
  font-weight: 600;
  flex: 0 0 auto; /* 내용 크기만큼 차지 */
`;

const TextContainerSubWrap = styled.div`
  flex: 1;
`;

const TextContainerStringWrap = styled.div`
  padding-right: 10px;
`;

const TextContainer = styled.div`
  max-width: calc(100% - 50px);
  word-break: break-word; /* 긴 단어도 줄바꿈 처리 */
  white-space: normal; /* 공백을 유지하면서 자동 줄바꿈 */
  overflow-wrap: break-word; /* 컨테이너 안에서 강제 줄바꿈 */
  padding: 4px 0; /* 텍스트 간격 추가 */
  
  
`;

const TextContainerHref = styled.a`
  max-width: calc(100% - 50px);
  word-break: break-word; /* 긴 단어도 줄바꿈 처리 */
  white-space: normal; /* 공백을 유지하면서 자동 줄바꿈 */
  overflow-wrap: break-word; /* 컨테이너 안에서 강제 줄바꿈 */
  padding: 4px 0; /* 텍스트 간격 추가 */
`;

export default PostUploadDetailPopup;
