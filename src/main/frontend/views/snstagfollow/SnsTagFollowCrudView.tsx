import {SnsTagFollowEndpoint} from 'Frontend/generated/endpoints'

import { AutoGrid} from '@vaadin/hilla-react-crud';
import SnsTagFollowEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsTagFollowEndpointDtoModel";

export default function SnsTagFollowCrudView() {
  return (
      <AutoGrid service={SnsTagFollowEndpoint} model={SnsTagFollowEndpointDtoModel}  style={{height:"100%"}}/>
  );
}
