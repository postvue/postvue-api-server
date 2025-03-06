import {
  SnsUserMessageRoomEndpoint,
} from 'Frontend/generated/endpoints'

import {AutoGrid} from '@vaadin/hilla-react-crud';
import SnsUserMessageRoomEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserMessageRoomEndpointDtoModel";

export default function SnsUserMessageRoomCrudView() {
  return (
      <AutoGrid
          service={SnsUserMessageRoomEndpoint}
          model={SnsUserMessageRoomEndpointDtoModel}
          style={{height:"100%"}}
          // formProps={{
          //   onDeleteError: handleOnDeleteError,
          //   onSubmitError:handleOnSubmitError,
          //   visibleFields:['createdAt']
          //   }}
      />
  );
}
