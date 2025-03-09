import {
 SnsUserMessageEndpoint
} from 'Frontend/generated/endpoints'

import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import SnsUserMessageEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserMessageEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import PostContentType from "Frontend/generated/com/postvue/feelogserver/domain/snsposts/vo/PostContentType";
import MsgMediaType from "Frontend/generated/com/postvue/feelogserver/domain/snsusermessages/vo/MsgMediaType";
import HlsPlayer from "Frontend/views/snspost/HlsPlayer";
import styled from "styled-components";

export default function SnsUserMessageCrudView() {
function GroupingLayoutRenderer({ children }: AutoFormLayoutRendererProps<SnsUserMessageEndpointDtoModel>) {

    const fieldsMapping = new Map<string, JSX.Element>();
    children.forEach((field) => {
        const name = field.props?.propertyInfo?.name;
        if(!name) return;
        fieldsMapping.set(name, field)
    });
    //
    let msgMetaInfo:{
        ogImage:string;
        ogTitle:string;
        ogDescription:string;
    } = {
        ogImage: '',
        ogTitle: '',
        ogDescription: ''
    }

    const msgMetaInfoString = fieldsMapping.get('id')?.props.form.defaultValue.msgMetaInfo;
    //
    if ( msgMetaInfoString){
        msgMetaInfo = JSON.parse(msgMetaInfoString);
    }

    return (
        <VerticalLayout>
            <h4>Personal Information:</h4>
            <VerticalLayout theme="spacing" className="pb-l">
                {fieldsMapping.get("msgTextContent")}
                {fieldsMapping.get("msgMediaType")}
                {fieldsMapping.get("msgMediaContent")}
                {fieldsMapping.get('id')?.props.form.defaultValue.msgMediaType === MsgMediaType.IMAGE
                    &&
                    <img src={fieldsMapping.get('id')?.props.form.defaultValue.msgMediaContent} style={{width:'100%',maxWidth:'250px',borderRadius:'5px'}}/>
                }
                {fieldsMapping.get('id')?.props.form.defaultValue.msgMediaType === MsgMediaType.VIDEO
                    &&
                    <HlsPlayer src={fieldsMapping.get('id')?.props.form.defaultValue.msgMediaContent} />
                }
                {fieldsMapping.get("msgMetaInfo")}
                <TableContainer >
                    <TableHead>
                        <TableHeadCol>
                            <TableHeadTh>ogTitle</TableHeadTh>
                            <TableHeadTh>ogImage</TableHeadTh>
                            <TableHeadTh>ogDescription</TableHeadTh>
                        </TableHeadCol>
                    </TableHead>
                    <tbody>
                    <TableBodyTr>

                        <TableHeadTd>
                            {msgMetaInfo.ogTitle}
                        </TableHeadTd>
                        <TableHeadTd>
                            {msgMetaInfo.ogImage}
                        </TableHeadTd>
                        <TableHeadTd>
                            {msgMetaInfo.ogDescription}
                        </TableHeadTd>

                    </TableBodyTr>

                    </tbody>
                </TableContainer>
                {fieldsMapping.get("createdAt")}
                {fieldsMapping.get("deletedAt")}
                {fieldsMapping.get("lastUpdatedAt")}
            </VerticalLayout>
        </VerticalLayout>
    );
}
  return (
      <AutoCrud
          service={SnsUserMessageEndpoint}
          model={SnsUserMessageEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
              onDeleteError: handleOnDeleteError,
              onSubmitError:handleOnSubmitError,
              layoutRenderer:GroupingLayoutRenderer,
      }}/>
  );
}


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