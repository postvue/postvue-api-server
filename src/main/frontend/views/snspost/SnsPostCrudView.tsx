import { SnsPostEndpoint } from 'Frontend/generated/endpoints'

import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import SnsPostEndPointDtoModel from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsPostEndPointDtoModel";

import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";

import 'swiper/css';
import { FreeMode, Navigation, Pagination, Thumbs } from 'swiper/modules';
import { Swiper, SwiperSlide } from 'swiper/react';
import styled from "styled-components";
import 'swiper/css/pagination';
import HlsPlayer from "Frontend/views/snspost/HlsPlayer";

export default function SnsPostCrudView() {
function GroupingLayoutRenderer({ children }: AutoFormLayoutRendererProps<SnsPostEndPointDtoModel>) {

    const fieldsMapping = new Map<string, JSX.Element>();
    children.forEach((field) => {
        const name = field.props?.propertyInfo?.name;
        if(!name) return;
        fieldsMapping.set(name, field)
    });

    let postContentList:Array<{
        postContentType: string;
        ascSortNum: number;
        content: string;
        bucketUrl: string;
        isLink:boolean;
        previewImg:string;
        fileType:string;
        isUploaded:boolean;
        videoDuration:number;
    }> = [];

    let postTagList:Array<{
        tagId:string;
        tagName:string;
    }> = [];

    const snsPostContentListString = fieldsMapping.get('id')?.props.form.defaultValue.snsPostContents;
    const snsPostTagListString = fieldsMapping.get('id')?.props.form.defaultValue.tags;
    const latitude = fieldsMapping.get('id')?.props.form.defaultValue.latitude;
    const longitude = fieldsMapping.get('id')?.props.form.defaultValue.longitude;
    const h3Index = fieldsMapping.get('id')?.props.form.defaultValue.h3Index;
    if (snsPostContentListString){
        postContentList = JSON.parse(snsPostContentListString);
    }

    if (snsPostTagListString){
        postTagList = JSON.parse(snsPostTagListString);
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
                <TableContainer >
                    <TableHead>
                    <TableHeadCol>
                        {postContentList.length > 0 &&
                            (Object.keys(postContentList[0]) as Array<keyof typeof postContentList[0]>).map((key) => (
                                <TableHeadTh key={key}>{key}</TableHeadTh>
                            ))}
                    </TableHeadCol>
                    </TableHead>
                    <tbody>
                    {postContentList.map((v, index) => (
                        <TableBodyTr key={index}>
                            {(Object.keys(v) as Array<keyof typeof v>).map((key) => (
                                <TableHeadTd key={key}>
                                    {typeof v[key] === "boolean" ? (v[key] ? "✅ Yes" : "❌ No") : v[key]}
                                </TableHeadTd>
                            ))}
                        </TableBodyTr>
                    ))}
                    </tbody>
                </TableContainer>


                {fieldsMapping.get('postTitle')}
                {fieldsMapping.get('postBodyText')}
                {fieldsMapping.get('postCaptionContent')}
                {fieldsMapping.get('latitude')}
                {fieldsMapping.get('longitude')}
                <div>h3Index: {h3Index}</div>
                {fieldsMapping.get('address')}
                {fieldsMapping.get('buildName')}
                <iframe
                    width="400"
                    height="300"
                    loading="lazy"
                    allowFullScreen
                    referrerPolicy="no-referrer-when-downgrade"
                    src={`https://www.google.com/maps?q=${latitude},${longitude}&hl=ko&z=15&output=embed`}>
                </iframe>

                {fieldsMapping.get('isShowAddress')}
                {fieldsMapping.get('tags')}
                <TableContainer >
                    <TableHead>
                        <TableHeadCol>
                            {postTagList.length > 0 &&
                                (Object.keys(postTagList[0]) as Array<keyof typeof postTagList[0]>).map((key) => (
                                    <TableHeadTh key={key}>{key}</TableHeadTh>
                                ))}
                        </TableHeadCol>
                    </TableHead>
                    <tbody>
                    {postTagList.map((v, index) => (
                        <TableBodyTr key={index}>
                            {(Object.keys(v) as Array<keyof typeof v>).map((key) => (
                                <TableHeadTd key={key}>
                                    {typeof v[key] === "boolean" ? (v[key] ? "✅ Yes" : "❌ No") : v[key]}
                                </TableHeadTd>
                            ))}
                        </TableBodyTr>
                    ))}
                    </tbody>
                </TableContainer>


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
                    {postContentList && postContentList.map((value, index) => {
                        return (
                            <SwiperSlide
                                key={index}
                            >
                                <PostWrap>
                                {value.postContentType === POST_CONTENT_TYPE.IMAGE && (
                                    <PostImgDiv src={value.bucketUrl + value.content} />
                                )}
                                {value.postContentType === POST_CONTENT_TYPE.VIDEO && (
                                    <HlsPlayer src={value.bucketUrl + value.content} />
                                )}
                                </PostWrap>
                            </SwiperSlide>
                        );
                    })}
                </StyledSwiper>

                {fieldsMapping.get('reactionCount')}
                {fieldsMapping.get('createdAt')}
                {fieldsMapping.get('deletedAt')}
                {fieldsMapping.get('lastUpdatedAt')}
            </VerticalLayout>
        </VerticalLayout>
    );
  }
  return (
      <>
      <AutoCrud
          service={SnsPostEndpoint}
          model={SnsPostEndPointDtoModel}
          style={{height:"100%"}}
          formProps={{
              layoutRenderer:GroupingLayoutRenderer,
              deleteButtonVisible: false
          }}/>
      </>
  );
}

const StyledSwiper = styled(Swiper)`
  width: 100%;
  max-width: 300px;
  margin-left: 0;
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


const TableContainer = styled.table`
  border-collapse: collapse;
  margin: 25px 0;
  font-size: 0.9em;
  font-family: sans-serif;
  min-width: 400px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.15);
`

const TableHead = styled.thead`
  background-color: #54A1D9;
  color: #ffffff;
  text-align: left;
`

const TableHeadCol = styled.tr`
  background-color: #54A1D9;
  color: #ffffff;
  text-align: left;
`

const TableBodyTr = styled.tr`
  border-bottom: 1px solid #dddddd;
  &:nth-of-type(even) {
      background-color: #f3f3f3;
    }
  &:last-of-type {
    border-bottom: 2px solid rgb(209 235 255);
  }
`

const TableHeadTh = styled.th`
  padding: 12px 15px;
`

const TableHeadTd = styled.td`
  padding: 12px 15px;
`