import {AutoCrud, AutoFormLayoutRendererProps} from "@vaadin/hilla-react-crud";
import SnsNotificationEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsNotificationEndpointDtoModel";
import {SnsNotificationEndpoint} from "Frontend/generated/endpoints";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import styled from "styled-components";

export default function SnsNotificationCrudView() {
    function GroupingLayoutRenderer({ children }: AutoFormLayoutRendererProps<SnsNotificationEndpointDtoModel>) {

        const fieldsMapping = new Map<string, JSX.Element>();
        children.forEach((field) => {
            const name = field.props?.propertyInfo?.name;
            if(!name) return;
            fieldsMapping.set(name, field)
        });

        let postNotificationContentList:Array<{ snsNotificationContentType: string; snsNotificationContent:string }> = [];
        const snsPostContentListString = fieldsMapping.get('id')?.props.form.defaultValue.snsNotificationContents;
        if (snsPostContentListString){
            postNotificationContentList = JSON.parse(snsPostContentListString);
        }
        const POST_NOTIFICATION_CONTENT_TYPE = {
            IMAGE:'IMAGE',
            TEXT:'TEXT'
        }


        return (
            <VerticalLayout>
                <VerticalLayout theme="spacing" className="pb-l">
                    {fieldsMapping.get('snsNotificationType')}
                    {fieldsMapping.get('snsNotificationContents')}
                    <TableContainer >
                        <TableHead>
                            <TableHeadCol>
                                {postNotificationContentList.length > 0 &&
                                    (Object.keys(postNotificationContentList[0]) as Array<keyof typeof postNotificationContentList[0]>).map((key) => (
                                        <TableHeadTh key={key}>{key}</TableHeadTh>
                                    ))}
                            </TableHeadCol>
                        </TableHead>
                        <tbody>
                        {postNotificationContentList.map((v, index) => (
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


                    <div>
                    <h5>알림 내용 개수: {postNotificationContentList.length}</h5>
                    <ul>
                    {postNotificationContentList.map((value, index) => {
                        return (

                        <PostWrap>
                            {value.snsNotificationContentType === POST_NOTIFICATION_CONTENT_TYPE.IMAGE && (
                                <PostImg src={value.snsNotificationContent} />
                            )}
                            {value.snsNotificationContentType === POST_NOTIFICATION_CONTENT_TYPE.TEXT && (
                                <PostText>{value.snsNotificationContent}</PostText>
                            )}
                        </PostWrap>
                        );
                    })}
                    </ul>
                    </div>
                </VerticalLayout>

                {fieldsMapping.get('createdAt')}
            </VerticalLayout>
        );
    }
  return (
      <AutoCrud
          service={SnsNotificationEndpoint}
          model={SnsNotificationEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
            layoutRenderer:GroupingLayoutRenderer
          }}
      />
  );
}


const PostWrap = styled.li``;

const PostImg = styled.img``;

const PostText = styled.div``;


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