import {IUser} from "./IUser";
import {IOrganization} from "./IOrganization";
import {IDemand} from "./IDemand";
import {IOffer} from "./IOffer";
import {IContract} from "./IContract";
import {IDelivery} from "./IDelivery";

export interface IFullProject{
   id?: string;
   task: string;
   demand: IDemand;
   offers: IOffer[];
   contract: IContract;
   delivery: IDelivery;
}
