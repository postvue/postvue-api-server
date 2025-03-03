import {SnsPostCommentLikeEndpoint, SnsPostCommentReactionEndpoint, SnsPostEndpoint} from 'Frontend/generated/endpoints'

import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import SnsPostCommentReactionEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsPostCommentReactionEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import PostCommentMediaType
    from "Frontend/generated/com/postvue/feelogserver/domain/snspostcommentreactions/vo/PostCommentMediaType";
import styled from "styled-components";

export default function SnsPostCommentReactionCrudView() {

    function GroupingLayoutRenderer({ children }: AutoFormLayoutRendererProps<SnsPostCommentReactionEndpointDtoModel>) {
        const fieldsMapping = new Map<string, JSX.Element>();
        children.forEach((field) => {
            const name = field.props?.propertyInfo?.name;
            if(!name) return;
            fieldsMapping.set(name, field)
        });

        return (
            <VerticalLayout>
                <h4>Personal Information:</h4>
                <VerticalLayout theme="spacing" className="pb-l">
                    {fieldsMapping.get('snsPost_id')}
                    {fieldsMapping.get('sourceComment_id')}
                    {fieldsMapping.get('commentUser_id')}
                    {fieldsMapping.get('commentMsg')}
                    {fieldsMapping.get('commentMediaType')}
                    {fieldsMapping.get('commentMediaContent')}
                    {fieldsMapping.get('isSource')}
                    {fieldsMapping.get('createdAt')}
                    {fieldsMapping.get('deletedAt')}
                    <PostWrap>
                    {fieldsMapping.get('id')?.props.form.defaultValue.commentMediaType === PostCommentMediaType.IMAGE
                        ?
                        <PostImgDiv src={fieldsMapping.get('id')?.props.form.defaultValue.commentMediaContent} />
                        : <PostVideoDiv src={fieldsMapping.get('id')?.props.form.defaultValue.commentMediaContent} />
                    }
                    </PostWrap>
                </VerticalLayout>
            </VerticalLayout>
        );
    }
    return (
      <AutoCrud
          service={SnsPostCommentReactionEndpoint}
          model={SnsPostCommentReactionEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
            layoutRenderer:GroupingLayoutRenderer,
      }}/>

    );
}

const PostWrap = styled.div`
  width: 100%;
  max-width: 360px;
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
