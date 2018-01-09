import {isNullOrUndefined} from "util";
import {IOrganization as IMarketOrganization} from "../../market/interfaces/IOrganization";
import {IOrganization as IProjectOrganization} from "../../project/interfaces/IOrganization";
import {IOrganization as IDemandOrganization} from "../../demand/interfaces/IOrganization";
import {IOrganization as IContractOrganization} from "../../contract/interfaces/IOrganization";


export class Organization implements IMarketOrganization, IProjectOrganization, IDemandOrganization, IContractOrganization {
  public id: string;
  public name: string;

  public static fromJSON (data: any): Organization {
    if(isNullOrUndefined(data)) {
      return null;
    }
    let organization = new Organization();
    organization.id = data.id;
    organization.name = data.name;

    return organization;
  }
}
