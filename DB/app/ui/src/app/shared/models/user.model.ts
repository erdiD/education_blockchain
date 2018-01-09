import {IUser as IStandardUser} from "../../standard-components/interfaces/IUser";
import {IUser as IMarketUser} from "../../market/interfaces/IUser";
import {IUser as IProjectUser} from "../../project/interfaces/IUser";
import {IUser as IDemandUser} from "../../demand/interfaces/IUser";
import {IUser as IContractUser} from "../../contract/interfaces/IUser";

import {isNullOrUndefined} from "util";
export class User implements IStandardUser, IMarketUser, IProjectUser, IDemandUser, IContractUser {

  id: string;
  lastName: string;
  firstName: string;
  username: string;
  avatarImageSrc: string;
  canCreateDemand:boolean;

  public static fromJSON (json: any | Promise<any>) {
    if (isNullOrUndefined(json)) {
      return undefined;
    }

    let user = new User();
    user.id = json.id;
    user.lastName = json.lastName;
    user.firstName = json.firstName;
    user.username = json.username;
    user.avatarImageSrc = json.avatarImageSrc;
    user.canCreateDemand = <boolean>json.canCreateDemand;

    return user;
  }
}
