import {SnsUserMessageReactionEndpoint} from 'Frontend/generated/endpoints'

import { AutoCrud } from '@vaadin/hilla-react-crud';
import SnsUserMessageReactionEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserMessageReactionEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";

export default function SnsUserMessageReactionCrudView() {
  return (
      <AutoCrud
          service={SnsUserMessageReactionEndpoint}
          model={SnsUserMessageReactionEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
      }}/>
  );
}
