import {IUser} from "./IUser";

export interface IHistoryEntry{
   date: Date;
   author: IUser;
   actionText: string;
   customCommentText: string;
   transactionId:string;
   blockId:string;
}
