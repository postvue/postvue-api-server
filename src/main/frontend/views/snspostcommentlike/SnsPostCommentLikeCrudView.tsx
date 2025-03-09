import {SnsPostCommentLikeEndpoint} from 'Frontend/generated/endpoints'

import { AutoCrud } from '@vaadin/hilla-react-crud';
import SnsPostCommentLikeEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsPostCommentLikeEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";

export default function SnsPostCommentLikeCrudView() {
  return (
      <AutoCrud
          service={SnsPostCommentLikeEndpoint}
          model={SnsPostCommentLikeEndpointDtoModel}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
              visibleFields:[
              'snsPost_id',
              'snsPostCommentReaction_id',
              'snsUser_id',
              'isLiked',
              'isLikedAt',
              ]
      }}/>

  );
}
