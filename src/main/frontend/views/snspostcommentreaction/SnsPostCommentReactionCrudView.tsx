import {SnsPostCommentLikeEndpoint, SnsPostCommentReactionEndpoint, SnsPostEndpoint} from 'Frontend/generated/endpoints'

import {AutoCrud, AutoFormLayoutRendererProps} from '@vaadin/hilla-react-crud';
import SnsPostCommentReactionEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsPostCommentReactionEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import {VerticalLayout} from "@vaadin/react-components/VerticalLayout";
import PostCommentMediaType
    from "Frontend/generated/com/postvue/feelogserver/domain/snspostcommentreactions/vo/PostCommentMediaType";

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
                    {fieldsMapping.get('id')?.props.form.defaultValue.commentMediaType === PostCommentMediaType.IMAGE
                        ?
                        <img src={fieldsMapping.get('id')?.props.form.defaultValue.commentMediaContent} />
                        : <video src={fieldsMapping.get('id')?.props.form.defaultValue.commentMediaContent} />
                    }
                </VerticalLayout>
            </VerticalLayout>
        );
    }
    return (
      <AutoCrud
          service={SnsPostCommentReactionEndpoint}
          model={SnsPostCommentReactionEndpointDtoModel}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
            visibleFields:[
              'snsPost_id',
              'sourceComment_id',
              'commentUser_id',
              'postCommentType',
              'postCommentContent',
              'isSource',
              'createdAt',
            ]
      }}/>

    );
}
