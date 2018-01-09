import {IHistoryEntry as IProjectHistoryEntry} from "../../project/interfaces/IHistoryEntry";
import {User} from "./user.model";
import {HistorySection} from "./history-section.model";
import {isNullOrUndefined} from "util";

export class HistoryEntry implements IProjectHistoryEntry {
  date: Date;
  author: User;
  actionText: string;
  customCommentText: string;
  transactionId: string;
  blockId: string;

  public static fromJSON (data: any, action: string) {
    let entry: HistoryEntry = new HistoryEntry();
    entry.author = User.fromJSON(data.author);
    entry.actionText = HistorySection.getActionTextFromAction(action);

    if (!isNullOrUndefined(data.transactionId)) {
      entry.transactionId = data.transactionId;
    }

    if (!isNullOrUndefined(data.blockId)) {
      entry.blockId = data.blockId;
    }

    if (!isNullOrUndefined(data.date)) {
      if (String(data.date).indexOf(".") >= 0){
        // already formated date
        entry.date = HistoryEntry.getDateFromFormattedString(data.date);
      } else {
        // unix timestamp
        entry.date = new Date(data.date);
      }
    }

    if (!isNullOrUndefined(data.customCommentText)) {
      entry.customCommentText = data.customCommentText;
    }

    return entry;
  }


  public static getDateFromFormattedString (dateString: string): Date {

    let ser = dateString.split('.');
    let date = new Date();

    date.setDate(Number(ser[0]));
    date.setMonth(Number(ser[1])-1);
    date.setFullYear(Number(ser[2]));

    return date;
  }

}
