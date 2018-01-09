import {EventEmitter, Injectable} from '@angular/core';

@Injectable()
export class FileUploadAreaService {

  public static UPLOAD_ALL:string = "UPLOAD_ALL";
  public static UPLOAD_ALL_COMPLETE:string = "UPLOAD_ALL_COMPLETE";
  public static SET_API_ENDPOINT:string = "SET_API_ENDPOINT";
  public static AFTER_ADDING_FILE:string = "AFTER_ADDING_FILE";
  public static AFTER_REMOVING_ALL_FILES:string = "AFTER_REMOVING_ALL_FILES";

  public dispatcher: EventEmitter<any> = new EventEmitter();
  public apiEndPoint:string;

  constructor() { }

  public uploadAllFilesInQueue():void{
    console.log("[FileUploadAreaService] - uploadAllFilesInQueue");
    this.dispatcher.emit(FileUploadAreaService.UPLOAD_ALL);
  }

  public uploadAllFilesInQueueComplete():void{
    console.log("[FileUploadAreaService] - uploadAllFilesInQueueComplete");
    this.dispatcher.emit(FileUploadAreaService.UPLOAD_ALL_COMPLETE);
  }

  public setApiEndPoint(url:string):void{
    console.log("[FileUploadAreaService] - setApiEndpoint", url);
    this.apiEndPoint = url;
    this.dispatcher.emit(FileUploadAreaService.SET_API_ENDPOINT);
  }

  public afterAddingFile():void{
    console.log("[FileUploadAreaService] - afterAddingFile");
    this.dispatcher.emit(FileUploadAreaService.AFTER_ADDING_FILE);
  }

  public afterRemovingAllFiles(): void{
    console.log("[FileUploadAreaService] - afterRemovingAllFiles");
    this.dispatcher.emit(FileUploadAreaService.AFTER_REMOVING_ALL_FILES);
  }

}
