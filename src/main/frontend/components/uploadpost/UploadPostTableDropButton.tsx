import React, {useCallback} from "react";
import styled from "styled-components";
import {useDropzone} from "react-dropzone";
import {read, utils} from "xlsx";
import {ExcelRow} from "Frontend/global/type/ExcelType";

interface UploadPostTableDropButtonProps {
    tableData: ExcelRow[];
    setTableData: (value: (((prevState: ExcelRow[]) => ExcelRow[]) | ExcelRow[])) => void

    setHeaders: (value: (((prevState: string[]) => string[]) | string[])) => void
}

const UploadPostTableDropButton:React.FC<UploadPostTableDropButtonProps> = ({
    tableData, setTableData, setHeaders
}) =>{
    const onDrop = useCallback((acceptedFiles: File[]) => {
        const file = acceptedFiles[0];
        const reader = new FileReader();

        if (tableData.length > 0) {
            setTableData([]);
            setHeaders([]);
        }

        reader.onload = (e: ProgressEvent<FileReader>) => {
            const result = e.target?.result;
            if (!result || !(result instanceof ArrayBuffer)) return;

            const data = new Uint8Array(result);
            const workbook = read(data, { type: "array" });

            const sheetName = workbook.SheetNames[0];
            const worksheet = workbook.Sheets[sheetName];

            // ✅ 객체 배열(JSON)로 변환
            const jsonData: ExcelRow[] = utils.sheet_to_json<ExcelRow>(worksheet, {
                header:0,
                defval: "", // 빈 셀을 빈 문자열로 유지
            });

            if (jsonData.length > 0) {
                setHeaders(Object.keys(jsonData[0])); // ✅ 헤더 저장
                setTableData(jsonData); // ✅ 데이터 저장
            }
        };

        reader.readAsArrayBuffer(file);
    }, [tableData]);

    const { getRootProps, getInputProps } = useDropzone({
        onDrop,
        accept: {
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": [".xlsx"],
            "application/vnd.ms-excel": [".xls"],
        },
    });

    return <DropButton {...getRootProps()} className="border-2 border-dashed p-10 text-center cursor-pointer">
        <input {...getInputProps()} />
        <DropButtonLabel>엑셀 파일을 업로드하려면 클릭하거나 드래그하세요.</DropButtonLabel>
    </DropButton>
}

const DropButton = styled.div`
  border: 2px;
  text-align: center;
  cursor: pointer;
  display: flex;
  justify-content: center;
`;

const DropButtonLabel = styled.p`
  transition: background 0.2s ease-in-out;
  @media (hover: hover) {
    &:hover {
      color: white;
      background: rgba(0, 0, 0, 0.1);
    }
  }
  padding: 5px 10px;
  border-radius: 10px;
  font-size:18px;
`


export default UploadPostTableDropButton;