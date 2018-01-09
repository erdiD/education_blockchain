
import {IOffer} from "./IOffer";
import {IProjectEntity} from "./IProjectEntity";

export interface IDemand extends IProjectEntity{
   budget: string;
   priority: string;
   name: string;
   offers: IOffer[];
   targetAccount:string;
}
