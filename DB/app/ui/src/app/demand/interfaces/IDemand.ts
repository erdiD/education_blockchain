import {IProjectEntity} from "./IProjectEntity";
import {IOffer} from "../../project/interfaces/IOffer";

export interface IDemand extends IProjectEntity{
   budget: string;
   priority: string;
   name: string;
   targetAccount:string;
   offers?: IOffer[];

   getEndDate():string;
}
