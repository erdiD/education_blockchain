import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FileItem, FileUploader} from "ng2-file-upload";
import {FileUploadAreaService} from "./file-upload-area.service";
import {IProjectEntity} from "../interfaces/IProjectEntity";
import {ApiService} from "../../shared/services/api.service";
import {LoggingService} from "../../shared/services/logging.service";
import {DemandService} from "../../demand/shared/demand.service";
import {OfferService} from "../../offer/shared/offer.service";


@Component({
  selector: 'app-file-upload-area',
  templateUrl: './file-upload-area.component.html',
  styleUrls: ['./file-upload-area.component.scss']
})
export class FileUploadAreaComponent implements OnInit, OnDestroy {

  public attachmentUploader: FileUploader;
  @Input() projectEntity: IProjectEntity;
  @Input() apiEndPoint: string;

  constructor (private fileUploadService: FileUploadAreaService,
               private apiService: ApiService,
               private demandService: DemandService,
               private offerService: OfferService,
               private logService: LoggingService) {
  }

  ngOnInit () {
    this.attachmentUploader = new FileUploader({});
    this.setApiEndPoint(this.apiEndPoint);

    this.fileUploadService.dispatcher.subscribe(event => {
      if (event === FileUploadAreaService.UPLOAD_ALL) {
        this.attachmentUploader.queue.forEach( file => file.alias = "attachment");
        this.attachmentUploader.getNotUploadedItems().length > 0 ? this.uploadAllFiles() : this.onUploadAllComplete();
      } else if(event === FileUploadAreaService.SET_API_ENDPOINT){
        this.setApiEndPoint(this.fileUploadService.apiEndPoint);
      }
    });

    this.attachmentUploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
      if (status === 200) {
        console.log("Fileupload successfull for Item: ", item);
      } else {
        console.error("Error while uploading:", item, status, response);
      }
      if ( this.attachmentUploader.getNotUploadedItems().length === 0 ){
        this.onUploadAllComplete();
        this.attachmentUploader.clearQueue();
      }
    }

    this.attachmentUploader.onAfterAddingFile = () => {
      if (this.attachmentUploader.queue.length > 0){
        this.fileUploadService.afterAddingFile();
      }
    };

  }

  public uploadAllFiles (): void {
    var items = this.attachmentUploader.getNotUploadedItems().filter(function (item) { return !item.isUploading; });
    if (!items.length) {
      this.onUploadAllComplete();
      return;
    }
    items.map(function (item) { return item._prepareToUploading(); });
    items[0].upload();
  }

  public setApiEndPoint (url: string): void {
    this.apiEndPoint = url;
    this.attachmentUploader.setOptions({url: this.apiEndPoint});
  }

  public onUploadAllComplete (): void {
    this.fileUploadService.uploadAllFilesInQueueComplete();
  }

  public onItemInformationClick (target): void {
    this.getItemInformation(target);
  }

  public getItemInformation (item: any): void {
    //console.log(item);
  }

  public deleteFileFromUploaderQueue (file: FileItem): void {
    this.attachmentUploader.removeFromQueue(file);

    if (this.attachmentUploader.queue.length == 0){
      this.fileUploadService.afterRemovingAllFiles();
    }

  }

  public getDownloadUrl( fileId: string) {
    if ( this.projectEntity.type === "offer"){
      return this.offerService.getDownloadUrl(this.projectEntity.id, fileId);
    }

    return this.demandService.getDownloadUrl(this.projectEntity.id, fileId);
  }

  ngOnDestroy () {
    // this.fileUploadService.dispatcher.unsubscribe();
  }

}
