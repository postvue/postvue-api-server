import {AdminServiceAdjustmentEndpoint} from 'Frontend/generated/endpoints'

import {AutoCrud} from '@vaadin/hilla-react-crud';

import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";

import 'swiper/css';
import 'swiper/css/pagination';
import AdminServiceAdjustmentEndpointDtoModel
    from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/AdminServiceAdjustmentEndpointDtoModel";

export default function AdminServiceAdjustmentCrudView() {
    return (
      <>
      <AutoCrud
          service={AdminServiceAdjustmentEndpoint}
          model={AdminServiceAdjustmentEndpointDtoModel}
          formProps={{
              onDeleteError: handleOnDeleteError,
              onSubmitError:handleOnSubmitError,
          }}/>
      </>
  );
}