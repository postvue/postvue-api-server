import { SnsTagPostEndpoint} from 'Frontend/generated/endpoints'

import {AutoGrid} from '@vaadin/hilla-react-crud';
import SnsTagPostEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsTagPostEndpointDtoModel";

export default function SnsTagPostCrudView() {
  return (
      <AutoGrid
          service={SnsTagPostEndpoint}
          model={SnsTagPostEndpointDtoModel}
          style={{height:"100%"}}
      />
  );
}
