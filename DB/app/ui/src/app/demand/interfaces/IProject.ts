import {IOrganization} from "./IOrganization";
import {IUser} from "./IUser";
import {IDemand} from "./IDemand";

export interface IProject {
   id: string;
   state: string;
   task: string;
   name: string;
   budget: string;
   role: string;
   ownerOrganization: IOrganization;
   creator: IUser;
   demand: IDemand;
   isDraft: boolean;
}
