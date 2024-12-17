import {SnsPostUserReactionEndpoint} from 'Frontend/generated/endpoints'

import {AutoCrud} from '@vaadin/hilla-react-crud';
import SnsPostUserReactionEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsPostUserReactionEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
export default function SnsPostUserReactionCrudView() {


  return (
      <>
      <AutoCrud
          service={SnsPostUserReactionEndpoint}
          model={SnsPostUserReactionEndpointDtoModel}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
            visibleFields:[
                'snsPost_id',
                'snsUser_id',
                'isShown',
                'notShownAt',
                'createdAt',]
            }}
      />
          </>

  );
}
