import {IHistoryEntry} from "./IHistoryEntry";

export interface IHistorySection{
   action: string;
   title: string;
   iconURL: string;
   entries: IHistoryEntry[];
}
