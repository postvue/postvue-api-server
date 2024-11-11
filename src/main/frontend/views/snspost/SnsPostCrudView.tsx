import { SnsPostEndpoint } from 'Frontend/generated/endpoints'

import { Notification } from '@vaadin/react-components/Notification.js';
import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import SnsPostEndPointDtoModel from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsPostEndPointDtoModel";

import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import SnsTagEndpointDtoModel from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsTagEndpointDtoModel";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import PostContentType from "Frontend/generated/com/postvue/feelogserver/domain/snsposts/vo/PostContentType";

import 'swiper/css';
import { FreeMode, Navigation, Pagination, Thumbs } from 'swiper/modules';
import { Swiper, SwiperSlide } from 'swiper/react';
import styled from "styled-components";
import 'swiper/css/pagination';

export default function SnsPostCrudView() {
function GroupingLayoutRenderer({ children }: AutoFormLayoutRendererProps<SnsPostEndPointDtoModel>) {

    const fieldsMapping = new Map<string, JSX.Element>();
    children.forEach((field) => {
        const name = field.props?.propertyInfo?.name;
        if(!name) return;
        fieldsMapping.set(name, field)
    });

    let postContentList:Array<{ postContentType: string; ascSortNum: number; content: string }> = [];
    const snsPostContentListString = fieldsMapping.get('id')?.props.form.defaultValue.snsPostContents;
    if (snsPostContentListString){
        postContentList = JSON.parse(snsPostContentListString);
    }
    const POST_CONTENT_TYPE = {
        IMAGE:'IMAGE',
        VIDEO:'VIDEO',
        LINK:'LINK'
    }

    return (
        <VerticalLayout>
            <h4>Personal Information:</h4>
            <VerticalLayout theme="spacing" className="pb-l">
                {fieldsMapping.get('snsUser_id')}
                {fieldsMapping.get('isExposed')}
                {fieldsMapping.get('snsPostContents')}
                {fieldsMapping.get('postTitle')}
                {fieldsMapping.get('postBodyText')}
                {fieldsMapping.get('postCaptionContent')}
                {fieldsMapping.get('latitude')}
                {fieldsMapping.get('longitude')}
                {fieldsMapping.get('address')}
                {fieldsMapping.get('isShowAddress')}
                {fieldsMapping.get('tags')}
                {fieldsMapping.get('repostOriginId')}
                {fieldsMapping.get('tgtAudType')}
                {fieldsMapping.get('postContentBusinessType')}

                <StyledSwiper
                    spaceBetween={0}
                    slidesPerView={1}
                    pagination={true}
                    loop={true}
                    modules={[Pagination, Navigation, FreeMode, Navigation, Thumbs]}
                >
                    {postContentList.map((value, index) => {
                        console.log(value.content,value.postContentType)
                        return (
                            <SwiperSlide
                                key={index}
                            >

                                <PostWrap>
                                {value.postContentType === POST_CONTENT_TYPE.IMAGE && (
                                    <PostImgDiv src={value.content} />
                                )}
                                {value.postContentType === POST_CONTENT_TYPE.VIDEO && (
                                    <PostVideoDiv src={value.content} playsInline webkit-playsinline="true"/>
                                )}
                                </PostWrap>
                            </SwiperSlide>
                        );
                    })}
                </StyledSwiper>
            </VerticalLayout>
        </VerticalLayout>
    );
}
  return (
      <>
      <AutoCrud
          service={SnsPostEndpoint}
          model={SnsPostEndPointDtoModel}
          formProps={{
              onDeleteError: handleOnDeleteError,
              onSubmitError:handleOnSubmitError,
              layoutRenderer:GroupingLayoutRenderer
          }}/>
      </>
  );
}

const StyledSwiper = styled(Swiper)`
  width: 100%;
  max-width: 360px;
  .swiper-pagination-bullet {
    background-color: white;
    opacity: 0.3;
  }

  .swiper-pagination-bullet-active {
    background-color: white;
    opacity: 1;
  }
`;


const PostWrap = styled.div`
  width: 100%;
`;

const PostImgDiv = styled.img`
  border-radius: 8px;
  width: 100%;
`;

const PostVideoDiv = styled.video`
  width: 100%;
  height: auto;
  vertical-align: bottom;
  border-radius: 8px;
`;