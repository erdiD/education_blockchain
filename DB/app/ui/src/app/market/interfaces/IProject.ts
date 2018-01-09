import {IOrganization} from "./IOrganization";
import {IUser} from "./IUser";
export interface IProject{
  id: string;
  state: string;
  task: string;
  name: string;
  budget: string;
  role:string;
  ownerOrganization: IOrganization;
  creator: IUser;
  lastModified:Date;

  isDraft: boolean;
}
