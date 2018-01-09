import {Injectable} from "@angular/core";

@Injectable()
export class LoggingService {

  public DEBUG_MODE_OFF: string = "DBILV_APP_DEBUGMODE_OFF";
  public DEBUG_MODE_ON: string = "DBILV_APP_DEBUGMODE_ON";

  public DEBUG_MODE: string = this.DEBUG_MODE_ON;


  public log (message: any): void {
    if (this.DEBUG_MODE === this.DEBUG_MODE_OFF) {
      return;
    }
    ;
    console.log(">>>>>>>>>>>> LOG >>>>>>>>>>>>>>>>>>");
    console.log(message);
    console.log("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
  }

  public error (message: any): void {
    if (this.DEBUG_MODE === this.DEBUG_MODE_OFF) {
      return;
    }
    ;
    console.log(">>>>>>>>>>>> ERROR >>>>>>>>>>>>>>>>");
    console.error(message);
    console.log("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
  }
}
