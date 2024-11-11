import {SnsPostReportEndpoint} from 'Frontend/generated/endpoints'

import {AutoCrud} from '@vaadin/hilla-react-crud';
import SnsPostReportEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsPostReportEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
export default function SnsPostReportCrudView() {
  return (
      <>
      <AutoCrud
          service={SnsPostReportEndpoint}
          model={SnsPostReportEndpointDtoModel}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
            visibleFields:[
                'createdAt']
            }}
      />
      </>
  );
}
