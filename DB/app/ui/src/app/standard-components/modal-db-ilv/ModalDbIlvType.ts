export class ModalDbIlvType {

  public userInputText:string;

  constructor (public iconUrl: string = null,
               public titleText: string = null,
               public acceptButtonText: string = null,
               public dialogText: string = null,
               public commentFieldText: string = null,
               public hideAbortButton:boolean = false) {
  }
}
