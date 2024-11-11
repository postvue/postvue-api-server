import {AutoCrud, AutoFormLayoutRendererProps, AutoGrid} from "@vaadin/hilla-react-crud";
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
        console.log(postNotificationContentList);
        const POST_NOTIFICATION_CONTENT_TYPE = {
            IMAGE:'IMAGE',
            TEXT:'TEXT'
        }

        return (
            <VerticalLayout>
                <VerticalLayout theme="spacing" className="pb-l">
                    {fieldsMapping.get('snsNotificationType')}
                    {fieldsMapping.get('snsNotificationContents')}

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
            </VerticalLayout>
        );
    }
  return (
      <AutoCrud
          service={SnsNotificationEndpoint}
          model={SnsNotificationEndpointDtoModel}
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