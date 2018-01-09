import {IOrganization} from "./IOrganization";
import {IUser} from "./IUser";

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
   endDate: Date;

   messageBoardUrl: string;
   availableActions: string;
   state: string;
   action: string;

   toJSON(): Object;
}
