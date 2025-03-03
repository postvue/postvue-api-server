import { SnsScrapBoardEndpoint} from 'Frontend/generated/endpoints'

import { AutoCrud } from '@vaadin/hilla-react-crud';
import SnsScrapBoardEndpointDtoModel
  from "Frontend/generated/com/postvue/feelogserver/endpoint/dto/SnsScrapBoardEndpointDtoModel";
import {handleOnDeleteError, handleOnSubmitError} from "Frontend/components/utils/ErrorHandle";
import ScrapTargetAudience_1
  from "Frontend/generated/com/postvue/feelogserver/domain/snsscrapboard/vo/ScrapTargetAudience";

export default function SnsScrapBoardCrudView() {
  return (
      <AutoCrud
          service={SnsScrapBoardEndpoint}
          model={SnsScrapBoardEndpointDtoModel}
          style={{height:"100%"}}
          formProps={{
            onDeleteError: handleOnDeleteError,
            onSubmitError:handleOnSubmitError,
            visibleFields:[
            'snsUser_id',
            'scrapName',
            'targetAudience',
            'createdAt',
            'deletedAt',
            'lastUpdatedAt',
            'lastUpdatedBy'
            ]
      }}/>
  );
}
