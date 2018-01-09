export class StatusNotification {
  public type: string;
  public text: string;
  public icon: string;
  public additionalString: string;


  //@TODO NA: Refactor to Interface
  constructor (type: string, text: string, iconURL:string) {
    this.type = type;
    this.text = text;
    this.icon = iconURL;
  }

}
