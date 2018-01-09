import {IProjectEntity} from "./IProjectEntity";
import {IUser} from "./IUser";
import {IDemand} from "./IDemand";
import {IOffer} from "./IOffer";

export interface IContract extends IProjectEntity{
  demand: IDemand;
  offer: IOffer;
}
