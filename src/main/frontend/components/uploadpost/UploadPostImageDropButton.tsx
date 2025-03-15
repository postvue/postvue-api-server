import React, {ChangeEvent, useCallback} from "react";
import styled from "styled-components";
import {ExcelRow} from "Frontend/global/type/ExcelType";
import {FILE_NAME_PROPERTY, IMAGE_PATH_PROPERTY, PREFIX_POST_IMAGE_FOLDER} from "Frontend/const/PostConst";

interface UploadPostImageDropButtonProps {
    tableData: ExcelRow[];
    setTableData: (value: (((prevState: ExcelRow[]) => ExcelRow[]) | ExcelRow[])) => void
    setHeaders: (value: (((prevState: string[]) => string[]) | string[])) => void
    setUploadedImages: (value: (((prevState: File[]) => File[]) | File[])) => void
}

const DROP_INPUT_ID = 'drop-input-id'


const UploadPostImageDropButton:React.FC<UploadPostImageDropButtonProps> = ({
    tableData, setTableData, setHeaders, setUploadedImages
}) =>{
    const onDrop = useCallback((acceptedFiles: File[]) => {
        const imageFiles = acceptedFiles.filter(file =>
            file.type.startsWith("image/") // ✅ 이미지 파일만 추출
        );

        setUploadedImages(imageFiles); // ✅ 이미지 파일 상태 업데이트

        // ✅ 파일명과 기존 데이터 매칭
        const updatedTableData = tableData.map(row => {
            const matchingImage = imageFiles.find(file => {
                // console.log(PREFIX_IMAGE_FOLDER + file.name, row["file_name"])
                return file.name.includes(row[FILE_NAME_PROPERTY].replace(PREFIX_POST_IMAGE_FOLDER, ""));
            });

            setHeaders((prev)=>{
                if (!prev.includes(IMAGE_PATH_PROPERTY)){
                    return [...prev,IMAGE_PATH_PROPERTY]
                }
                return prev
            })
            return {
                ...row,
                imagePath: matchingImage ? URL.createObjectURL(matchingImage) : "" // ✅ 이미지 URL 추가
            };
        });


        setTableData(updatedTableData);
    }, [tableData]);

    // ✅ 폴더 업로드 핸들러
    const handleFolderUpload = (e: ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            onDrop(Array.from(e.target.files)); // ✅ 파일 리스트를 `onDrop`에 전달
        }
    };

    return <DropButton className="border-2 border-dashed p-10 text-center cursor-pointer">
        <div>
        <DropButtonInput
            type="file"
            multiple
            onChange={handleFolderUpload}
            id={DROP_INPUT_ID}
            {...({ webkitdirectory: "" } as unknown as React.InputHTMLAttributes<HTMLInputElement>)} // ✅ TypeScript 오류 해결
        />
        <DropButtonLabel htmlFor={DROP_INPUT_ID} >이미지 파일을 업로드 할려면 클릭해서 폴더를 넣어주세요.</DropButtonLabel>
        </div>
        <DropButtonGuide>이미지 폴더명은 images이며, images안에 이미지가 들어 있어야 됩니다.</DropButtonGuide>
    </DropButton>
}

const DropButton = styled.div`
  border: 2px;
  text-align: center;
  cursor: pointer;
  display:flex;
  justify-content: center;
  margin: 15px 0;
  flex-direction: column;
`;

const DropButtonLabel = styled.label`
  transition: background 0.2s ease-in-out;
  @media (hover: hover) {
    &:hover {
      color: white;
      background: rgba(0, 0, 0, 0.1);
    }
  }
  cursor: pointer;
  border-radius: 5px;
  padding: 5px 10px;
  font-size: 18px;
`

const DropButtonGuide = styled.p`
    color: darkgrey;
  font-size: 13px;
`


const DropButtonInput = styled.input`
  display: none;
  
`



export default UploadPostImageDropButton;