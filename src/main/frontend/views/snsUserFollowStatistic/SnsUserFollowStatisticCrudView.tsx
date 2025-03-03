import { SnsUserFollowStatisticEndpoint} from 'Frontend/generated/endpoints'

import { AutoGrid} from '@vaadin/hilla-react-crud';
import SnsUserFollowStatisticEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserFollowStatisticEndpointDtoModel";

export default function SnsUserFollowStatisticCrudView() {
  return (
      <AutoGrid service={SnsUserFollowStatisticEndpoint} model={SnsUserFollowStatisticEndpointDtoModel}  style={{height:"100%"}}/>
  );
}
