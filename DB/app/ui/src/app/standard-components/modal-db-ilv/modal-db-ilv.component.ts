import {Component, Inject, OnInit} from '@angular/core';
import {MD_DIALOG_DATA, MdDialogRef} from "@angular/material";
import {LoggingService} from "../../shared/services/logging.service";
import {ModalDbIlvType} from "./ModalDbIlvType";
import {AuthService} from "../../shared/services/auth.service";

@Component({
  selector: 'app-modal-db-ilv',
  templateUrl: './modal-db-ilv.component.html',
  styleUrls: ['./modal-db-ilv.component.scss'],
  providers: [LoggingService]
})
export class ModalDbIlvComponent implements OnInit {

  public static ACCEPT: string = 'ACCEPT';
  public static REJECT: string = 'REJECT';

  public static WRONG_PASSWORD_OR_USERNAME: ModalDbIlvType = new ModalDbIlvType('assets/img/icons/icon_Denied.png',
                                                                              'Invalid password or username',
                                                                              'ok',
    'Ihr Versuch, sich einzuloggen ist leider fehlgeschlagen. Bitte überprüfen Sie Ihre eingegebenen Daten und versuchen Sie es erneut.',
                                                                              null,
                                                                              true);

  public static MODAL_TYPE_SUBMIT_DEMAND: ModalDbIlvType = new ModalDbIlvType('assets/img/icons/smallIcons_64x64/icon_small_check.png',
                                                                              'Bedarf veröffentlichen',
                                                                              'Veröffentlichen',
                                                                              'Hiermit wird der erstellte Bedarf veröffentlicht.',
                                                                              null);

  public static MODAL_TYPE_REJECT_DEMAND: ModalDbIlvType = new ModalDbIlvType('assets/img/icons/icon_Denied.png',
                                                                             'Bedarf ablehnen',
                                                                             'Ablehnen',
                                                                              null,
                                                                             'Begründung (optional)');


  public static MODAL_TYPE_ACCEPT_DEMAND: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/smallIcons_64x64/icon_small_check.png',
                                                                              'Bedarf akzeptieren',
                                                                              'Akzeptieren',
                                                                              'Accept Demand description Text',
                                                                              null);

  public static MODAL_TYPE_REVOKE_DEMAND: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/icon_Denied.png',
                                                                              'Bedarf aufheben',
                                                                              'Aufheben',
                                                                              null,
                                                                              'Mit Kommentar (optional)');

  public static MODAL_TYPE_RESIGN_DEMAND: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/icon_Denied.png',
                                                                              'Bedarf abbrechen',
                                                                              'Bedarf abbrechen',
                                                                               null,
                                                                               'Mit Kommentar (optional)');

  public static MODAL_TYPE_BLOCK_DEMAND: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/icon_Denied.png',
                                                                              'Bedarf blockieren',
                                                                              'Blockieren',
                                                                              null,
                                                                              'Begründung (optional)');

  public static MODAL_TYPE_CLOSE_DEMAND: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/icon_Denied.png',
                                                                              'Bedarf abbrechen',
                                                                              'Schließen',
                                                                              'Der Bedarf wird abgebrochen. Bitte beachten Sie, dass der Bedarf anschließend nicht wiedereröffnet werden kann.',
                                                                              null);

  public static MODAL_TYPE_COMPLETE_DEMAND: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/smallIcons_64x64/icon_small_check.png',
                                                                              'Bedarf abschließen',
                                                                              'Bedarf abschließen',
                                                                              'Complete Demand description Text: Demand will be completed',
                                                                              null);

  public static MODAL_TYPE_APPROVE_DEMAND: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/smallIcons_64x64/icon_small_check.png',
                                                                              'Bedarf genehmigen',
                                                                              'Genehmigen',
                                                                               'Approve Demand Description Text',
                                                                              null);

  public static MODAL_TYPE_SUBMIT_OFFER: ModalDbIlvType = new ModalDbIlvType('assets/img/icons/smallIcons_64x64/icon_small_check.png',
                                                                              'Angebot übermitteln/veröffentlichen',
                                                                              'Übermitteln',
                                                                              'Hiermit übermitteln Sie ihr Angebot. Ihr Angebot kann dann vom Empfänger eingesehen werden.',
                                                                              null);


  public static MODAL_TYPE_ACCEPT_OFFER: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/smallIcons_64x64/icon_small_check.png',
                                                                              'Angebot annehmen',
                                                                              'Annehmen',
                                                                              'Hiermit bestätigen Sie das vorliegende Angebot. Diese Entscheidung kann nicht Rückgängig gemacht werden.',
                                                                              null);

  public static MODAL_TYPE_REJECT_OFFER: ModalDbIlvType = new ModalDbIlvType('assets/img/icons/icon_Denied.png',
                                                                              'Angebot ablehnen',
                                                                              'Ablehnen',
                                                                              null,
                                                                              'Begründung (optional)');

  public static MODAL_TYPE_APPROVE_OFFER: ModalDbIlvType = new ModalDbIlvType('assets/img/icons/smallIcons_64x64/icon_small_check.png',
                                                                              'Angebot genehmigen',
                                                                              'Genehmigen',
                                                                              'Hiermit erteilen Sie eine verbindliche Genehmigung des Angebots.',
                                                                              null);

  public static MODAL_TYPE_APPROVE_OFFER_INTERNAL: ModalDbIlvType = new ModalDbIlvType('assets/img/icons/smallIcons_64x64/icon_small_check.png',
                                                                              'Angebot genehmigen',
                                                                              'Genehmigen',
                                                                              'Hiermit erteilen Sie eine verbindliche Genehmigung des Angebots.',
                                                                              null);

  public static MODAL_TYPE_CLOSE_OFFER: ModalDbIlvType = new ModalDbIlvType('assets/img/icons/smallIcons_64x64/icon_small_stop.png',
                                                                            'Angebot schließen',
                                                                            'Schließen',
                                                                            'Hiermit schließen Sie das Angebots. Dieses Angebot wird nach dem schließen entfernt. Dieser Vorgang kann nicht rückgän gig gemacht werden.',
                                                                            null);

  public static MODAL_TYPE_RESIGN_OFFER: ModalDbIlvType = new ModalDbIlvType('assets/img/icons/smallIcons_64x64/icon_small_stop.png',
                                                                            'Angebot abbrechen',
                                                                             'Angebot abbrechen',
                                                                              null,
                                                                              'Mit Kommentar (optional)');

  public static MODAL_TYPE_COMPLETE_OFFER: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/smallIcons_64x64/icon_small_check.png',
                                                                              'Angebot abschließen',
                                                                              'Angebot abschließen',
                                                                              'Complete Demand description Text: Demand will be completed',
                                                                              null);

  public static MODAL_TYPE_REVOKE_OFFER: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/icon_Denied.png',
                                                                              'Angebot aufheben',
                                                                              'Angebot aufheben',
                                                                               null,
                                                                              'Begründung (optional)');

  public static MODAL_TYPE_ACTIVATE_OFFER: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/smallIcons_64x64/icon_small_questionmark.png',
                                                                              'Angebot reaktivieren',
                                                                              'Angebot reaktivieren',
                                                                              'Hiermit reaktivieren Sie das vorliegende Angebot. Die Gültigkeit des Angebots wird somit um weitere XX Monate verlängert.',
                                                                              null);

  public static MODAL_TYPE_REVIEW_OFFER: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/smallIcons_64x64/icon_small_questionmark.png',
                                                                              'Freigabe anfragen',
                                                                              'Freigabe anfragen',
                                                                              'Hiermit legen Sie ihr Angebot dem zuständigen Genehmiger vor. Sobald dieser ihr Angebot genehmigt hat, können Sie es abschicken.',
                                                                              null);


  public static MODAL_TYPE_ASK_FOR_APPROVAL: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/smallIcons_64x64/icon_small_questionmark.png',
                                                                              'Genehmigung anfragen',
                                                                              'Anfragen',
                                                                              null,
                                                                              'Mit Kommentar (optional)');

  public static MODAL_TYPE_SIGN_CONTRACT: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/smallIcons_64x64/icon_small_sign.png',
                                                                              'Vertrag unterzeichnen',
                                                                              'Verbindlich unterzeichnen',
                                                                              'Hiermit unterzeichnen Sie den Vertrag. Bitte beachten Sie, dass diese Unterschrift rechtlich verbindlich ist.',
                                                                              null);
  public static MODAL_TYPE_REJECT_CONTRACT: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/smallIcons_64x64/icon_small_sign.png',
                                                                              'Vertrag ablehnen',
                                                                              'Ablehnen',
                                                                              null,
                                                                              'Mit Kommentar (optional)');


  public static LOGOUT: ModalDbIlvType = new ModalDbIlvType( 'assets/img/icons/icon_logout.png',
                                                                                  'Abmelden',
                                                                                  'Logout',
                                                                                  'Hiermit verlassen Sie die Applikation. Zur erneuten Anmeldung wird Ihr Passwort benötigt.',
                                                                                  null);


  public modalType: ModalDbIlvType;

  constructor (private dialogRef: MdDialogRef<ModalDbIlvComponent>,
               @Inject(MD_DIALOG_DATA) public data: any,
               private authService: AuthService,
               private logService: LoggingService) {
  }

  public ngOnInit (): void {
    this.modalType = this.data.modalType;
  }

  public onCloseModalClick (e: Event): void {
    this.dialogRef.close({ action: ModalDbIlvComponent.REJECT, message: this.modalType.userInputText });
  }

  public onAcceptModalClick (e: Event): void {
    this.dialogRef.close({ action: ModalDbIlvComponent.ACCEPT, message: this.modalType.userInputText });
  }
}




