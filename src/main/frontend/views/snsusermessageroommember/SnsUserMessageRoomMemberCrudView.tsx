import {
  SnsUserMessageRoomMemberEndpoint
} from 'Frontend/generated/endpoints'

import { AutoCrud } from '@vaadin/hilla-react-crud';
import SnsUserMessageRoomMemberEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserMessageRoomMemberEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";

export default function SnsUserMessageRoomMemberCrudView() {
  return (
      <AutoCrud
          service={SnsUserMessageRoomMemberEndpoint}
          model={SnsUserMessageRoomMemberEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
            visibleFields:[
              'readAt',
              'isHidden',
              'isBlocked',
              'createdAt'
            ]
      }} />
  );
}
