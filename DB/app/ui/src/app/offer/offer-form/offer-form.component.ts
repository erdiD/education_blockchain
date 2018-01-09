import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";

import {IOffer} from "../interfaces/IOffer";
import {LoggingService} from "../../shared/services/logging.service";
import {IProject} from "../interfaces/IProject";
import {OfferService} from "../shared/offer.service";
import {StatusNotificationService} from "../../standard-components/services/status-notification.service";
import {Actions} from "../../shared/types/action.types";
import {States} from "../../shared/types/states.types";
import {StandardButtonDBILVComponent} from "../../standard-components/standard-button-dbilv/standard-button-dbilv.component";
import {FileUploadAreaService} from "../../standard-components/file-upload-area/file-upload-area.service";
import {isNullOrUndefined, isUndefined} from "util";
import {StatusNotification} from "../../standard-components/status-notifications/StatusNotification";


@Component({
  selector: 'app-offer-form-component',
  templateUrl: './offer-form.component.html',
  styleUrls: ['./offer-form.component.scss']
})
export class OfferFormComponent implements OnInit, OnDestroy {

  @Input() currentProject: IProject;
  @Input() projectEntityOffer: IOffer;
  @Input() isNewOffer: boolean;
  public apiEndPointUpload: string;

  @Output('closeProject') closeProject = new EventEmitter();

  public States = States;
  public Actions = Actions;
  public form: FormGroup;
  public calendarStartDate: Date = new Date();
  private withFileUpload: boolean = false;

  constructor (private logService: LoggingService,
               private offerService: OfferService,
               private statusNotificationService: StatusNotificationService,
               private fileUploadService: FileUploadAreaService,
               private router: Router) {
  }

  public ngOnInit () {
    this.initFormGroup();
    this.logService.log(this.projectEntityOffer);
    this.logService.log(this.currentProject);

    if (this.projectEntityOffer && this.projectEntityOffer.id) {
      this.apiEndPointUpload = this.offerService.getUploadUrl(this.projectEntityOffer.id);
    } else {
      console.log("OFC*[ngOnInit] There is no this.projectEntityOffer.id!", this.projectEntityOffer);
    }

    this.fileUploadService.dispatcher.subscribe(event => {
      if (event === FileUploadAreaService.AFTER_ADDING_FILE) {
        this.withFileUpload = true;
      }
      if (event === FileUploadAreaService.AFTER_REMOVING_ALL_FILES) {
        this.withFileUpload = false;
      }
    });

  }

  public ngOnChanges (changed) {
    if (!isUndefined(changed.projectEntityOffer)
      && !isUndefined(changed.projectEntityOffer.currentValue)
      && !isUndefined(changed.projectEntityOffer.currentValue.price)) {

      const priceString = changed.projectEntityOffer.currentValue.price;
      changed.projectEntityOffer.currentValue.price = priceString.replace(/\./g, '');
    }
  }

  public onStartDateInputChange (dateValue: any): void {
    if (this.isValidDate(dateValue)){
      const newDate = this.getValidDateOrNull(dateValue);
      this.projectEntityOffer.startDate = newDate;
    }
    this.form.controls['startDate'].updateValueAndValidity();
  }

  public onEndDateInputChange (dateValue: any): void {
    if (this.isValidDate(dateValue)){
      const newDate = this.getValidDateOrNull(dateValue);
      this.projectEntityOffer.endDate = newDate;
    }
    this.form.controls['endDate'].updateValueAndValidity();
  }

  private isValidDate(dateString: string ) {
    if (!dateString){
      return false;
    }
    let ser = dateString.split('.');
    if (ser[0] && ser[1] && ser[2] &&
      ser[0].length > 0 && ser[0].length <= 2
      && ser[1].length > 0 && ser[1].length < 3
      && ser[2].length == 4
      && Number(ser[0]) < 32
      && Number(ser[1]) < 13
      && Number(ser[2]) < 3000) {
      // looks like a date
      return true;
    }
    return false;
  }

  private getValidDateOrNull(dateString: string ) {

    let ser = dateString.split('.');
    let date = new Date();

    const newDay: number = Number(ser[0]);
    const newMonth: number = Number(ser[1])-1;
    const newYear: number = Number(ser[2]);

    if (ser[0] && ser[1] && ser[2] &&
      ser[0].length > 0 && ser[0].length <= 2
      && ser[1].length > 0 && ser[1].length < 3
      && ser[2].length == 4
      && Number(ser[0]) < 32
      && Number(ser[1]) < 13
      && Number(ser[2]) < 3000) {
      // looks like a date
      date.setDate(newDay);
      date.setMonth(newMonth);
      date.setFullYear(newYear);
      return date;
    }

    return null;
  }


  public initFormGroup (): void {
    this.form = new FormGroup({
      'name': new FormControl({value: '', disabled: true}, [Validators.required]),
      'description': new FormControl({}),
      'document': new FormControl({value: '', disabled: true}),
      'startDate': new FormControl({}, [Validators.required]),
      'endDate': new FormControl({}, [Validators.required]),
      'price': new FormControl({}, [Validators.required, Validators.pattern('^[0-9]{1,45}$')]),
      'contractType': new FormControl({}, [Validators.required]),
      'paymentType': new FormControl({}, [Validators.required])
    });
  }

  public laddaIntercept(btn:StandardButtonDBILVComponent, callback:Function):void{
    if (this.form.valid) {
      btn.isLoading = true;
    }
    callback.call(this);
  }

  public onCloseClick (e: Event): void {
    this.closeProject.emit('');
  }

  private showStatusNotification( statusNotification:StatusNotification = StatusNotificationService.SHOW_STATUS_OFFER_OPEN,
                                  additionalString?: string){
    this.statusNotificationService.showStatusNotification(statusNotification, additionalString);
    let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
      subscription.unsubscribe();
      // this.router.navigate(['project/' + this.currentProject.id]);
      this.router.navigate(['marketplace']);
    });
  }

  private uploadAllFiles( offerId: string, action: string = Actions.EDIT_OFFER,
                          statusNotification: StatusNotification = StatusNotificationService.SHOW_STATUS_OFFER_OPEN ){
    this.apiEndPointUpload = this.offerService.getUploadUrl(offerId);
    this.fileUploadService.setApiEndPoint(this.apiEndPointUpload);

    this.fileUploadService.dispatcher.subscribe(event => {
      if (event === FileUploadAreaService.UPLOAD_ALL_COMPLETE) {
        this.showStatusNotification(statusNotification);
      }
    });

    this.fileUploadService.uploadAllFilesInQueue();
  }

  public onSaveOfferClick (): void {
    console.log(this.form);
    if (this.form.invalid) {
      this.markFormGroupTouched(this.form);
      return;
    }

    if (this.isNewOffer) {
      this.logService.log("SAVE OFFER CLICKED -> NEW OFFER");
      this.projectEntityOffer.action = Actions.OPEN_OFFER;
      this.offerService.createOffer(this.projectEntityOffer).subscribe((response) => {
          this.uploadAllFiles(response.json()['id'], Actions.EDIT_OFFER, StatusNotificationService.SHOW_STATUS_OFFER_EDITED);
        },
        (errRes) => {
          this.logService.error(errRes);
          const msg = errRes.json().error;
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_ERROR, msg);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.router.navigate(['marketplace']);
          });
        });
    } else if( this.withFileUpload ) {
      this.logService.log("SAVE OFFER CLICKED -> EDIT OFFER (WITH FILE UPLOAD)");

      this.offerService.updateOffer(this.projectEntityOffer).subscribe((response) => {
          this.uploadAllFiles(this.projectEntityOffer.id, Actions.EDIT_OFFER, StatusNotificationService.SHOW_STATUS_OFFER_EDITED);
          this.offerService.updateOfferState(this.projectEntityOffer.id, Actions.EDIT_OFFER).subscribe((resp) => {
            this.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_EDITED);
          });

          this.fileUploadService.uploadAllFilesInQueue();
        },
        (error) => {
          this.logService.error(error);
          const msg = error.json().error;
          this.showStatusNotification(StatusNotificationService.SHOW_STATUS_ERROR, msg);
        });


    } else {
      this.logService.log("SAVE OFFER CLICKED -> EDIT OFFER");
      this.offerService.updateOffer(this.projectEntityOffer).subscribe((response) => {
          this.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_EDITED);
        },
        (error) => {
          this.logService.error(error);
          const msg = error.json().error;
          this.showStatusNotification(StatusNotificationService.SHOW_STATUS_ERROR, msg);
        });
    }
  }

  public onSubmitOfferClick (): void {
    this.logService.log("SUBMIT OFFER CLICKED");
    if (this.form.invalid) {
      this.markFormGroupTouched(this.form);
      return;
    }

    if (this.isNewOffer) {
      this.projectEntityOffer.action = Actions.SUBMIT_OFFER;
      this.offerService.createOffer(this.projectEntityOffer).subscribe(
        (response) => {
          this.statusNotificationService.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_SUBMITTED);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.router.navigate(['marketplace']);
          });
        },
        (error) => {
          this.logService.error(error);
          const msg = error.json().error;
          this.showStatusNotification(StatusNotificationService.SHOW_STATUS_ERROR, msg);
          let subscription = this.statusNotificationService.hideStatusEmitter.subscribe(() => {
            subscription.unsubscribe();
            this.router.navigate(['marketplace']);
          });
        });
    } else {
      this.offerService.updateOffer(this.projectEntityOffer).subscribe((response) => {
        //TODO - NA: Test if it works if no changes are done before submitting in Form-View
        this.logService.log("UPDATE OFFER RESPONSE");
        this.logService.log(response);
        this.offerService.updateOfferState(this.projectEntityOffer.id, Actions.SUBMIT_OFFER).subscribe((response) => {
          this.logService.log("UPDATE OFFER-STATE RESPONSE");
          this.logService.log(response);
          this.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_SUBMITTED);
        });
      }, (error) => {
        this.logService.error(error);
        const msg = error.json().error;
        this.showStatusNotification(StatusNotificationService.SHOW_STATUS_ERROR, msg);
      });
    }

  }

  public onEditOfferClick (e: Event): void {
    if (this.form.invalid) {
      this.markFormGroupTouched(this.form);
      return;
    }
    this.offerService.updateOffer(this.projectEntityOffer).subscribe(
      (response) => {
        this.showStatusNotification(StatusNotificationService.SHOW_STATUS_OFFER_EDITED);
      },
      (error) => {
        this.logService.error(error);
        const msg = error.json().error;
        this.showStatusNotification(StatusNotificationService.SHOW_STATUS_ERROR, msg);
      });
  }

  private markFormGroupTouched (formGroup: FormGroup) {
    (<any>Object).values(formGroup.controls).forEach(control => {
      control.markAsTouched();

      if (control.controls) {
        control.controls.forEach(c => this.markFormGroupTouched(c));
      }
    });
  }

  public getCurrentContractType(): string{
    return this.form.get("contractType").value;
  }

  public hasContractType( contractType: string ): boolean{
    if (isNullOrUndefined(this.form.get("contractType").value)){
      return false;
    }
    return this.form.get("contractType").value === contractType ;
  }


  /**
   * returns true if the option should be disabled, false if it is enabled
   * and null if it should be removed completly
   *
   *
   * @param pt
   * @returns {any}
   */
  public isDisabledForPaymentType( pt: string ): Boolean{

    const ct = this.form.get("contractType").value;

    if( isNullOrUndefined(ct) ) {
      return false;
    }

    // Dienstleistungsvertrag
    if (ct == "SERVICE_CONTRACT") {
      if (pt === "MONTHLY") {
        return false;
      } else {
        return null;
      }
    }

    // Werkvertrag
    if (ct == "WORK_AND_SERVICE_CONTRACT") {
      if (pt === "MONTHLY" || pt === "OBJECTIVE_ORIENTED") {
        return false;
      } else{
        return true;
      }
    }

    // Leistungsschein
    if (ct == "SUBSCRIPTION_CONTRACT") {
      if (pt === "MONTHLY") {
        return false;
      } else {
        return null;
      }
    }

    return false;
  }

  ngOnDestroy () {
    // this.fileUploadService.dispatcher.unsubscribe();
  }

}
