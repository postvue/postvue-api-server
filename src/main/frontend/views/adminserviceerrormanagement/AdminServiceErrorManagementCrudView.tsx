import {AdminServiceErrorManagementEndpoint} from 'Frontend/generated/endpoints'

import {AutoCrud} from '@vaadin/hilla-react-crud';

import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";

import 'swiper/css';
import 'swiper/css/pagination';
import AdminServiceErrorManagementEndpointDtoModel
    from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/AdminServiceErrorManagementEndpointDtoModel";

export default function AdminServiceErrorManagementCrudView() {
    return (
      <>
      <AutoCrud
          service={AdminServiceErrorManagementEndpoint}
          model={AdminServiceErrorManagementEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
              onDeleteError: handleOnDeleteError,
              onSubmitError:handleOnSubmitError,
          }}/>
      </>
  );
}