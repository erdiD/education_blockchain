import {IHistorySection as IProjectHistorySection} from "../../project/interfaces/IHistorySection";
import {HistoryEntry} from "./history-entry.model";
import {Actions} from "../types/action.types";

export class HistorySection implements IProjectHistorySection {
  action: string;
  title: string;
  iconURL: string;
  entries: HistoryEntry[];

  constructor (action: string, entries: HistoryEntry[]) {
    this.action = action;
    this.title = HistorySection.getTitleFromAction(action);
    this.iconURL = HistorySection.getIconURLFromAction(action);
    this.entries = entries;
  }

  public static fromJSON (data: any): HistorySection {

    let entries: HistoryEntry[] = [];
    data.entries.forEach((entryData: any) => {
      entries.push(HistoryEntry.fromJSON(entryData, data.action));
    });

    return new HistorySection(data.action, entries);
  }


  //TODO: NA - Move logic for "translation" etc to Translation-Machanism
  public static getTitleFromAction (action: string): string {

    switch (action) {
      case Actions.UPDATE:
        return "Änderung";

      /*DEMAND ACTIONS*/
      case Actions.CREATE_DEMAND:
        return "Neuer Bedarf";
      case Actions.EDIT_DEMAND:
        return "Änderungen im Bedarf";
      case Actions.OPEN_DEMAND:
        return "Neuer Bedarf";
      case Actions.CLOSE_DEMAND:
        return "Bedarf abgebrochen";
      case Actions.SUBMIT_DEMAND:
        return "Bedarf veröffentlicht";
      case Actions.BLOCK_DEMAND:
        return "Bedarf blockiert";
      case Actions.APPROVE_DEMAND:
        return "Genehmigung";
      case Actions.REJECT_DEMAND:
        return "Zurückweisung";
      case Actions.CORRECT_DEMAND:
        return "Korrektur";
      case Actions.ACCEPT_DEMAND:
        return "Bedarf akzeptiert";
      case Actions.REVOKE_DEMAND:
        return "Bedarf aufgehoben";
      case Actions.COMPLETE_DEMAND:
        return "Bedarf abgeschlossen";
      case Actions.RESIGN_DEMAND:
        return "Bedarf aufgelöst";

      /*OFFER ACTIONS*/
      case Actions.MAKE_OFFER:
        return "Angebot angelegt";
      case Actions.EDIT_OFFER:
        return "Änderungen im Angebot";
      case Actions.OPEN_OFFER:
        return "Neues Angebot";
      case Actions.SUBMIT_OFFER:
        return "Angebot übermittelt";
      case Actions.ACCEPT_OFFER:
        return "Angebot akzeptiert";
      case Actions.REJECT_OFFER:
        return "Zurückweisung";
      case Actions.APPROVE_OFFER:
        return "Genehmigung";
      case Actions.APPROVE_OFFER_INTERNAL:
        return "Interne Genehmigung";
      case Actions.REVIEW_OFFER:
        return "Review";
      case Actions.CLOSE_OFFER:
        return "Angebot geschlossen";
      case Actions.RESIGN_OFFER:
        return "Angebot aufgelöst";
      case Actions.REWORK_OFFER:
        return "Überarbeitet";
      case Actions.COMPLETE_OFFER:
        return "Abgeschlossen";
      case Actions.REVOKE_OFFER:
        return "Angebot aufgehoben";
      case Actions.EXPIRE:
        return "Ablauftermin überschritten";
      case Actions.ACTIVATE_OFFER:
        return "Ablauftermin verlängert";
      case Actions.ASK_FOR_APPROVAL:
        return "Genehmigung";

      /*CONTRACT ACTIONS*/
      case Actions.SIGN_CONTRACT:
        return "Vertragsabschluss";
      case Actions.REJECT_CONTRACT:
        return "Vertragsabschluss";

      default:
        console.error("Unknnown Action for History Section / action=" + action);
        return "UNKNOWN TYPE";
    }
  }

  public static getIconURLFromAction (action: string): string {
    switch (action) {

      case Actions.UPDATE:
        return "assets/img/icons/smallIcons_64x64/icon_small_edit.png";

      /*DEMAND ACTIONS*/
      case Actions.CREATE_DEMAND:
        return "assets/img/icons/icon_Bedarf.png";
      case Actions.EDIT_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_edit.png";
      case Actions.OPEN_DEMAND:
        return "assets/img/icons/icon_Bedarf.png";
      case Actions.CLOSE_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_close.png";
      case Actions.SUBMIT_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";
      case Actions.BLOCK_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_block.png";
      case Actions.APPROVE_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";
      case Actions.REJECT_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_stop.png";
      case Actions.CORRECT_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_edit.png";
      case Actions.ACCEPT_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";
      case Actions.REVOKE_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_stop.png";
      case Actions.COMPLETE_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";
      case Actions.RESIGN_DEMAND:
        return "assets/img/icons/smallIcons_64x64/icon_small_stop.png";

      /*OFFER ACTIONS*/
      case Actions.MAKE_OFFER:
        return "assets/img/icons/icon_Angebot.png";
      case Actions.EDIT_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_edit.png";
      case Actions.OPEN_OFFER:
        return "assets/img/icons/icon_Angebot.png";
      case Actions.SUBMIT_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";
      case Actions.ACCEPT_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";
      case Actions.REJECT_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_stop.png";
      case Actions.APPROVE_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";
      case Actions.APPROVE_OFFER_INTERNAL:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";
      case Actions.REVIEW_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";
      case Actions.CLOSE_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_stop.png";
      case Actions.RESIGN_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_stop.png";
      case Actions.REWORK_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_edit.png";
      case Actions.COMPLETE_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";
      case Actions.REVOKE_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_stop.png";
      case Actions.EXPIRE:
        return "assets/img/icons/smallIcons_64x64/icon_small_stop.png";
      case Actions.ACTIVATE_OFFER:
        return "assets/img/icons/smallIcons_64x64/icon_small_check.png";

      case Actions.ASK_FOR_APPROVAL:
        return "assets/img/icons/smallIcons_64x64/icon_small_questionmark.png";


      /*CONTRACT ACTIONS*/
      case Actions.SIGN_CONTRACT:
        return "assets/img/icons/smallIcons_64x64/icon_small_sign.png";
      case Actions.REJECT_CONTRACT:
        return "assets/img/icons/smallIcons_64x64/icon_small_stop.png";

      default:
        console.error("Unknnown Action for History Section / action=" + action);
        return "assets/img/icons/smallIcons_64x64/icon_small_questionmark.png";
    }
  }

  public static getActionTextFromAction (action: string): string {


    switch (action) {

      case Actions.UPDATE:
        return "hat eine Änderung vorgenommen";

      /*DEMAND ACTIONS*/
      case Actions.CREATE_DEMAND:
        return "hat einen neuen Bedarf erstellt.";
      case Actions.EDIT_DEMAND:
        return "hat eine Änderung vorgenommen.";
      case Actions.OPEN_DEMAND:
        return "hat den Bedarf angelegt.";
      case Actions.CLOSE_DEMAND:
        return "hat den Bedarf geschlossen.";
      case Actions.SUBMIT_DEMAND:
        return "hat den Bedarf veröffentlicht.";
      case Actions.BLOCK_DEMAND:
        return "hat den Bedarf blockiert.";
      case Actions.APPROVE_DEMAND:
        return "hat den Bedarf genehmigt.";
      case Actions.REJECT_DEMAND:
        return "hat den Bedarf zurückgewiesen.";
      case Actions.CORRECT_DEMAND:
        return "hat eine Überarbeitung vorgenommen.";
      case Actions.ACCEPT_DEMAND:
        return "hat den Bedarf akzeptiert.";
      case Actions.REVOKE_DEMAND:
        return "hat den Bedarf aufgehoben.";
      case Actions.COMPLETE_DEMAND:
        return "hat den Bedarf abgeschlossen.";
      case Actions.RESIGN_DEMAND:
        return "hat den Bedarf aufgelöst.";

      /*OFFER ACTIONS*/
      case Actions.MAKE_OFFER:
        return "hat eine Änderungen vorgenommen.";
      case Actions.EDIT_OFFER:
        return "hat eine Änderungen vorgenommen.";
      case Actions.OPEN_OFFER:
        return "hat das Angebot angelegt.";
      case Actions.SUBMIT_OFFER:
        return "hat das Angebot übermittelt.";
      case Actions.ACCEPT_OFFER:
        return "hat das Angebot akzeptiert.";
      case Actions.REJECT_OFFER:
        return "hat das Angebot zurückgewiesen.";
      case Actions.APPROVE_OFFER:
        return "hat das Angebot genehmigt.";
      case Actions.APPROVE_OFFER_INTERNAL:
        return "hat das Angebot genehmigt.";
      case Actions.REVIEW_OFFER:
        return "hat das Angebot zur Genehmigung weitergereicht.";
      case Actions.CLOSE_OFFER:
        return "hat das Angebot geschlossen.";
      case Actions.RESIGN_OFFER:
        return "hat das Angebot aufgelöst.";
      case Actions.REWORK_OFFER:
        return "hat das Angebot überarbeitet.";
      case Actions.COMPLETE_OFFER:
        return "hat das Angebot abgeschlossen.";
      case Actions.REVOKE_OFFER:
        return "hat das Angebot aufgehoben.";
      case Actions.EXPIRE:
        return "Der Ablauftermin dieses Elements ist überschritten.";
      case Actions.ACTIVATE_OFFER:
        return "Der Ablauftermin dieses Elements wurde verschoben.";

      case Actions.ASK_FOR_APPROVAL:
        return "Genehmigung";

      /*OFFER ACTIONS*/
      case Actions.SIGN_CONTRACT:
        return "hat den Vertrag unterzeichnet.";
      case Actions.REJECT_CONTRACT:
        return "hat den Vertrag abgeleht.";

      default:
        console.error("Unknnown Action for History Section / action=" + action);
        return "UNKNOWN TYPE";
    }
  }

}
