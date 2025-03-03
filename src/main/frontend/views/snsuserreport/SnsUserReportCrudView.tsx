import {SnsUserReportEndpoint} from 'Frontend/generated/endpoints'

import {AutoCrud} from '@vaadin/hilla-react-crud';
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import SnsUserReportEndpointDtoModel
    from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsUserReportEndpointDtoModel";
export default function SnsUserReportCrudView() {
  return (
      <>
      <AutoCrud
          service={SnsUserReportEndpoint}
          model={SnsUserReportEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
            visibleFields:[
                'reportReason',
                'userReportReasonType',
                'userReportStatus',
                'lastUpdatedAt',
                'createdAt'
            ]
            }}
      />
      </>
  );
}
