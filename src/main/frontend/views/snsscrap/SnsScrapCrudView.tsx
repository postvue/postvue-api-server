import {SnsScrapEndpoint} from 'Frontend/generated/endpoints'

import {AutoCrud} from '@vaadin/hilla-react-crud';
import SnsScrapEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsScrapEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
export default function SnsScrapCrudView() {
  return (
      <>
      <AutoCrud
          service={SnsScrapEndpoint}
          model={SnsScrapEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
              onDeleteError: handleOnDeleteError,
              onSubmitError:handleOnSubmitError,
              visibleFields:[
              'snsUser_id',
              'snsPost_id',
              'snsScrapBoard_id',
              'createdAt',
              'deletedAt'
              ]
      }}/>
      </>
  );
}
