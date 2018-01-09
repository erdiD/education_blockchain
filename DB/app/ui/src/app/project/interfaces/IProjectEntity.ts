import {IOrganization} from "./IOrganization";
import {IUser} from "./IUser";
import {IHistorySection} from "./IHistorySection";

export interface IProjectEntity{
   type: string;
   typeIcon: string;
   typeTitleText: string;

   id: string;
   projectID: string;
   creator: IUser;
   ownerOrg: IOrganization;
   creationTime: Date;
   description: string;
   attachments: string[];
   progressHistory: IHistorySection[];
   endDate: Date;

   messageBoardUrl: string;
   availableActions: string;
   state: string;
   action: string;

   toJSON(): Object;
}
