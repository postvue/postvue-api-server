import { SnsUserFollowEndpoint} from 'Frontend/generated/endpoints'

import {AutoGrid} from '@vaadin/hilla-react-crud';
import SnsUserFollowEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserFollowEndpointDtoModel";

export default function SnsUserFollowCrudView() {
  return (
      <AutoGrid
          service={SnsUserFollowEndpoint}
          model={SnsUserFollowEndpointDtoModel}
      />
  );
}
