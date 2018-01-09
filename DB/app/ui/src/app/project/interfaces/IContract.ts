import {IProjectEntity} from "./IProjectEntity";
import {IUser} from "./IUser";
import {IDemand} from "./IDemand";
import {IOffer} from "./IOffer";
import {IHistorySection} from "./IHistorySection";

export interface IContract extends IProjectEntity{
  demand: IDemand;
  offer: IOffer;
}
