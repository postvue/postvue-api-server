import Modal from "Frontend/components/Modal";
import React from "react";
import styled from "styled-components";
import {createPostCompose} from "Frontend/services/post/createPostComposeList";
import {isValidString} from "Frontend/global/util/ValidUtil";
import {AdminSnsPostComposeCreateReq} from "Frontend/global/type/PostType";
import {
    FILE_NAME_PROPERTY, POST_ADDRESS_PROPERTY,
    POST_BODY_TEXT_PROPERTY, POST_BUILD_NAME_PROPERTY, POST_LATITUDE_PROPERTY, POST_LONGITUDE_PROPERTY,
    POST_TAG_1_PROPERTY,
    POST_TAG_2_PROPERTY,
    POST_TAG_3_PROPERTY,
    POST_TAG_4_PROPERTY,
    POST_TAG_5_PROPERTY, POST_TAG_6_PROPERTY, POST_TAG_7_PROPERTY,
    POST_TITLE_PROPERTY, POST_USERNAME_PROPERTY,
    PREFIX_POST_IMAGE_FOLDER
} from "Frontend/const/PostConst";

interface ExcelRow {
    [key: string]: any;
}

interface PostUploadConfirmPopupProps {
    onClose: () => void;
    onReset: () =>void;
    tableData: ExcelRow[]; // ✅ 객체 배열로 변경
    uploadedImages: File[];
}

const PostUploadConfirmPopup: React.FC<PostUploadConfirmPopupProps> = ({ onClose, onReset, tableData,uploadedImages}) => {

    const uploadImagePostList = async () => {
        try {
            const filteredData = tableData.filter((v) => isValidString(v.imagePath));

            const formData = new FormData();
            // ✅ 데이터 변환: tableData 객체 배열을 서버가 원하는 형식으로 변환
            const postData:AdminSnsPostComposeCreateReq[] = filteredData.map((value) => {
                const tagList:string[] =  [
                    value[POST_TAG_1_PROPERTY] || "", value[POST_TAG_2_PROPERTY] || "", value[POST_TAG_3_PROPERTY] || "",
                    value[POST_TAG_4_PROPERTY] || "", value[POST_TAG_5_PROPERTY] || "", value[POST_TAG_6_PROPERTY] || "",
                    value[POST_TAG_7_PROPERTY] || "" ].filter((v) => isValidString(v));

                const data: AdminSnsPostComposeCreateReq = {
                    username: value[POST_USERNAME_PROPERTY],
                    imageFilePathList:[value[FILE_NAME_PROPERTY].replace(PREFIX_POST_IMAGE_FOLDER,"")],
                    title:value[POST_TITLE_PROPERTY] || '',
                    bodyText:value[POST_BODY_TEXT_PROPERTY] || '',
                    tagList:tagList,
                    address: value[POST_ADDRESS_PROPERTY] || undefined,
                    buildName: value[POST_BUILD_NAME_PROPERTY] || undefined,
                    latitude: value[POST_LATITUDE_PROPERTY] || undefined,
                    longitude: value[POST_LONGITUDE_PROPERTY] || undefined,
                    targetAudienceValue: 0, //전체
                };
                return data;
            });


            const snsPostComposeCreateBlob = new Blob(
                [JSON.stringify(postData)],
                {
                    type: 'application/json',
                },
            );

            formData.append('snsPostComposeList', snsPostComposeCreateBlob);
            filteredData.forEach((row) => {
                const matchedFile:File|undefined = uploadedImages.find((file) => {
                    return file.name === row[FILE_NAME_PROPERTY].replace(PREFIX_POST_IMAGE_FOLDER,"");
                });

                if (matchedFile) {
                    formData.append('files', matchedFile, matchedFile.name);
                }
            });

            createPostCompose(formData).then((value)=>{
                alert("포스트 업로드가 완료되었습니다!");
                onReset();
                onClose();
            }).catch((error)=>{
                alert("업로드 실패: " + error);
            })

        } catch (error) {
            console.error("업로드 중 오류 발생:", error);
            alert("업로드 중 오류가 발생했습니다.");
        }
    };

    return (
        <Modal
            onClose={onClose}
            ModalContainerStyle={{ height: 250, width: 380 }}
            ModalSubContainerStyle={{ height: '100%', overflow: 'hidden' }}
        >
            <PopupUploadTitle>{uploadedImages.length.toLocaleString()}개의 포스트를 업로드 하시겠습니까?</PopupUploadTitle>
            <PopupUploadSubTitle>업로드 시, 서버에서 순차적으로 포스트 업로드 스케줄링됩니다.</PopupUploadSubTitle>
            <PopupUploadConfirmWrap>
                <PopupUploadConfirmSubWrap>
                    <PopupUploadConfirmSelectCancelItem onClick={onClose}>취소</PopupUploadConfirmSelectCancelItem>
                    <PopupUploadConfirmSelectItem onClick={uploadImagePostList}>확인</PopupUploadConfirmSelectItem>
                </PopupUploadConfirmSubWrap>
            </PopupUploadConfirmWrap>
        </Modal>
    );
};

// ✅ 스타일드 컴포넌트
const PopupUploadTitle = styled.div`
  font-weight: 600;
  padding-top: 25px;
  font-size: 20px;
  text-align: center;
`;

const PopupUploadSubTitle = styled.div`
  font-weight: 400;
  padding-top: 15px;
  text-align: center;
`;

const PopupUploadConfirmWrap = styled.div`
  font-weight: 500;
  padding-top: 15px;
  display: flex;
`;

const PopupUploadConfirmSubWrap = styled.div`
  display: flex;
  margin: auto;
  gap: 10px;
`;

const PopupUploadConfirmSelectItem = styled.div`
  font-weight: 500;
  font-size: 20px;
  cursor: pointer;
  padding: 5px 20px;
  background-color: black;
  color: white;
  border-radius: 10px;
`;

const PopupUploadConfirmSelectCancelItem = styled(PopupUploadConfirmSelectItem)`
  color: black;
  background-color: white;
  border: 1px solid black;
`;

export default PostUploadConfirmPopup;
