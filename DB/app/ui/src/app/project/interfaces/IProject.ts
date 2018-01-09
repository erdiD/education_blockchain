import {IUser} from "./IUser";
import {IOrganization} from "./IOrganization";
import {IDemand} from "./IDemand";
import {IOffer} from "./IOffer";

export interface IProject{
   id: string;
   state: string;
   task: string;
   name: string;
   budget: string;
   role:string;
   ownerOrganization: IOrganization;
   creator: IUser;
   demand: IDemand;
   offers: IOffer[];


   isDraft: boolean;
}
