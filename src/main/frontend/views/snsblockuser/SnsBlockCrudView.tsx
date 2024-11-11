import {AutoGrid} from "@vaadin/hilla-react-crud";
import {SnsBlockUserEndpoint} from "Frontend/generated/endpoints";
import SnsBlockUserEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsBlockUserEndpointDtoModel";

export default function SnsBlockUserCrudView() {

  return (
      <AutoGrid service={SnsBlockUserEndpoint} model={SnsBlockUserEndpointDtoModel}
      />
  );
}