import {
  SnsUserMessageRoomEndpoint,
} from 'Frontend/generated/endpoints'

import { AutoCrud } from '@vaadin/hilla-react-crud';
import SnsUserMessageRoomEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserMessageRoomEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";

export default function SnsUserMessageRoomCrudView() {
  return (
      <AutoCrud
          service={SnsUserMessageRoomEndpoint}
          model={SnsUserMessageRoomEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
            visibleFields:['createdAt']
      }} />
  );
}
